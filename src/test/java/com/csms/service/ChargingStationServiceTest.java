package com.csms.service;

import com.csms.model.ChargingStation;
import com.csms.model.Connector;
import com.csms.repository.ChargingStationRepository;
import com.csms.repository.ConnectorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ChargingStationServiceTest {

    @Mock
    private ChargingStationRepository chargingStationRepository;

    @Mock
    private ConnectorRepository connectorRepository;

    @InjectMocks
    private ChargingStationService chargingStationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterNewChargingStation() {
        // Given
        String chargePointId = "CP001";
        String vendor = "TestVendor";
        String model = "TestModel";
        String serialNumber = "SN123";
        String firmwareVersion = "1.0.0";

        when(chargingStationRepository.findByChargePointId(chargePointId))
                .thenReturn(Optional.empty());

        ChargingStation savedStation = new ChargingStation();
        savedStation.setId(UUID.randomUUID());
        savedStation.setChargePointId(chargePointId);
        savedStation.setChargePointVendor(vendor);
        savedStation.setChargePointModel(model);

        when(chargingStationRepository.save(any(ChargingStation.class)))
                .thenReturn(savedStation);

        // When
        ChargingStation result = chargingStationService.registerChargingStation(
                chargePointId, vendor, model, serialNumber, firmwareVersion);

        // Then
        assertNotNull(result);
        assertEquals(chargePointId, result.getChargePointId());
        assertEquals(vendor, result.getChargePointVendor());
        assertEquals(model, result.getChargePointModel());

        verify(chargingStationRepository).save(any(ChargingStation.class));
    }

    @Test
    void testUpdateExistingChargingStation() {
        // Given
        String chargePointId = "CP001";
        ChargingStation existingStation = new ChargingStation();
        existingStation.setId(UUID.randomUUID());
        existingStation.setChargePointId(chargePointId);
        existingStation.setChargePointVendor("OldVendor");

        when(chargingStationRepository.findByChargePointId(chargePointId))
                .thenReturn(Optional.of(existingStation));
        when(chargingStationRepository.save(any(ChargingStation.class)))
                .thenReturn(existingStation);

        // When
        ChargingStation result = chargingStationService.registerChargingStation(
                chargePointId, "NewVendor", "NewModel", "SN123", "2.0.0");

        // Then
        assertNotNull(result);
        assertEquals("NewVendor", result.getChargePointVendor());
        verify(chargingStationRepository).save(existingStation);
    }

    @Test
    void testUpdateHeartbeat() {
        // Given
        String chargePointId = "CP001";
        ChargingStation station = new ChargingStation();
        station.setChargePointId(chargePointId);

        when(chargingStationRepository.findByChargePointId(chargePointId))
                .thenReturn(Optional.of(station));
        when(chargingStationRepository.save(any(ChargingStation.class)))
                .thenReturn(station);

        // When
        chargingStationService.updateHeartbeat(chargePointId);

        // Then
        assertNotNull(station.getLastHeartbeat());
        verify(chargingStationRepository).save(station);
    }

    @Test
    void testUpdateConnectorStatus() {
        // Given
        UUID stationId = UUID.randomUUID();
        Integer connectorId = 1;
        String status = "Available";
        String errorCode = "NoError";
        String info = "Connector ready";

        Connector existingConnector = new Connector();
        ChargingStation station = new ChargingStation();
        station.setId(stationId);
        existingConnector.setChargingStation(station);
        existingConnector.setConnectorId(connectorId);

        when(connectorRepository.findByChargingStationIdAndConnectorId(stationId, connectorId))
                .thenReturn(Optional.of(existingConnector));
        when(connectorRepository.save(any(Connector.class)))
                .thenReturn(existingConnector);

        // When
        chargingStationService.updateConnectorStatus(stationId, connectorId, status, errorCode, info);

        // Then
        assertEquals(status, existingConnector.getStatus());
        assertEquals(errorCode, existingConnector.getErrorCode());
        assertEquals(info, existingConnector.getInfo());
        verify(connectorRepository).save(existingConnector);
    }

    @Test
    void testUpdateConnectorStatusCreatesNewConnector() {
        // Given
        UUID stationId = UUID.randomUUID();
        Integer connectorId = 1;
        String status = "Available";

        when(connectorRepository.findByChargingStationIdAndConnectorId(stationId, connectorId))
                .thenReturn(Optional.empty());

        Connector newConnector = new Connector();
        when(connectorRepository.save(any(Connector.class)))
                .thenReturn(newConnector);

        // When
        chargingStationService.updateConnectorStatus(stationId, connectorId, status, null, null);

        // Then
        verify(connectorRepository).save(any(Connector.class));
    }
}