package com.sprint.frontoffice.controller;

import com.sprint.frontoffice.dto.VehicleDTO;
import com.sprint.frontoffice.service.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Controller
@RequestMapping("/vehicles")
@Slf4j
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public String listVehicles(Model model) {
        try {
            List<VehicleDTO> vehicles = vehicleService.getAllVehicles();
            model.addAttribute("vehicles", vehicles);
        } catch (HttpClientErrorException e) {
            log.error("Erreur API BackOffice lors de la récupération des véhicules: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());

            String message;
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED || e.getStatusCode() == HttpStatus.FORBIDDEN) {
                message = "Accès refusé: token invalide ou expiré.";
            } else {
                message = "Erreur BackOffice: " + e.getStatusCode();
            }
            model.addAttribute("error", message);
            model.addAttribute("vehicles", List.of());
        } catch (RestClientException e) {
            log.error("Erreur réseau lors de la récupération des véhicules: {}", e.getMessage());
            model.addAttribute("error", "Impossible de contacter le BackOffice.");
            model.addAttribute("vehicles", List.of());
        }

        return "vehicles/list";
    }
}
