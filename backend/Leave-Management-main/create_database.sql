-- =====================================================
-- Script de Création de la Base de Données MySQL
-- Pour le Système de Gestion des Congés
-- =====================================================

-- Créer la base de données
CREATE DATABASE IF NOT EXISTS conges_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Utiliser la base
USE conges_db;

-- Afficher confirmation
SELECT 'Base de données conges_db créée avec succès!' AS status;

-- =====================================================
-- Optionnel: Créer un utilisateur dédié
-- =====================================================
-- Décommenter les lignes ci-dessous pour créer un utilisateur spécifique

-- CREATE USER IF NOT EXISTS 'conges_user'@'localhost' IDENTIFIED BY 'conges_password';
-- GRANT ALL PRIVILEGES ON conges_db.* TO 'conges_user'@'localhost';
-- FLUSH PRIVILEGES;
-- SELECT 'Utilisateur conges_user créé avec succès!' AS status;

-- =====================================================
-- Vérification
-- =====================================================
SHOW DATABASES LIKE 'conges_db';

