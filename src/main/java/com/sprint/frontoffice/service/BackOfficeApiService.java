package com.sprint.frontoffice.service;

import com.sprint.frontoffice.entity.Client;
import com.sprint.frontoffice.entity.Hotel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import java.util.List;

/**
 * Service pour consommer l'API du BackOffice
 */
@Service
@Slf4j
public class BackOfficeApiService {
    
    @Value("${backoffice.api.baseurl}")
    private String baseUrl;
    
    private final RestTemplate restTemplate;
    
    public BackOfficeApiService() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * Récupérer tous les hôtels du BackOffice
     */
    public List<Hotel> getAllHotelsFromBackOffice() {
        try {
            String url = baseUrl + "/hotels";
            log.info("Appel API BackOffice: {}", url);
            Hotel[] hotels = restTemplate.getForObject(url, Hotel[].class);
            return hotels != null ? List.of(hotels) : List.of();
        } catch (HttpClientErrorException e) {
            log.error("Erreur API BackOffice (getAllHotels): {}", e.getMessage());
            return List.of();
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des hôtels: {}", e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Récupérer un hôtel spécifique du BackOffice
     */
    public Hotel getHotelFromBackOffice(Integer hotelId) {
        try {
            String url = baseUrl + "/hotels/" + hotelId;
            log.info("Appel API BackOffice: {}", url);
            return restTemplate.getForObject(url, Hotel.class);
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Hôtel non trouvé: {}", hotelId);
            return null;
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de l'hôtel {}: {}", hotelId, e.getMessage());
            return null;
        }
    }
    
    /**
     * Récupérer tous les clients du BackOffice
     */
    public List<Client> getAllClientsFromBackOffice() {
        try {
            String url = baseUrl + "/clients";
            log.info("Appel API BackOffice: {}", url);
            Client[] clients = restTemplate.getForObject(url, Client[].class);
            return clients != null ? List.of(clients) : List.of();
        } catch (HttpClientErrorException e) {
            log.error("Erreur API BackOffice (getAllClients): {}", e.getMessage());
            return List.of();
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des clients: {}", e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Récupérer un client spécifique du BackOffice
     */
    public Client getClientFromBackOffice(String clientId) {
        try {
            String url = baseUrl + "/clients/" + clientId;
            log.info("Appel API BackOffice: {}", url);
            return restTemplate.getForObject(url, Client.class);
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Client non trouvé: {}", clientId);
            return null;
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du client {}: {}", clientId, e.getMessage());
            return null;
        }
    }
    
    /**
     * Créer un client dans le BackOffice
     */
    public Client createClientInBackOffice(Client client) {
        try {
            String url = baseUrl + "/clients";
            log.info("POST API BackOffice: {}", url);
            return restTemplate.postForObject(url, client, Client.class);
        } catch (Exception e) {
            log.error("Erreur lors de la création du client: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Créer un hôtel dans le BackOffice
     */
    public Hotel createHotelInBackOffice(Hotel hotel) {
        try {
            String url = baseUrl + "/hotels";
            log.info("POST API BackOffice: {}", url);
            return restTemplate.postForObject(url, hotel, Hotel.class);
        } catch (Exception e) {
            log.error("Erreur lors de la création de l'hôtel: {}", e.getMessage());
            return null;
        }
    }
}
