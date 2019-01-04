package com.geccocrawler.gecco.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * @author: zhangwei
 * @date: 16:04/2019-01-03
 */
public final class SystemPropertyUtil {

    private static final Log logger = LogFactory.getLog(SystemPropertyUtil.class);

    public static int getInt(String key, int def) {
        String value = get(key);
        if (value == null) {
            return def;
        }

        value = value.trim();
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            // Ignore
        }

        logger.warn(String.format("Unable to parse the integer system property '%s':%d - using the default value: %d", key, value, def));

        return def;
    }

    public static String get(String key) {
        return get(key, null);
    }

    public static String get(final String key, String def) {
        if (key == null) {
            throw new NullPointerException("key");
        }
        if (key.isEmpty()) {
            throw new IllegalArgumentException("key must not be empty.");
        }

        String value = null;
        try {
            if (System.getSecurityManager() == null) {
                value = System.getProperty(key);
            } else {
                value = AccessController.doPrivileged(new PrivilegedAction<String>() {
                    @Override
                    public String run() {
                        return System.getProperty(key);
                    }
                });
            }
        } catch (SecurityException e) {
            logger.warn(String.format("Unable to retrieve a system property '%s'; default values will be used.", key), e);
        }

        if (value == null) {
            return def;
        }

        return value;
    }
}
