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

    public static String concat(List<String> arr, String delim, int from, int to) {
        StringBuilder builder = new StringBuilder();
        boolean nf = false;
        int idx = 0;
        for (String s : arr) {
            if ((idx < from) || ((idx > to) && (to >= 0))) {
                idx++;
                continue;
            }
            idx++;

            if (nf) {
                builder.append(delim);
            }
            nf = true;
            builder.append(s);
        }
        return builder.toString();
    }

    public static String concat(String[] arr, String delim, int from, int to) {

        if(from >= arr.length) return "";
        if(to < 0 || to >= arr.length) to = arr.length - 1;
        if(from > to) return "";
        boolean nf = false;
        StringBuilder builder = new StringBuilder();
        for(int i = from; i <= to; i++) {
            if (nf) {
                builder.append(delim);
            }
            nf = true;
            builder.append(arr[i]);
        }
        return builder.toString();
    }

    public static List<String> split(String path, String delim) {
        return Arrays.asList(path.split(delim));
    }
}
