package com.csms.controller;

import com.csms.model.ChargingStation;
import com.csms.model.Connector;
import com.csms.model.Transaction;
import com.csms.service.ChargingStationService;
import com.csms.service.TransactionService;
import com.csms.service.RemoteCommandService;
import com.csms.repository.ConnectorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/charging-stations")
@RequiredArgsConstructor
@Slf4j
public class ChargingStationController {

    private final ChargingStationService chargingStationService;
    private final TransactionService transactionService;
    private final RemoteCommandService remoteCommandService;
    private final ConnectorRepository connectorRepository;

    @GetMapping
    public ResponseEntity<List<ChargingStation>> getAllChargingStations() {
        List<ChargingStation> stations = chargingStationService.findAll();
        return ResponseEntity.ok(stations);
    }

    @GetMapping("/{chargePointId}")
    public ResponseEntity<ChargingStation> getChargingStation(@PathVariable String chargePointId) {
        return chargingStationService.findByChargePointId(chargePointId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{chargePointId}/connectors")
    public ResponseEntity<List<Connector>> getConnectors(@PathVariable String chargePointId) {
        return chargingStationService.findByChargePointId(chargePointId)
                .map(station -> {
                    List<Connector> connectors = connectorRepository.findByChargingStationId(station.getId());
                    return ResponseEntity.ok(connectors);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{chargePointId}/transactions")
    public ResponseEntity<List<Transaction>> getActiveTransactions(@PathVariable String chargePointId) {
        // Return only active transactions (where endTimestamp is null)
        List<Transaction> activeTransactions = transactionService.findActiveByChargePointId(chargePointId);
        return ResponseEntity.ok(activeTransactions);
    }
    
    @GetMapping("/{chargePointId}/transactions/all")
    public ResponseEntity<List<Transaction>> getAllTransactions(@PathVariable String chargePointId) {
        // Return all transactions (including completed ones)
        return chargingStationService.findByChargePointId(chargePointId)
                .map(station -> {
                    List<Transaction> transactions = transactionService.findByChargingStation(station.getId());
                    return ResponseEntity.ok(transactions);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{chargePointId}/remote-start")
    public CompletableFuture<ResponseEntity<String>> remoteStartTransaction(
            @PathVariable String chargePointId,
            @RequestParam String idTag,
            @RequestParam Integer connectorId) {

        return remoteCommandService.sendRemoteStartTransaction(chargePointId, idTag, connectorId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.badRequest().body("Error: " + ex.getMessage()));
    }

    @PostMapping("/{chargePointId}/remote-stop")
    public CompletableFuture<ResponseEntity<String>> remoteStopTransaction(
            @PathVariable String chargePointId,
            @RequestParam Integer transactionId) {

        return remoteCommandService.sendRemoteStopTransaction(chargePointId, transactionId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.badRequest().body("Error: " + ex.getMessage()));
    }

    @PostMapping("/{chargePointId}/reset")
    public CompletableFuture<ResponseEntity<String>> resetChargingStation(
            @PathVariable String chargePointId,
            @RequestParam(defaultValue = "Soft") String type) {

        return remoteCommandService.sendReset(chargePointId, type)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.badRequest().body("Error: " + ex.getMessage()));
    }

    @PostMapping("/{chargePointId}/unlock-connector")
    public CompletableFuture<ResponseEntity<String>> unlockConnector(
            @PathVariable String chargePointId,
            @RequestParam Integer connectorId) {

        return remoteCommandService.sendUnlockConnector(chargePointId, connectorId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.badRequest().body("Error: " + ex.getMessage()));
    }
}