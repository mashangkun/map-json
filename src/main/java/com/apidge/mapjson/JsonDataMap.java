package com.apidge.mapjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.apidge.mapjson.internal.JsonType;
import com.apidge.mapjson.internal.Utils;

import java.util.*;

public class JsonDataMap {
    private String id = "";
    private int[] index = new int[0];
    private Map<String, List<JsonValues>> fieldAllValue = new HashMap<>();

    public static Map<String, JsonDataMap> parseFromJson(Object json, String idField, Collection<String> fields) {
        if (json instanceof String) {
            try {
                Object tmp = JSON.parse((String) json);
                return parseFromJson(tmp, idField, fields);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        if (json instanceof JSONArray || json instanceof JSONObject) {
            return parseFromJson((JSON) json, idField, fields);
        }
        return null;
    }

    private static Map<String, JsonDataMap> parseFromJson(JSON json, String idField, Collection<String> fields) {
        Map<String, JsonDataMap> jsonDataMap = new HashMap<>();
        JsonValues result = JsonAnalysis.parseJson(json, JsonAnalysis.parsePath(idField));
        if (null == result || null == result.getOneValue()) {
            return null;
        }
        jsonDataMap.put(result.getOneValue(), appendJsonDataFieldValue(json, new JsonDataMap(result.getOneValue(), result.getArrayIndex()), fields));
        int[] lastIndex = Utils.getNextIndex(result.getArrayIndex());
        boolean arrayEmpty;
        int lastEmptyIndex = result.getArrayIndex().length - 1;
        while (true) {
            arrayEmpty = true;
            while (lastIndex.length > 0) {
                int[] index = lastIndex.clone();
                JsonValues resultTmp = JsonAnalysis.parseJson(json, JsonAnalysis.parsePath(idField), index);
                if (null != resultTmp && JsonType.NONE != resultTmp.getJsonType()) {
                    arrayEmpty = false;
                    lastEmptyIndex = lastIndex.length - 1;
                    jsonDataMap.put(resultTmp.getOneValue(), appendJsonDataFieldValue(json, new JsonDataMap(resultTmp.getOneValue(), resultTmp.getArrayIndex()), fields));

                    if (resultTmp.getArrayIndex().length > 0 && index.length > 1) {
                        lastIndex = Utils.getNextIndex(resultTmp.getArrayIndex());
                    }
                    // will never happen
                    else {
                        break;
                    }
                } else {
                    break;
                }
            }

            if (arrayEmpty) {
                lastEmptyIndex--;
            }

            if (lastEmptyIndex >= 0) {
                if (lastEmptyIndex >= lastIndex.length) {
//                    LogHelper.warn("WTF! lastEmptyIndex is bigger than lastIndex len, lastEmptyIndex: " + lastEmptyIndex + ", lastIndex: " + (null == lastIndex ? "null" : lastIndex.length));
                    break;
                }
                lastIndex[lastEmptyIndex]++;
                for (int j = lastEmptyIndex + 1; j < lastIndex.length; j++) {
                    lastIndex[j] = 0;
                }
            } else {
                break;
            }
        }
        return jsonDataMap;
    }

    private static JsonDataMap appendJsonDataFieldValue(JSON json, JsonDataMap data, Collection<String> fields) {
        for (String field : fields) {
            if (!data.fieldAllValue.containsKey(field)) {
                data.fieldAllValue.put(field, new ArrayList<>());
            }
            JsonValues jsonValues = JsonAnalysis.parseJson(json, JsonAnalysis.parsePath(field), data.index);

            if (null != jsonValues && jsonValues.getJsonType() != JsonType.NONE) {
                data.fieldAllValue.get(field).add(jsonValues);
                int[] newIndex = jsonValues.getArrayIndex().clone();
                if (newIndex.length > 1) {
                    newIndex[newIndex.length - 1]++;
                    appendFieldValue(data, json, field, newIndex, data.index.length, jsonValues.getArrayIndex().length);
                }
            }
        }
        return data;
    }

    private static void appendFieldValue(JsonDataMap data, JSON json, String field, int[] index, int bottom, int len) {
        JsonValues jsonValues = JsonAnalysis.parseJson(json, JsonAnalysis.parsePath(field), index);
        if (len <= bottom || len < 2) {
            return;
        }
        if (null == jsonValues || jsonValues.getJsonType() == JsonType.NONE) {
            int[] newIndex = index.clone();
            if (index.length >= len) {
                newIndex[len - 2]++;
                for (int i = len - 1; i < newIndex.length; i++) {
                    newIndex[i] = 0;
                }
                appendFieldValue(data, json, field, newIndex, bottom, len - 1);
            }
            return;
        }
        data.fieldAllValue.get(field).add(jsonValues);
        int[] newIndex = jsonValues.getArrayIndex().clone();
        newIndex[len - 1]++;
        appendFieldValue(data, json, field, newIndex, bottom, len);
    }

    private JsonDataMap(String id, int[] index) {
        setId(id);
        setIndex(null == index ? null : index.clone());
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    public Map<String, List<JsonValues>> getFieldAllValue() {
        return fieldAllValue;
    }

    public int[] getIndex() {
        return index.clone();
    }

    private void setIndex(int[] index) {
        this.index = index;
    }
}
