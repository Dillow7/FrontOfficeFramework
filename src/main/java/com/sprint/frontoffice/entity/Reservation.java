package com.sprint.frontoffice.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entité Reservation correspondant à la table 'reservation' en base de données
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    
    private Integer id;
    
    @NotNull(message = "Le client ne peut pas être null")
    private String idClient;
    
    @Min(value = 1, message = "Le nombre de passagers doit être au moins 1")
    @NotNull(message = "Le nombre de passagers ne peut pas être null")
    private Integer nbPassager;
    
    @NotNull(message = "La date et heure d'arrivée ne peuvent pas être nulles")
    private LocalDateTime dateHeureArrive;
    
    @NotNull(message = "L'hôtel ne peut pas être null")
    private Integer idHotel;
    
    // Relations optionnelles pour les jointures
    private Client client;
    
    private Hotel hotel;
}
