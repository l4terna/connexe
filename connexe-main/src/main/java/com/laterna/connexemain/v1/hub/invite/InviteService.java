package com.laterna.connexemain.v1.hub.invite;

import com.laterna.connexemain.v1.hub.Hub;
import com.laterna.connexemain.v1.hub.HubService;
import com.laterna.connexemain.v1.hub.member.HubMemberService;
import com.laterna.connexemain.v1.hub.invite.dto.AcceptInviteDTO;
import com.laterna.connexemain.v1.hub.invite.dto.CreateInviteDTO;
import com.laterna.connexemain.v1.hub.invite.dto.InviteDTO;
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

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class InviteService {
    private final InviteRepository inviteRepository;
    private final HubService hubService;
    private final InviteMapper inviteMapper;
    private final SecureRandom secureRandom;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqstuvwxyz0123456789";
    private static final int CODE_LENGTH = 10;
    private final HubMemberService hubMemberService;
    private final PermissionService permissionService;

    @Transactional
    public InviteDTO create(Long hubId, CreateInviteDTO createInviteDTO, User currentUser) {
        Hub hub = hubService.findHubById(hubId);

        permissionService.hasPermissionsThrow(currentUser.getId(), hubId, Permission.CREATE_INVITE);

        OffsetDateTime expiresAt = createInviteDTO.expiresAt();

        if (expiresAt == null) {
            expiresAt = OffsetDateTime.now(ZoneOffset.UTC).plusDays(7);
        }

        Invite invite = Invite.builder()
                .hub(hub)
                .code(generateCode())
                .maxUses(createInviteDTO.maxUses())
                .expiresAt(expiresAt)
                .createdBy(currentUser)
                .build();

        return inviteMapper.toDTO(inviteRepository.save(invite));
    }

    private String generateCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(secureRandom.nextInt(CHARACTERS.length())));
        }
        return code.toString();

    }

    @Transactional
    public void accept(Long hubId, AcceptInviteDTO acceptInviteDTO, User user) {
        Invite invite = inviteRepository.findByCode(acceptInviteDTO.code())
                .orElseThrow(() -> new EntityNotFoundException("Invite not found"));

        // maxUses = null = endless uses
        if (!invite.getIsActive() ||
                (invite.getMaxUses() != null && invite.getMaxUses() <= invite.getCurrentUses()) ||
                invite.getExpiresAt().isBefore(OffsetDateTime.now(ZoneOffset.UTC))
        ) {
            throw new AccessDeniedException("Code expired");
        }

        hubMemberService.create(hubId, user);

        invite.setCurrentUses(invite.getCurrentUses() + 1);
        inviteRepository.save(invite);
    }

    @Transactional(readOnly = true)
    public Page<InviteDTO> getAllInvites(Long hubId, Pageable pageable) {
        return inviteRepository.findAllByHubId(hubId, pageable)
                .map(inviteMapper::toDTO);
    }

    @Transactional
    public void delete(Long hubId, Long inviteId, User currentUser) {
        Hub hub = hubService.findHubById(hubId);
        if (!hub.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Permission denied");
        }

        inviteRepository.deleteById(inviteId);
    }
}
