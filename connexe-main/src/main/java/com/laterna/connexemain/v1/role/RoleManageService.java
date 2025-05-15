package com.laterna.connexemain.v1.role;

import com.laterna.connexemain.v1._shared.exception.EntityAlreadyExistsException;
import com.laterna.connexemain.v1.hub.Hub;
import com.laterna.connexemain.v1.hub.HubService;
import com.laterna.connexemain.v1.permission.PermissionService;
import com.laterna.connexemain.v1.permission.enumeration.Permission;
import com.laterna.connexemain.v1.role.dto.RoleCreateDTO;
import com.laterna.connexemain.v1.role.dto.RoleDTO;
import com.laterna.connexemain.v1.role.dto.RoleUpdateDTO;
import com.laterna.connexemain.v1.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleManageService {
    private final PermissionService permissionService;
    private final RoleRepository roleRepository;
    private final HubService hubService;
    private final RoleMapper roleMapper;

    @Transactional
    public RoleDTO create(Long hubId, RoleCreateDTO roleCreateDTO, User user) {
        Hub hub = hubService.findHubById(hubId);

        permissionService.hasPermissionsThrow(user.getId(), hubId, Permission.MANAGE_ROLES);

        if (roleRepository.existsByHubIdAndName(hubId, roleCreateDTO.name())) {
            throw new EntityAlreadyExistsException("Role with name " + roleCreateDTO.name() + " already exists");
        }

        String permissionsMask = Permission.toBinary(roleCreateDTO.permissions());

        Role role = Role.builder()
                .name(roleCreateDTO.name())
                .hub(hub)
                .color(roleCreateDTO.color())
                .permissions(roleCreateDTO.permissions())
                .permissionsMask(permissionsMask)
                .build();

        return roleMapper.toDTO(roleRepository.save(role));
    }

    @Transactional
    public RoleDTO updateRole(Long hubId, Long id, RoleUpdateDTO updateDTO, User user) {
        permissionService.hasPermissionsThrow(user.getId(), hubId, Permission.MANAGE_ROLES);

        Role role = roleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Role not found"));

        Optional.ofNullable(updateDTO.name()).ifPresent(role::setName);
        Optional.ofNullable(updateDTO.color()).ifPresent(role::setColor);
        Optional.ofNullable(updateDTO.permissions()).ifPresent(role::setPermissions);

        return roleMapper.toDTO(roleRepository.save(role));
    }

    @Transactional
    public void delete(Long hubId, Long id, User user) {
        permissionService.hasPermissionsThrow(user.getId(), hubId, Permission.MANAGE_ROLES);

        Role role = roleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Role not found"));

        if (!role.getHub().getId().equals(hubId)) {
            throw new AccessDeniedException("Permission denied");
        }

        roleRepository.deleteById(role.getId());
    }
}
