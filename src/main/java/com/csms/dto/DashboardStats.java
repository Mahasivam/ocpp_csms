package com.csms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStats {
    private long totalStations;
    private long onlineStations;
    private long offlineStations;
    private long activeTransactions;
    private long totalConnectors;
    private long availableConnectors;
    private long chargingConnectors;
    private long faultedConnectors;
}