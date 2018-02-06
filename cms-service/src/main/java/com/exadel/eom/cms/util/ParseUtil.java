package com.exadel.eom.cms.util;

import java.util.*;

public class ParseUtil {
    public static Map<String, String> string2map(String text, String delimiter) {
        String pattern = " *"+delimiter+" *";
        Map<String, String> map = new LinkedHashMap<>();
        for(String keyValue : text.split(pattern)) {
            String[] pairs = keyValue.split(" *= *", 2);
            if (pairs.length > 0) {
                map.put(pairs[0], pairs.length == 1 ? "" : pairs[1]);
            }
        }
        return map;
    }

    public static StringBuilder concat(List<String> arr, String delim, int from, int to) {
        StringBuilder builder = new StringBuilder();

        if(from >= arr.size()) return builder;
        if(to < 0) return builder;
        if(to >= arr.size()) to = arr.size() - 1;
        if(from > to) return builder;

        boolean nf = false;

        for(int i = from; i <= to; i++) {
            if (nf) {
                builder.append(delim);
            }
            nf = true;
            builder.append(arr.get(i));
        }
        return builder;
    }

    public static StringBuilder concat(String[] arr, String delim, int from, int to) {
        StringBuilder builder = new StringBuilder();

        if(from >= arr.length) return builder;
        if(to < 0) return builder;
        if(to >= arr.length) to = arr.length - 1;
        if(from > to) return builder;

        boolean nf = false;

        for(int i = from; i <= to; i++) {
            if (nf) {
                builder.append(delim);
            }
            nf = true;
            builder.append(arr[i]);
        }
        return builder;
    }

    public static List<String> split(String path, String delim) {
        return Arrays.asList(path.split(delim));
    }
}
