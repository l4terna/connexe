package com.laterna.connexemain.v1._shared.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.laterna.connexemain.v1._shared.jackson.StringEmptyToNullModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneOffset;
import java.util.TimeZone;

@Configuration
public class JacksonConfig {
   
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper =  new ObjectMapper()
           .registerModule(new JavaTimeModule())
           .registerModule(new StringEmptyToNullModule())
           .registerModule(new Jdk8Module())
           .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
           .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
           .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
           .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
           .setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));

        return objectMapper;
    }
}