package com.laterna.connexemain.v1.hub.mute;

import com.laterna.connexemain.v1.hub.Hub;
import com.laterna.connexemain.v1.hub.HubService;
import com.laterna.connexemain.v1.hub.mute.dto.HubMuteDTO;
import com.laterna.connexemain.v1.hub.mute.dto.HubUnmuteDTO;
import com.laterna.connexemain.v1.user.User;
import com.laterna.connexemain.v1.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HubMuteService {

    private final HubService hubService;
    private final UserService userService;
    private final HubMuteRepository hubMuteRepository;

    @Transactional
    public void muteUser(Long hubId, HubMuteDTO hubMuteDTO) {
        Hub hub = hubService.findHubById(hubId);
        User user = userService.findUserById(hubMuteDTO.userId());

        HubMute hubMute = HubMute.builder()
                .userId(user.getId())
                .hubId(hub.getId())
                .expiresAt(hubMuteDTO.expiresAt())
                .build();

        hubMuteRepository.save(hubMute);
    }

    @Transactional
    public void unmuteUser(Long hubId, HubUnmuteDTO hubUnmuteDTO) {
        Hub hub = hubService.findHubById(hubId);
        User user = userService.findUserById(hubUnmuteDTO.userId());

        HubMute hubMute = findHubMuteByHubIdAndUserId(hub.getId(), user.getId());

        hubMuteRepository.delete(hubMute);
    }

    @Transactional(readOnly = true)
    public HubMute findHubMuteByHubIdAndUserId(Long hubId, Long userId) {
        return hubMuteRepository.findByHubIdAndUserId(hubId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Hub mute not found"));
    }
}
