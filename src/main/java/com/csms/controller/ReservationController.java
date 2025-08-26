package com.csms.controller;

import com.csms.service.RemoteCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReservationController {

    private final RemoteCommandService remoteCommandService;

    @PostMapping("/{chargePointId}/reserve")
    public CompletableFuture<ResponseEntity<String>> reserveNow(
            @PathVariable String chargePointId,
            @RequestParam Integer connectorId,
            @RequestParam String idTag,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime expiryDate,
            @RequestParam(required = false) String parentIdTag) {

        return remoteCommandService.sendReserveNow(chargePointId, connectorId, expiryDate, idTag, parentIdTag)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.badRequest().body("Error: " + ex.getMessage()));
    }

    @PostMapping("/{chargePointId}/cancel")
    public CompletableFuture<ResponseEntity<String>> cancelReservation(
            @PathVariable String chargePointId,
            @RequestParam Integer reservationId) {

        return remoteCommandService.sendCancelReservation(chargePointId, reservationId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.badRequest().body("Error: " + ex.getMessage()));
    }
}
