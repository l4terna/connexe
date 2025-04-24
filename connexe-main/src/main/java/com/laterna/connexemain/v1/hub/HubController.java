package com.laterna.connexemain.v1.hub;

import com.laterna.connexemain.v1.channel.dto.HubEntitiesDTO;
import com.laterna.connexemain.v1.hub.dto.CreateHubDTO;
import com.laterna.connexemain.v1.hub.dto.HubDTO;
import com.laterna.connexemain.v1.hub.dto.UpdateHubDTO;
import com.laterna.connexemain.v1.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/hubs")
@RequiredArgsConstructor
public class HubController {
    private final HubService hubService;
    private final HubEntitiesService hubEntitiesService;
    private final HubCreationService hubCreationService;

    @GetMapping
    public ResponseEntity<Page<HubDTO>> getAllHubs(@PageableDefault(size = 10, page = 0, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(hubService.getAllHubs(pageable));
    }

    @GetMapping("/@me")
    public ResponseEntity<Page<HubDTO>> getAllUserHubs(
            @PageableDefault(size = 10, page = 0, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(hubService.getAllUserHubs(pageable, user));
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<HubDTO> create(
            @Valid @ModelAttribute CreateHubDTO createHubDTO,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(hubCreationService.create(createHubDTO, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HubDTO> update(
            @ModelAttribute UpdateHubDTO updateHubDTO,
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(hubService.update(id, updateHubDTO, user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HubDTO> getHubById(@PathVariable Long id) {
        return ResponseEntity.ok(hubService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        hubService.delete(id, user);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}/entities")
    public ResponseEntity<HubEntitiesDTO> getHubEntities(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(hubEntitiesService.getHubEntities(id, user));
    }
}
