package com.sprint.frontoffice.service;

import com.sprint.frontoffice.entity.Client;
import com.sprint.frontoffice.entity.Hotel;
import com.sprint.frontoffice.entity.Reservation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service métier pour les réservations
 */
@Service
@Slf4j
public class ReservationService {
    
    @Autowired
    private BackOfficeApiService backOfficeApiService;
    
    /**
     * Récupérer toutes les réservations
     */
    public List<Reservation> getAllReservations() {
        log.info("Récupération de toutes les réservations");
        List<Reservation> reservations = backOfficeApiService.getAllReservationsFromBackOffice();
        enrichReservations(reservations);
        return reservations;
    }
    
    /**
     * Récupérer une réservation par ID
     */
    public Optional<Reservation> getReservationById(Integer id) {
        log.info("Récupération de la réservation avec l'ID: {}", id);
        Reservation r = backOfficeApiService.getReservationFromBackOffice(id);
        if (r == null) {
            return Optional.empty();
        }
        enrichReservation(r);
        return Optional.of(r);
    }
    
    /**
     * Récupérer les réservations par client
     */
    public List<Reservation> getReservationsByClient(String idClient) {
        log.info("Récupération des réservations pour le client: {}", idClient);
        List<Reservation> reservations = new ArrayList<>();
        for (Reservation r : backOfficeApiService.getAllReservationsFromBackOffice()) {
            if (idClient != null && idClient.equals(r.getIdClient())) {
                reservations.add(r);
            }
        }
        enrichReservations(reservations);
        return reservations;
    }
    
    /**
     * Récupérer les réservations par hôtel
     */
    public List<Reservation> getReservationsByHotel(Integer idHotel) {
        log.info("Récupération des réservations pour l'hôtel: {}", idHotel);
        List<Reservation> reservations = new ArrayList<>();
        for (Reservation r : backOfficeApiService.getAllReservationsFromBackOffice()) {
            if (idHotel != null && idHotel.equals(r.getIdHotel())) {
                reservations.add(r);
            }
        }
        enrichReservations(reservations);
        return reservations;
    }
    
    /**
     * Récupérer les réservations dans une plage de dates
     */
    public List<Reservation> getReservationsByDateRange(LocalDateTime dateDebut, LocalDateTime dateFin) {
        log.info("Récupération des réservations entre {} et {}", dateDebut, dateFin);
        List<Reservation> reservations = new ArrayList<>();
        for (Reservation r : backOfficeApiService.getAllReservationsFromBackOffice()) {
            if (r.getDateHeureArrive() == null) {
                continue;
            }
            if ((dateDebut == null || !r.getDateHeureArrive().isBefore(dateDebut))
                    && (dateFin == null || !r.getDateHeureArrive().isAfter(dateFin))) {
                reservations.add(r);
            }
        }
        enrichReservations(reservations);
        return reservations;
    }
    
    /**
     * Récupérer les réservations filtrées par client ET date
     */
    public List<Reservation> searchReservations(String idClient, Integer idHotel, LocalDateTime dateDebut, LocalDateTime dateFin) {
        log.info("Recherche des réservations avec filtres - Client: {}, Hôtel: {}, Dates: {} à {}", 
                 idClient, idHotel, dateDebut, dateFin);

        List<Reservation> out = new ArrayList<>();
        for (Reservation r : backOfficeApiService.getAllReservationsFromBackOffice()) {
            if (idClient != null && !idClient.isEmpty() && (r.getIdClient() == null || !idClient.equals(r.getIdClient()))) {
                continue;
            }
            if (idHotel != null && (r.getIdHotel() == null || !idHotel.equals(r.getIdHotel()))) {
                continue;
            }
            if ((dateDebut != null || dateFin != null) && r.getDateHeureArrive() == null) {
                continue;
            }
            if (dateDebut != null && r.getDateHeureArrive().isBefore(dateDebut)) {
                continue;
            }
            if (dateFin != null && r.getDateHeureArrive().isAfter(dateFin)) {
                continue;
            }
            out.add(r);
        }

        enrichReservations(out);
        return out;
    }
    
    /**
     * Créer une nouvelle réservation
     */
    public Reservation createReservation(Reservation reservation) {
        log.info("Création d'une nouvelle réservation");
        boolean ok = backOfficeApiService.createReservationInBackOffice(reservation);
        return ok ? reservation : null;
    }
    
    /**
     * Mettre à jour une réservation
     */
    public Reservation updateReservation(Integer id, Reservation reservationDetails) {
        log.info("Mise à jour de la réservation avec l'ID: {}", id);
        Reservation r = backOfficeApiService.getReservationFromBackOffice(id);
        if (r == null) {
            return null;
        }
        if (reservationDetails.getIdClient() != null) r.setIdClient(reservationDetails.getIdClient());
        if (reservationDetails.getNbPassager() != null) r.setNbPassager(reservationDetails.getNbPassager());
        if (reservationDetails.getDateHeureArrive() != null) r.setDateHeureArrive(reservationDetails.getDateHeureArrive());
        if (reservationDetails.getIdHotel() != null) r.setIdHotel(reservationDetails.getIdHotel());
        boolean ok = backOfficeApiService.updateReservationInBackOffice(id, r);
        return ok ? r : null;
    }
    
    /**
     * Supprimer une réservation
     */
    public void deleteReservation(Integer id) {
        log.info("Suppression de la réservation avec l'ID: {}", id);
        backOfficeApiService.deleteReservationInBackOffice(id);
    }
    
    /**
     * Enrichir les réservations avec les données du BackOffice (client et hôtel)
     */
    private void enrichReservations(List<Reservation> reservations) {
        for (Reservation reservation : reservations) {
            enrichReservation(reservation);
        }
    }
    
    /**
     * Enrichir une réservation avec les données du BackOffice
     */
    private void enrichReservation(Reservation reservation) {
        try {
            if (reservation.getIdClient() != null) {
                reservation.setClient(backOfficeApiService.getClientFromBackOffice(reservation.getIdClient()));
            }
            if (reservation.getIdHotel() != null) {
                reservation.setHotel(backOfficeApiService.getHotelFromBackOffice(reservation.getIdHotel()));
            }
        } catch (Exception e) {
            log.warn("Erreur lors de l'enrichissement de la réservation ID {}: {}", reservation.getId(), e.getMessage());
        }
    }
}
