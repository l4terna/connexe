package com.laterna.connexemain.v1._shared.config;

import com.laterna.proto.v1.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class StreamConfig {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Bean
    public Consumer<UserEvent> userEvent() {
        return applicationEventPublisher::publishEvent;
    }
}
