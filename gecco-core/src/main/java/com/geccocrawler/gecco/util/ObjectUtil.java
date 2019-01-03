package com.geccocrawler.gecco.util;

/**
 * @author: zhangwei
 * @date: 13:24/2019-01-02
 */
public final class ObjectUtil {

    public static <T> T checkNotNull(T arg, String text) {
        if (arg == null) {
            throw new NullPointerException(text);
        }
        return arg;
    }
}
