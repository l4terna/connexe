package com.laterna.connexeauth.v1._shared.config;

import com.laterna.proto.v1.UserEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class StreamConfig {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Bean
    public Consumer<UserEvent> userEvent() {
        return applicationEventPublisher::publishEvent;
    }
}
