DELIMITER //

CREATE TABLE ImageMetadata (
    `image_id`      INT             NOT NULL,
    `beta`          BIT             NOT NULL DEFAULT 0,
    `category`      VARCHAR(255)    DEFAULT NULL,
    `changes`       VARCHAR(1000)   DEFAULT NULL,
    `support`       VARCHAR(500)    DEFAULT NULL,
    `app_url`       VARCHAR(500)    DEFAULT NULL,
    `description`   VARCHAR(1000)   DEFAULT NULL,
    `base_image`    VARCHAR(255)    DEFAULT NULL,
    `icon_url`      VARCHAR(1000)   DEFAULT NULL,
    PRIMARY KEY (`image_id`),
    FOREIGN KEY (`image_id`) REFERENCES Image(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;
//

CREATE TABLE ImageTemplateBase (
    `image_id`      INT             NOT NULL,
    `url`           VARCHAR(255)    DEFAULT NULL,
    `restart`       ENUM('no', 'always', 'unless-stopped', 'on-failure') NOT NULL,
    `host_network`  BIT             DEFAULT 0,
    `privileged`    BIT             DEFAULT 0,
    PRIMARY KEY (`image_id`),
    FOREIGN KEY (`image_id`) REFERENCES Image(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;
//

CREATE TABLE ImageTemplateEnvironment (
    `image_id`      INT             NOT NULL,
    `env_key`       VARCHAR(100)    NOT NULL,
    `description`   VARCHAR(255)    NOT NULL,
    PRIMARY KEY (`image_id`, `env_key`),
    FOREIGN KEY (`image_id`) REFERENCES Image(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;
//

CREATE TABLE ImageTemplateExtra (
    `image_id`      INT             NOT NULL,
    `extra_key`     VARCHAR(100)    NOT NULL,
    `description`   VARCHAR(255)    NOT NULL,
    PRIMARY KEY (`image_id`, `extra_key`),
    FOREIGN KEY (`image_id`) REFERENCES Image(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;
//

CREATE TABLE ImageTemplateDevices (
    `image_id`      INT             NOT NULL,
    `device`        VARCHAR(100)    NOT NULL,
    `description`   VARCHAR(255)    NOT NULL,
    PRIMARY KEY (`image_id`, `device`),
    FOREIGN KEY (`image_id`) REFERENCES Image(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;
//

CREATE TABLE ImageTemplateVolumes (
    `image_id`      INT             NOT NULL,
    `volume`        VARCHAR(255)    DEFAULT NULL,
    `description`   VARCHAR(255)    NOT NULL,
    `read_only`     BIT             DEFAULT 0,
    PRIMARY KEY (`image_id`, `volume`),
    FOREIGN KEY (`image_id`) REFERENCES Image(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;
//

CREATE TABLE ImageTemplatePorts (
    `image_id`      INT                 NOT NULL,
    `port`          SMALLINT            NOT NULL,
    `description`   VARCHAR(255)        NOT NULL,
    `protocol`      ENUM('tcp', 'udp')  DEFAULT 'tcp',
    PRIMARY KEY (`image_id`, `port`),
    FOREIGN KEY (`image_id`) REFERENCES Image(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;
//

CREATE OR REPLACE PROCEDURE `Image_ClearMetaData`
(
    in_id   INT,
    in_current_volumes TEXT,
    in_current_ports   TEXT,
    in_current_env     TEXT,
    in_current_caps    TEXT,
    in_current_devices TEXT
)
BEGIN

    -- Deletes only orphans. Deletion will not reclaim space, so this should only remove items whenever
    -- one of them has legimitately been removed, rather than clearing and reinserting each time.
    DELETE FROM ImageTemplateVolumes     WHERE `image_id` = in_id AND NOT FIND_IN_SET(`volume`,    in_current_volumes);
    DELETE FROM ImageTemplatePorts       WHERE `image_id` = in_id AND NOT FIND_IN_SET(`port`,      in_current_ports);
    DELETE FROM ImageTemplateEnvironment WHERE `image_id` = in_id AND NOT FIND_IN_SET(`env_key`,   in_current_env);
    DELETE FROM ImageTemplateExtra       WHERE `image_id` = in_id AND NOT FIND_IN_SET(`extra_key`, in_current_caps);
    DELETE FROM ImageTemplateDevices     WHERE `image_id` = in_id AND NOT FIND_IN_SET(`device`,    in_current_devices);

END;
//
