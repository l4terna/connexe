package com.laterna.connexeauth.v1._shared.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;

public class StringEmptyToNullModule extends SimpleModule {
    public StringEmptyToNullModule() {
        addDeserializer(String.class, new StringEmptyToNullDeserializer());
    }
}