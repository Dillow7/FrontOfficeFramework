package com.sprint.frontoffice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Contrôleur pour la page d'accueil
 */
@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home() {
        return "redirect:/reservations";
    }
}
