package com.aliyun.tea.okhttp;

import com.aliyun.tea.Tea;
import com.aliyun.tea.TeaRequest;
import okhttp3.Request;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.URL;

public class OkRequestBuilderTest {
    @Test
    public void httpMethodTest() throws Exception{
        OkRequestBuilder builder = Mockito.spy(new OkRequestBuilder(new Request.Builder()));
        TeaRequest request = new TeaRequest();
        builder.url(new URL("http://ecs.aliyuncs.com"));
        request.method = "DELETE";
        builder.buildRequest(request);
        Mockito.verify(builder, Mockito.times(1)).buildRequest(request);
        request.body = Tea.toReadable("request body");
        builder.buildRequest(request);
        Mockito.verify(builder, Mockito.times(2)).buildRequest(request);

        builder = Mockito.spy(new OkRequestBuilder(new Request.Builder()));
        builder.url(new URL("http://ecs.aliyuncs.com"));
        request.method = "POST";
        builder.buildRequest(request);
        Mockito.verify(builder, Mockito.times(1)).buildRequest(request);

        builder = Mockito.spy(new OkRequestBuilder(new Request.Builder()));
        builder.url(new URL("http://ecs.aliyuncs.com"));
        request.method = "PATCH";
        builder.buildRequest(request);
        Mockito.verify(builder, Mockito.times(1)).buildRequest(request);

        builder = Mockito.spy(new OkRequestBuilder(new Request.Builder()));
        builder.url(new URL("http://ecs.aliyuncs.com"));
        request.method = "PUT";
        builder.buildRequest(request);
        Mockito.verify(builder, Mockito.times(1)).buildRequest(request);

        builder = Mockito.spy(new OkRequestBuilder(new Request.Builder()));
        builder.url(new URL("http://ecs.aliyuncs.com"));
        request.method = "HEAD";
        builder.buildRequest(request);
        Mockito.verify(builder, Mockito.times(1)).buildRequest(request);

    }
}
