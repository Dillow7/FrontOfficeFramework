package com.sprint.frontoffice.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entité Client correspondant à la table 'client' en base de données
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    
    private String id;
    
    @NotBlank(message = "Le nom ne peut pas être vide")
    private String nom;
    
    @NotBlank(message = "Le prénom ne peut pas être vide")
    private String prenom;
    
    @NotBlank(message = "Le téléphone ne peut pas être vide")
    private String telephone;
    
    @Email(message = "L'email doit être valide")
    private String email;
}
