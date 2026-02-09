package com.sprint.frontoffice.repository;

import com.sprint.frontoffice.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour l'entité Reservation avec méthodes de recherche
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    
    /**
     * Chercher les réservations par client
     */
    List<Reservation> findByIdClient(String idClient);
    
    /**
     * Chercher les réservations par hôtel
     */
    List<Reservation> findByIdHotel(Integer idHotel);
    
    /**
     * Chercher les réservations dans une plage de dates
     */
    @Query("SELECT r FROM Reservation r WHERE r.dateHeureArrive >= :dateDebut AND r.dateHeureArrive <= :dateFin")
    List<Reservation> findByDateRange(@Param("dateDebut") LocalDateTime dateDebut, @Param("dateFin") LocalDateTime dateFin);
    
    /**
     * Chercher les réservations par client ET dans une plage de dates
     */
    @Query("SELECT r FROM Reservation r WHERE r.idClient = :idClient AND r.dateHeureArrive >= :dateDebut AND r.dateHeureArrive <= :dateFin")
    List<Reservation> findByIdClientAndDateRange(@Param("idClient") String idClient, @Param("dateDebut") LocalDateTime dateDebut, @Param("dateFin") LocalDateTime dateFin);
    
    /**
     * Chercher les réservations par hôtel ET dans une plage de dates
     */
    @Query("SELECT r FROM Reservation r WHERE r.idHotel = :idHotel AND r.dateHeureArrive >= :dateDebut AND r.dateHeureArrive <= :dateFin")
    List<Reservation> findByIdHotelAndDateRange(@Param("idHotel") Integer idHotel, @Param("dateDebut") LocalDateTime dateDebut, @Param("dateFin") LocalDateTime dateFin);
}
