package com.reiasu.reiparticlesapi.reflect;

import java.lang.annotation.Annotation;
import java.util.HashSet;

public final class SimpleClassInfo {

    private final String type;
    private final HashSet<String> annotations;

    public SimpleClassInfo(String type, HashSet<String> annotations) {
        this.type = type;
        this.annotations = annotations;
    }

    public String getType() {
        return type;
    }

    public HashSet<String> getAnnotations() {
        return annotations;
    }

        public boolean isAnnotationPresent(Class<? extends Annotation> anno) {
        return annotations.contains(anno.getName());
    }

        public Class<?> toClass() throws ClassNotFoundException {
        return Class.forName(type);
    }
}
