package com.aliyun.darabonba.okhttp;

import com.aliyun.darabonba.Core;
import com.aliyun.darabonba.Request;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.URL;

public class OkRequestBuilderTest {
    @Test
    public void httpMethodTest() throws Exception{
        OkRequestBuilder builder = Mockito.spy(new OkRequestBuilder(new okhttp3.Request.Builder()));
        Request request = new Request();
        builder.url(new URL("http://ecs.aliyuncs.com"));
        request.method = "DELETE";
        builder.buildRequest(request);
        Mockito.verify(builder, Mockito.times(1)).buildRequest(request);
        request.body = Core.toReadable("request body");
        builder.buildRequest(request);
        Mockito.verify(builder, Mockito.times(2)).buildRequest(request);

        builder = Mockito.spy(new OkRequestBuilder(new okhttp3.Request.Builder()));
        builder.url(new URL("http://ecs.aliyuncs.com"));
        request.method = "POST";
        builder.buildRequest(request);
        Mockito.verify(builder, Mockito.times(1)).buildRequest(request);

        builder = Mockito.spy(new OkRequestBuilder(new okhttp3.Request.Builder()));
        builder.url(new URL("http://ecs.aliyuncs.com"));
        request.method = "PATCH";
        builder.buildRequest(request);
        Mockito.verify(builder, Mockito.times(1)).buildRequest(request);

        builder = Mockito.spy(new OkRequestBuilder(new okhttp3.Request.Builder()));
        builder.url(new URL("http://ecs.aliyuncs.com"));
        request.method = "PUT";
        builder.buildRequest(request);
        Mockito.verify(builder, Mockito.times(1)).buildRequest(request);

        builder = Mockito.spy(new OkRequestBuilder(new okhttp3.Request.Builder()));
        builder.url(new URL("http://ecs.aliyuncs.com"));
        request.method = "HEAD";
        builder.buildRequest(request);
        Mockito.verify(builder, Mockito.times(1)).buildRequest(request);

    }
}
