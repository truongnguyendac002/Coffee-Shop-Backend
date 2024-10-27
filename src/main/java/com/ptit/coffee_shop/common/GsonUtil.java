package com.ptit.coffee_shop.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GsonUtil {

    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    public static Gson getInstance(){
        return gson;
    }
}
