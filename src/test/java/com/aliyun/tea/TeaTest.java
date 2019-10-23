package com.aliyun.tea;

import com.aliyun.tea.utils.StringUtils;
import com.aliyun.tea.utils.X509TrustManagerImp;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import javax.net.ssl.SSLSocketFactory;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;


public class TeaTest {
    @Test
    public void init() {
        new Tea();
    }


    @Test
    public void composeUrlTest() throws UnsupportedEncodingException, NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        Method composeUrl = Tea.class.getDeclaredMethod("composeUrl", TeaRequest.class);
        composeUrl.setAccessible(true);

        TeaRequest request = new TeaRequest();
        Map<String,String> headers = new HashMap<>();
        headers.put("test","test");
        request.protocol = "HTTP";
        request.pathname = "?";
        request.method = "GET";
        request.headers = headers;
        request.query = headers;

        String str = (String) composeUrl.invoke(Tea.class, request);
        Assert.assertEquals("HTTP://null??test=test", str);

    }

    @Test
    public void doActionTest() throws Exception{
        Map<String,Object> map = new HashMap<>();
        TeaRequest request = new TeaRequest();
        try {
            Tea.doAction(request,map);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals("nullnull", e.getMessage());
        }
        map.put("readTimeout", 1000);
        map.put("connectTimeout", 1000);
        request = new TeaRequest();
        request.protocol = "https";
        Map<String, String> header = new HashMap<>();
        header.put("host", "www.baidu.com");
        request.headers = header;
        request.pathname = "";
        request.body = "{}";
        request.method = "PUT";
        Assert.assertNotNull(Tea.doAction(request,map));
    }

    @Test
    public void toUpperFirstCharTest() {
        String name = Tea.toUpperFirstChar("word");
        Assert.assertEquals("Word", name);
    }

    @Test
    public void createSSLSocketFactoryTest() throws Exception {
        X509Certificate x509Certificates = mock(X509Certificate.class);
        Tea tea = new Tea();
        X509TrustManagerImp x509 = new X509TrustManagerImp();
        x509.getAcceptedIssuers();
        x509.checkServerTrusted(new X509Certificate[] { x509Certificates }, "test");
        x509.checkClientTrusted(new X509Certificate[] { x509Certificates }, "test1");
        SSLSocketFactory sslSocketFactory = Whitebox.invokeMethod(tea, "createSSLSocketFactory");
        Assert.assertNotNull(sslSocketFactory);
    }
    @Test
    public void allowRetryTest(){
        long now = 1L;
        Map<String,Object> map = new HashMap<>();
        Assert.assertEquals(false,Tea.allowRetry(map,1,now));
        Assert.assertEquals(true,Tea.allowRetry(map,0,now));
    }

    @Test
    public void getBackoffTimeTest()  {
        StringUtils stringUtils = new StringUtils();
        Map<String,Object> map = new HashMap<>();
        stringUtils.isEmpty("policy");
        Assert.assertEquals(0,Tea.getBackoffTime(map,0));
    }
    @Test
    public void sleepTest() throws InterruptedException {
        int time = 0;
        Tea.sleep(time);
        Assert.assertNotNull(time);
    }
    @Test
    public void isRetryableTest() {
        Exception exception = new Exception();
        Assert.assertEquals(false,Tea.isRetryable(exception));
    }
}
