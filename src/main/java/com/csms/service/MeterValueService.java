package com.csms.service;

import com.csms.dto.ocpp.StopTransactionRequest;
import com.csms.model.ChargingStation;
import com.csms.model.MeterValue;
import com.csms.repository.MeterValueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeterValueService {

    private final MeterValueRepository meterValueRepository;

    @Transactional
    public void storeMeterValues(ChargingStation chargingStation,
                                 Integer connectorId,
                                 Integer transactionId,
                                 List<StopTransactionRequest.MeterValue> meterValues) {

        if (meterValues == null || meterValues.isEmpty()) {
            return;
        }

        for (StopTransactionRequest.MeterValue mv : meterValues) {
            if (mv.getSampledValue() != null) {
                for (StopTransactionRequest.MeterValue.SampledValue sv : mv.getSampledValue()) {
                    MeterValue meterValue = new MeterValue();
                    meterValue.setChargingStation(chargingStation);
                    meterValue.setConnectorId(connectorId);
                    meterValue.setTransactionId(transactionId);
                    meterValue.setTimestamp(mv.getTimestamp() != null ? mv.getTimestamp() : LocalDateTime.now());

                    // Parse the value as integer (Wh)
                    try {
                        meterValue.setMeterValue(Integer.parseInt(sv.getValue()));
                    } catch (NumberFormatException e) {
                        log.warn("Invalid meter value: {}", sv.getValue());
                        continue;
                    }

                    meterValue.setContext(sv.getContext());
                    meterValue.setFormat(sv.getFormat());
                    meterValue.setMeasurand(sv.getMeasurand());
                    meterValue.setPhase(sv.getPhase());
                    meterValue.setLocation(sv.getLocation());
                    meterValue.setUnit(sv.getUnit());

                    meterValueRepository.save(meterValue);
                }
            }
        }

        log.info("Stored {} meter value records for transaction {}",
                meterValues.size(), transactionId);
    }

    public List<MeterValue> getMeterValuesByTransaction(Integer transactionId) {
        return meterValueRepository.findByTransactionId(transactionId);
    }

    public List<MeterValue> getMeterValuesByConnector(UUID chargingStationId, Integer connectorId) {
        return meterValueRepository.findByChargingStationIdAndConnectorId(chargingStationId, connectorId);
    }
}