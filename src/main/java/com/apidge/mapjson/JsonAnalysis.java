package com.apidge.mapjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.apidge.mapjson.internal.Globals;
import com.apidge.mapjson.internal.JsonType;

import java.util.TreeSet;

import static com.apidge.mapjson.internal.Utils.isNullStr;

public class JsonAnalysis {
    public static TreeSet<String> parseJsonFields(String jsonStr) {
        TreeSet<String> re = new TreeSet<>();
        parseJsonBracesIntervalFields(re, "", jsonStr);
        return re;
    }

    private static void parseJsonBracesIntervalFields(TreeSet<String> resultSet, String field, String json) {
        JSONObject jsonObject = parseJsonObj(json);
        JSONArray jsonArray = parseJsonArr(json);
        if (null != jsonObject) {
            for (String k : jsonObject.keySet()) {
                parseJsonBracesIntervalFields(resultSet, field + getInterval(false)
                        + k, jsonObject.getString(k));
            }
            return;
        }
        if (null != jsonArray) {
            for (int i = 0; i < jsonArray.size(); i++) {
                parseJsonBracesIntervalFields(resultSet, field + getInterval(true), jsonArray.getString(i));
            }
            return;
        }
        if (!isNullStr(field)) {
            resultSet.add(field);
        }
    }

    private static String getInterval(boolean isArray) {
        return isArray ? Globals.JSON_TYPE_STRING_ARRAY : Globals.JSON_TYPE_STRING_OBJECT;
    }

    public static JSON parseFullJson(String str, boolean fast) {
        JSONObject jsonObject = parseJsonObj(str);
        if (null != jsonObject) {
            return parseFullJsonObject(jsonObject, fast);
        } else {
            JSONArray jsonArray = parseJsonArr(str);
            if (null != jsonArray) {
                return parseFullJsonArray(jsonArray, fast);
            }
        }
        return null;
    }

    private static JSONObject parseFullJsonObject(JSONObject jsonObject, boolean fast) {
        JSONObject result = new JSONObject();
        for (String key : jsonObject.keySet()) {
            if (fast && result.containsKey(key)) {
                continue;
            }
            JSONArray tmpArr = parseJsonArr(jsonObject.getString(key));
            if (null != tmpArr) {
                result.put(key, parseFullJsonArray(tmpArr, fast));
                continue;
            } else {
                JSONObject tmpObj = parseJsonObj(jsonObject.getString(key));
                if (null != tmpObj) {
                    result.put(key, parseFullJsonObject(tmpObj, true));
                    continue;
                }
            }
            addJsonObjField(result, key, jsonObject.getString(key));
        }
        return result;
    }


