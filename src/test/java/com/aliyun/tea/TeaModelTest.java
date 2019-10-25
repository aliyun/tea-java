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
        BaseDriveResponse baseDriveResponse = new BaseDriveResponse();
        baseDriveResponse.driveId = "1";
        response.items = new BaseDriveResponse[]{baseDriveResponse};
        System.out.println(response.toMap());
        Assert.assertEquals("{next_marker=test, items=[{domain_id=null, drive_type=null, owner=null, " +
                "store_id=null, creator=null, drive_id=1, total_size=null, description=null, used_size=null, " +
                "drive_name=null, relative_path=null, status=null}]}", response.toMap().toString());
    }
}
