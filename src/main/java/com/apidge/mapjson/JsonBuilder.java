package com.apidge.mapjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.apidge.mapjson.internal.Globals;
import com.apidge.mapjson.internal.JsonType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class JsonBuilder {
    public static JSON buildJson(Map<String, JsonDataMap> jsonData) {
        if (null == jsonData || jsonData.size() == 0) {
            return null;
        }
        boolean isArray = true;
        for (JsonDataMap data : jsonData.values()) {
            for (String key : data.getFieldAllValue().keySet()) {
                isArray = key.startsWith(Globals.JSON_TYPE_STRING_ARRAY);
                break;
            }
            break;
        }
        if (isArray) {
            JSONArray array = new JSONArray();
            for (JsonDataMap data : jsonData.values()) {
                buildJsonArr(array, data.getFieldAllValue());
            }
            return array;
        } else {
            JSONObject json = new JSONObject();
            for (JsonDataMap data : jsonData.values()) {
                buildJsonObj(json, data.getFieldAllValue());
            }
            return json;
        }
    }

    private static void buildJsonObj(JSONObject json, Map<String, List<JsonValues>> data) {
        for (String field : data.keySet()) {
            String[] path = JsonAnalysis.parsePath(field);
            for (JsonValues values : data.get(field)) {
                buildJsonArr(null, json, path, values);
            }
        }
    }

    private static void buildJsonArr(JSONArray array, Map<String, List<JsonValues>> data) {
        for (String field : data.keySet()) {
            String[] path = JsonAnalysis.parsePath(field);
            for (JsonValues values : data.get(field)) {
                buildJsonArr(array, null, path, values);
            }
        }
    }

    private static void buildJsonArr(JSONArray array, JSONObject json, String[] path, JsonValues jsonValues) {
        JSONArray lastArr = array;
        JSONObject lastObj = json;
        String lastStr = null;

        int size = path.length;
        int arrCount = 0;
        for (int j = 0; j < size; j++) {
            if ((JsonType.ARRAY.str.equals(path[j]) || JsonType.OBJ_ARRAY.str.equals(path[j])) && j < (size - 1)) {
                arrCount++;
            }
        }
        int[] arrIndex = new int[arrCount];

        int arrPos = 0;
        for (int j = 0; j < size; j++) {
            if ((JsonType.ARRAY.str.equals(path[j]) || JsonType.OBJ_ARRAY.str.equals(path[j])) && j < (size - 1)) {
                arrIndex[arrPos] = null == jsonValues.getArrayIndex() || jsonValues.getArrayIndex().length < (arrPos + 1) ? 0 : jsonValues.getArrayIndex()[arrPos];
                arrPos++;
            }
        }

        JsonType lastType = null == array ? JsonType.OBJECT : JsonType.ARRAY;
        arrPos = 0;

        try {
            for (int i = 1; i < path.length; i++) {
                switch (path[i]) {
                    case Globals.JSON_TYPE_STRING_OBJECT:
                        switch (lastType) {
                            case OBJECT:
                                if (null == getObj(lastObj, 0, lastStr)) {
                                    lastObj.put(lastStr, new JSONObject());
                                }
                                lastObj = lastObj.getJSONObject(lastStr);
                                break;
                            case ARRAY:
                                while (null == getObj(lastArr, arrIndex[arrPos], null)) {
                                    lastArr.add(new JSONObject());
                                }
                                lastObj = lastArr.getJSONObject(arrIndex[arrPos++]);
                                break;
                            default:
                                //error
                        }
                        lastType = JsonType.OBJECT;
                        break;
                    case Globals.JSON_TYPE_STRING_ARRAY:
                        switch (lastType) {
                            case OBJECT:
                                if (null == getArr(lastObj, 0, lastStr)) {
                                    lastObj.put(lastStr, new JSONArray());
                                }
                                lastArr = lastObj.getJSONArray(lastStr);
                                break;
                            case ARRAY:
                                while (null == getArr(lastArr, arrIndex[arrPos], null)) {
                                    lastArr.add(new JSONArray());
                                }
                                lastArr = lastArr.getJSONArray(arrIndex[arrPos++]);
                                break;
                            default:
                                //error
                        }
                        lastType = JsonType.ARRAY;
                        break;
                    case Globals.JSON_TYPE_STRING_OBJ_ARRAY:
                        break;
                    default:
                        lastStr = path[i];
                }
            }
            switch (lastType) {
                case OBJECT:
                    lastObj.put(lastStr, jsonValues.getOneValue());
                    break;
                case ARRAY:
                    lastArr.addAll(Arrays.asList(jsonValues.getValues()));
                    break;
                default:
                    //error
            }
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    private static JSONArray getArr(JSON json, int index, String key) {
        try {
            if (json instanceof JSONObject) {
                return ((JSONObject) json).getJSONArray(key);
            }
            if (json instanceof JSONArray) {
                return ((JSONArray) json).getJSONArray(index);
            }
        } catch (Exception ignore) {
        }
        return null;
    }

    private static JSONObject getObj(JSON json, int index, String key) {
        try {
            if (json instanceof JSONObject) {
                return ((JSONObject) json).getJSONObject(key);
            }
            if (json instanceof JSONArray) {
                return ((JSONArray) json).getJSONObject(index);
            }
        } catch (Exception ignore) {
        }
        return null;
    }
}