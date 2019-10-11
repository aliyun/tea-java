package com.aliyun.tea;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TeaRequestTest {
    @Test
    public void TeaRequestTest(){
        TeaRequest request = new TeaRequest();
        Assert.assertEquals("http",request.protocol);
        Assert.assertEquals("GET",request.method);
    }
    @Test
    public void toStringTest(){
        TeaRequest request = new TeaRequest();
        Map<String,String> map = new HashMap<>();
        map.containsKey(" ");
        map.containsValue(":");
        Assert.assertEquals(map,request.query);
//        request.query(map);

        Assert.assertEquals("Protocol: http\n" +
                "Port: null\n" +
                "GET null\n" +
                "Query:\n" +
                "Headers:\n",request.toString());

    }
}
