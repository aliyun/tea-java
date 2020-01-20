package com.aliyun.tea;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TeaResponse {

    // private CloseableHttpResponse response;
    private HttpURLConnection conn;
    public int statusCode;
    public String statusMessage;
    public HashMap<String, String> headers = new HashMap<String, String>();
    public InputStream body;

    public TeaResponse() {
    }

    public TeaResponse(HttpURLConnection conn) throws IOException {
        this.conn = conn;
        statusCode = conn.getResponseCode();
        statusMessage = conn.getResponseMessage();
        body = getResponse();
        Map<String, List<String>> headers = conn.getHeaderFields();
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
            this.headers.put(key.toLowerCase(), builder.toString());
        }
    }

    public String getResponseBody() throws IOException {

        try {
            if (null == body) {
                return String.format("{\"message\":\"%s\"}", statusMessage);
            }
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            while (true) {
                final int read = body.read(buff);
                if (read == -1) {
                    break;
                }
                os.write(buff, 0, read);
            }
            return new String(os.toByteArray());
        } finally {
            conn.disconnect();
        }
    }

    public InputStream getResponse() throws IOException {
        InputStream content;
        try {
            content = this.conn.getInputStream();
        } catch (IOException e) {
            content = this.conn.getErrorStream();
        }
        return content;
    }
}
