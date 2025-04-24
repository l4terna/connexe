package com.laterna.connexemain.v1.hub.member;

import com.laterna.connexemain.v1._shared.exception.EntityAlreadyExistsException;
import com.laterna.connexemain.v1.hub.Hub;
import com.laterna.connexemain.v1.hub.HubService;
import com.laterna.connexemain.v1.hub.enumeration.HubType;
import com.laterna.connexemain.v1.hub.member.dto.HubMemberDTO;
import com.laterna.connexemain.v1.user.User;
import com.laterna.connexemain.v1.user.presence.tracking.HubTrackingService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class HubMemberService {
    private final HubMemberRepository hubMemberRepository;
    private final HubMemberMapper hubMemberMapper;
    private final HubTrackingService hubTrackingService;
    private final HubService hubService;

    @Transactional(readOnly = true)
    public List<HubMemberDTO> getAllMembers(Long hubId, Long after) {
        Set<Long> userIds = hubTrackingService.getAllOnlineUserIds(hubId);

        return hubMemberRepository.findAllByHubIdAndSortByOnlineUserIds(hubId, userIds, after)
                .stream()
                .map(hubMemberMapper::toDTO)
                .toList();
    }

    @Transactional
    public void deleteMember(Long hubId, Long memberId, User currentUser) {
        HubMember hubMember = hubMemberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        if (hubMember.getHub().getId().equals(hubId) || hubMember.getHub().getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Permission denied");
        }

        hubMemberRepository.delete(hubMember);
    }

    @Transactional
    public HubMember createHubMember(Hub hub, User user) {
        hubMemberRepository.findByHubAndUser(hub, user)
                .ifPresent((member) -> {
                    throw new EntityAlreadyExistsException("Member already exists");
                });

        HubMember hubMember = HubMember.builder()
                .hub(hub)
                .user(user)
                .build();

        return hubMemberRepository.save(hubMember);
    }

    @Transactional
    public HubMemberDTO create(Long hubId, User user) {
        Hub hub = hubService.findHubById(hubId);

        if (hub.getType() != HubType.PUBLIC) {
            throw new AccessDeniedException("Permission denied");
        }

        return hubMemberMapper.toDTO(createHubMember(hub, user));
    }
}
