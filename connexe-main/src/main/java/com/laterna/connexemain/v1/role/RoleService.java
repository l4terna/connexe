package com.laterna.connexemain.v1.role;

import com.laterna.connexemain.v1.role.dto.RoleDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<RoleDTO> getHubRoles(Long hubId, Pageable pageable) {
        return roleRepository.findByHubId(hubId, pageable)
                .map(roleMapper::toDTO);
    }
}
