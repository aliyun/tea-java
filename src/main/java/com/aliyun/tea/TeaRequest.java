package com.aliyun.tea;

import com.aliyun.tea.utils.TrueHostnameVerifier;
import com.aliyun.tea.utils.X509TrustManagerImp;
import org.apache.http.client.ClientProtocolException;

import javax.net.ssl.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TeaRequest {
    private final static String SEPARATOR = "&";
    public final static String URL_ENCODING = "UTF-8";
    private static final String ALGORITHM_NAME = "HmacSHA1";

    public String protocol;

    public Integer port;

    public String method;

    public String pathname;

    public Map<String, String> query;

    public Map<String, String> headers;

    public String body;

    public TeaRequest() {
        protocol = "http";
        method = "GET";
        query = new HashMap<String, String>();
        headers = new HashMap<String, String>();
    }

    public static TeaRequest create() {
        return new TeaRequest();
    }

    public static TeaResponse doAction(TeaRequest request)
            throws URISyntaxException, ClientProtocolException, IOException, KeyManagementException, NoSuchAlgorithmException {
        String strUrl = request.composeUrl(request.headers.get("host"), request.query, request.protocol);
        URL url = new URL(strUrl);
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        SSLSocketFactory sslSocketFactory = request.createSSLSocketFactory();
        HttpURLConnection httpConn;
        if ("https".equalsIgnoreCase(url.getProtocol())) {
            HttpsURLConnection httpsConn = (HttpsURLConnection) url.openConnection();
            httpsConn.setSSLSocketFactory(sslSocketFactory);
            httpsConn.setHostnameVerifier(new TrueHostnameVerifier());
            httpConn = httpsConn;
        } else {
            httpConn = (HttpURLConnection) url.openConnection();
        }
        httpConn.setRequestMethod(request.method.toUpperCase());
        httpConn.setInstanceFollowRedirects(false);
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        httpConn.setUseCaches(false);
//        httpConn.setConnectTimeout(runtime.get("connectTimeout") == null ? 5000 : (int) runtime.get("connectTimeout"));
//        httpConn.setReadTimeout(runtime.get("readTimeout") == null ? 15000 : (int) runtime.get("readTimeout"));
        httpConn.setRequestProperty("Accept-Encoding", "identity");
        InputStream content = null;
        try {
            httpConn.connect();
            content = httpConn.getInputStream();
            TeaResponse response = new TeaResponse();
            request.parseHttpConn(response, httpConn, content);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (content != null) {
                content.close();
            }
            httpConn.disconnect();
        }
    }

    public SSLSocketFactory createSSLSocketFactory() throws NoSuchAlgorithmException, KeyManagementException {
        X509TrustManager compositeX509TrustManager = new X509TrustManagerImp();
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{compositeX509TrustManager},
                new java.security.SecureRandom());
        return sslContext.getSocketFactory();
    }


    public boolean allowRetry(Object o, int retryTimes) {
        int retry;
        if (o == null) {
            return false;
        } else {
            Map<String, Object> map = (Map<String, Object>) o;
            retry = map.get("maxattempts") == null ? 0 : (int) map.get("maxattempts");
        }
        if (retry < retryTimes) {
            return true;
        }
        return false;
    }

    public int getBackoffTime(Object o, int retryTimes) {
        int backOffTime = 0;
        if (o == null || (backOffTime = Integer.valueOf(String.valueOf(o))) <= 0) {
            return retryTimes;
        }
        return backOffTime;
    }

    public void sleep(int time) throws InterruptedException {
        Thread.sleep(time);
    }

    @Override
    public String toString() {
        String output = "Protocol: " + this.protocol + "\nPort: " + this.port + "\n" + this.method + " " + this.pathname
                + "\n";
        output += "Query:\n";
        for (Entry<String, String> e : this.query.entrySet()) {
            output += "    " + e.getKey() + ": " + e.getValue() + "\n";
        }
        output += "Headers:\n";
        for (Entry<String, String> e : this.headers.entrySet()) {
            output += "    " + e.getKey() + ": " + e.getValue() + "\n";
        }
        return output;
    }

    public String composeUrl(String endpoint, Map<String, String> queries, String protocol) throws UnsupportedEncodingException {
        Map<String, String> mapQueries = queries;
        StringBuilder urlBuilder = new StringBuilder("");
        urlBuilder.append(protocol);
        urlBuilder.append("://").append(endpoint);
        urlBuilder.append("/?");
        StringBuilder builder = new StringBuilder("");
        for (Map.Entry<String, String> entry : mapQueries.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();
            if (val == null) {
                continue;
            }
            builder.append(URLEncoder.encode(key, "UTF-8"));
            builder.append("=").append(URLEncoder.encode(val, "UTF-8"));
            builder.append("&");
        }

        int strIndex = builder.length();
        builder.deleteCharAt(strIndex - 1);
        String query = builder.toString();
        return urlBuilder.append(query).toString();
    }

    public void parseHttpConn(TeaResponse response, HttpURLConnection httpConn, InputStream content)
            throws IOException, NoSuchAlgorithmException {
        byte[] buff = readContent(content);
        response.body = buff;
        response.statusCode = httpConn.getResponseCode();
        response.statusMessage = httpConn.getResponseMessage();
        Map<String, List<String>> headers = httpConn.getHeaderFields();
        for (Entry<String, List<String>> entry : headers.entrySet()) {
            String key = entry.getKey();
            if (null == key) {
                continue;
            }
            List<String> values = entry.getValue();
            StringBuilder builder = new StringBuilder(values.get(0));
            for (int i = 1; i < values.size(); i++) {
                builder.append(",");
                builder.append(values.get(i));
            }
            response.headers.put(key, builder.toString());
        }
    }

    public byte[] readContent(InputStream content) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];

        while (true) {
            final int read = content.read(buff);
            if (read == -1) {
                break;
            }
            outputStream.write(buff, 0, read);
        }

        return outputStream.toByteArray();
    }
}