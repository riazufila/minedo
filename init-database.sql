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
    player_id INT NOT NULL,
    prefix_preset VARCHAR(20) DEFAULT NULL,
    prefix_custom VARCHAR(7) DEFAULT NULL,
    content_preset VARCHAR(20) DEFAULT NULL,
    content_custom VARCHAR(7) DEFAULT NULL,
    UNIQUE KEY player_id_UNIQUE (player_id),
    FOREIGN KEY (player_id) REFERENCES player_profile (id)
);

CREATE TABLE player_home (
    player_id INT NOT NULL,
    name VARCHAR(20) NOT NULL,
    world_type VARCHAR(20) NOT NULL,
    coordinate_x DOUBLE DEFAULT NULL,
    coordinate_y DOUBLE DEFAULT NULL,
    coordinate_z DOUBLE DEFAULT NULL,
    CONSTRAINT player_id_name_UNIQUE UNIQUE (player_id, name),
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

CREATE TABLE custom_item (
    id INT NOT NULL AUTO_INCREMENT,
    material VARCHAR(50) NOT NULL,
    display_name VARCHAR(50) NOT NULL,
    color VARCHAR(20) DEFAULT NULL,
    decoration varchar(10) DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE custom_item_lore (
    custom_item_id INT NOT NULL,
    text VARCHAR(250) NOT NULL,
    color VARCHAR(20) DEFAULT NULL,
    decoration VARCHAR(10) DEFAULT NULL,
    FOREIGN KEY (custom_item_id) REFERENCES custom_item (id)
);

CREATE TABLE custom_item_enchantment (
    custom_item_id INT NOT NULL,
    enchantment varchar(50) NOT NULL,
    is_custom TINYINT(1) NOT NULL,
    level INT NOT NULL,
    FOREIGN KEY (custom_item_id) REFERENCES custom_item (id)
);

CREATE TABLE custom_item_probability (
    custom_item_id INT NOT NULL,
    probability DOUBLE NOT NULL,
    UNIQUE KEY custom_item_id_UNIQUE (custom_item_id),
    FOREIGN KEY (custom_item_id) REFERENCES custom_item (id)
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
INSERT INTO custom_item VALUES
(1, 'IRON_BOOTS', 'Rabbit\'s Foot', 'GOLD', NULL),
(2, 'IRON_AXE', 'Mjolnir', 'DARK_PURPLE', NULL),
(3, 'DIAMOND_HOE', 'Death\'s Scythe', 'DARK_PURPLE', NULL),
(4, 'IRON_HELMET', 'Iron Helmet', NULL, 'OBFUSCATED'),
(5, 'IRON_CHESTPLATE', 'Iron Chestplate', NULL, 'OBFUSCATED'),
(6, 'IRON_LEGGINGS', 'Iron Leggings', NULL, 'OBFUSCATED'),
(7, 'IRON_BOOTS', 'Iron Boots', NULL, 'OBFUSCATED'),
(8, 'IRON_SWORD', 'Iron Sword', NULL, 'OBFUSCATED'),
(9, 'SHIELD', 'Shield', NULL, 'OBFUSCATED'),
(10, 'MAGMA_CREAM', 'Bomberman', 'DARK_PURPLE', NULL);

INSERT INTO custom_item_lore VALUES
(1, 'Boots infused with the agility of a rabbit.', 'GREEN', NULL),
(2, 'The legendary weapon of the thunder God.', 'DARK_PURPLE', NULL),
(3, 'A weapon by Death, itself.', 'DARK_PURPLE', NULL),
(10, 'Suicidal attack.', 'DARK_PURPLE', NULL);

INSERT INTO custom_item_enchantment VALUES
(1, 'PROTECTION', false, 5),
(1, 'MENDING', false, 1),
(1, 'UNBREAKING', false, 10),
(1, 'HEALTH_BOOST', true, 2),
(1, 'SPEED', true, 1),
(2, 'SHARPNESS', false, 7),
(2, 'MENDING', false, 1),
(2, 'UNBREAKING', false, 10),
(2, 'LIGHTNING', true, 1),
(2, 'WEAKNESS', true, 3),
(3, 'SHARPNESS', false, 9),
(3, 'MENDING', false, 1),
(3, 'UNBREAKING', false, 10),
(3, 'POISON', true, 5),
(3, 'BLINDNESS', true, 5),
(10, 'ARROW_FIRE', false, 1),
(10, 'EXPLOSION', true, 1);

INSERT INTO custom_item_probability VALUES
(1, 20),
(2, 10),
(3, 10),
(4, 100),
(5, 100),
(6, 100),
(7, 100),
(8, 100),
(9, 100),
(10, 15);

INSERT INTO region VALUES
(1, 'Spawn', 'world', -128, 127, -128, 127);
