package com.aliyun.tea;

import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TeaModelTest {

    public static class SubModel extends TeaModel {
        public String accessToken;

        @NameInMap("access_key_id")
        public String accessKeyId;

        public String[] list;
    }

    @Test
    public void toModel() throws Exception {
        SubModel submodel = TeaModel.toModel(new HashMap<String, Object>(), new SubModel());
        Assert.assertEquals(null, submodel.accessKeyId);
        SubModel submodel2 = TeaModel.toModel(new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;
            {
                put("accessToken", "the access token");
                put("access_key_id", "the access key id");
                put("list", new String[]{"string0", "string1"});
            }
        }, new SubModel());

        Assert.assertEquals("the access key id", submodel2.accessKeyId);
        Assert.assertEquals("the access token", submodel2.accessToken);
        Assert.assertArrayEquals(new String[]{"string0", "string1"}, submodel2.list);
    }

    @Test
    public void toMap() throws IllegalArgumentException, IllegalAccessException {
        SubModel submodel = new SubModel();
        submodel.accessToken = "the access token";
        submodel.accessKeyId = "the access key id";
        submodel.list = new String[]{"string0", "string1"};

        Map<String, Object> map = submodel.toMap();
        Assert.assertEquals(3, map.size());
        Assert.assertEquals("the access key id", map.get("access_key_id"));
        Assert.assertEquals("the access token", map.get("accessToken"));
        Assert.assertTrue(map.get("list") instanceof String[]);
        String[] list = (String[]) map.get("list");
        Assert.assertArrayEquals(new String[]{"string0", "string1"}, list);
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
    @SuppressWarnings("unchecked")
    public void toMapWithList() throws Exception {
        Gson gson = new Gson();
        Map<String, Object> map = gson.fromJson(
                "{\"items\":[{\"status\":\"status\", \"domain_id\":\"test\"}],\"next_marker\":\"\"}", Map.class);
        ListDriveResponse response = TeaModel.toModel(map, new ListDriveResponse());
        Assert.assertTrue(response.items[0] instanceof BaseDriveResponse);
    }
}
