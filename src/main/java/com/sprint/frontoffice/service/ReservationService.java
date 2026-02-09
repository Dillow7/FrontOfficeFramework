package com.sprint.frontoffice.service;

import com.sprint.frontoffice.entity.Reservation;
import com.sprint.frontoffice.repository.ReservationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service métier pour les réservations
 */
@Service
@Slf4j
public class ReservationService {
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private BackOfficeApiService backOfficeApiService;
    
    /**
     * Récupérer toutes les réservations
     */
    public List<Reservation> getAllReservations() {
        log.info("Récupération de toutes les réservations");
        List<Reservation> reservations = reservationRepository.findAll();
        enrichReservations(reservations);
        return reservations;
    }
    
    /**
     * Récupérer une réservation par ID
     */
    public Optional<Reservation> getReservationById(Integer id) {
        log.info("Récupération de la réservation avec l'ID: {}", id);
        Optional<Reservation> reservation = reservationRepository.findById(id);
        if (reservation.isPresent()) {
            enrichReservation(reservation.get());
        }
        return reservation;
    }
    
    /**
     * Récupérer les réservations par client
     */
    public List<Reservation> getReservationsByClient(String idClient) {
        log.info("Récupération des réservations pour le client: {}", idClient);
        List<Reservation> reservations = reservationRepository.findByIdClient(idClient);
        enrichReservations(reservations);
        return reservations;
    }
    
    /**
     * Récupérer les réservations par hôtel
     */
    public List<Reservation> getReservationsByHotel(Integer idHotel) {
        log.info("Récupération des réservations pour l'hôtel: {}", idHotel);
        List<Reservation> reservations = reservationRepository.findByIdHotel(idHotel);
        enrichReservations(reservations);
        return reservations;
    }
    
    /**
     * Récupérer les réservations dans une plage de dates
     */
    public List<Reservation> getReservationsByDateRange(LocalDateTime dateDebut, LocalDateTime dateFin) {
        log.info("Récupération des réservations entre {} et {}", dateDebut, dateFin);
        List<Reservation> reservations = reservationRepository.findByDateRange(dateDebut, dateFin);
        enrichReservations(reservations);
        return reservations;
    }
    
    /**
     * Récupérer les réservations filtrées par client ET date
     */
    public List<Reservation> searchReservations(String idClient, Integer idHotel, LocalDateTime dateDebut, LocalDateTime dateFin) {
        log.info("Recherche des réservations avec filtres - Client: {}, Hôtel: {}, Dates: {} à {}", 
                 idClient, idHotel, dateDebut, dateFin);
        
        List<Reservation> reservations;
        
        if (idClient != null && !idClient.isEmpty() && dateDebut != null && dateFin != null) {
            reservations = reservationRepository.findByIdClientAndDateRange(idClient, dateDebut, dateFin);
        } else if (idHotel != null && dateDebut != null && dateFin != null) {
            reservations = reservationRepository.findByIdHotelAndDateRange(idHotel, dateDebut, dateFin);
        } else if (idClient != null && !idClient.isEmpty()) {
            reservations = reservationRepository.findByIdClient(idClient);
        } else if (idHotel != null) {
            reservations = reservationRepository.findByIdHotel(idHotel);
        } else if (dateDebut != null && dateFin != null) {
            reservations = reservationRepository.findByDateRange(dateDebut, dateFin);
        } else {
            reservations = reservationRepository.findAll();
        }
        
        enrichReservations(reservations);
        return reservations;
    }
    
    /**
     * Créer une nouvelle réservation
     */
    public Reservation createReservation(Reservation reservation) {
        log.info("Création d'une nouvelle réservation");
        return reservationRepository.save(reservation);
    }
    
    /**
     * Mettre à jour une réservation
     */
    public Reservation updateReservation(Integer id, Reservation reservationDetails) {
        log.info("Mise à jour de la réservation avec l'ID: {}", id);
        Optional<Reservation> reservation = reservationRepository.findById(id);
        if (reservation.isPresent()) {
            Reservation r = reservation.get();
            if (reservationDetails.getIdClient() != null) r.setIdClient(reservationDetails.getIdClient());
            if (reservationDetails.getNbPassager() != null) r.setNbPassager(reservationDetails.getNbPassager());
            if (reservationDetails.getDateHeureArrive() != null) r.setDateHeureArrive(reservationDetails.getDateHeureArrive());
            if (reservationDetails.getIdHotel() != null) r.setIdHotel(reservationDetails.getIdHotel());
            return reservationRepository.save(r);
        }
        return null;
    }
    
    /**
     * Supprimer une réservation
     */
    public void deleteReservation(Integer id) {
        log.info("Suppression de la réservation avec l'ID: {}", id);
        reservationRepository.deleteById(id);
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
