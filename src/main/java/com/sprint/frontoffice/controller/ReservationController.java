package com.sprint.frontoffice.controller;

import com.sprint.frontoffice.entity.Reservation;
import com.sprint.frontoffice.service.ReservationService;
import com.sprint.frontoffice.service.BackOfficeApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Contrôleur Spring MVC pour les réservations
 */
@Controller
@RequestMapping("/reservations")
@Slf4j
public class ReservationController {
    
    @Autowired
    private ReservationService reservationService;
    
    @Autowired
    private BackOfficeApiService backOfficeApiService;
    
    /**
     * Afficher la liste des réservations
     */
    @GetMapping
    public String listReservations(Model model) {
        log.info("Affichage de la liste des réservations");
        List<Reservation> reservations = reservationService.getAllReservations();
        model.addAttribute("reservations", reservations);
        model.addAttribute("hotels", backOfficeApiService.getAllHotelsFromBackOffice());
        model.addAttribute("clients", backOfficeApiService.getAllClientsFromBackOffice());
        return "reservations/list";
    }
    
    /**
     * Afficher la page de détail d'une réservation
     */
    @GetMapping("/{id}")
    public String viewReservation(@PathVariable Integer id, Model model) {
        log.info("Affichage de la réservation ID: {}", id);
        Optional<Reservation> reservation = reservationService.getReservationById(id);
        if (reservation.isPresent()) {
            model.addAttribute("reservation", reservation.get());
            return "reservations/detail";
        }
        return "redirect:/reservations";
    }
    
    /**
     * Rechercher et filtrer les réservations
     */
    @PostMapping("/search")
    public String searchReservations(
            @RequestParam(value = "idClient", required = false) String idClient,
            @RequestParam(value = "idHotel", required = false) Integer idHotel,
            @RequestParam(value = "dateDebut", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam(value = "dateFin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin,
            Model model) {
        
        log.info("Recherche de réservations - Client: {}, Hôtel: {}, Dates: {} à {}", 
                 idClient, idHotel, dateDebut, dateFin);
        
        List<Reservation> reservations = reservationService.searchReservations(idClient, idHotel, dateDebut, dateFin);
        
        model.addAttribute("reservations", reservations);
        model.addAttribute("hotels", backOfficeApiService.getAllHotelsFromBackOffice());
        model.addAttribute("clients", backOfficeApiService.getAllClientsFromBackOffice());
        model.addAttribute("searchedIdClient", idClient);
        model.addAttribute("searchedIdHotel", idHotel);
        model.addAttribute("searchedDateDebut", dateDebut);
        model.addAttribute("searchedDateFin", dateFin);
        
        return "reservations/list";
    }
    
    /**
     * Afficher le formulaire de création
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        log.info("Affichage du formulaire de création de réservation");
        model.addAttribute("reservation", new Reservation());
        model.addAttribute("hotels", backOfficeApiService.getAllHotelsFromBackOffice());
        model.addAttribute("clients", backOfficeApiService.getAllClientsFromBackOffice());
        return "reservations/form";
    }
    
    /**
     * Sauvegarder une nouvelle réservation
     */
    @PostMapping
    public String createReservation(@ModelAttribute Reservation reservation, Model model) {
        log.info("Création d'une nouvelle réservation");
        try {
            reservationService.createReservation(reservation);
            return "redirect:/reservations?success=true";
        } catch (Exception e) {
            log.error("Erreur lors de la création de la réservation: {}", e.getMessage());
            model.addAttribute("reservation", reservation);
            model.addAttribute("error", "Erreur lors de la création de la réservation");
            model.addAttribute("hotels", backOfficeApiService.getAllHotelsFromBackOffice());
            model.addAttribute("clients", backOfficeApiService.getAllClientsFromBackOffice());
            return "reservations/form";
        }
    }
    
    /**
     * Afficher le formulaire de modification
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        log.info("Affichage du formulaire de modification pour la réservation ID: {}", id);
        Optional<Reservation> reservation = reservationService.getReservationById(id);
        if (reservation.isPresent()) {
            model.addAttribute("reservation", reservation.get());
            model.addAttribute("hotels", backOfficeApiService.getAllHotelsFromBackOffice());
            model.addAttribute("clients", backOfficeApiService.getAllClientsFromBackOffice());
            return "reservations/form";
        }
        return "redirect:/reservations";
    }
    
    /**
     * Mettre à jour une réservation
     */
    @PostMapping("/{id}")
    public String updateReservation(@PathVariable Integer id, @ModelAttribute Reservation reservation, Model model) {
        log.info("Mise à jour de la réservation ID: {}", id);
        try {
            reservationService.updateReservation(id, reservation);
            return "redirect:/reservations?success=true";
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour de la réservation: {}", e.getMessage());
            model.addAttribute("reservation", reservation);
            model.addAttribute("error", "Erreur lors de la mise à jour");
            model.addAttribute("hotels", backOfficeApiService.getAllHotelsFromBackOffice());
            model.addAttribute("clients", backOfficeApiService.getAllClientsFromBackOffice());
            return "reservations/form";
        }
    }
    
    /**
     * Supprimer une réservation
     */
    @GetMapping("/{id}/delete")
    public String deleteReservation(@PathVariable Integer id) {
        log.info("Suppression de la réservation ID: {}", id);
        try {
            reservationService.deleteReservation(id);
            return "redirect:/reservations?deleted=true";
        } catch (Exception e) {
            log.error("Erreur lors de la suppression: {}", e.getMessage());
            return "redirect:/reservations?error=true";
        }
    }
    
    /**
     * Page d'accueil
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/reservations";
    }
}
