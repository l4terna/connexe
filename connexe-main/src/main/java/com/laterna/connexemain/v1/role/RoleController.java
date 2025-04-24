package com.laterna.connexemain.v1.role;

import com.laterna.connexemain.v1.role.dto.CreateRoleDTO;
import com.laterna.connexemain.v1.role.dto.RoleDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/hubs/{hubId}/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleCreationService roleCreationService;
    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<Page<RoleDTO>> getRoles(
            @PathVariable Long hubId,
            @PageableDefault(size = 10, page = 0, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
            ) {
        return ResponseEntity.ok(roleService.getHubRoles(hubId, pageable));
    }

    @PostMapping
    public ResponseEntity<RoleDTO> create(
            @PathVariable Long hubId,
            @Valid @RequestBody CreateRoleDTO createRoleDTO
    ) {
        return ResponseEntity.ok(roleCreationService.create(hubId, createRoleDTO));
    }
}
