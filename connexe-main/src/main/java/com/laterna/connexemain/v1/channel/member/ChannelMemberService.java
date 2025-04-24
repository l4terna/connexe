package com.laterna.connexemain.v1.channel.member;

import com.laterna.connexemain.v1.channel.Channel;
import com.laterna.connexemain.v1.channel.ChannelService;
import com.laterna.connexemain.v1.channel.enumeration.ChannelType;
import com.laterna.connexemain.v1.channel.member.dto.ChannelMemberDTO;
import com.laterna.connexemain.v1.user.User;
import com.laterna.connexemain.v1.user.presence.tracking.ChannelTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChannelMemberService {

    private final ChannelService channelService;
    private final ChannelMemberRepository channelMemberRepository;
    private final ChannelTrackingService channelTrackingService;
    private final ChannelMemberMapper channelMemberMapper;

    public Page<ChannelMemberDTO> getChannelMembers(Long channelId, Pageable pageable) {
        Channel channel = channelService.findChannelById(channelId);

        if (channel.getType() == ChannelType.DC || channel.getType() == ChannelType.GROUP_DC) {
            Set<Long> userIds = channelTrackingService.getAllOnlineUserIds(channelId);

            return channelMemberRepository.findAllByChannelIdAndSortByUserIds(channelId, userIds, pageable)
                    .map(channelMemberMapper::toDTO);
        } else if (channel.getType() == ChannelType.TEXT) {
//            channelMemberRepository.findAllByChannelIdAndSortByUserIdsWithPermissions(channelId, );
        }

        return null;
    }

    @Transactional
    public void delete(Long channelId, Long memberId, User currentUser) {
        Channel channel = channelService.findChannelById(channelId);

        if (!channel.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Permission denied");
        }

        channelMemberRepository.deleteById(memberId);
    }

    @Transactional(readOnly = true)
    public boolean isMember(Long channelId, Long userId) {
        return channelMemberRepository.existsByIdAndUserId(channelId, userId);
    }

    @Transactional(readOnly = true)
    public void isMemberThrow(Long channelId, Long userId) {
        if (!isMember(channelId, userId)) {
            throw new AccessDeniedException("Permissions denied");
        }
    }
}
