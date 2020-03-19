package com.aliyun.tea;

import com.aliyun.tea.okhttp.OkHttpClientBuilder;
import com.aliyun.tea.okhttp.OkRequestBuilder;
import com.aliyun.tea.utils.StringUtils;
import com.aliyun.tea.utils.TrueHostnameVerifier;
import com.aliyun.tea.utils.X509TrustManagerImp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Tea {
    private static final List<String> HAVE_BODY_METHOD_LSIT = new ArrayList<>();
    public static final ConcurrentHashMap<Object, OkHttpClient> clients = new ConcurrentHashMap<>();

    static {
        HAVE_BODY_METHOD_LSIT.add("POST");
        HAVE_BODY_METHOD_LSIT.add("PUT");
        HAVE_BODY_METHOD_LSIT.add("PATCH");
    }

    public static String composeUrl(TeaRequest request) throws UnsupportedEncodingException {
        Map<String, String> queries = request.query;
        String host = request.headers.get("host");
        String protocol = null == request.protocol ? "http" : request.protocol;
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(protocol);
        urlBuilder.append("://").append(host);
        if (null != request.pathname) {
            urlBuilder.append(request.pathname);
        }
        if (queries.size() > 0) {
            if (urlBuilder.indexOf("?") >= 1) {
                urlBuilder.append("&");
            } else {
                urlBuilder.append("?");
            }
            for (Map.Entry<String, String> entry : queries.entrySet()) {
                String key = entry.getKey();
                String val = entry.getValue();
                if (val == null) {
                    continue;
                }
                urlBuilder.append(URLEncoder.encode(key, "UTF-8"));
                urlBuilder.append("=");
                urlBuilder.append(URLEncoder.encode(val, "UTF-8"));
                urlBuilder.append("&");
            }
            int strIndex = urlBuilder.length();
            urlBuilder.deleteCharAt(strIndex - 1);
        }
        return urlBuilder.toString();
    }

    public static OkHttpClient creatOkHttp(Map<String, Object> map) throws Exception {
        OkHttpClientBuilder builder = new OkHttpClientBuilder();
        builder = builder.connectTimeout(map).readTimeout(map).connectionPool(map).certificate(map).proxy(map);
        return builder.buildOkHttpClient();
    }

    public static TeaResponse doAction(TeaRequest request, Map<String, Object> runtimeOptions, Object client) throws Exception {
        OkHttpClient okHttpClient = Tea.clients.get(client);
        if (null == okHttpClient) {
            okHttpClient = creatOkHttp(runtimeOptions);
            Tea.clients.put(client, okHttpClient);
        }
       return doAction(request, okHttpClient);
    }

    public static TeaResponse doAction(TeaRequest request, OkHttpClient okHttpClient) throws Exception {
        Request.Builder requestBuilder = new Request.Builder();
        OkRequestBuilder okRequestBuilder = new OkRequestBuilder(requestBuilder).url(request).header(request).httpMethod(request);
        Response response = okHttpClient.newCall(okRequestBuilder.buildRequest()).execute();
        return new TeaResponse(response);
    }

    public static TeaResponse doAction(TeaRequest request, Map<String, Object> runtimeOptions)
            throws IOException, KeyManagementException, NoSuchAlgorithmException {
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
        if (!StringUtils.isEmpty(runtimeOptions.get("readTimeout"))) {
            httpConn.setReadTimeout(Integer.valueOf(String.valueOf(runtimeOptions.get("readTimeout"))));
        }

        if (!StringUtils.isEmpty(runtimeOptions.get("connectTimeout"))) {
            httpConn.setConnectTimeout(Integer.valueOf(String.valueOf(runtimeOptions.get("connectTimeout"))));
        }

        for (String headerName : request.headers.keySet()) {
            httpConn.setRequestProperty(toUpperFirstChar(headerName), request.headers.get(headerName));
        }

        httpConn.connect();
        if (request.body != null && HAVE_BODY_METHOD_LSIT.contains(request.method.toUpperCase())) {
            OutputStream out = httpConn.getOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = request.body.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        }
        return new TeaResponse(httpConn);
    }

    public static String toUpperFirstChar(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private static SSLSocketFactory createSSLSocketFactory() throws NoSuchAlgorithmException, KeyManagementException {
        X509TrustManager compositeX509TrustManager = new X509TrustManagerImp();
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{compositeX509TrustManager}, new java.security.SecureRandom());
        return sslContext.getSocketFactory();
    }

    public static boolean allowRetry(Map<String, ?> map, int retryTimes, long now) {
        int retry;
        if (map == null) {
            return false;
        } else {
            retry = map.get("maxAttempts") == null ? 0 : Integer.parseInt(String.valueOf(map.get("maxAttempts")));
        }
        return retry >= retryTimes;
    }

    public static int getBackoffTime(Object o, int retryTimes) {
        int backOffTime = 0;
        Map<String, Object> map = (Map<String, Object>) o;
        if (StringUtils.isEmpty(map.get("policy")) || "no".equals(map.get("policy"))) {
            return backOffTime;
        }
        if (!StringUtils.isEmpty(map.get("period")) &&
                (backOffTime = Integer.valueOf(String.valueOf(map.get("period")))) <= 0) {
            return retryTimes;
        }
        return backOffTime;
    }

    public static void sleep(int time) throws InterruptedException {
        Thread.sleep(time);
    }

    public static boolean isRetryable(Exception e) {
        return e instanceof TeaRetryableException;
    }

    public static InputStream toReadable(String string) throws UnsupportedEncodingException {
        return toReadable(string.getBytes("UTF-8"));
    }

    public static InputStream toReadable(byte[] byteArray) {
        return new ByteArrayInputStream(byteArray);
    }
}
