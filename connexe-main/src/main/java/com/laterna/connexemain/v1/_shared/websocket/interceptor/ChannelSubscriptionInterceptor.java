package com.laterna.connexemain.v1._shared.websocket.interceptor;

import com.laterna.connexemain.v1.channel.Channel;
import com.laterna.connexemain.v1.channel.ChannelService;
import com.laterna.connexemain.v1.channel.enumeration.ChannelType;
import com.laterna.connexemain.v1.channel.member.ChannelMemberService;
import com.laterna.connexemain.v1.hub.Hub;
import com.laterna.connexemain.v1.hub.HubService;
import com.laterna.connexemain.v1.permission.PermissionService;
import com.laterna.connexemain.v1.permission.enumeration.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Objects;


@Component
@RequiredArgsConstructor
public class ChannelSubscriptionInterceptor implements ChannelInterceptor {
    private final PermissionService permissionService;
    private final HubService hubService;
    private final ChannelService channelService;
    private final ChannelMemberService channelMemberService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel messageChannel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            if (destination != null && destination.startsWith("/topic/channels/")) {
                Long userId = (Long) Objects.requireNonNull(accessor.getSessionAttributes()).get("userId");

                Channel channel = extractChannel(destination);

                if ((channel.getType() == ChannelType.DC || channel.getType() == ChannelType.GROUP_DC)) {
                    channelMemberService.isMemberThrow(channel.getId(), userId);
                } else {
                    Hub hub = hubService.findHubByChannelId(channel.getId());
                    permissionService.hasPermissionsThrow(userId, hub.getId(), Permission.SEND_MESSAGES);
                }
            }
        }

        return message;
    }

    private Channel extractChannel(String destination) {
        String[] parts = destination.split("/");

        Long channelId = Long.valueOf(parts[3]);

        return channelService.findChannelById(channelId);
    }
}
