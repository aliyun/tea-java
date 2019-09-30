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

    public int statusCode;
    public String statusMessage;
    public HashMap<String, String> headers = new HashMap<String, String>();
    private HttpURLConnection content;

    public TeaResponse() {
    }

    public TeaResponse(HttpURLConnection conn) throws IOException {
        this.content = conn;
        statusCode = conn.getResponseCode();
        statusMessage = conn.getResponseMessage();
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
            this.headers.put(key, builder.toString());
        }
    }

    public String getResponseBody() throws IOException {
        InputStream content;
        try {
            content = this.content.getInputStream();
        } catch (IOException e){
            content = this.content.getErrorStream();
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];

        while (true) {
            final int read = content.read(buff);
            if (read == -1) {
                break;
            }
            os.write(buff, 0, read);
        }

        return new String(os.toByteArray());
    }

}

