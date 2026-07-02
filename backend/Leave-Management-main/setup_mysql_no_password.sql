-- =====================================================
-- Configuration MySQL pour Connexion Sans Mot de Passe
-- =====================================================

-- Créer/Modifier l'utilisateur root pour accepter connexion sans mot de passe
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '';

-- Donner tous les privilèges
GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost' WITH GRANT OPTION;

-- Appliquer les changements
FLUSH PRIVILEGES;

-- Créer la base de données
CREATE DATABASE IF NOT EXISTS conges_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Confirmer
SELECT 'Configuration terminée! Base de données créée.' AS Status;

