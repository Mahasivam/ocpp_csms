package com.csms.scheduler;

import com.csms.model.ChargingStation;
import com.csms.service.ChargingStationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HeartbeatMonitor {

    private final ChargingStationService chargingStationService;

    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void checkHeartbeats() {
        List<ChargingStation> offlineStations = chargingStationService.findOfflineStations(10); // 10 min timeout

        if (!offlineStations.isEmpty()) {
            log.warn("Found {} offline charging stations", offlineStations.size());
            offlineStations.forEach(station ->
                    log.warn("Charging station {} is offline - last heartbeat: {}",
                            station.getChargePointId(), station.getLastHeartbeat()));
        }
    }
}
