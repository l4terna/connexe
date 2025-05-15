package com.laterna.connexemain.v1.role;

import com.laterna.connexemain.v1.role.dto.RoleDTO;
import com.laterna.connexemain.v1.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleMapper roleMapper;
    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public Set<RoleDTO> findAllMemberRoles(Long memberId) {
        return roleRepository.findAllMemberRoles(memberId).stream().map(roleMapper::toDTO).collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public Page<RoleDTO> getHubRoles(Long hubId, Long excludedMemberRolesById, String search, Pageable pageable) {
        if (excludedMemberRolesById != null) {
            return roleRepository.findByHubIdWithoutMemberIdRoles(hubId, excludedMemberRolesById, "%" + search + "%", pageable)
                    .map(roleMapper::toDTO);
        }

        if (search != null) {
            return roleRepository.findByHubIdAndNameLikeIgnoreCase(hubId, "%" + search + "%", pageable)
                    .map(roleMapper::toDTO);
        }

        return roleRepository.findByHubId(hubId, pageable)
                .map(roleMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Role findRoleById(Long id) {
        return roleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Role not found"));
    }
}
