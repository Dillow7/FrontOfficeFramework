package com.sprint.frontoffice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entité Hotel correspondant à la table 'hotel' en base de données
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hotel {
    
    private Integer id;
    
    private String nom;
}
