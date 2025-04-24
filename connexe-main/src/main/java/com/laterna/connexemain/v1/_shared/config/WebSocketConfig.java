package com.laterna.connexemain.v1._shared.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laterna.connexemain.v1._shared.websocket.error.StompSubProtocolErrorHandlerImpl;
import com.laterna.connexemain.v1._shared.websocket.interceptor.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final ChannelAuthInterceptor channelAuthInterceptor;
    private final HandshakeInterceptorImpl handshakeInterceptorImpl;
    private final ChannelSubscriptionInterceptor channelSubscriptionInterceptor;
    private final StompSubProtocolErrorHandlerImpl stompSubProtocolErrorHandlerImpl;
    private final UserPresenceUpdateInterceptor userPresenceUpdateInterceptor;
    private final ChannelActiveUpdateInterceptor channelActiveUpdateInterceptor;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app/v1");
        config.enableSimpleBroker("/v1/topic", "/v1/user")
                .setHeartbeatValue(new long[]{15000, 15000})
                .setTaskScheduler(heartbeatScheduler());
        config.setUserDestinationPrefix("/v1/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins(allowedOrigins)
                .addInterceptors(handshakeInterceptorImpl)
                .withSockJS();

        registry.setErrorHandler(stompSubProtocolErrorHandlerImpl);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(
                channelAuthInterceptor,
                channelSubscriptionInterceptor,
                userPresenceUpdateInterceptor,
                channelActiveUpdateInterceptor
        );
    }

    @Bean
    public MappingJackson2MessageConverter jacksonMessageConverter(ObjectMapper objectMapper) {
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);

        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        converter.setContentTypeResolver(resolver);
        return converter;
    }

    @Bean
    public TaskScheduler heartbeatScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("ws-heartbeat-scheduler-");
        scheduler.setRemoveOnCancelPolicy(true);
        scheduler.setDaemon(true);
        return scheduler;
    }

}
