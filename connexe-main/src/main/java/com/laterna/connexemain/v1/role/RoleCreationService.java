package com.laterna.connexemain.v1.role;

import com.laterna.connexemain.v1._shared.exception.EntityAlreadyExistsException;
import com.laterna.connexemain.v1.hub.Hub;
import com.laterna.connexemain.v1.hub.HubService;
import com.laterna.connexemain.v1.permission.enumeration.Permission;
import com.laterna.connexemain.v1.role.dto.CreateRoleDTO;
import com.laterna.connexemain.v1.role.dto.RoleDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleCreationService {
    private final RoleRepository roleRepository;
    private final HubService hubService;
    private final RoleMapper roleMapper;

    @Transactional
    public RoleDTO create(Long hubId, CreateRoleDTO createRoleDTO) {
        Hub hub = hubService.findHubById(hubId);

        if (roleRepository.existsByHubIdAndName(hubId, createRoleDTO.name())) {
            throw new EntityAlreadyExistsException("Role with name " + createRoleDTO.name() + " already exists");
        }

        String permissionsMask = Permission.toBinary(createRoleDTO.permissions());

        Role role = Role.builder()
                .name(createRoleDTO.name())
                .hub(hub)
                .color(createRoleDTO.color())
                .permissions(createRoleDTO.permissions())
                .permissionsMask(permissionsMask)
                .build();

        return roleMapper.toDTO(roleRepository.save(role));
    }
}
