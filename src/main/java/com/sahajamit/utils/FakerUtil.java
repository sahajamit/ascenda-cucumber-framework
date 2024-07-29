package com.sahajamit.utils;


import net.datafaker.Faker;

public class FakerUtil {
    private static Faker instance = null;
    private FakerUtil() { }

    public static Faker getInstance() {
        if (instance == null) {
            synchronized (FakerUtil.class) {
                if (instance == null) {
                    instance = new Faker();
                }
            }
        }
        return instance;
    }
}

