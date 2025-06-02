package com.laterna.connexemain.v1._shared.websocket.interceptor;

import com.laterna.connexemain.v1._shared.integration.auth.AuthServiceClient;
import com.laterna.connexemain.v1.user.User;
import com.laterna.connexemain.v1.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ChannelAuthInterceptor implements ChannelInterceptor {

    private final AuthServiceClient authServiceClient;
    private final UserService userService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> authorization = accessor.getNativeHeader("Authorization");

            if (authorization != null && !authorization.isEmpty()) {
                String token = authorization.get(0);

                if (token != null && token.startsWith("Bearer ")) {
                    Map<String, Object> attrs = accessor.getSessionAttributes();

                    if (attrs == null || attrs.get("__fprid") == null) {
                        throw new MessagingException("Unauthorized");
                    }

                    Long userId = authServiceClient.validateToken(token, attrs.get("__fprid").toString());

                    if (userId == null ) throw new MessagingException("Unauthorized");

                    try {
                        User user = userService.findUserById(userId);

                        if (user != null) {
                            UsernamePasswordAuthenticationToken auth =
                                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                            accessor.setUser(auth);

                            attrs.put("userId", user.getId());
                            accessor.setSessionAttributes(attrs);
                        }
                    } catch (Exception e) {
                        throw new MessagingException("Unauthorized");
                    }
                }
            }
        }

        return message;
    }
}
