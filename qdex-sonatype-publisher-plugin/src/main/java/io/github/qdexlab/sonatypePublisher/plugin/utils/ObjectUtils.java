package io.github.qdexlab.sonatypePublisher.plugin.utils;

public class ObjectUtils {
    public static <T> T defaultIfNull(T object, T defaultValue) {
        return object != null ? object : defaultValue;
    }
}
