package com.laterna.connexemain.v1.hub.member;

import com.laterna.connexemain.v1.hub.member.dto.HubMemberDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HubMemberProviderService {
    private final HubMemberRepository hubMemberRepository;
    private final HubMemberMapper hubMemberMapper;

    @Transactional(readOnly = true)
    public HubMember findMemberByHubIdAndUserId(Long hubId, Long userId) {
        return hubMemberRepository.findByHubIdAndUserId(hubId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));
    }

    @Transactional(readOnly = true)
    public HubMemberDTO findByHubIdAndUserId(Long hubId, Long userId) {
        return hubMemberMapper.toDTO(findMemberByHubIdAndUserId(hubId, userId));
    }

}
