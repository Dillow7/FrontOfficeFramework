package com.sprint.frontoffice.service;

import com.sprint.frontoffice.dto.VehicleDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final BackOfficeApiService backOfficeApiService;

    public List<VehicleDTO> getAllVehicles() {
        return backOfficeApiService.getAllVehiclesFromBackOffice();
    }
}
