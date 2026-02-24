package com.reiasu.reiparticlesapi.utils;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.function.Supplier;

public final class ReflectUtil {

    private static final Logger LOGGER = LogUtils.getLogger();

    private ReflectUtil() {
    }

        public static void infoTimeWith(String name, Runnable invoker) {
        long start = System.currentTimeMillis();
        invoker.run();
        long end = System.currentTimeMillis();
        LOGGER.debug(name + " " + (end - start) + "ms");
    }

        public static void infoTimeWith(Runnable invoker) {
        infoTimeWith("", invoker);
    }

        public static <T> T infoTimeCallable(String name, Supplier<T> invoker) {
        long start = System.currentTimeMillis();
        T result = invoker.get();
        long end = System.currentTimeMillis();
        LOGGER.debug(name + " " + (end - start) + "ms");
        return result;
    }

        public static <T> T infoTimeCallable(Supplier<T> invoker) {
        return infoTimeCallable("", invoker);
    }
}
