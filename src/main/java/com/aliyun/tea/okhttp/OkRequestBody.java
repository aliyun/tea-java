package com.aliyun.tea.okhttp;

import com.aliyun.tea.TeaRequest;
import com.aliyun.tea.utils.StringUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

import java.io.IOException;
import java.io.InputStream;

public class OkRequestBody extends RequestBody {

    private InputStream inputStream;
    private String contentType;

    public OkRequestBody(TeaRequest teaRequest) {
        this.inputStream = teaRequest.body;
        this.contentType = teaRequest.headers.get("content-type");
    }


    @Override
    public MediaType contentType() {
        MediaType type;
        if (StringUtils.isEmpty(contentType)) {
            if (null == inputStream) {
                return null;
            }
            type = MediaType.parse("application/json; charset=UTF-8;");
            return type;
        }
        return MediaType.parse(contentType);
    }

    @Override
    public long contentLength() throws IOException {
        if (null != inputStream && inputStream.available() > 0) {
            return inputStream.available();
        }
        return super.contentLength();
    }

    @Override
    public void writeTo(BufferedSink bufferedSink) throws IOException {
        if (null == inputStream) {
            return;
        }
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            bufferedSink.write(buffer, 0, bytesRead);
        }
    }
}
