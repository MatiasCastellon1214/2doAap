package com.app.toDoApp.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDate;

public class JsonPrinter {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .setPrettyPrinting()
            .create();

    public static String toString(Object object) {
        return gson.toJson(object).trim()
                .replace("\n", "")
                .replace("\t", "")
                .replaceAll("\\s+", " ");
    }
}