package com.csms.controller;

import com.csms.model.ChargingStation;
import com.csms.repository.ChargingStationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
class ChargingStationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChargingStationRepository chargingStationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllChargingStations() throws Exception {
        // Given
        ChargingStation station1 = new ChargingStation();
        station1.setId(UUID.randomUUID());
        station1.setChargePointId("CP001");
        station1.setChargePointVendor("Vendor1");
        station1.setRegistrationStatus("Available");

        ChargingStation station2 = new ChargingStation();
        station2.setId(UUID.randomUUID());
        station2.setChargePointId("CP002");
        station2.setChargePointVendor("Vendor2");
        station2.setRegistrationStatus("Occupied");

        when(chargingStationRepository.findAll())
                .thenReturn(Arrays.asList(station1, station2));

        // When & Then
        mockMvc.perform(get("/api/charging-stations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].chargePointId").value("CP001"))
                .andExpect(jsonPath("$[1].chargePointId").value("CP002"));
    }

    @Test
    void testGetChargingStationById() throws Exception {
        // Given
        String chargePointId = "CP001";
        ChargingStation station = new ChargingStation();
        station.setId(UUID.randomUUID());
        station.setChargePointId(chargePointId);
        station.setChargePointVendor("TestVendor");
        station.setChargePointModel("TestModel");
        station.setRegistrationStatus("Available");
        station.setLastHeartbeat(LocalDateTime.now());

        when(chargingStationRepository.findByChargePointId(chargePointId))
                .thenReturn(Optional.of(station));

        // When & Then
        mockMvc.perform(get("/api/charging-stations/{chargePointId}", chargePointId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.chargePointId").value(chargePointId))
                .andExpect(jsonPath("$.chargePointVendor").value("TestVendor"))
                .andExpect(jsonPath("$.registrationStatus").value("Available"));
    }

    @Test
    void testGetChargingStationByIdNotFound() throws Exception {
        // Given
        String chargePointId = "NONEXISTENT";
        when(chargingStationRepository.findByChargePointId(chargePointId))
                .thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/charging-stations/{chargePointId}", chargePointId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}