package com.csms.controller;

import com.csms.model.MeterValue;
import com.csms.service.MeterValueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/meter-values")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MeterValueController {

    private final MeterValueService meterValueService;

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<MeterValue>> getMeterValuesByTransaction(@PathVariable Integer transactionId) {
        List<MeterValue> meterValues = meterValueService.getMeterValuesByTransaction(transactionId);
        return ResponseEntity.ok(meterValues);
    }

    @GetMapping("/connector/{chargingStationId}/{connectorId}")
    public ResponseEntity<List<MeterValue>> getMeterValuesByConnector(
            @PathVariable UUID chargingStationId,
            @PathVariable Integer connectorId) {
        List<MeterValue> meterValues = meterValueService.getMeterValuesByConnector(chargingStationId, connectorId);
        return ResponseEntity.ok(meterValues);
    }
}
