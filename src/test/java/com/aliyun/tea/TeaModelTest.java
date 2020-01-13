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

        @NameInMap("list")
        public String[] list;

        @NameInMap("size")
        public Long size;

        @NameInMap("limit")
        public Integer limit;
    }

    @Test
    public void toModel() throws IllegalArgumentException, IllegalAccessException, InstantiationException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("list", new String[]{"test"});
        SubModel submodel = TeaModel.toModel(map, new SubModel());
        Assert.assertNull(submodel.accessKeyId);
        Assert.assertNull(submodel.limit);
        Assert.assertNull(submodel.size);
        Assert.assertNull(submodel.accessToken);
        Assert.assertEquals("test", submodel.list[0]);

        map.put("accessToken", null);
        map.put("limit", 1);
        map.put("size", 1L);
        map.put("access_key_id", "test");
        List list = new ArrayList();
        list.add("test");
        map.put("list", list);
        submodel = TeaModel.toModel(map, new SubModel());
        Assert.assertEquals("test", submodel.accessKeyId);
        Assert.assertEquals(1, (int) submodel.limit);
        Assert.assertEquals(1L, (long) submodel.size);
        Assert.assertNull(submodel.accessToken);
        Assert.assertEquals("test", submodel.list[0]);
    }

    @Test
    public void toMap() throws IllegalArgumentException, IllegalAccessException {
        SubModel submodel = new SubModel();
        submodel.accessToken = "the access token";
        submodel.accessKeyId = "the access key id";
        submodel.list = new String[]{"string0", "string1"};

        Map<String, Object> map = submodel.toMap();
        Assert.assertEquals(5, map.size());
        Assert.assertEquals("the access key id", map.get("access_key_id"));
        Assert.assertEquals("the access token", map.get("accessToken"));
        ArrayList list = (ArrayList) map.get("list");
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
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("creator", "test");
        ArrayList<Map<String, Object>> mapList = new ArrayList<>();
        mapList.add(modelMap);
        baseDriveResponse.driveId = "driveId";
        map.put("item", baseDriveResponse);
        map.put("items", mapList);
        result = TeaModel.build(map, response);
        Assert.assertEquals("test", result.items[0].creator);
        Assert.assertEquals("driveId", result.item.driveId);

        SubModel subModel = new SubModel();
        map.clear();
        ArrayList<String> stringList = new ArrayList<>();
        stringList.add("test");
        map.put("list", stringList);
        SubModel subModelResult = TeaModel.build(map, subModel);
        Assert.assertEquals("test", subModelResult.list[0]);

        map.clear();
        map.put("list", new String[]{"test"});
        subModelResult = TeaModel.build(map, subModel);
        Assert.assertEquals("test", subModelResult.list[0]);
    }

    @Test
    public void toMapTest() throws IllegalArgumentException, IllegalAccessException {
        SubModel submodel = new SubModel();
        submodel.accessToken = "the access token";
        submodel.accessKeyId = "the access key id";
        submodel.list = new String[]{"string0", "string1"};
        Assert.assertEquals(0, TeaModel.toMap(null).size());
        Assert.assertEquals(0, TeaModel.toMap("test").size());

        Map<String, Object> map = TeaModel.toMap(submodel);
        Assert.assertEquals(5, map.size());
        Assert.assertEquals("the access key id", map.get("accessKeyId"));
        Assert.assertEquals("the access token", map.get("accessToken"));
        ArrayList list = (ArrayList) map.get("list");
        Assert.assertEquals("string0", list.get(0));
        Assert.assertEquals("string1", list.get(1));

        ListDriveResponse response = new ListDriveResponse();
        map = TeaModel.toMap(response);
        Assert.assertNull(map.get("nextMarker"));
        Assert.assertNull(map.get("items"));

        BaseDriveResponse[] baseDriveResponses = new BaseDriveResponse[1];
        baseDriveResponses[0] = new BaseDriveResponse();
        response.items = baseDriveResponses;
        response.nextMarker = "test";
        map = TeaModel.toMap(response);
        Assert.assertNotNull(map.get("items"));
        Assert.assertEquals("test", map.get("nextMarker"));
    }

    public static class BaseDriveResponse extends TeaModel {
        @NameInMap("creator")
        public String creator;

        @NameInMap("description")
        public String description;

        @NameInMap("domain_id")
        public String domainId;

        @NameInMap("drive_id")
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
        @NameInMap("items")
        public BaseDriveResponse[] items;

        @NameInMap("next_marker")
        public String nextMarker;

        @NameInMap("item")
        public BaseDriveResponse item;
    }

    @Test
    public void toMapWithList() throws IllegalArgumentException, IllegalAccessException, InstantiationException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        Map<String, Object> map = new HashMap<String, Object>();
        ArrayList<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("domainId", "test");
        responseMap.put("status", "test");
        items.add(responseMap);
        map.put("items", items);
        map.put("next_marker", "");
        ListDriveResponse response = TeaModel.toModel(map, new ListDriveResponse());
        Assert.assertTrue(response.items[0] instanceof BaseDriveResponse);
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
    public void toMapWithGeneric() throws IllegalArgumentException, IllegalAccessException, InstantiationException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("data", new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;

            {
                put("message", "Hello jacksontian");
            }
        });

        HelloResponse response = TeaModel.toModel(map, new HelloResponse());
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.data);
        Assert.assertEquals("Hello jacksontian", response.data.message);
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
    public void transformFieldTest() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("size", Double.valueOf("6"));
        SubModel submodel = TeaModel.toModel(map, new SubModel());
        Assert.assertTrue(submodel.size instanceof Long);

        map.put("limit", Double.valueOf("6"));
        submodel = TeaModel.toModel(map, new SubModel());
        Assert.assertTrue(submodel.limit instanceof Integer);

        List list = new ArrayList();
        list.add("test");
        map.put("list", list);
        submodel = TeaModel.toModel(map, new SubModel());
        Assert.assertEquals("test", submodel.list[0]);
    }


    @Test
    public void toMapTransformTest() throws IllegalAccessException {
        ListDriveResponse response = new ListDriveResponse();
        response.nextMarker = "test";
        Assert.assertEquals("{next_marker=test, item=null, items=null}", response.toMap().toString());

        response.nextMarker = "test";
        BaseDriveResponse baseDriveResponse = new BaseDriveResponse();
        baseDriveResponse.driveId = "1";
        response.items = new BaseDriveResponse[]{baseDriveResponse};
        Assert.assertEquals("{next_marker=test, item=null, items=[{domain_id=null, drive_type=null, owner=null, " +
                "store_id=null, creator=null, drive_id=1, total_size=null, description=null, used_size=null, " +
                "drive_name=null, relative_path=null, status=null}]}", response.toMap().toString());
    }

    public static class ValidateParamModel extends TeaModel {
        public String nullValidation;
        @Validation
        public String nullObject;
        @Validation
        public String notNullObject = "test";
        @Validation(pattern = "[1-9]")
        public Map<String,Hello[]> hasPattern = new HashMap<>();
        @Validation(pattern = "[1-9]")
        public Map<String,String> hasStringPattern = new HashMap<>();
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
            validateMap.invoke(new ValidateParamModel(),"test", 4, map);
            validateMap.invoke(new ValidateParamModel(),"[1-9]", 0, map);
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
            determineType.invoke(validateParamModel,map.getClass(),map, "test", 1);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals("param don't matched", e.getCause().getMessage());
        }

        try {
            determineType.invoke(validateParamModel,map.getClass(),map, "[1-9]", 0);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals("param don't matched", e.getCause().getMessage());
        }

        try {
            determineType.invoke(new ValidateParamModel(),validateParamModel.getClass(),validateParamModel, "test", 4);
            validateParamModel.hasStringPattern.put("test", "test");
            determineType.invoke(new ValidateParamModel(),validateParamModel.getClass(),validateParamModel, "[1-9]", 0);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals("param don't matched", e.getCause().getMessage());
        }

        List<String> list = new ArrayList<>();
        list.add("test");
        try {
            determineType.invoke(new ValidateParamModel(),list.getClass(),list, "test", 0);
            determineType.invoke(new ValidateParamModel(),list.getClass(),list, "[1-9]", 0);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals("param don't matched", e.getCause().getMessage());
        }
        String[] strs = new String[]{"test"};
        try {
            determineType.invoke(new ValidateParamModel(),strs.getClass(),strs, "test", 0);
            determineType.invoke(new ValidateParamModel(),strs.getClass(),strs, "[1-9]", 0);
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
        Assert.assertNotNull(TeaModel.buildMap(teaModel));

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
