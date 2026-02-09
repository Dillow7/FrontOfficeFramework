package com.sprint.frontoffice.repository;

import com.sprint.frontoffice.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository pour l'entité Client
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
}
