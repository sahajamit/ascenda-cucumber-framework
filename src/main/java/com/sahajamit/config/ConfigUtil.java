package com.sahajamit.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public class ConfigUtil {
    private static final Logger log = LoggerFactory.getLogger(ConfigUtil.class);
    private static final String filePath = "properties/";
    private static final String fileExtension = ".properties";
    private static final Properties properties = new Properties();

    static {
        try {
            try (InputStream inputStream = ConfigUtil.class.getClassLoader().getResourceAsStream(filePath + "test" + fileExtension)) {
                properties.load(inputStream);
            }
        } catch (Exception e) {
            log.error("Loading test.properties failed. exiting...");
            System.exit(-1);
        }
    }


    public static String get(String key) {
        return get(key, null);
    }

    public static String get(String key, String defaultValue) {
        String value = getProperty(key);
        return Objects.nonNull(value) ? value : defaultValue;
    }

    public static int getInt(String key, int defaultValue) {
        String value = getProperty(key);
        return Objects.nonNull(value) ? Integer.parseInt(value) : defaultValue;
    }

    public static String getString(String key, String defaultValue){
        String value = getProperty(key);
        return Objects.nonNull(value) ? value : defaultValue;
    }

    public static String getString(String key){
        return getString(key, null);
    }


    private static String getProperty(String key) {
        if (System.getProperties().containsKey(key))
            return System.getProperty(key);
        if(Objects.nonNull(System.getenv(key)))
            return System.getenv(key);
        if (properties.containsKey(key)) {
            return properties.getProperty(key);
        }
        return null;
    }

}
