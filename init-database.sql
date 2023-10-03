-- Create database (and drop if exists).
DROP DATABASE IF EXISTS minedo;
CREATE DATABASE minedo;

-- Select database.
USE minedo;

-- Create tables.
CREATE TABLE player_profile (
    id INT NOT NULL AUTO_INCREMENT,
    uuid VARCHAR(36) NOT NULL,
    nickname VARCHAR(20) DEFAULT NULL,
    UNIQUE KEY nickname_UNIQUE (nickname),
    PRIMARY KEY (id)
);

CREATE TABLE player_color (
    player_id int NOT NULL,
    prefix_preset VARCHAR(20) DEFAULT NULL,
    prefix_custom VARCHAR(7) DEFAULT NULL,
    content_preset VARCHAR(20) DEFAULT NULL,
    content_custom VARCHAR(7) DEFAULT NULL,
    UNIQUE KEY player_id_UNIQUE (player_id),
    FOREIGN KEY (player_id) REFERENCES player_profile (id)
);

CREATE TABLE player_home (
    player_id DOUBLE NOT NULL,
    name VARCHAR(20) NOT NULL,
    coordinate_x DOUBLE DEFAULT NULL,
    coordinate_y DOUBLE DEFAULT NULL,
    coordinate_z DOUBLE DEFAULT NULL,
    FOREIGN KEY (player_id) REFERENCES player_profile (id)
);

CREATE TABLE player_like (
    player_id int NOT NULL,
    like_received_count INT DEFAULT 0,
    like_sent_count INT DEFAULT 0,
    last_like_sent TIMESTAMP DEFAULT NULL,
    UNIQUE KEY player_id_UNIQUE (player_id),
    FOREIGN KEY (player_id) REFERENCES player_profile (id)
);

CREATE TABLE player_blocked (
    player_id INT NOT NULL,
    blocked_player_id INT NOT NULL,
    CONSTRAINT player_id_blocked_player_id_UNIQUE UNIQUE (player_id, blocked_player_id),
    FOREIGN KEY (player_id) REFERENCES player_profile (id),
    FOREIGN KEY (blocked_player_id) REFERENCES player_profile (id)
);

CREATE TABLE better_item (
    id INT NOT NULL AUTO_INCREMENT,
    material VARCHAR(50) NOT NULL,
    display_name VARCHAR(50) NOT NULL,
    color VARCHAR(7) DEFAULT NULL,
    decoration varchar(10) DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE better_item_lore (
    id INT NOT NULL AUTO_INCREMENT,
    text VARCHAR(250) NOT NULL,
    color VARCHAR(7) DEFAULT NULL,
    decoration VARCHAR(10) DEFAULT NULL,
    better_item_id INT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (better_item_id) REFERENCES better_item (id)
);

CREATE TABLE better_item_enchantment (
    id INT NOT NULL AUTO_INCREMENT,
    enchantment varchar(50) NOT NULL,
    level INT NOT NULL,
    better_item_id INT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (better_item_id) REFERENCES better_item (id)
);

CREATE TABLE better_item_attribute (
    id INT NOT NULL AUTO_INCREMENT,
    attribute VARCHAR(50) NOT NULL,
    modifier DOUBLE NOT NULL,
    operation VARCHAR(50) NOT NULL,
    slot VARCHAR(20) NOT NULL,
    better_item_id INT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (better_item_id) REFERENCES better_item (id)
);

CREATE TABLE better_item_probability (
    better_item_id INT NOT NULL,
    probability DOUBLE NOT NULL,
    UNIQUE KEY better_item_id_UNIQUE (better_item_id),
    FOREIGN KEY (better_item_id) REFERENCES better_item (id)
);

CREATE TABLE region (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(20) NOT NULL,
    world_type VARCHAR(20) NOT NULL,
    minX INT NOT NULL,
    maxX INT NOT NULL,
    minZ INT NOT NULL,
    maxZ INT NOT NULL,
    PRIMARY KEY (id)
);

-- Insert values.
INSERT INTO better_item VALUES
(1, 'DIAMOND_HELMET', 'Moon Knight\'s Helmet', '#008B8B', 'BOLD'),
(2, 'DIAMOND_CHESTPLATE', 'Moon Knight\'s Chestplate', '#008B8B', 'BOLD'),
(3, 'DIAMOND_LEGGINGS', 'Moon Knight\'s Leggings', '#008B8B', 'BOLD'),
(4, 'DIAMOND_BOOTS', 'Moon Knight\'s Boots', '#008B8B', 'BOLD'),
(5, 'DIAMOND_SWORD', 'Moon Knight\'s Sword', '#008B8B', 'BOLD');

INSERT INTO better_item_lore VALUES
(1, 'A helmet adorned with lunar symbols providing protection under the moonlight.', '#008B8B', NULL, 1),
(2, 'A sturdy chestplate infused with the essence of the moon, granting fearlessness in the darkest of nights.', '#008B8B', NULL, 2),
(3, 'Leggings imbued with the power of the moon, granting agility and enhanced movement.', '#008B8B', NULL, 3),
(4, 'Boots crafted with lunar energy, allowing the wearer to move silently like the moon\'s shadow while under water.', '#008B8B', NULL, 4),
(5, 'A mighty blade forged under the moon\'s glow. Cuts through darkness and empowers its wielder. Unleashes swift strikes. Harnesses the lunar energy to dispel evil.', '#008B8B', NULL, 5);

INSERT INTO better_item_enchantment VALUES
(1, 'protection', 5, 1),
(2, 'respiration', 1, 1),
(3, 'protection', 7, 2),
(4, 'mending', 1, 2),
(5, 'mending', 1, 1),
(6, 'protection', 6, 3),
(7, 'mending', 1, 3),
(8, 'protection', 5, 4),
(9, 'depth_strider', 3, 4),
(10, 'mending', 1, 4),
(11, 'sharpness', 6, 5),
(12, 'mending', 1, 5);

INSERT INTO better_item_attribute VALUES
(1, 'GENERIC_KNOCKBACK_RESISTANCE', 0.1, 'MULTIPLY_SCALAR_1', 'CHEST', 2),
(2, 'GENERIC_MAX_HEALTH', 0.1, 'MULTIPLY_SCALAR_1', 'CHEST', 2),
(3, 'GENERIC_ARMOR', 3, 'ADD_NUMBER', 'CHEST', 2),
(4, 'GENERIC_ARMOR_TOUGHNESS', 2, 'ADD_NUMBER', 'CHEST', 2);

INSERT INTO better_item_probability VALUES
(1, 100),
(2, 15),
(3, 30),
(4, 100),
(5, 10);

INSERT INTO region VALUES
(1, 'Spawn', 'world', -128, 127, -128, 127);
