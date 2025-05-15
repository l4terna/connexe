package com.laterna.connexemain.v1.role;

import com.laterna.connexemain.v1.role.dto.RoleCreateDTO;
import com.laterna.connexemain.v1.role.dto.RoleDTO;
import com.laterna.connexemain.v1.role.dto.RoleUpdateDTO;
import com.laterna.connexemain.v1.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/hubs/{hubId}/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleManageService roleManageService;
    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<Page<RoleDTO>> getRoles(
            @PathVariable Long hubId,
            @RequestParam(value = "excludedMemberRolesById", required = false) Long excludedMemberRolesById,
            @RequestParam(value = "s", required = false) String search,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable
            ) {
        return ResponseEntity.ok(roleService.getHubRoles(hubId, excludedMemberRolesById, search, pageable));
    }

    @PostMapping
    public ResponseEntity<RoleDTO> create(
            @PathVariable Long hubId,
            @Valid @RequestBody RoleCreateDTO roleCreateDTO,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(roleManageService.create(hubId, roleCreateDTO, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleDTO> update(
            @PathVariable Long hubId,
            @PathVariable Long id,
            @Valid @RequestBody RoleUpdateDTO roleDTO,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(roleManageService.updateRole(hubId, id, roleDTO, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long hubId,
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        roleManageService.delete(hubId, id, user);
        return ResponseEntity.noContent().build();
    }
}