    private static JSONArray parseFullJsonArray(JSONArray jsonArray, boolean fast) {
        JSONArray result = new JSONArray();
        JSONObject childObj = new JSONObject();
        JSONArray childArr = new JSONArray();
        String childValue = null;
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject tmpObj = parseJsonObj(jsonArray.getString(i));
            if (null != tmpObj) {
                for (String key : tmpObj.keySet()) {
                    if (fast && childObj.containsKey(key)) {
                        continue;
                    }
                    JSONObject tmpObj2 = parseJsonObj(tmpObj.getString(key));
                    if (null != tmpObj2) {
                        childObj.put(key, parseFullJsonObject(tmpObj2, fast));
                        continue;
                    } else {
                        JSONArray tmpArr2 = parseJsonArr(tmpObj.getString(key));
                        if (null != tmpArr2) {
                            childObj.put(key, parseFullJsonArray(tmpArr2, fast));
                            continue;
                        }
                    }
                    addJsonObjField(childObj, key, tmpObj.getString(key));
                }
                continue;
            }
            JSONArray tmpArr = parseJsonArr(jsonArray.getString(i));
            if (null != tmpArr) {
                childArr = parseFullJsonArray(tmpArr, fast);
                continue;
            }
            if (null == childValue) {
                childValue = jsonArray.getString(i);
                if (null != childValue && childValue.length() > Globals.RESPONSE_API_FULL_RESULT_MAX_LENGTH) {
                    childValue = childValue.substring(0, Globals.RESPONSE_API_FULL_RESULT_MAX_LENGTH);
                }
            }

        }
        if (null != childArr && !childArr.isEmpty()) {
            result.add(childArr);
        }
        if (!childObj.isEmpty()) {
            result.add(childObj);
        }
        if (null != childValue) {
            result.add(childValue);
        }
        return result;
    }

    private static void addJsonObjField(JSONObject obj, String key, String value) {
        if (!obj.containsKey(key) || null == obj.get(key)) {
            value = substringValue(value);
            obj.put(key, value);
        }
    }

    private static String substringValue(String value) {
        if (null != value && value.length() > Globals.RESPONSE_API_FULL_RESULT_MAX_LENGTH) {
            return value.substring(0, Globals.RESPONSE_API_FULL_RESULT_MAX_LENGTH) + "...";
        }
        return value;
    }

    private static JSONObject parseJsonObj(String json) {
        try {
            return JSONObject.parseObject(json);
        } catch (Exception e) {
            return null;
        }
    }

    private static JSONArray parseJsonArr(String jsonArr) {
        try {
            return JSONObject.parseArray(jsonArr);
        } catch (Exception e) {
            return null;
        }
    }

    static JsonValues parseJson(Object json, String[] path, int... index) {
        if (json instanceof JSONObject) {
            return parseJson((JSONObject) json, new JSONArray(), JsonType.OBJECT.type, path, index);
        } else if (json instanceof JSONArray) {
            return parseJson(new JSONObject(), (JSONArray) json, JsonType.ARRAY.type, path, index);
        }
        return null;
    }

    private static JsonValues parseJson(JSONObject json, JSONArray array, int inType, String[] path, final int[] index) {
        if (!checkPath(path)) {
            return new JsonValues();
        }

        int size = path.length;

        JsonType jsonType = JsonType.OBJECT.str.equals(path[size - 1]) ? JsonType.OBJECT : JsonType.ARRAY.str.equals(path[size - 1]) ? JsonType.ARRAY : JsonType.STRING;

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
                arrIndex[arrPos] = null == index || index.length < (arrPos + 1) ? 0 : index[arrPos];
                arrPos++;
            }
        }
        arrPos = 0;

        JsonType lastDataType = JsonType.valueOf(inType);
        switch (lastDataType) {
            case ARRAY:
                if (!JsonType.ARRAY.str.equals(path[0])) {
                    return new JsonValues();
                }
                break;
            case OBJECT:
                if (!JsonType.OBJECT.str.equals(path[0])) {
                    return new JsonValues();
                }
                break;
        }

        String strResult = null;

        try {
            for (int i = 1; i < size; i++) {
                switch (path[i]) {
                    case Globals.JSON_TYPE_STRING_OBJECT:
                    case Globals.JSON_TYPE_STRING_OBJ_ARRAY:
                        switch (lastDataType) {
                            case OBJ_ARRAY:
                                int tmpIndex = arrIndex[arrPos++];
                                int count = 0;
                                boolean bingo = false;
                                for (String key : json.keySet()) {
                                    if (tmpIndex == count++) {
                                        json = json.getJSONObject(key);
                                        bingo = true;
                                        break;
                                    }
                                }
                                if (!bingo) {
                                    return new JsonValues();
                                }
                                break;
                            case OBJECT:
                                json = json.getJSONObject(path[i - 1]);
                                break;
                            case ARRAY:
                                json = array.getJSONObject(arrIndex[arrPos++]);
                                break;
                            default:
                                //error
                        }

                        if (Globals.JSON_TYPE_STRING_OBJ_ARRAY.equals(path[i])) {
                            lastDataType = JsonType.OBJ_ARRAY;
                        } else {
                            lastDataType = JsonType.OBJECT;
                        }
                        break;
                    case Globals.JSON_TYPE_STRING_ARRAY:
                        switch (lastDataType) {
                            case OBJ_ARRAY:
                                int tmpIndex = arrIndex[arrPos++];
                                int count = 0;
                                boolean bingo = false;
                                for (String key : json.keySet()) {
                                    if (tmpIndex == count++) {
                                        array = json.getJSONArray(key);
                                        bingo = true;
                                        break;
                                    }
                                }
                                if (!bingo) {
                                    return new JsonValues();
                                }
                                break;
                            case OBJECT:
                                array = json.getJSONArray(path[i - 1]);
                                break;
                            case ARRAY:
                                array = array.getJSONArray(arrIndex[arrPos++]);
                                break;
                            default:
                                //error
                        }
                        lastDataType = JsonType.ARRAY;
                        break;
                    default:
                        if (i == (size - 1)) {
                            if (JsonType.OBJECT == lastDataType) {
                                strResult = json.getString(path[i]);
                            } else {
//                                LogHelper.warn("json field error: pre str is not '.' ");
                            }
                        }
                        break;
                }
            }
        } catch (Exception ignore) {
            return new JsonValues();
        }

        return new JsonValues(array, strResult, arrIndex, jsonType);
    }

    private static boolean checkPath(String[] path) {
        return null != path && path.length > 0
                && (JsonType.OBJECT.str.equals(path[0]) || JsonType.ARRAY.str.equals(path[0]));
    }

    static String[] parsePath(String path) {
        if (isNullStr(path)) {
            return null;
        }
        if (!path.startsWith(JsonType.OBJECT.str) && !path.startsWith(JsonType.ARRAY.str)) {
            return null;
        }
        int size = 0;
        char[] chars = path.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == JsonType.OBJECT.c) {
                if (i < (chars.length - 1)) {
                    size += 2;
                } else {
                    size++;
                }
            } else if (chars[i] == JsonType.ARRAY.c || chars[i] == JsonType.OBJ_ARRAY.c) {
                size++;
            }
        }
        String[] re = new String[size];

        int index = 1;
        re[0] = String.valueOf(chars[0]);
        for (int i = 1; i < chars.length; i++) {
            if (chars[i] == JsonType.ARRAY.c) {
                if (!isNullStr(re[index])) {
                    index++;
                }
                re[index] = JsonType.ARRAY.str;
            } else if (chars[i] == JsonType.OBJECT.c) {
                if (!isNullStr(re[index])) {
                    index++;
                }
                re[index++] = JsonType.OBJECT.str;
            } else if (chars[i] == JsonType.OBJECT.c) {
                if (!isNullStr(re[index])) {
                    index++;
                }
                re[index++] = JsonType.OBJ_ARRAY.str;
            } else {
                if (isNullStr(re[index])) {
                    re[index] = "";
                }
                re[index] += String.valueOf(chars[i]);
            }
        }

        return re;
    }
}