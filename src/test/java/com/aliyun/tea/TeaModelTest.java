package com.aliyun.tea;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
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

        @NameInMap("floatTest")
        public Float floatTest;

        @NameInMap("boolTest")
        public Boolean boolTest;

        @NameInMap("teaModel")
        public BaseDriveResponse baseDriveResponse;

        @NameInMap("readable")
        public InputStream readable;

        @NameInMap("writeable")
        public OutputStream writeable;
    }

    @Test
    public void toModelTest() {
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
        map.put("floatTest", 0.1D);
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
        Assert.assertEquals(0.1f, submodel.floatTest, 0.0);
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
    public void toMap() {
        SubModel submodel = new SubModel();
        submodel.accessToken = "the access token";
        submodel.accessKeyId = "the access key id";
        String str = "test";
        submodel.readable = Tea.toReadable(str);
        submodel.writeable = Tea.toWriteable();
        ArrayList paramList = new ArrayList();
        paramList.add("string0");
        paramList.add("string1");
        submodel.list = paramList;

        Map<String, Object> map = submodel.toMap();
        System.out.println(map.toString());
        Assert.assertEquals(9, map.size());
        Assert.assertEquals("the access key id", map.get("access_key_id"));
        Assert.assertEquals("the access token", map.get("accessToken"));
        ArrayList list = (ArrayList) map.get("listTest");
        Assert.assertEquals("string0", list.get(0));
        Assert.assertEquals("string1", list.get(1));
    }

    @Test
    public void buildTest() throws IllegalArgumentException, SecurityException {
        Map<String, Object> map = new HashMap<String, Object>();
        ListDriveResponse response = new ListDriveResponse();
        ArrayList<BaseDriveResponse> list = new ArrayList<>();
        BaseDriveResponse baseDriveResponse = new BaseDriveResponse();
        list.add(baseDriveResponse);
        map.put("nextMarker", "test");
        map.put("items", list);
        map.put("HasNameInMap", "test");
        ListDriveResponse result = TeaModel.build(map, response);
        Assert.assertEquals("test", result.nextMarker);
        Assert.assertEquals("test", result.hasNameInMap);
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

    @Test
    public void mapNestedMapTest() {
        BaseDriveResponse baseDriveResponse = new BaseDriveResponse();
        baseDriveResponse.driveName = "test";

        ListDriveResponse listDriveResponse = new ListDriveResponse();
        List<BaseDriveResponse> items = new ArrayList<>();
        items.add(baseDriveResponse);
        listDriveResponse.items = items;

        Map<String, Map<String, ListDriveResponse>> mapNestedMap = new HashMap<>();
        Map<String, ListDriveResponse> subNestedMap = new HashMap<>();
        subNestedMap.put("subNestedTeaModel", listDriveResponse);
        mapNestedMap.put("subNestedMap", subNestedMap);

        NestedTest nestedTest = new NestedTest();
        nestedTest.mapnestedMap = mapNestedMap;

        Map result = nestedTest.toMap();
        Map result_mapNestedMap = (Map) result.get("MapNestedMap");
        Map mapNestedMap_subNestedMap = (Map) result_mapNestedMap.get("subNestedMap");
        Map subNestedMap_subNestedTeaModel = (Map) mapNestedMap_subNestedMap.get("subNestedTeaModel");
        List subNestedTeaModel_items = (List) subNestedMap_subNestedTeaModel.get("itemsTest");
        Map baseDriveResponseMap = (Map) subNestedTeaModel_items.get(0);
        String drive_name = (String) baseDriveResponseMap.get("drive_name");
        Assert.assertEquals("test", drive_name);

        NestedTest resultModel = TeaModel.build(result, new NestedTest());
        result_mapNestedMap = resultModel.mapnestedMap;
        mapNestedMap_subNestedMap = (Map) result_mapNestedMap.get("subNestedMap");
        ListDriveResponse resultListDriveResponse = (ListDriveResponse) mapNestedMap_subNestedMap.get("subNestedTeaModel");
        subNestedTeaModel_items = resultListDriveResponse.items;
        BaseDriveResponse resultBaseDriveResponse = (BaseDriveResponse) subNestedTeaModel_items.get(0);
        Assert.assertEquals("test", resultBaseDriveResponse.driveName);
    }

    @Test
    public void listNestedListTest() {
        Map<String, String> map = new HashMap<>();
        map.put("test", "test");

        List<Map<String, ?>> list = new ArrayList<Map<String, ?>>();
        list.add(map);

        List<List<Map<String, ?>>> listNestedList = new ArrayList<List<Map<String, ?>>>();
        listNestedList.add(list);

        NestedTest nestedTest = new NestedTest();
        nestedTest.listNestedList = listNestedList;

        Map<String, Object> resultMap = TeaModel.buildMap(nestedTest);
        List<List<Map<String, String>>> resultMap_ListNestedList = (List<List<Map<String, String>>>) resultMap.get("ListNestedList");
        List<Map<String, String>> listNestedList_list = resultMap_ListNestedList.get(0);
        Map<String, String> list_map = listNestedList_list.get(0);
        Assert.assertEquals("test", list_map.get("test"));

        NestedTest resultTeaModel = TeaModel.toModel(resultMap, new NestedTest());
        List<List<Map<String, ?>>> resultTeaModel_ListNestedList = resultTeaModel.listNestedList;
        List<Map<String, ?>> listNestedList_result = resultTeaModel_ListNestedList.get(0);
        Map<String, ?> result_map = listNestedList_result.get(0);
        Assert.assertEquals("test", result_map.get("test"));
    }

    @Test
    public void wildcardTest() {
        List<Object> wildcardTest =  new ArrayList<Object>();
        wildcardTest.add(1);

        NestedTest nestedTest = new NestedTest();
        nestedTest.wildcardTest = wildcardTest;

        Map<String, Object> resultMap = TeaModel.buildMap(nestedTest);
        NestedTest result = TeaModel.toModel(resultMap, new NestedTest());
        Assert.assertEquals(1, result.wildcardTest.get(0));
    }

    public class NestedTest extends TeaModel {
        @NameInMap("MapNestedMap")
        public Map<String, Map<String, ListDriveResponse>> mapnestedMap;

        @NameInMap("ListNestedList")
        public List<List<Map<String, ?>>> listNestedList;

        @NameInMap("WildcardTest")
        public List<?> wildcardTest;
    }

    public static class BaseDriveResponse extends TeaModel {
        @NameInMap("creator")
        public String creator;

        @NameInMap("driveId")
        public String driveId;

        @NameInMap("drive_name")
        public String driveName;
    }

    public static class ListDriveResponse extends TeaModel {
        @NameInMap("itemsTest")
        public List<BaseDriveResponse> items;

        @NameInMap("nextMarker")
        public String nextMarker;

        @NameInMap("baseItem")
        public BaseDriveResponse item;

        public String noNameInMap;

        @NameInMap("HasNameInMap")
        public String hasNameInMap;
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
    public void toMapNoParamTest() {
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
    public void toMapOneParamTest() {
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
        Method validateMap = TeaModel.class.getDeclaredMethod("validateMap", String.class, int.class, int.class, Map.class, String.class);
        Map<String, Object> map = new HashMap<>();
        map.put("1", null);
        map.put("2", "test");
        validateMap.setAccessible(true);
        try {
            validateMap.invoke(new ValidateParamModel(), "test", 4, 0, map, "test");
            validateMap.invoke(new ValidateParamModel(), "[1-9]", 0, 0, map, "test");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals("com.aliyun.tea.TeaModelTest$ValidateParamModel.test regular match failed", e.getCause().getMessage());
        }
    }

    @Test
    public void determineTypeTest() throws NoSuchMethodException {
        Method determineType = TeaModel.class.getDeclaredMethod("determineType",
                Class.class, Object.class, String.class, int.class, int.class, String.class);
        determineType.setAccessible(true);
        ValidateParamModel validateParamModel = new ValidateParamModel();
        Map<String, Object> map = new HashMap<>();
        map.put("test", "test");

        try {
            determineType.invoke(validateParamModel, map.getClass(), map, "test", 1, 0, "test");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals("com.aliyun.tea.TeaModelTest$ValidateParamModel.test exceeds the maximum length", e.getCause().getMessage());
        }

        try {
            determineType.invoke(validateParamModel, map.getClass(), map, "[1-9]", 0, 0, "test");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals("com.aliyun.tea.TeaModelTest$ValidateParamModel.test regular match failed", e.getCause().getMessage());
        }

        try {
            determineType.invoke(new ValidateParamModel(), validateParamModel.getClass(), validateParamModel, "test", 4, 0, "test");
            validateParamModel.hasStringPattern.put("test", "test");
            determineType.invoke(new ValidateParamModel(), validateParamModel.getClass(), validateParamModel, "[1-9]", 0, 0, "test");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals("com.aliyun.tea.TeaModelTest$ValidateParamModel.hasStringPattern regular match failed", e.getCause().getMessage());
        }

        List<String> list = new ArrayList<>();
        list.add("test");
        try {
            determineType.invoke(new ValidateParamModel(), list.getClass(), list, "test", 0, 10, "test");
            determineType.invoke(new ValidateParamModel(), list.getClass(), list, "[1-9]", 0, 0, "test");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals("com.aliyun.tea.TeaModelTest$ValidateParamModel.test less than minimum length", e.getCause().getMessage());
        }
        String[] strs = new String[]{"test"};
        try {
            determineType.invoke(new ValidateParamModel(), strs.getClass(), strs, "test", 0, 0, "test");
            determineType.invoke(new ValidateParamModel(), strs.getClass(), strs, "[1-9]", 0, 0, "test");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals("com.aliyun.tea.TeaModelTest$ValidateParamModel.test regular match failed", e.getCause().getMessage());
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
            Assert.assertEquals("com.aliyun.tea.TeaModelTest$Hello.message regular match failed", e.getMessage());
        }
    }

    @Test
    public void buildMapTest() {
        TeaModel teaModel = null;
        Assert.assertNull(TeaModel.buildMap(teaModel));

        teaModel = new ValidateParamModel();
        Assert.assertEquals(6, TeaModel.buildMap(teaModel).size());
    }

    @Test
    public void validateParamsTest() {
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
