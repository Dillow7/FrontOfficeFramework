package com.sprint.frontoffice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.frontoffice.dto.VehicleDTO;
import com.sprint.frontoffice.entity.Client;
import com.sprint.frontoffice.entity.Hotel;
import com.sprint.frontoffice.entity.Reservation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

/**
 * Service pour consommer l'API du BackOffice
 */
@Service
@Slf4j
public class BackOfficeApiService {
    
    @Value("${backoffice.api.baseurl}")
    private String baseUrl;

    @Value("${backoffice.api.token}")
    private String apiToken;

    @Value("${backoffice.api.timeout:30000}")
    private int timeoutMs;
    
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @PostConstruct
    public void initRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeoutMs);
        factory.setReadTimeout(timeoutMs);
        this.restTemplate = new RestTemplate(factory);
        log.info("BackOfficeApiService RestTemplate initialized with timeoutMs={}", timeoutMs);
    }

    public List<VehicleDTO> getAllVehiclesFromBackOffice() {
        try {
            String url = baseUrl + "/vehicules?token=" + apiToken;
            log.info("Appel API BackOffice: {}", url);
            String json = getForString(url);
            try {
                JsonNode dataNode = extractDataNode(json);
                return objectMapper.readerFor(new TypeReference<List<VehicleDTO>>() {}).readValue(dataNode);
            } catch (Exception ex) {
                log.error("JSON véhicules invalide (extrait): {}", snippet(json));
                throw ex;
            }
        } catch (ResourceAccessException e) {
            log.error("BackOffice injoignable (getAllVehicles): {}", e.getMessage());
            throw e;
        } catch (HttpClientErrorException e) {
            log.error("Erreur API BackOffice (getAllVehicles): status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        } catch (RestClientException e) {
            log.error("Erreur réseau lors de la récupération des véhicules: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors du parsing des véhicules: {}", e.getMessage());
            throw new RestClientException(e.getMessage(), e);
        }
    }
    
    /**
     * Récupérer tous les hôtels du BackOffice
     */
    public List<Hotel> getAllHotelsFromBackOffice() {
        try {
            String url = baseUrl + "/hotels";
            log.info("Appel API BackOffice: {}", url);
            String json = getForString(url);
            List<Map<String, Object>> items;
            try {
                JsonNode dataNode = extractDataNode(json);
                items = objectMapper.readerFor(new TypeReference<List<Map<String, Object>>>() {}).readValue(dataNode);
            } catch (Exception ex) {
                log.error("JSON hôtels invalide (extrait): {}", snippet(json));
                throw ex;
            }
            return items.stream().map(this::mapToHotel).toList();
        } catch (ResourceAccessException e) {
            log.error("BackOffice injoignable (getAllHotels): {}", e.getMessage());
            return List.of();
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
            String url = baseUrl + "/hotels/by-id?id=" + hotelId;
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
            String json = getForString(url);
            List<Map<String, Object>> items;
            try {
                JsonNode dataNode = extractDataNode(json);
                items = objectMapper.readerFor(new TypeReference<List<Map<String, Object>>>() {}).readValue(dataNode);
            } catch (Exception ex) {
                log.error("JSON clients invalide (extrait): {}", snippet(json));
                throw ex;
            }
            return items.stream().map(this::mapToClient).toList();
        } catch (ResourceAccessException e) {
            log.error("BackOffice injoignable (getAllClients): {}", e.getMessage());
            return List.of();
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
            String url = baseUrl + "/clients/by-id?id=" + clientId;
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
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("id", client.getId());
            form.add("nom", client.getNom());
            form.add("prenom", client.getPrenom());
            form.add("telephone", client.getTelephone());
            form.add("email", client.getEmail());
            HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(form, headers);
            ResponseEntity<Client> resp = restTemplate.postForEntity(url, req, Client.class);
            return resp.getBody();
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
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("nom", hotel.getNom());
            HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(form, headers);
            ResponseEntity<Hotel> resp = restTemplate.postForEntity(url, req, Hotel.class);
            return resp.getBody();
        } catch (Exception e) {
            log.error("Erreur lors de la création de l'hôtel: {}", e.getMessage());
            return null;
        }
    }

    public List<Reservation> getAllReservationsFromBackOffice() {
        try {
            String url = baseUrl + "/reservations";
            log.info("Appel API BackOffice: {}", url);
            String json = getForString(url);
            List<Map<String, Object>> items;
            try {
                JsonNode dataNode = extractDataNode(json);
                items = objectMapper.readerFor(new TypeReference<List<Map<String, Object>>>() {}).readValue(dataNode);
            } catch (Exception ex) {
                log.error("JSON réservations invalide (extrait): {}", snippet(json));
                throw ex;
            }
            return items.stream().map(this::mapToReservation).toList();
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des réservations: {}", e.getMessage());
            return List.of();
        }
    }

    public Reservation getReservationFromBackOffice(Integer reservationId) {
        try {
            String url = baseUrl + "/reservations/by-id?id=" + reservationId;
            log.info("Appel API BackOffice: {}", url);
            String json = getForString(url);
            JsonNode dataNode = extractDataNode(json);
            if (dataNode.isMissingNode() || dataNode.isNull()) {
                return null;
            }
            Map<String, Object> item = objectMapper.convertValue(dataNode, new TypeReference<Map<String, Object>>() {});
            return mapToReservation(item);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de la réservation {}: {}", reservationId, e.getMessage());
            return null;
        }
    }

    private JsonNode extractDataNode(String json) throws Exception {
        JsonNode root = objectMapper.readTree(json);
        if (root == null) {
            return objectMapper.getNodeFactory().missingNode();
        }
        if (root.isArray()) {
            return root;
        }
        if (root.isObject()) {
            JsonNode data = root.get("data");
            return data != null ? data : objectMapper.getNodeFactory().missingNode();
        }
        return objectMapper.getNodeFactory().missingNode();
    }

    private String getForString(String url) {
        try {
            ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            String body = resp.getBody();
            return sanitizeJson(body != null ? body : "");
        } catch (HttpClientErrorException e) {
            log.error("Erreur HTTP ({}): status={}, body={}", url, e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        } catch (RestClientException e) {
            log.error("Erreur réseau ({}): {}", url, e.getMessage());
            throw e;
        }
    }

    private String sanitizeJson(String raw) {
        if (raw == null) {
            return "";
        }
        String s = raw;
        if (!s.isEmpty() && s.charAt(0) == '\uFEFF') {
            s = s.substring(1);
        }
        s = s.trim();

        int obj = s.indexOf('{');
        int arr = s.indexOf('[');
        int start;
        if (obj == -1 && arr == -1) {
            return s;
        }
        if (obj == -1) {
            start = arr;
        } else if (arr == -1) {
            start = obj;
        } else {
            start = Math.min(obj, arr);
        }
        return s.substring(start);
    }

    private String snippet(String s) {
        if (s == null) {
            return "<null>";
        }
        int max = 500;
        String oneLine = s.replace("\r", " ").replace("\n", " ");
        return oneLine.length() <= max ? oneLine : oneLine.substring(0, max) + "...";
    }

    public boolean createReservationInBackOffice(Reservation reservation) {
        return postReservationForm(baseUrl + "/reservations", null, reservation);
    }

    public boolean updateReservationInBackOffice(Integer id, Reservation reservation) {
        return postReservationForm(baseUrl + "/reservations/update", id, reservation);
    }

    public boolean deleteReservationInBackOffice(Integer id) {
        try {
            String url = baseUrl + "/reservations/delete";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("token", apiToken);
            form.add("id", String.valueOf(id));
            HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(form, headers);
            ResponseEntity<Map> resp = restTemplate.postForEntity(url, req, Map.class);
            return resp.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.error("Erreur lors de la suppression de la réservation {}: {}", id, e.getMessage());
            return false;
        }
    }

    private boolean postReservationForm(String url, Integer id, Reservation reservation) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("token", apiToken);
            if (id != null) {
                form.add("id", String.valueOf(id));
            }
            form.add("id_client", reservation.getIdClient());
            form.add("nb_passager", String.valueOf(reservation.getNbPassager()));
            form.add("date_heure_arrive", reservation.getDateHeureArrive().toString());
            form.add("id_hotel", String.valueOf(reservation.getIdHotel()));

            HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(form, headers);
            ResponseEntity<Map> resp = restTemplate.postForEntity(url, req, Map.class);
            return resp.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi réservation vers BackOffice: {}", e.getMessage());
            return false;
        }
    }

    private Reservation mapToReservation(Map item) {
        Reservation r = new Reservation();
        Object id = item.get("id");
        if (id instanceof Number) {
            r.setId(((Number) id).intValue());
        }
        r.setIdClient(item.get("idClient") != null ? item.get("idClient").toString() : null);
        Object nbPass = item.get("nbPassager");
        if (nbPass instanceof Number) {
            r.setNbPassager(((Number) nbPass).intValue());
        } else if (nbPass != null) {
            r.setNbPassager(Integer.parseInt(nbPass.toString()));
        }
        Object dt = item.get("dateHeureArrive");
        if (dt != null) {
            r.setDateHeureArrive(LocalDateTime.parse(dt.toString()));
        }
        Object hid = item.get("idHotel");
        if (hid instanceof Number) {
            r.setIdHotel(((Number) hid).intValue());
        } else if (hid != null) {
            r.setIdHotel(Integer.parseInt(hid.toString()));
        }
        return r;
    }

    private Hotel mapToHotel(Map item) {
        Hotel h = new Hotel();
        Object id = item.get("id");
        if (id instanceof Number) {
            h.setId(((Number) id).intValue());
        } else if (id != null) {
            h.setId(Integer.parseInt(id.toString()));
        }
        h.setNom(item.get("nom") != null ? item.get("nom").toString() : null);
        return h;
    }

    private Client mapToClient(Map item) {
        Client c = new Client();
        c.setId(item.get("id") != null ? item.get("id").toString() : null);
        c.setNom(item.get("nom") != null ? item.get("nom").toString() : null);
        c.setPrenom(item.get("prenom") != null ? item.get("prenom").toString() : null);
        c.setTelephone(item.get("telephone") != null ? item.get("telephone").toString() : null);
        Object email = item.get("email");
        c.setEmail(email != null ? email.toString() : null);
        return c;
    }
}
