package com.aliyun.tea;

import com.aliyun.tea.interceptor.InterceptorChain;
import com.aliyun.tea.interceptor.InterceptorContext;
import com.aliyun.tea.okhttp.ClientHelper;
import com.aliyun.tea.okhttp.OkRequestBuilder;
import com.aliyun.tea.utils.AttributeMap;
import com.aliyun.tea.utils.StringUtils;
import okhttp3.*;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class Tea {

    private static String composeUrl(TeaRequest request, Map<String, Object> runtimeOptions) {
        Map<String, String> queries = request.query;
        String host;
        if (!StringUtils.isEmpty(runtimeOptions.get("domain"))) {
            host = String.valueOf(runtimeOptions.get("domain"));
        } else {
            host = request.headers.get("host");
        }
        
        String protocol = null == request.protocol ? "https" : request.protocol;
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(protocol);
        urlBuilder.append("://").append(host);
        if (null != request.port) {
            urlBuilder.append(":").append(request.port);
        }
        if (null != request.pathname) {
            urlBuilder.append(request.pathname);
        }
        if (queries != null && !queries.isEmpty()) {
            if (urlBuilder.indexOf("?") >= 1) {
                urlBuilder.append("&");
            } else {
                urlBuilder.append("?");
            }
            try {
                for (Map.Entry<String, String> entry : queries.entrySet()) {
                    String key = entry.getKey();
                    String val = entry.getValue();
                    if (null == val) {
                        continue;
                    }
                    urlBuilder.append(URLEncoder.encode(key, "UTF-8"));
                    urlBuilder.append("=");
                    urlBuilder.append(URLEncoder.encode(val, "UTF-8"));
                    urlBuilder.append("&");
                }
            } catch (Exception e) {
                throw new TeaException(e.getMessage(), e);
            }
            int strIndex = urlBuilder.length();
            urlBuilder.deleteCharAt(strIndex - 1);
        }
        return urlBuilder.toString();
    }

    public static TeaResponse doAction(TeaRequest request) {
        return doAction(request, new HashMap<String, Object>());
    }

    public static TeaResponse doAction(TeaRequest request, Map<String, Object> runtimeOptions) {
        try {
            String urlString = Tea.composeUrl(request, runtimeOptions);
            URL url = new URL(urlString);
            OkHttpClient okHttpClient = ClientHelper.getOkHttpClient(url.getHost(), url.getPort(), runtimeOptions);
            Request.Builder requestBuilder = new Request.Builder();
            OkRequestBuilder okRequestBuilder = new OkRequestBuilder(requestBuilder).url(url).header(request.headers);
            Response response = okHttpClient.newCall(okRequestBuilder.buildRequest(request)).execute();
            return new TeaResponse(response);
        } catch (Exception e) {
            throw new TeaRetryableException(e);
        }
    }

    public static TeaResponse doAction(TeaRequest request, Map<String, Object> runtimeOptions, InterceptorChain chain) {
        if (null == chain) {
            return doAction(request, runtimeOptions);
        }
        InterceptorContext context = InterceptorContext.create().setRuntimeOptions(runtimeOptions);
        context = chain.modifyRuntimeOptions(context, AttributeMap.empty());
        context.setTeaRequest(request);
        context = chain.modifyRequest(context, AttributeMap.empty());
        TeaResponse response = doAction(request, runtimeOptions);
        context.setTeaResponse(response);
        context = chain.modifyResponse(context, AttributeMap.empty());
        return context.teaResponse();
    }

    public static boolean allowRetry(Map<String, ?> map, int retryTimes, long now) {
        if (0 == retryTimes) {
            return true;
        }
        if (map == null) {
            return false;
        }
        Object shouldRetry = map.get("retryable");
        if (shouldRetry instanceof Boolean && (boolean) shouldRetry) {
            int retry = map.get("maxAttempts") == null ? 0 : Integer.parseInt(String.valueOf(map.get("maxAttempts")));
            return retry >= retryTimes;
        }
        return false;
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

    public static void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new TeaException(e.getMessage(), e);
        }
    }

    public static boolean isRetryable(Exception e) {
        return e instanceof TeaRetryableException;
    }

    public static InputStream toReadable(String string) {
        try {
            return toReadable(string.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new TeaException(e.getMessage(), e);
        }
    }

    public static InputStream toReadable(byte[] byteArray) {
        return new ByteArrayInputStream(byteArray);
    }

    public static OutputStream toWriteable(int size) {
        try {
            return new ByteArrayOutputStream(size);
        } catch (IllegalArgumentException e) {
            throw new TeaException(e.getMessage(), e);
        }
    }

    public static OutputStream toWriteable() {
        return new ByteArrayOutputStream();
    }
}
