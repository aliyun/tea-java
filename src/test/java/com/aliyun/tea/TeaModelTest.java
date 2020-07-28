package com.aliyun.tea;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeaModelTest {

    public static class SubModel extends TeaModel {
        public String accessToken;

        @NameInMap("access_key_id")
        public String accessKeyId;

        @NameInMap("listTest")
        public List<String> list;

        @NameInMap("size")
        public Long size;

        @NameInMap("limit")
        public Integer limit;

        @NameInMap("doubleTest")
        public Double doubleTest;

        @NameInMap("boolTest")
        public Boolean boolTest;

        @NameInMap("teaModel")
        public BaseDriveResponse baseDriveResponse;
    }

    @Test
    public void toModelTest() throws Exception {
        Map<String, Object> map = new HashMap<>();
        ArrayList testList = new ArrayList();
        testList.add("test");
        map.put("listTest", testList);
        SubModel submodel = TeaModel.toModel(map, new SubModel());
        Assert.assertNull(submodel.accessKeyId);
        Assert.assertNull(submodel.limit);
        Assert.assertNull(submodel.size);
        Assert.assertNull(submodel.accessToken);
        Assert.assertEquals("test", submodel.list.get(0));

        map.put("accessToken", null);
        map.put("limit", 1);
        map.put("size", 1);
        map.put("access_key_id", "test");
        List list = new ArrayList();
        list.add("test");
        map.put("list", list);
        map.put("boolTest", true);
        map.put("doubleTest", 0.1f);
        BaseDriveResponse baseDriveResponse = new BaseDriveResponse();
        baseDriveResponse.driveId = "1";
        map.put("teaModel", baseDriveResponse);
        submodel = TeaModel.toModel(map, new SubModel());
        Assert.assertEquals("test", submodel.accessKeyId);
        Assert.assertEquals(1, (int) submodel.limit);
        Assert.assertEquals(1L, (long) submodel.size);
        Assert.assertNull(submodel.accessToken);
        Assert.assertEquals("test", submodel.list.get(0));
        Assert.assertEquals(0.1D, submodel.doubleTest, 0.0);
        Assert.assertTrue(submodel.boolTest);
        Assert.assertEquals("1", submodel.baseDriveResponse.driveId);

        Map<String, Object> teaModelMap = new HashMap<>();
        teaModelMap.put("driveId", "2");
        map.put("teaModel", teaModelMap);
        submodel = TeaModel.toModel(map, new SubModel());
        Assert.assertEquals("2", submodel.baseDriveResponse.driveId);

        map.clear();
        List<BaseDriveResponse> baseDriveResponseList = new ArrayList<>();
        baseDriveResponseList.add(baseDriveResponse);
        map.put("itemsTest", baseDriveResponseList);
        ListDriveResponse listDriveResponse = new ListDriveResponse();
        listDriveResponse = TeaModel.toModel(map, listDriveResponse);
        Assert.assertEquals("1", listDriveResponse.items.get(0).driveId);

        List<Map> mapList = new ArrayList<>();
        mapList.add(teaModelMap);
        map.put("itemsTest", mapList);
        listDriveResponse = TeaModel.toModel(map, listDriveResponse);
        Assert.assertEquals("2", listDriveResponse.items.get(0).driveId);
    }

    @Test
    public void toMap() throws IllegalArgumentException, IllegalAccessException {
        SubModel submodel = new SubModel();
        submodel.accessToken = "the access token";
        submodel.accessKeyId = "the access key id";
        ArrayList paramList = new ArrayList();
        paramList.add("string0");
        paramList.add("string1");
        submodel.list = paramList;

        Map<String, Object> map = submodel.toMap();
        Assert.assertEquals(8, map.size());
        Assert.assertEquals("the access key id", map.get("access_key_id"));
        Assert.assertEquals("the access token", map.get("accessToken"));
        ArrayList list = (ArrayList) map.get("listTest");
        Assert.assertEquals("string0", list.get(0));
        Assert.assertEquals("string1", list.get(1));
    }

    @Test
    public void buildTest() throws IllegalArgumentException, IllegalAccessException, InstantiationException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        Map<String, Object> map = new HashMap<String, Object>();
        ListDriveResponse response = new ListDriveResponse();
        ArrayList<BaseDriveResponse> list = new ArrayList<>();
        BaseDriveResponse baseDriveResponse = new BaseDriveResponse();
        list.add(baseDriveResponse);
        map.put("nextMarker", "test");
        map.put("items", list);
        ListDriveResponse result = TeaModel.build(map, response);
        Assert.assertEquals("test", result.nextMarker);
        Assert.assertNotNull(result.items);

        map.clear();
        baseDriveResponse = new BaseDriveResponse();
        baseDriveResponse.creator = "test";
        ArrayList<BaseDriveResponse> mapList = new ArrayList<>();
        mapList.add(baseDriveResponse);
        baseDriveResponse.driveId = "driveId";
        map.put("item", baseDriveResponse);
        map.put("items", mapList);
        result = TeaModel.build(map, response);
        Assert.assertEquals("test", result.items.get(0).creator);
        Assert.assertEquals("driveId", result.item.driveId);

        SubModel subModel = new SubModel();
        map.clear();
        ArrayList<String> stringList = new ArrayList<>();
        stringList.add("test");
        map.put("list", stringList);
        map.put("boolTest", true);
        map.put("doubleTest", 0.1f);
        map.put("size", 1);
        map.put("limit", 1);
        SubModel subModelResult = TeaModel.build(map, subModel);
        Assert.assertEquals("test", subModelResult.list.get(0));
        Assert.assertEquals(0.1D, subModelResult.doubleTest, 0.0);
        Assert.assertEquals(1, (int) subModelResult.limit);
        Assert.assertEquals(1L, (long) subModelResult.size);
        Assert.assertTrue(subModelResult.boolTest);

        map.clear();
        ArrayList<String> strings = new ArrayList<>();
        strings.add("test");
        map.put("list", strings);
        subModelResult = TeaModel.build(map, subModel);
        Assert.assertEquals("test", subModelResult.list.get(0));

        List<Map> modelList = new ArrayList<>();
        Map<String, Object> teaModelMap = new HashMap<>();
        teaModelMap.put("driveId", "2");
        modelList.add(teaModelMap);
        map.put("items", modelList);
        result = TeaModel.build(map, new ListDriveResponse());
        Assert.assertEquals("2", result.items.get(0).driveId);
    }

    public static class BaseDriveResponse extends TeaModel {
        @NameInMap("creator")
        public String creator;

        @NameInMap("description")
        public String description;

        @NameInMap("domain_id")
        public String domainId;

        @NameInMap("driveId")
        public String driveId;

        @NameInMap("drive_name")
        public String driveName;

        @NameInMap("drive_type")
        public String driveType;

        @NameInMap("owner")
        public String owner;

        @NameInMap("relative_path")
        public String relativePath;

        @NameInMap("status")
        public String status;

        @NameInMap("store_id")
        public String storeId;

        @NameInMap("total_size")
        public Integer totalSize;

        @NameInMap("used_size")
        public Integer usedSize;
    }

    public static class ListDriveResponse extends TeaModel {
        @NameInMap("itemsTest")
        public List<BaseDriveResponse> items;

        @NameInMap("nextMarker")
        public String nextMarker;

        @NameInMap("baseItem")
        public BaseDriveResponse item;
    }

    public static class HelloResponse extends TeaModel {
        @NameInMap("data")
        public Hello data;

    }

    public static class Hello extends TeaModel {
        @NameInMap("message")
        @Validation(pattern = "test")
        public String message;
    }

    @Test
    public void parseNumberTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class teaModel = TeaModel.class;
        Method parseNumber = teaModel.getDeclaredMethod("parseNumber", Object.class, Class.class);
        parseNumber.setAccessible(true);
        Object arg = null;

        arg = 2D;
        Object result = parseNumber.invoke(teaModel, arg, Integer.class);
        Assert.assertEquals(2, result);

        arg = 2D;
        result = parseNumber.invoke(teaModel, arg, int.class);
        Assert.assertEquals(2, result);

        arg = 2L;
        result = parseNumber.invoke(teaModel, arg, Integer.class);
        Assert.assertEquals(2L, result);

        arg = 2D;
        result = parseNumber.invoke(teaModel, arg, double.class);
        Assert.assertEquals(2D, result);

        arg = Integer.MAX_VALUE + 1D;
        result = parseNumber.invoke(teaModel, arg, Long.class);
        Assert.assertEquals(Integer.MAX_VALUE + 1L, result);

        arg = Integer.MAX_VALUE + 1D;
        result = parseNumber.invoke(teaModel, arg, long.class);
        Assert.assertEquals(Integer.MAX_VALUE + 1L, result);

        arg = 2;
        result = parseNumber.invoke(teaModel, arg, Long.class);
        Assert.assertEquals(2, result);
    }

    @Test
    public void toMapNoParamTest() throws IllegalAccessException {
        ListDriveResponse response = new ListDriveResponse();
        BaseDriveResponse baseDriveResponse = new BaseDriveResponse();
        baseDriveResponse.driveId = "1";
        ArrayList<BaseDriveResponse> baseDriveResponses = new ArrayList<>();
        baseDriveResponses.add(baseDriveResponse);
        response.items = baseDriveResponses;
        response.item = baseDriveResponse;
        response.nextMarker = "test";
        Map<String, Object> map = response.toMap();
        Assert.assertEquals(response.nextMarker, map.get("nextMarker"));
        Assert.assertEquals(baseDriveResponse.driveId, ((Map) map.get("baseItem")).get("driveId"));
        Assert.assertEquals(baseDriveResponse.driveId, ((Map) ((List) map.get("itemsTest")).get(0)).get("driveId"));
    }

    @Test
    public void toMapOneParamTest() throws IllegalAccessException {
        Map<String, Object> nullMap = new HashMap<>();
        Assert.assertEquals(0, TeaModel.toMap(nullMap).size());

        Assert.assertEquals(0, TeaModel.toMap(null).size());
        Assert.assertEquals(0, TeaModel.toMap("test").size());
        ListDriveResponse response = new ListDriveResponse();
        BaseDriveResponse baseDriveResponse = new BaseDriveResponse();
        baseDriveResponse.driveId = "1";
        ArrayList<BaseDriveResponse> baseDriveResponses = new ArrayList<>();
        baseDriveResponses.add(baseDriveResponse);
        response.items = baseDriveResponses;
        response.item = baseDriveResponse;
        response.nextMarker = "test";
        Map<String, Object> map = TeaModel.toMap(response);
        Assert.assertEquals(response.nextMarker, map.get("nextMarker"));
        Assert.assertEquals(baseDriveResponse.driveId, ((Map) map.get("baseItem")).get("driveId"));
        Assert.assertEquals(baseDriveResponse.driveId, ((Map) ((List) map.get("itemsTest")).get(0)).get("driveId"));

        SubModel submodel = new SubModel();
        ArrayList<String> list = new ArrayList<>();
        list.add("test");
        submodel.list = list;
        map = TeaModel.toMap(submodel);
        Assert.assertEquals("test", ((List) map.get("listTest")).get(0));
    }

    public static class ValidateParamModel extends TeaModel {
        public String nullValidation;
        @Validation
        public String nullObject;
        @Validation
        public String notNullObject = "test";
        @Validation(pattern = "[1-9]")
        public Map<String, Hello[]> hasPattern = new HashMap<>();
        @Validation(pattern = "[1-9]")
        public Map<String, String> hasStringPattern = new HashMap<>();
        @Validation(required = true)
        public String requiredTrue = "test";
    }

    @Test
    public void validateMapTest() throws NoSuchMethodException {
        Method validateMap = TeaModel.class.getDeclaredMethod("validateMap", String.class, int.class, Map.class);
        Map<String, Object> map = new HashMap<>();
        map.put("1", null);
        map.put("2", "test");
        validateMap.setAccessible(true);
        try {
            validateMap.invoke(new ValidateParamModel(), "test", 4, map);
            validateMap.invoke(new ValidateParamModel(), "[1-9]", 0, map);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals("param don't matched", e.getCause().getMessage());
        }
    }

    @Test
    public void determineTypeTest() throws NoSuchMethodException {
        Method determineType = TeaModel.class.getDeclaredMethod("determineType",
                Class.class, Object.class, String.class, int.class);
        determineType.setAccessible(true);
        ValidateParamModel validateParamModel = new ValidateParamModel();
        Map<String, Object> map = new HashMap<>();
        map.put("test", "test");

        try {
            determineType.invoke(validateParamModel, map.getClass(), map, "test", 1);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals("param don't matched", e.getCause().getMessage());
        }

        try {
            determineType.invoke(validateParamModel, map.getClass(), map, "[1-9]", 0);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals("param don't matched", e.getCause().getMessage());
        }

        try {
            determineType.invoke(new ValidateParamModel(), validateParamModel.getClass(), validateParamModel, "test", 4);
            validateParamModel.hasStringPattern.put("test", "test");
            determineType.invoke(new ValidateParamModel(), validateParamModel.getClass(), validateParamModel, "[1-9]", 0);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals("param don't matched", e.getCause().getMessage());
        }

        List<String> list = new ArrayList<>();
        list.add("test");
        try {
            determineType.invoke(new ValidateParamModel(), list.getClass(), list, "test", 0);
            determineType.invoke(new ValidateParamModel(), list.getClass(), list, "[1-9]", 0);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals("param don't matched", e.getCause().getMessage());
        }
        String[] strs = new String[]{"test"};
        try {
            determineType.invoke(new ValidateParamModel(), strs.getClass(), strs, "test", 0);
            determineType.invoke(new ValidateParamModel(), strs.getClass(), strs, "[1-9]", 0);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals("param don't matched", e.getCause().getMessage());
        }
    }

    @Test
    public void validateTest() throws NoSuchMethodException {
        Method validate = TeaModel.class.getDeclaredMethod("validate");
        validate.setAccessible(true);
        ValidateParamModel validateParamModel = new ValidateParamModel();
        try {
            validateParamModel.requiredTrue = null;
            validateParamModel.validate();
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals("Field requiredTrue is required", e.getMessage());
        }

        try {
            validateParamModel.requiredTrue = "test";
            Hello[] hellos = new Hello[]{new Hello()};
            hellos[0].message = "test";
            validateParamModel.hasPattern.put("test", hellos);
            validateParamModel.validate();
            hellos[0].message = "0";
            validateParamModel.hasPattern.put("test", hellos);
            validateParamModel.validate();
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals("param don't matched", e.getMessage());
        }
    }

    @Test
    public void buildMapTest() throws IllegalAccessException {
        TeaModel teaModel = null;
        Assert.assertNull(TeaModel.buildMap(teaModel));

        teaModel = new ValidateParamModel();
        Assert.assertEquals(6, TeaModel.buildMap(teaModel).size());
    }

    @Test
    public void validateParamsTest() throws IllegalAccessException, ValidateException {
        TeaModel teaModel = new ValidateParamModel();
        try {
            TeaModel.validateParams(teaModel, "test");
            teaModel = null;
            TeaModel.validateParams(teaModel, "test");
            Assert.fail();
        } catch (ValidateException e) {
            Assert.assertEquals("parameter test is not allowed as null", e.getMessage());
        }
    }
}
