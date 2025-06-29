package com.example.reactive_backend.utils;

import org.bson.types.ObjectId;

public final class UtilMethods {
    private UtilMethods() {
        throw new UnsupportedOperationException("Utility class should not be extended.");
    }

    public static boolean checkIdIntegrity(String id) {
        return !ObjectId.isValid(id);
    }
}
