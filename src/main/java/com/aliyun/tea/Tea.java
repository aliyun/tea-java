package com.aliyun.tea;

import com.aliyun.tea.okhttp.ClientHelper;
import com.aliyun.tea.okhttp.OkRequestBuilder;
import com.aliyun.tea.utils.StringUtils;
import com.aliyun.tea.utils.X509TrustManagerImp;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class Tea {

    private static String composeUrl(TeaRequest request) throws UnsupportedEncodingException {
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
                if (val == null || "null".equals(val)) {
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


    public static TeaResponse doAction(TeaRequest request, Map<String, Object> runtimeOptions) throws Exception {
        String urlString = Tea.composeUrl(request);
        URL url = new URL(urlString);
        OkHttpClient okHttpClient = ClientHelper.getOkHttpClient(url.getHost(), url.getPort(), runtimeOptions);
        Request.Builder requestBuilder = new Request.Builder();
        Map<String, String> header = setProxyAuthorization(request.headers, runtimeOptions.get("httpsProxy"));
        OkRequestBuilder okRequestBuilder = new OkRequestBuilder(requestBuilder).url(url).header(header);
        Response response = okHttpClient.newCall(okRequestBuilder.buildRequest(request)).execute();
        return new TeaResponse(response);
    }

    private static Map<String, String> setProxyAuthorization(Map<String, String> header, Object httpsProxy) throws MalformedURLException {
        if (!StringUtils.isEmpty(httpsProxy)) {
            URL proxyUrl = new URL(String.valueOf(httpsProxy));
            String userInfo = proxyUrl.getUserInfo();
            if (null != userInfo) {
                String[] userMessage = userInfo.split(":");
                String credential = Credentials.basic(userMessage[0], userMessage[1]);
                header.put("Proxy-Authorization", credential);
            }
        }
        return header;
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
