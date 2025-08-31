package com.csms.controller;

import com.csms.dto.DashboardStats;
import com.csms.service.ChargingStationService;
import com.csms.service.TransactionService;
import com.csms.repository.ConnectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final ChargingStationService chargingStationService;
    private final TransactionService transactionService;
    private final ConnectorRepository connectorRepository;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStats> getDashboardStats() {
        long totalStations = chargingStationService.findAll().size();
        long offlineStations = chargingStationService.findOfflineStations(10).size(); // 10 min timeout
        long onlineStations = totalStations - offlineStations;
        long activeTransactions = transactionService.findActiveTransactions().size();

        long totalConnectors = connectorRepository.count();
        long availableConnectors = connectorRepository.findByStatus("Available").size();
        long chargingConnectors = connectorRepository.findByStatus("Charging").size();
        long faultedConnectors = connectorRepository.findByStatus("Faulted").size();

        DashboardStats stats = new DashboardStats(
                totalStations, onlineStations, offlineStations, activeTransactions,
                totalConnectors, availableConnectors, chargingConnectors, faultedConnectors
        );

        return ResponseEntity.ok(stats);
    }
}
