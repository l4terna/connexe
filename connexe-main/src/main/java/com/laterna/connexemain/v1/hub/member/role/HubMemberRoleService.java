package com.laterna.connexemain.v1.hub.member.role;

import com.laterna.connexemain.v1._shared.exception.EntityAlreadyExistsException;
import com.laterna.connexemain.v1.hub.Hub;
import com.laterna.connexemain.v1.hub.HubService;
import com.laterna.connexemain.v1.hub.member.HubMember;
import com.laterna.connexemain.v1.hub.member.HubMemberService;
import com.laterna.connexemain.v1.hub.member.role.dto.HubMemberRoleResponseDTO;
import com.laterna.connexemain.v1.permission.PermissionService;
import com.laterna.connexemain.v1.permission.enumeration.Permission;
import com.laterna.connexemain.v1.role.RoleService;
import com.laterna.connexemain.v1.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HubMemberRoleService {
    private final HubService hubService;
    private final HubMemberService hubMemberService;
    private final HubMemberRoleRepository hubMemberRoleRepository;
    private final PermissionService permissionService;
    private final RoleService roleService;

    @Transactional
    public void create(Long hubId, Long memberId, HubMemberRoleResponseDTO createDTO, User user) {
        Hub hub = hubService.findHubById(hubId);
        HubMember member = hubMemberService.findHubMemberById(memberId);

        if (!hub.getId().equals(member.getHub().getId())) {
            throw new AccessDeniedException("Permission denied");
        }

        permissionService.hasPermissionsThrow(user.getId(), hubId, Permission.MANAGE_ROLES);

        Optional.ofNullable(createDTO.roleId()).ifPresent(roleId -> {
            if (hubMemberRoleRepository.existsByHubMemberIdAndRoleId(memberId, createDTO.roleId())) {
                throw new EntityAlreadyExistsException("Role already exists");
            }

            HubMemberRole memberRole = HubMemberRole.builder()
                    .hubMember(member)
                    .role(roleService.findRoleById(createDTO.roleId()))
                    .build();

            hubMemberRoleRepository.save(memberRole);
        });
    }

    @Transactional
    public void delete(Long hubId, Long memberId, Long roleId, User user) {
        Hub hub = hubService.findHubById(hubId);
        HubMember member = hubMemberService.findHubMemberById(memberId);

        if (!hub.getId().equals(member.getHub().getId())) {
            throw new AccessDeniedException("Permission denied");
        }

        permissionService.hasPermissionsThrow(user.getId(), hubId, Permission.MANAGE_ROLES);

        HubMemberRole memberRole = hubMemberRoleRepository
                .findByHubMemberIdAndRoleId(memberId, roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        hubMemberRoleRepository.delete(memberRole);
    }
}
