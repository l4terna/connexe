package com.laterna.connexemain.v1.hub.mute;

import com.laterna.connexemain.v1.hub.mute.dto.HubMuteDTO;
import com.laterna.connexemain.v1.hub.mute.dto.HubUnmuteDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/hubs/{hubId}")
@RequiredArgsConstructor
public class HubMuteController {

    private final HubMuteService hubMuteService;

    @PostMapping("/mute")
    public void muteUser(
            @PathVariable Long hubId,
            @RequestBody HubMuteDTO hubMuteDTO) {
        hubMuteService.muteUser(hubId, hubMuteDTO);
    }

    @PostMapping("/unmute")
    public void unmuteUser(
            @PathVariable Long hubId,
            @RequestBody HubUnmuteDTO hubUnmuteDTO) {
        hubMuteService.unmuteUser(hubId, hubUnmuteDTO);
    }
}
