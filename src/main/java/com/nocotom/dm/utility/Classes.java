package com.nocotom.dm.utility;

import org.springframework.util.Assert;

import java.util.Optional;

public final class Classes {

    private Classes(){}

    public static <T> Optional<T> tryCast(Object object, Class<T> clazz) {
        Assert.notNull(clazz, "the class is null");
        if (clazz.isInstance(object)) {
            return Optional.of(clazz.cast(object));
        } else {
            return Optional.empty();
        }
    }
}
