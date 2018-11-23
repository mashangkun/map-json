package com.apidge.mapjson.internal;

public final class Utils {
    public static boolean isNullStr(String str) {
        return null == str || "".equals(str);
    }

    public static int[] getNextIndex(int[] index) {
        if (null == index || index.length == 0) {
            return new int[0];
        }
        int[] nextIndex = index.clone();
        nextIndex[nextIndex.length - 1]++;
        return nextIndex;
    }
}