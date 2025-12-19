-- Créer la base de données grpc_demo avec charset utf8mb4
CREATE DATABASE IF NOT EXISTS grpc_demo
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Utiliser la base de données
USE grpc_demo;

-- Créer la table user avec les colonnes id, name, email
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insérer les données de test (Alice Dupont, Bob Martin)
INSERT INTO `user` (`name`, `email`) VALUES
('Alice Dupont', 'alice.dupont@example.com'),
('Bob Martin', 'bob.martin@example.com')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);
