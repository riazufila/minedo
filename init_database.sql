-- Create database (and drop if exists).
DROP DATABASE IF EXISTS minestorm;
CREATE DATABASE minestorm;

-- Select database.
USE minestorm;

-- Create tables.
CREATE TABLE astral_gear (
    id int NOT NULL AUTO_INCREMENT,
    material varchar(50) NOT NULL,
    display_name varchar(50) NOT NULL,
    color varchar(50) DEFAULT NULL,
    decoration varchar(50) DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE astral_gear_lore (
    id int NOT NULL AUTO_INCREMENT,
    text varchar(250) NOT NULL,
    color varchar(50) DEFAULT NULL,
    decoration varchar(50) DEFAULT NULL,
    astral_gear_id int NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (astral_gear_id) REFERENCES astral_gear (id)
);

CREATE TABLE astral_gear_enchantment (
    id int NOT NULL AUTO_INCREMENT,
    enchantment varchar(50) NOT NULL,
    level int NOT NULL,
    astral_gear_id int NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (astral_gear_id) REFERENCES astral_gear (id)
);

CREATE TABLE astral_gear_attribute (
    id int NOT NULL AUTO_INCREMENT,
    attribute varchar(50) NOT NULL,
    modifier double NOT NULL,
    operation varchar(50) NOT NULL,
    slot varchar(20) NOT NULL,
    astral_gear_id int NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (astral_gear_id) REFERENCES astral_gear (id)
);

CREATE TABLE astral_gear_probability (
    astral_gear_id int NOT NULL,
    probability double NOT NULL,
    UNIQUE KEY astral_gear_id_UNIQUE (astral_gear_id),
    FOREIGN KEY (astral_gear_id) REFERENCES astral_gear (id)
);

-- Insert values.
INSERT INTO astral_gear VALUES
(1,'DIAMOND_HELMET','Moon Knight\'s Helmet','#008B8B','BOLD'),
(2,'DIAMOND_CHESTPLATE','Moon Knight\'s Chestplate','#008B8B','BOLD'),
(3,'DIAMOND_LEGGINGS','Moon Knight\'s Leggings','#008B8B','BOLD'),
(4,'DIAMOND_BOOTS','Moon Knight\'s Boots','#008B8B','BOLD'),
(5,'DIAMOND_SWORD','Moon Knight\'s Sword','#008B8B','BOLD');

INSERT INTO astral_gear_lore VALUES
(1,'A helmet adorned with lunar symbols providing protection under the moonlight.','#008B8B',NULL,1),
(2,'A sturdy chestplate infused with the essence of the moon, granting fearlessness in the darkest of nights.','#008B8B',NULL,2),
(3,'Leggings imbued with the power of the moon, granting agility and enhanced movement.','#008B8B',NULL,3),
(4,'Boots crafted with lunar energy, allowing the wearer to move silently like the moon\'s shadow while under water.','#008B8B',NULL,4),
(5,'A mighty blade forged under the moon\'s glow. Cuts through darkness and empowers its wielder. Unleashes swift strikes. Harnesses the lunar energy to dispel evil.','#008B8B',NULL,5);

INSERT INTO astral_gear_enchantment VALUES
(1,'protection',5,1),
(2,'respiration',1,1),
(3,'protection',7,2),
(4,'mending',1,2),
(5,'mending',1,1),
(6,'protection',6,3),
(7,'mending',1,3),
(8,'protection',5,4),
(9,'depth_strider',3,4),
(10,'mending',1,4),
(11,'sharpness',6,5),
(12,'mending',1,5);

INSERT INTO astral_gear_attribute VALUES
(1,'GENERIC_KNOCKBACK_RESISTANCE',0.1,'MULTIPLY_SCALAR_1','CHEST',2),
(2,'GENERIC_MAX_HEALTH',0.1,'MULTIPLY_SCALAR_1','CHEST',2),
(3,'GENERIC_ARMOR',3,'ADD_NUMBER','CHEST',2),
(4,'GENERIC_ARMOR_TOUGHNESS',2,'ADD_NUMBER','CHEST',2);

INSERT INTO astral_gear_probability VALUES
(1,100),
(2,15),
(3,30),
(4,100),
(5,10);
