package com.laterna.connexeauth.v1._shared.config;

import com.laterna.connexeauth.v1._shared.converter.ProtobufMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.MessageConverter;

import java.util.Collections;

@Configuration
public class MessagingConfig {
    @Bean
    public ProtobufMessageConverter protobufMessageConverter() {
        return new ProtobufMessageConverter();
    }

    @Bean
    public MessageConverter messageConverter() {
        return new CompositeMessageConverter(Collections.singletonList(protobufMessageConverter()));
    }
}