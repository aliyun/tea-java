package com.aliyun.darabonba.okhttp;

import com.aliyun.darabonba.Request;
import okhttp3.MediaType;
import okio.BufferedSink;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class OkRequestBodyTest {

    @Test
    public void contentTypeTest() {
        Request request = new Request();
        OkRequestBody body = new OkRequestBody(request);
        MediaType result = body.contentType();
        Assert.assertNull(result);

        request.body = new ByteArrayInputStream(new byte[]{1});
        body = new OkRequestBody(request);
        result = body.contentType();
        Assert.assertEquals("application/json; charset=UTF-8;", result.toString());

        request.headers.put("content-type", "text/h323");
        body = new OkRequestBody(request);
        result = body.contentType();
        Assert.assertEquals("text/h323", result.toString());
    }

    @Test
    public void writeToTest() throws IOException {
        Request request = new Request();
        OkRequestBody body = Mockito.spy(new OkRequestBody(request));
        BufferedSink sink = Mockito.mock(BufferedSink.class);
        body.writeTo(sink);
        Mockito.verify(body).writeTo(sink);

        request.body = new ByteArrayInputStream("tes".getBytes("UTF-8"));
        body = Mockito.spy(new OkRequestBody(request));
        body.writeTo(sink);
        Mockito.verify(body).writeTo(sink);
    }


}
