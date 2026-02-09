package com.sprint.frontoffice.repository;

import com.sprint.frontoffice.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository pour l'entité Hotel
 */
@Repository
public interface HotelRepository extends JpaRepository<Hotel, Integer> {
}
