package org.mickey.billing.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtils {

    final static Gson GSON = new GsonBuilder()
            .create();

    public static void print(Object src) {
        System.out.println(GSON.toJson(src));
    }
}
