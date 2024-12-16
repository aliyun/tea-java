package com.aliyun.darabonba.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONUtilsTest {
    @Test
    public void stringifyTest() {
        Context context = new Context();
        context.setContextInteger(123);
        context.setContextLong(123L);
        context.setContextDouble(1.123);
        context.setContextFloat(3.456f);
        context.setContextListLong(Arrays.asList(123L, 456L));
        Map<String, List<Integer>> integerListMap = new HashMap<>();
        List<Integer> integerList = Arrays.asList(123, 456);
        integerListMap.put("integerList", integerList);
        context.setIntegerListMap(integerListMap);

        List<Integer> integerList1 = Arrays.asList(789, 123);
        List<List<Integer>> listList = Arrays.asList(integerList, integerList1);
        context.setContextListList(listList);

        String str = JSONUtils.stringify(context);
        Assert.assertEquals("{\"contextInteger\":123,\"contextLong\":123,\"contextListLong\":[123,456],\"listList\":[[123,456],[789,123]],\"contextDouble\":1.123,\"contextFloat\":3.456,\"integerListMap\":{\"integerList\":[123,456]}}", str);
    }

    @Test
    public void parseJSONTest() {
        String context = "{\"contextHtml\": \"<b>world</b>\" ,\"contextInteger\":123,\"contextLong\":123,\"contextListLong\":[123,456],\"listList\":[[123,456],[789,123]],\"contextDouble\":1.123,\"contextFloat\":3.456,\"integerListMap\":{\"integerList\":[123,456]}}";
        Map<String, Object> res = (Map<String, Object>) JSONUtils.parseJSON(context);
        Assert.assertEquals("<b>world</b>", res.get("contextHtml"));
        Assert.assertEquals(123L, res.get("contextInteger"));
        Assert.assertEquals(3.456d, res.get("contextFloat"));
        Assert.assertEquals(Arrays.asList(123L, 456L), res.get("contextListLong"));
    }

    @Test
    public void readPathTest() {
        Context context = new Context();
        context.setContextInteger(123);
        context.setContextLong(123L);
        context.setContextDouble(1.123);
        context.setContextFloat(3.456f);
        context.setContextListLong(Arrays.asList(123L, 456L));
        Map<String, List<Integer>> integerListMap = new HashMap<>();
        List<Integer> integerList = Arrays.asList(123, 456);
        integerListMap.put("integerList", integerList);
        context.setIntegerListMap(integerListMap);

        List<Integer> integerList1 = Arrays.asList(789, 123);
        List<List<Integer>> listList = Arrays.asList(integerList, integerList1);
        context.setContextListList(listList);

        Assert.assertTrue(JSONUtils.readPath(context, "$.listList") instanceof List);
        Assert.assertTrue(JSONUtils.readPath(context, "$.contextInteger") instanceof Long);
        Assert.assertTrue(JSONUtils.readPath(context, "$.contextLong") instanceof Long);
        Assert.assertTrue(JSONUtils.readPath(context, "$.contextDouble") instanceof Double);
        Assert.assertTrue(JSONUtils.readPath(context, "$.contextFloat") instanceof Double);
        Assert.assertTrue(JSONUtils.readPath(context, "$.contextListLong") instanceof List);
        Assert.assertTrue(JSONUtils.readPath(context, "$.integerListMap") instanceof Map);
        List<Long> list = JSONUtils.readPath(context, "$.contextListLong");
        Assert.assertEquals(123L, list.get(0).longValue());

        List<List<Long>> listList1 = JSONUtils.readPath(context, "$.listList");
        Assert.assertEquals(123L, listList1.get(0).get(0).longValue());

        Map<String, List<Long>> map = JSONUtils.readPath(context, "$.integerListMap");
        Assert.assertEquals(123L, map.get("integerList").get(0).longValue());


        Context context1 = new Context();
        context1.setContextListLong(JSONUtils.readPath(context, "$.contextListLong"));
        context1.setIntegerListMap(JSONUtils.readPath(context, "$.integerListMap"));
        context1.setContextLong(JSONUtils.readPath(context, "$.contextLong"));
        context1.setContextDouble(JSONUtils.readPath(context, "$.contextDouble"));
        context1.setContextInteger(((Long) JSONUtils.readPath(context, "$.contextInteger")).intValue());
        context1.setContextFloat(((Double) JSONUtils.readPath(context, "$.contextFloat")).floatValue());
    }
}

class Context {
    public String str;

    public Integer contextInteger;
    public Long contextLong;
    public List<Long> contextListLong;
    public List<List<Integer>> listList;
    public Double contextDouble;
    public Float contextFloat;

    public Map<String, List<Integer>> integerListMap;

    public Context setStr(String str) {
        this.str = str;
        return this;
    }

    public String getStr() {
        return this.str;
    }

    public Context setContextInteger(Integer contextInteger) {
        this.contextInteger = contextInteger;
        return this;
    }

    public Integer getContextInteger() {
        return this.contextInteger;
    }

    public Context setContextLong(Long contextLong) {
        this.contextLong = contextLong;
        return this;
    }

    public Long getContextLong() {
        return this.contextLong;
    }

    public Context setContextListLong(List<Long> contextListLong) {
        this.contextListLong = contextListLong;
        return this;
    }

    public List<Long> getContextListLong() {
        return this.contextListLong;
    }

    public Context setContextListList(List<List<Integer>> listList) {
        this.listList = listList;
        return this;
    }

    public List<List<Integer>> getContextListList() {
        return this.listList;
    }

    public Context setContextDouble(Double contextDouble) {
        this.contextDouble = contextDouble;
        return this;
    }

    public Double getContextDouble() {
        return this.contextDouble;
    }

    public Context setContextFloat(Float contextFloat) {
        this.contextFloat = contextFloat;
        return this;
    }

    public Float getContextFloat() {
        return this.contextFloat;
    }

    public Context setIntegerListMap(Map<String, List<Integer>> integerListMap) {
        this.integerListMap = integerListMap;
        return this;
    }

    public Map<String, List<Integer>> getIntegerListMap() {
        return this.integerListMap;
    }
}