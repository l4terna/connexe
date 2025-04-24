package com.laterna.connexemain.v1.hub;

import com.laterna.connexemain.v1.category.CategoryService;
import com.laterna.connexemain.v1.category.dto.CategoryDTO;
import com.laterna.connexemain.v1.channel.ChannelMapper;
import com.laterna.connexemain.v1.channel.ChannelService;
import com.laterna.connexemain.v1.channel.dto.ChannelDTO;
import com.laterna.connexemain.v1.channel.dto.HubEntitiesDTO;
import com.laterna.connexemain.v1.channel.voice.ChannelVoiceService;
import com.laterna.connexemain.v1.user.User;
import com.laterna.connexemain.v1.user.UserMapper;
import com.laterna.connexemain.v1.user.UserService;
import com.laterna.connexemain.v1.user.dto.UserDTO;
import com.laterna.connexemain.v1.user.settings.p2p.P2PSettingsService;
import com.laterna.connexemain.v1.user.settings.p2p.dto.P2PSettingsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class HubEntitiesService {

    private final CategoryService categoryService;
    private final ChannelService channelService;
    private final ChannelVoiceService channelVoiceService;
    private final UserService userService;
    private final ChannelMapper channelMapper;
    private final P2PSettingsService p2PSettingsService;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public HubEntitiesDTO getHubEntities(Long hubId, User currentUser) {
        List<CategoryDTO> categories = categoryService.getAllCategories(hubId);

        List<ChannelDTO> channels = channelService.findAllChannelsByHubId(hubId)
                .stream()
                .map(channel -> {
            Set<Long> userIds = channelVoiceService.getUserIdsByChannelId(channel.getId());


            List<User> rawUsers = userService.findAllUsersByIds(userIds);
            List<P2PSettingsDTO> p2pSettings = p2PSettingsService.findAllBySourceUserIdAndTargetUserIds(
                    currentUser.getId(), rawUsers.stream().map(User::getId).toList());

            List<UserDTO> users = rawUsers.stream().map(user -> {
                P2PSettingsDTO userSettings = p2pSettings
                        .stream()
                        .filter(settings -> settings.targetUserId().equals(user.getId()))
                        .findFirst()
                        .orElse(p2PSettingsService.createDefaultSettingsDTO(user.getId(), currentUser.getId()));

                return userMapper.toDTO(user, userSettings);
            }).toList();

            return channelMapper.toDTO(channel, users);
        }).toList();

        return HubEntitiesDTO.builder()
                .categories(categories)
                .channels(channels)
                .build();
    }
}
