package com.aliyun.tea;

import com.aliyun.tea.utils.X509TrustManagerImp;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import javax.net.ssl.SSLSocketFactory;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.cert.X509Certificate;

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
        request.protocol = "HTTP";
        String str = (String) composeUrl.invoke(Tea.class, request);
        Assert.assertEquals("HTTP://nullnull", str);
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

}
