package com.csms.repository;

import com.csms.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Optional<Transaction> findByTransactionId(Integer transactionId);
    List<Transaction> findByStatus(String status);
    List<Transaction> findByChargingStationId(UUID chargingStationId);

    @Query("SELECT MAX(t.transactionId) FROM Transaction t")
    Optional<Integer> findMaxTransactionId();

    Optional<Transaction> findByChargingStationIdAndConnectorIdAndStatus(
            UUID chargingStationId, Integer connectorId, String status);

    // Add methods to find active transactions (where endTimestamp is null)
    List<Transaction> findByEndTimestampIsNull();
    List<Transaction> findByChargingStationIdAndEndTimestampIsNull(UUID chargingStationId);
    
    @Query("SELECT t FROM Transaction t JOIN t.chargingStation cs WHERE cs.chargePointId = :chargePointId AND t.endTimestamp IS NULL")
    List<Transaction> findActiveByChargePointId(String chargePointId);
}
