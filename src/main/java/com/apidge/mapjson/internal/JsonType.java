package com.apidge.mapjson.internal;

public enum JsonType {
    NONE(Globals.JSON_TYPE_INT_NONE, Globals.JSON_TYPE_STRING_NONE, Globals.JSON_TYPE_CHAR_NONE),
    STRING(Globals.JSON_TYPE_INT_STRING, Globals.JSON_TYPE_STRING_NONE, Globals.JSON_TYPE_CHAR_NONE),
    OBJECT(Globals.JSON_TYPE_INT_OBJECT, Globals.JSON_TYPE_STRING_OBJECT, Globals.JSON_TYPE_CHAR_OBJECT),
    ARRAY(Globals.JSON_TYPE_INT_ARRAY, Globals.JSON_TYPE_STRING_ARRAY, Globals.JSON_TYPE_CHAR_ARRAY),
    OBJ_ARRAY(Globals.JSON_TYPE_INT_OBJ_ARRAY, Globals.JSON_TYPE_STRING_OBJ_ARRAY, Globals.JSON_TYPE_CHAR_OBJ_ARRAY);

    public final String str;
    public final char c;
    public final int type;

    JsonType(int type, String str, char c) {
        this.type = type;
        this.str = str;
        this.c = c;
    }

    public static JsonType valueOf(int type) {
        switch (type) {
            case Globals.JSON_TYPE_INT_STRING:
                return STRING;
            case Globals.JSON_TYPE_INT_OBJECT:
                return OBJECT;
            case Globals.JSON_TYPE_INT_ARRAY:
                return ARRAY;
            case Globals.JSON_TYPE_INT_OBJ_ARRAY:
                return OBJ_ARRAY;
        }
        return NONE;
    }

    public static JsonType valueOf(char c) {
        switch (c) {
            case Globals.JSON_TYPE_CHAR_OBJECT:
                return OBJECT;
            case Globals.JSON_TYPE_CHAR_ARRAY:
                return ARRAY;
            case Globals.JSON_TYPE_CHAR_OBJ_ARRAY:
                return OBJ_ARRAY;
        }
        return NONE;
    }
}
