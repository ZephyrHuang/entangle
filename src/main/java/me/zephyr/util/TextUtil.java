package me.zephyr.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class TextUtil {
    public static Map<String, String> toMap(String input) {
        Map<String, String> result = new HashMap<String, String>();
        if (StringUtils.isBlank(input)) {
            return result;
        }

        String[] entries = input.split("\\|");
        if (ArrayUtils.isEmpty(entries)) {
            return result;
        }

        for (String entry : entries) {
            if (!entry.contains("=")) {
                continue;
            }
            String[] keyValue = entry.split("=");
        }
        return result;
    }

    public static void main(String[] args) {
        String s = "b=";
        String[] ar = s.split("=");
        for (String st : ar) {
            System.out.println(st);
        }
    }
}
