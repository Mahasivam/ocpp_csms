package com.csms.repository;

import com.csms.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    Optional<Reservation> findByReservationId(Integer reservationId);
    List<Reservation> findByChargingStationIdAndConnectorId(UUID chargingStationId, Integer connectorId);
    List<Reservation> findByStatus(String status);

    @Query("SELECT r FROM Reservation r WHERE r.expiryDate < :now AND r.status = 'Accepted'")
    List<Reservation> findExpiredReservations(LocalDateTime now);
}