package com.example.unswpolicieschatgpt.database;

import androidx.room.TypeConverter;

import java.net.MalformedURLException;
import java.net.URL;

public class Converters {
    @TypeConverter
    public static URL fromString(String value) {
        try {
            return new URL(value);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @TypeConverter
    public static String fromUrl(String url) {
        return url == null ? null : url.toString();
    }
}

