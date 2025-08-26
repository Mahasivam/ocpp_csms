package com.csms.service;

import com.csms.model.Transaction;
import com.csms.model.ChargingStation;
import com.csms.repository.TransactionRepository;
import com.csms.repository.ConnectorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final ConnectorRepository connectorRepository;

    @Transactional
    public Integer startTransaction(ChargingStation chargingStation,
                                    Integer connectorId,
                                    String idTag,
                                    Integer meterStart,
                                    LocalDateTime timestamp) {
        // Generate new transaction ID
        Integer transactionId = generateTransactionId();

        Transaction transaction = new Transaction();
        transaction.setTransactionId(transactionId);
        transaction.setChargingStation(chargingStation);
        transaction.setConnectorId(connectorId);
        transaction.setIdTag(idTag);
        transaction.setStartTimestamp(timestamp != null ? timestamp : LocalDateTime.now());
        transaction.setStartMeterValue(meterStart != null ? meterStart : 0);
        transaction.setStatus("Active");

        Transaction savedTransaction = transactionRepository.save(transaction);

        // Update connector with current transaction
        connectorRepository.findByChargingStationIdAndConnectorId(
                        chargingStation.getId(), connectorId)
                .ifPresent(connector -> {
                    connector.setCurrentTransactionId(savedTransaction.getId());
                    connector.setStatus("Charging");
                    connectorRepository.save(connector);
                });

        log.info("Started transaction {} for charge point {} on connector {}",
                transactionId, chargingStation.getChargePointId(), connectorId);

        return transactionId;
    }

    @Transactional
    public void stopTransaction(Integer transactionId,
                                String idTag,
                                Integer meterStop,
                                LocalDateTime timestamp,
                                String reason) {
        Optional<Transaction> transactionOpt = transactionRepository.findByTransactionId(transactionId);

        if (transactionOpt.isPresent()) {
            Transaction transaction = transactionOpt.get();
            transaction.setEndTimestamp(timestamp != null ? timestamp : LocalDateTime.now());
            transaction.setEndMeterValue(meterStop);
            transaction.setStatus("Completed");
            transaction.setReason(reason);

            transactionRepository.save(transaction);

            // Update connector status
            connectorRepository.findByChargingStationIdAndConnectorId(
                            transaction.getChargingStation().getId(), transaction.getConnectorId())
                    .ifPresent(connector -> {
                        connector.setCurrentTransactionId(null);
                        connector.setStatus("Available");
                        connectorRepository.save(connector);
                    });

            log.info("Stopped transaction {} with reason: {}", transactionId, reason);
        } else {
            log.warn("Transaction {} not found for stop request", transactionId);
        }
    }

    public Optional<Transaction> findByTransactionId(Integer transactionId) {
        return transactionRepository.findByTransactionId(transactionId);
    }

    public List<Transaction> findActiveTransactions() {
        return transactionRepository.findByStatus("Active");
    }

    public List<Transaction> findByChargingStation(UUID chargingStationId) {
        return transactionRepository.findByChargingStationId(chargingStationId);
    }

    private Integer generateTransactionId() {
        Optional<Integer> maxId = transactionRepository.findMaxTransactionId();
        return maxId.orElse(0) + 1;
    }
}