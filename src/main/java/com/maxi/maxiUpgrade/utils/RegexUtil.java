package com.maxi.maxiUpgrade.utils;

import java.util.regex.Pattern;

public final class RegexUtil {
    private RegexUtil() {}

    public static Pattern safeCompile(String regex) {
        try {
            return Pattern.compile(regex);
        } catch (Exception e) {
            return Pattern.compile("$a"); // match nothing
        }
    }
}
