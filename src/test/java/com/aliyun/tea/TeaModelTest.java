package com.aliyun.tea;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class TeaModelTest {

    public static class SubModel extends TeaModel {
        public String accessToken;

        @NameInMap("access_key_id")
        public String accessKeyId;
    }

    @Test
    public void toModel() throws IllegalArgumentException, IllegalAccessException {
        SubModel submodel = TeaModel.toModel(new HashMap<String, Object>(), new SubModel());
        Assert.assertEquals(null, submodel.accessKeyId);
        SubModel submodel2 = TeaModel.toModel(new HashMap<String, Object>(){
            private static final long serialVersionUID = 1L;
            {
                put("accessToken", "the access token");
                put("access_key_id", "the access key id");
            }
        }, new SubModel());

        Assert.assertEquals("the access key id", submodel2.accessKeyId);
        Assert.assertEquals("the access token", submodel2.accessToken);
    }

    @Test
    public void toMap() throws IllegalArgumentException, IllegalAccessException {

        SubModel submodel = new SubModel();
        submodel.accessToken = "the access token";
        submodel.accessKeyId = "the access key id";

        Map<String, Object> map = submodel.toMap();

        Assert.assertEquals("the access key id", map.get("access_key_id"));
        Assert.assertEquals("the access token", map.get("accessToken"));
        Assert.assertEquals(2, map.size());
    }
}
