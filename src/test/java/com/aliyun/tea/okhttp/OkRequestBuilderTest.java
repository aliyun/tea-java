package com.aliyun.tea.okhttp;

import com.aliyun.tea.TeaRequest;
import okhttp3.Request;
import org.junit.Test;
import org.mockito.Mockito;

public class OkRequestBuilderTest {
    @Test
    public void httpMethodTest() {
        OkRequestBuilder builder = Mockito.spy(new OkRequestBuilder(new Request.Builder()));
        TeaRequest request = new TeaRequest();
        request.method = "DELETE";
        builder.httpMethod(request);
        request.method = "POST";
        builder.httpMethod(request);
        request.method = "PATCH";
        builder.httpMethod(request);
        request.method = "PUT";
        builder.httpMethod(request);
        Mockito.verify(builder, Mockito.times(4)).httpMethod(request);
    }
}
