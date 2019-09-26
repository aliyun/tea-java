package com.aliyun.tea;

import org.apache.http.Header;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.IOException;
import java.util.HashMap;

public class TeaResponse {

    private CloseableHttpResponse response;
    public int statusCode;
    public String statusMessage;
    public HashMap<String, String> headers = new HashMap<String, String>();
    public byte[] body;

    public TeaResponse() {
    }

    public TeaResponse(CloseableHttpResponse res) {
        this.response = res;
        StatusLine status = res.getStatusLine();
        this.statusCode = status.getStatusCode();
        this.statusMessage = status.getReasonPhrase();
        // headers
        for (Header header : res.getAllHeaders()) {
            headers.put(header.getName(), header.getValue());
        }
    }

    public String getResponseBody() throws IOException {
        return new String(this.body, "UTF-8");
    }
}
