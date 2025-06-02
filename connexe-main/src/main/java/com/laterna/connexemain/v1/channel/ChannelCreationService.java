package com.laterna.connexemain.v1.channel;

import com.laterna.connexemain.v1.category.Category;
import com.laterna.connexemain.v1.category.CategoryService;
import com.laterna.connexemain.v1.channel.dto.DirectChannelDTO;
import com.laterna.connexemain.v1.channel.dto.HubChannelDTO;
import com.laterna.connexemain.v1.channel.dto.CreateDirectChannelDTO;
import com.laterna.connexemain.v1.channel.dto.CreateHubChannelDTO;
import com.laterna.connexemain.v1.channel.enumeration.ChannelType;
import com.laterna.connexemain.v1.channel.member.ChannelMemberCreationService;
import com.laterna.connexemain.v1.channel.member.dto.CreateChannelMemberDTO;
import com.laterna.connexemain.v1.permission.PermissionService;
import com.laterna.connexemain.v1.permission.enumeration.Permission;
import com.laterna.connexemain.v1.user.User;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChannelCreationService {
    private final ChannelRepository channelRepository;
    private final CategoryService categoryService;
    private final PermissionService permissionService;
    private final ChannelMapper channelMapper;
    private final ChannelService channelService;
    private final ChannelMemberCreationService channelMemberCreationService;

    @Transactional
    public HubChannelDTO createHubChannel(Long hubId, CreateHubChannelDTO createChannelDTO, User currentUser) {
        Category category = categoryService.findCategoryByIdWithHub(createChannelDTO.categoryId());

        if (createChannelDTO.type() != ChannelType.TEXT && createChannelDTO.type() != ChannelType.VOICE) {
            throw new ValidationException("type: must be either TEXT or VOICE");
        }

        permissionService.hasPermissionsThrow(currentUser.getId(), hubId, Permission.MANAGE_CHANNELS);

        Channel channel = Channel.builder()
                .categoryId(category.getId())
                .type(createChannelDTO.type())
                .owner(currentUser)
                .name(createChannelDTO.name())
                .position(channelService.getLastPosition(category.getId()))
                .build();

        return channelMapper.toHubDTO(channelRepository.save(channel));
    }

    @Transactional
    public DirectChannelDTO createDirectChannel(CreateDirectChannelDTO createChannelDTO, User currentUser) {
        Channel channel;

        createChannelDTO.members().add(currentUser.getId());

        long countMembers = createChannelDTO.members().stream().distinct().count();

        // check member ids greater than 2 and all ids are positive
        if (countMembers < 2 || !createChannelDTO.members().stream().allMatch(num -> num >= 0)) {
            throw new ValidationException("members: count must be greater than 2 or equals and ids greater than 0");
        }

        // if members = 2 - Direct Chat, if more - Group Direct Chat
        if (countMembers == 2) {
            channel = channelRepository.findDirectChannelByMemberIds(currentUser.getId(), createChannelDTO.members().get(0))
                    .orElseGet(() -> createDirectChannel(createChannelDTO.members()));
        } else {
            channel = createGroupChannel(createChannelDTO.members(), currentUser);
        }

        return channelMapper.toDirectDTO(channel, currentUser.getId());
    }

    private Channel createDirectChannel(List<Long> memberIds) {
        Channel channel = Channel.builder()
                .type(ChannelType.DC)
                .build();

        Channel newChan = channelRepository.save(channel);

        CreateChannelMemberDTO createChanMembers = CreateChannelMemberDTO.builder()
                .users(memberIds)
                .build();

        channelMemberCreationService.create(newChan.getId(), createChanMembers);

        return newChan;
    }

    private Channel createGroupChannel(List<Long> memberIds, User currentUser) {
        Channel channel = Channel.builder()
                .type(ChannelType.GROUP_DC)
                .owner(currentUser)
                .build();

        Channel newChan = channelRepository.save(channel);

        CreateChannelMemberDTO createChanMembers = CreateChannelMemberDTO.builder()
                .users(memberIds)
                .build();

        channelMemberCreationService.create(newChan.getId(), createChanMembers);

        return newChan;
    }
}
