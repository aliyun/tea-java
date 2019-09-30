package com.aliyun.tea;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.aliyun.tea.utils.TrueHostnameVerifier;
import com.aliyun.tea.utils.X509TrustManagerImp;

import org.apache.http.client.ClientProtocolException;

public class Tea {

    private static String composeUrl(TeaRequest request) throws UnsupportedEncodingException {
        Map<String, String> queries = request.query;
        String endpoint = request.headers.get("host");
        String protocol = request.protocol;
        StringBuilder urlBuilder = new StringBuilder("");
        urlBuilder.append(protocol);
        urlBuilder.append("://").append(endpoint);
        urlBuilder.append(request.pathname);

        StringBuilder builder = new StringBuilder("");
        if (null != queries && queries.size() > 0) {
            urlBuilder.append("?");
            for (Map.Entry<String, String> entry : queries.entrySet()) {
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
        }
        String query = builder.toString();
        return urlBuilder.append(query).toString();
    }

    public static String toUpperFirstChar(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static TeaResponse doAction(TeaRequest request) throws URISyntaxException, ClientProtocolException,
            IOException, KeyManagementException, NoSuchAlgorithmException {
        String strUrl = composeUrl(request);
        URL url = new URL(strUrl);
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        System.setProperty("sun.net.http.retryPost", "false");
        HttpURLConnection httpConn;
        if ("https".equalsIgnoreCase(url.getProtocol())) {
            SSLSocketFactory sslSocketFactory = createSSLSocketFactory();
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
        Map<String, String> headerMap = request.headers;
        if (null != headerMap && headerMap.size() > 0) {
            for (String headerName : request.headers.keySet()) {
                httpConn.setRequestProperty(toUpperFirstChar(headerName), request.headers.get(headerName));
            }
        }
        httpConn.connect();
        if (request.body != null && "POST".equals(request.method)) {
            OutputStream out = httpConn.getOutputStream();
            out.write(request.body.getBytes("UTF-8"));
            out.flush();
        }
        return new TeaResponse(httpConn);
    }

    private static SSLSocketFactory createSSLSocketFactory() throws NoSuchAlgorithmException, KeyManagementException {
        X509TrustManager compositeX509TrustManager = new X509TrustManagerImp();
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[] { compositeX509TrustManager }, new java.security.SecureRandom());
        return sslContext.getSocketFactory();
    }

    public static boolean allowRetry(Object o, int retryTimes) {
        int retry;
        if (o == null) {
            return false;
        } else {
            Map<String, Object> map = (Map<String, Object>) o;
            retry = map.get("maxAttempts") == null ? 0 : (int) map.get("maxAttempts");
        }
        if (retry < retryTimes) {
            return true;
        }
        return false;
    }

    public static int getBackoffTime(Object o, int retryTimes) {
        int backOffTime = 0;
        if (o == null || (backOffTime = Integer.valueOf(String.valueOf(o))) <= 0) {
            return retryTimes;
        }
        return backOffTime;
    }

    public static void sleep(int time) throws InterruptedException {
        Thread.sleep(time);
    }
}
