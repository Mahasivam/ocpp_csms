package com.csms.controller;

import com.csms.dto.ApiResponse;
import com.csms.dto.ocpp.SendLocalListRequest;
import com.csms.service.LocalAuthListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/local-auth")
@RequiredArgsConstructor
@Slf4j
public class LocalAuthListController {
    
    private final LocalAuthListService localAuthListService;
    
    @PostMapping("/{chargePointId}/send-list")
    public ResponseEntity<ApiResponse<String>> sendLocalList(
            @PathVariable String chargePointId,
            @RequestParam Integer listVersion,
            @RequestParam String updateType,
            @RequestBody List<SendLocalListRequest.AuthorizationData> authList) {
        
        try {
            CompletableFuture<String> future = localAuthListService.sendLocalList(
                    chargePointId, listVersion, updateType, authList);
            
            String result = future.get(); // This will wait for the response
            return ResponseEntity.ok(new ApiResponse<>(true, "Local authorization list sent successfully", result));
        } catch (Exception e) {
            log.error("Error sending local list to {}: {}", chargePointId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Error sending local list: " + e.getMessage(), null));
        }
    }
    
    @PostMapping("/{chargePointId}/get-version")
    public ResponseEntity<ApiResponse<String>> getLocalListVersion(@PathVariable String chargePointId) {
        try {
            CompletableFuture<String> future = localAuthListService.getLocalListVersion(chargePointId);
            
            String result = future.get(); // This will wait for the response
            return ResponseEntity.ok(new ApiResponse<>(true, "Local list version retrieved successfully", result));
        } catch (Exception e) {
            log.error("Error getting local list version from {}: {}", chargePointId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Error getting local list version: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/{chargePointId}/current-version")
    public ResponseEntity<ApiResponse<Integer>> getCurrentListVersion(@PathVariable String chargePointId) {
        try {
            Integer version = localAuthListService.getCurrentListVersion(chargePointId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Current local list version retrieved", version));
        } catch (Exception e) {
            log.error("Error getting current local list version for {}: {}", chargePointId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Error getting current local list version: " + e.getMessage(), null));
        }
    }
}