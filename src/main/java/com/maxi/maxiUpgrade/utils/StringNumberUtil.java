package com.maxi.maxiUpgrade.utils;

public final class StringNumberUtil {
    private StringNumberUtil() {}

    public static boolean isFinite(double d) {
        return !Double.isNaN(d) && !Double.isInfinite(d);
    }
}
