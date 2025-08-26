package com.csms.service;

import com.csms.model.Reservation;
import com.csms.model.ChargingStation;
import com.csms.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;

    @Transactional
    public Reservation createReservation(ChargingStation chargingStation,
                                         Integer connectorId,
                                         String idTag,
                                         LocalDateTime expiryDate,
                                         String parentIdTag) {

        // Generate reservation ID
        Integer reservationId = generateReservationId();

        Reservation reservation = new Reservation();
        reservation.setReservationId(reservationId);
        reservation.setChargingStation(chargingStation);
        reservation.setConnectorId(connectorId);
        reservation.setIdTag(idTag);
        reservation.setExpiryDate(expiryDate);
        reservation.setParentIdTag(parentIdTag);
        reservation.setStatus("Accepted");

        Reservation savedReservation = reservationRepository.save(reservation);
        log.info("Created reservation {} for charge point {} connector {}",
                reservationId, chargingStation.getChargePointId(), connectorId);

        return savedReservation;
    }

    @Transactional
    public void cancelReservation(Integer reservationId) {
        Optional<Reservation> reservationOpt = reservationRepository.findByReservationId(reservationId);
        if (reservationOpt.isPresent()) {
            Reservation reservation = reservationOpt.get();
            reservation.setStatus("Cancelled");
            reservationRepository.save(reservation);
            log.info("Cancelled reservation {}", reservationId);
        }
    }

    public Optional<Reservation> findByReservationId(Integer reservationId) {
        return reservationRepository.findByReservationId(reservationId);
    }

    public List<Reservation> findActiveReservations(UUID chargingStationId, Integer connectorId) {
        return reservationRepository.findByChargingStationIdAndConnectorId(chargingStationId, connectorId)
                .stream()
                .filter(r -> "Accepted".equals(r.getStatus()) && r.getExpiryDate().isAfter(LocalDateTime.now()))
                .toList();
    }

    @Scheduled(fixedRate = 60000) // Check every minute
    @Transactional
    public void cleanupExpiredReservations() {
        List<Reservation> expiredReservations = reservationRepository.findExpiredReservations(LocalDateTime.now());

        for (Reservation reservation : expiredReservations) {
            reservation.setStatus("Expired");
            reservationRepository.save(reservation);
            log.info("Expired reservation {}", reservation.getReservationId());
        }
    }

    private Integer generateReservationId() {
        // Simple implementation - could be improved with database sequence
        return (int) (System.currentTimeMillis() % 1000000);
    }
}