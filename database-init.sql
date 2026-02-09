-- ========================================
-- SCRIPT D'INITIALISATION DE LA BASE DE DONNÉES
-- Système de Gestion de Réservations
-- ========================================

-- ===== CRÉATION DES TABLES =====

-- Table HOTEL
CREATE TABLE IF NOT EXISTS hotel (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE
);

-- Table CLIENT
CREATE TABLE IF NOT EXISTS client (
    id CHAR(4) PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    telephone VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(150) UNIQUE
);

-- Table RESERVATION
CREATE TABLE IF NOT EXISTS reservation (
    id SERIAL PRIMARY KEY,
    idClient CHAR(4) NOT NULL,
    nbPassager INT CHECK (nbPassager > 0),
    dateHeureArrive TIMESTAMP NOT NULL,
    idHotel INT NOT NULL,
    FOREIGN KEY (idClient) REFERENCES client(id) ON DELETE RESTRICT,
    FOREIGN KEY (idHotel) REFERENCES hotel(id) ON DELETE RESTRICT
);

-- ===== CRÉATION DES INDEX =====
CREATE INDEX IF NOT EXISTS idx_reservation_client ON reservation(idClient);
CREATE INDEX IF NOT EXISTS idx_reservation_hotel ON reservation(idHotel);
CREATE INDEX IF NOT EXISTS idx_reservation_date ON reservation(dateHeureArrive);
CREATE INDEX IF NOT EXISTS idx_client_email ON client(email);
CREATE INDEX IF NOT EXISTS idx_client_telephone ON client(telephone);

-- ===== INSERTION DE DONNÉES TEST =====

-- Insertion d'hôtels exemples
INSERT INTO hotel (nom) VALUES
('Hilton Paris Orly Airport'),
('Holiday Inn Express Paris CDG'),
('Novotel Paris Aéroport'),
('Sofitel Paris La Défense'),
('Marriott Paris Opera'),
('Hyatt Centric Paris Madeleine'),
('Renaissance Paris Vendôme'),
('Four Seasons Hotel George V')
ON CONFLICT DO NOTHING;

-- Insertion de clients test
INSERT INTO client (id, nom, prenom, telephone, email) VALUES
('A001', 'Dupont', 'Jean', '+33601234567', 'jean.dupont@email.com'),
('A002', 'Martin', 'Marie', '+33602345678', 'marie.martin@email.com'),
('A003', 'Bernard', 'Pierre', '+33603456789', 'pierre.bernard@email.com'),
('B001', 'Dubois', 'Sophie', '+33604567890', 'sophie.dubois@email.com'),
('B002', 'Laurent', 'Philippe', '+33605678901', 'philippe.laurent@email.com'),
('C001', 'Petit', 'Anne', '+33606789012', 'anne.petit@email.com'),
('C002', 'Durand', 'Luc', '+33607890123', 'luc.durand@email.com'),
('D001', 'Lefevre', 'Claire', '+33608901234', 'claire.lefevre@email.com')
ON CONFLICT DO NOTHING;

-- Insertion de réservations test
INSERT INTO reservation (id_client, nb_passager, date_heure_arrive, id_hotel) VALUES
('A001', 2, '2026-02-10 14:30:00', 1),
('A002', 4, '2026-02-11 10:15:00', 2),
('A003', 1, '2026-02-12 16:45:00', 3),
('B001', 3, '2026-02-13 12:00:00', 4),
('B002', 2, '2026-02-14 09:30:00', 5),
('C001', 5, '2026-02-15 18:00:00', 6),
('C002', 2, '2026-02-16 11:20:00', 7),
('D001', 1, '2026-02-17 15:45:00', 8)
ON CONFLICT DO NOTHING;

-- ===== VÉRIFICATION =====
SELECT 'Hotels' as table_name, COUNT(*) as count FROM hotel
UNION ALL
SELECT 'Clients', COUNT(*) FROM client
UNION ALL
SELECT 'Reservations', COUNT(*) FROM reservation;
