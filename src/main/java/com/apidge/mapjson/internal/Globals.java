package com.apidge.mapjson.internal;

public interface Globals {
    int RESPONSE_API_FULL_RESULT_MAX_LENGTH = 16;

    int JSON_TYPE_INT_NONE = -1;
    String JSON_TYPE_STRING_NONE = "/";
    char JSON_TYPE_CHAR_NONE = '/';

    int JSON_TYPE_INT_STRING = 1;
    int JSON_TYPE_INT_OBJECT = 2;
    int JSON_TYPE_INT_ARRAY = 3;
    int JSON_TYPE_INT_OBJ_ARRAY = 4;

    String JSON_TYPE_STRING_OBJECT = ".";
    String JSON_TYPE_STRING_ARRAY = ":";
    String JSON_TYPE_STRING_OBJ_ARRAY = ";";

    char JSON_TYPE_CHAR_OBJECT = '.';
    char JSON_TYPE_CHAR_ARRAY = ':';
    char JSON_TYPE_CHAR_OBJ_ARRAY = ';';
}
