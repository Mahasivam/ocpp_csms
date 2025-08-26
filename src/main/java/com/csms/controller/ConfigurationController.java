package com.csms.controller;

import com.csms.model.ChargePointConfiguration;
import com.csms.service.ConfigurationService;
import com.csms.service.RemoteCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/configuration")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ConfigurationController {

    private final ConfigurationService configurationService;
    private final RemoteCommandService remoteCommandService;

    @GetMapping("/{chargePointId}")
    public ResponseEntity<List<ChargePointConfiguration>> getConfiguration(@PathVariable String chargePointId) {
        // Implementation would require station lookup first
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{chargePointId}/change")
    public CompletableFuture<ResponseEntity<String>> changeConfiguration(
            @PathVariable String chargePointId,
            @RequestParam String key,
            @RequestParam String value) {

        return remoteCommandService.sendChangeConfiguration(chargePointId, key, value)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.badRequest().body("Error: " + ex.getMessage()));
    }

    @PostMapping("/{chargePointId}/clear-cache")
    public CompletableFuture<ResponseEntity<String>> clearCache(@PathVariable String chargePointId) {
        return remoteCommandService.sendClearCache(chargePointId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.badRequest().body("Error: " + ex.getMessage()));
    }
}