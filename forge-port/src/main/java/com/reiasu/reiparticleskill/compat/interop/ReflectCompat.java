// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticleskill.compat.interop;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public final class ReflectCompat {
    private static final Logger LOGGER = LogUtils.getLogger();

    private ReflectCompat() {
    }

    public static Object getStaticField(String className, String fieldName) throws Exception {
        Class<?> clazz = Class.forName(className);
        Field field = clazz.getField(fieldName);
        return field.get(null);
    }

    public static Method findMethod(Class<?> clazz, String methodName, Class<?>... argTypes) throws NoSuchMethodException {
        try {
            return clazz.getMethod(methodName, argTypes);
        } catch (NoSuchMethodException ignored) {
            for (Method method : clazz.getMethods()) {
                if (!method.getName().equals(methodName)) {
                    continue;
                }
                Class<?>[] params = method.getParameterTypes();
                if (params.length != argTypes.length) {
                    continue;
                }
                boolean compatible = true;
                for (int i = 0; i < params.length; i++) {
                    if (!params[i].isAssignableFrom(argTypes[i])) {
                        compatible = false;
                        break;
                    }
                }
                if (compatible) {
                    return method;
                }
            }
            throw new NoSuchMethodException(methodName);
        }
    }

    public static boolean invokeStaticNoArg(String className, String methodName) {
        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getMethod(methodName);
            method.invoke(null);
            return true;
        } catch (Throwable t) {
            LOGGER.debug("invokeStaticNoArg failed: {}.{}(): {}", className, methodName, t.getMessage());
            return false;
        }
    }

    public static boolean invokeOnInstanceNoArg(String className, String instanceFieldName, String methodName) {
        try {
            Object instance = getStaticField(className, instanceFieldName);
            Method method = instance.getClass().getMethod(methodName);
            method.invoke(instance);
            return true;
        } catch (Throwable t) {
            LOGGER.debug("invokeOnInstanceNoArg failed: {}.{}.{}(): {}", className, instanceFieldName, methodName, t.getMessage());
            return false;
        }
    }

    public static boolean invokeAnyStaticNoArg(String className, String... methodNames) {
        for (String methodName : methodNames) {
            if (invokeStaticNoArg(className, methodName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean invokeAnyOnInstanceNoArg(String className, String instanceFieldName, String... methodNames) {
        for (String methodName : methodNames) {
            if (invokeOnInstanceNoArg(className, instanceFieldName, methodName)) {
                return true;
            }
        }
        return false;
    }

    public static String tried(String... methodNames) {
        return Arrays.toString(methodNames);
    }
}
