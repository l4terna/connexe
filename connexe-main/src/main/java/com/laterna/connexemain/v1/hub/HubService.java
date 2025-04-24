package com.laterna.connexemain.v1.hub;

import com.laterna.connexemain.v1.hub.dto.HubDTO;
import com.laterna.connexemain.v1.hub.dto.UpdateHubDTO;
import com.laterna.connexemain.v1.hub.enumeration.HubType;
import com.laterna.connexemain.v1.hub.avatar.HubAvatarService;
import com.laterna.connexemain.v1.permission.PermissionService;
import com.laterna.connexemain.v1.permission.enumeration.Permission;
import com.laterna.connexemain.v1.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;


@Service
@RequiredArgsConstructor
public class HubService {
    private final HubMapper hubMapper;
    private final HubRepository hubRepository;
    private final PermissionService permissionService;
    private final HubAvatarService hubAvatarService;

    @Transactional(readOnly = true)
    public Page<HubDTO> getAllHubs(Pageable pageable) {
        return hubRepository.findAll(pageable)
                .map(hubMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<HubDTO> getAllUserHubs(Pageable pageable, User currentUser) {
        return hubRepository.findAllByUserId(pageable, currentUser.getId())
                .map(hubMapper::toDTO);
    }

    @Transactional
    public HubDTO update(Long id, UpdateHubDTO updateHubDTO, User currentUser) {
        Hub hub = hubRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Hub not found"));

        permissionService.hasPermissionsThrow(currentUser.getId(), id, Permission.MANAGE_HUB);

        if (updateHubDTO.name() != null && !updateHubDTO.name().equals(hub.getName())) {
            hub.setName(updateHubDTO.name());
        }

        if (updateHubDTO.type() != null) {
            HubType type = HubType.fromValue(updateHubDTO.type());

            if (type != hub.getType()) {
                hub.setType(type);
            }
        }

        if (updateHubDTO.avatar() != null && !updateHubDTO.avatar().isEmpty()) {
            hub.setAvatar(hubAvatarService.update(updateHubDTO.avatar(), hub, currentUser.getId()));
        }

        hubRepository.save(hub);

        return hubMapper.toDTO(hub);
    }

    @Transactional(readOnly = true)
    public HubDTO findById(Long id) {
        return hubRepository.findById(id)
                .map(hubMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Hub not found"));
    }

    @Transactional(readOnly = true)
    public Hub findHubById(Long id) {
        return hubRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Hub not found"));
    }

    @Transactional
    public void delete(Long id, User currentUser) {
        Hub hub = findHubById(id);

        if (!hub.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Permission denied");
        }

        hubRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Hub findHubByChannelId(Long channelId) {
        return hubRepository.findHubByChannelId(channelId)
                .orElseThrow(() -> new EntityNotFoundException("Hub not found by channel id: " + channelId));
    }

    public Set<Long> findAllHubIdsByUserId(Long userId) {
        return hubRepository.findAllHubIdsByUserId(userId);
    }
}
