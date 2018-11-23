package com.apidge.mapjson;

import com.alibaba.fastjson.JSONArray;
import com.apidge.mapjson.internal.JsonType;

public class JsonValues {
    private int[] arrayIndex = new int[0];
    private JsonType jsonType = JsonType.NONE;
    private String[] strings = new String[0];

    public String[] getValues() {
        return strings.clone();
    }

    public String getOneValue() {
        if (strings.length > 0) {
            return strings[0];
        }
        return null;
    }

    public int[] getArrayIndex() {
        return arrayIndex.clone();
    }

    public JsonType getJsonType() {
        return jsonType;
    }

    JsonValues() {
    }

    JsonValues(JSONArray array, String str, int[] arrayIndex, JsonType jsonType) {
        this.arrayIndex = arrayIndex;
        this.jsonType = jsonType;
        switch (jsonType) {
            case STRING:
                this.strings = new String[]{str};
                break;
            case OBJECT:
                break;
            case ARRAY:
                if (null != array && array.size() > 0) {
                    this.strings = new String[array.size()];
                    for (int i = 0; i < array.size(); i++) {
                        this.strings[i] = array.get(i).toString();
                    }
                }
                break;
        }
    }
}
