package com.laterna.connexemain.v1.channel;

import com.laterna.connexemain.v1.category.Category;
import com.laterna.connexemain.v1.category.CategoryService;
import com.laterna.connexemain.v1.channel.dto.ChannelDTO;
import com.laterna.connexemain.v1.channel.dto.UpdateChannelDTO;
import com.laterna.connexemain.v1.channel.enumeration.ChannelType;
import com.laterna.connexemain.v1.hub.Hub;
import com.laterna.connexemain.v1.hub.HubService;
import com.laterna.connexemain.v1.permission.PermissionService;
import com.laterna.connexemain.v1.permission.enumeration.Permission;
import com.laterna.connexemain.v1.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final CategoryService categoryService;
    private final PermissionService permissionService;
    private final ChannelMapper channelMapper;
    private final HubService hubService;

    public List<Channel> findAllChannelsByHubId(Long hubId) {
        return channelRepository.findAllByHubId(hubId);
    }

    @Transactional(readOnly = true)
    public int getLastPosition(Long categoryId) {
        return channelRepository.findMaxPositionByCategoryId(categoryId)
                .orElse(0) + 1;
    }

    private void shiftPositions(Long categoryId, Integer startPosition, Integer endPosition, Integer delta) {
        channelRepository.shiftPositions(categoryId, startPosition, endPosition, delta);
    }

    @Transactional
    public ChannelDTO update(Long id, UpdateChannelDTO updateChannelDTO, User currentUser) {
        Channel channel = findChannelById(id);

        if (channel.getType() != ChannelType.TEXT && channel.getType() == ChannelType.VOICE) {
            throw new IllegalArgumentException("Invalid channel type");
        }

        Category category = categoryService.findCategoryByIdWithHub(channel.getCategoryId());

        permissionService.hasPermissionsThrow(currentUser.getId(), category.getHub().getId(), Permission.MANAGE_CHANNELS);

        if (updateChannelDTO.name() != null && !updateChannelDTO.name().equals(channel.getName())) {
            channel.setName(updateChannelDTO.name());
        }

        if (updateChannelDTO.categoryId() != null && !updateChannelDTO.categoryId().equals(category.getId())) {
            Category updatedCategory = categoryService.findCategoryById(category.getId());
            channel.setCategoryId(updatedCategory.getId());
        }

        if (updateChannelDTO.position() != null && !updateChannelDTO.position().equals(channel.getPosition())) {
            int lastPosition = getLastPosition(category.getId());
            int newPosition = Math.min(Math.max(updateChannelDTO.position(), 1), lastPosition - 1);

            if(newPosition > channel.getPosition()) {
                shiftPositions(category.getId(), channel.getPosition() + 1, newPosition, -1);
            } else if (newPosition < category.getPosition()) {
                shiftPositions(category.getId(), newPosition, channel.getPosition(), 1);
            }

            channel.setPosition(newPosition);
        }

        return channelMapper.toDTO(channelRepository.save(channel));
    }

    @Transactional(readOnly = true)
    public Channel findChannelById(Long id) {
        return channelRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Channel not found"));
    }

    @Transactional
    public void delete(Long id, User currentUser) {
        Hub hub = hubService.findHubByChannelId(id);

        permissionService.hasPermissionsThrow(currentUser.getId(), hub.getId(), Permission.MANAGE_CHANNELS);

        channelRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Set<Long> findAllChannelIdsByUserId(Long userId) {
        return channelRepository.findAllChannelIdsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Page<ChannelDTO> getDirectAndGroupChannels(Pageable pageable, User user) {
        return channelRepository.findAllUserChannels(user.getId(), pageable)
                .map(channelMapper::toDTO);
    }
}
