package com.aliyun.tea;

import com.aliyun.tea.utils.StringUtils;
import okhttp3.Headers;
import okhttp3.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeaResponse {

    public Response response;
    public int statusCode;
    public String statusMessage;
    public HashMap<String, String> headers;
    public InputStream body;

    public TeaResponse() {
        headers = new HashMap<String, String>();
    }

    public TeaResponse(Response response) {
        headers = new HashMap<String, String>();
        this.response = response;
        statusCode = response.code();
        statusMessage = response.message();
        body = response.body().byteStream();
        Headers headers = response.headers();
        Map<String, List<String>> resultHeaders = headers.toMultimap();
        for(Map.Entry<String, List<String>> entry: resultHeaders.entrySet()) {
            this.headers.put(entry.getKey(), StringUtils.join(";", entry.getValue()));
        }
    }

    public InputStream getResponse() throws IOException {
        return this.body;
    }

    public String getResponseBody() throws IOException {
        if (null == body) {
            return String.format("{\"message\":\"%s\"}", statusMessage);
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buff = new byte[4096];
        while (true) {
            final int read = body.read(buff);
            if (read == -1) {
                break;
            }
            os.write(buff, 0, read);
        }
        return new String(os.toByteArray());
    }


}
