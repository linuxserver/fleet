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
    `description`   VARCHAR(255)    DEFAULT NULL,
    PRIMARY KEY (`image_id`, `env_key`),
    FOREIGN KEY (`image_id`) REFERENCES Image(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;
//

CREATE TABLE ImageTemplateExtra (
    `image_id`      INT             NOT NULL,
    `extra_key`     VARCHAR(100)    NOT NULL,
    `description`   VARCHAR(255)    DEFAULT NULL,
    PRIMARY KEY (`image_id`, `extra_key`),
    FOREIGN KEY (`image_id`) REFERENCES Image(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;
//

CREATE TABLE ImageTemplateDevices (
    `image_id`      INT             NOT NULL,
    `device`        VARCHAR(100)    NOT NULL,
    `description`   VARCHAR(255)    DEFAULT NULL,
    PRIMARY KEY (`image_id`, `device`),
    FOREIGN KEY (`image_id`) REFERENCES Image(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;
//

CREATE TABLE ImageTemplateVolumes (
    `image_id`      INT             NOT NULL,
    `volume`        VARCHAR(255)    NOT NULL,
    `description`   VARCHAR(255)    DEFAULT NULL,
    `read_only`     BIT             DEFAULT 0,
    PRIMARY KEY (`image_id`, `volume`),
    FOREIGN KEY (`image_id`) REFERENCES Image(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;
//

CREATE TABLE ImageTemplatePorts (
    `image_id`      INT                 NOT NULL,
    `port`          INT                 NOT NULL,
    `description`   VARCHAR(255)        DEFAULT NULL,
    `protocol`      ENUM('tcp', 'udp')  DEFAULT 'tcp',
    PRIMARY KEY (`image_id`, `port`),
    FOREIGN KEY (`image_id`) REFERENCES Image(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;
//

CREATE OR REPLACE PROCEDURE `Image_StoreTemplateBase`
(
    in_image_id        INT,
    in_url             VARCHAR(255),
    in_restart_policy  ENUM('no', 'always', 'unless-stopped', 'on-failure'),
    in_host_network    BIT,
    in_privileged_mode BIT,

    OUT out_status ENUM('NoChange', 'Updated', 'Inserted')
)
BEGIN

    DECLARE var_rows_affected INT;

    IF EXISTS(SELECT 1 FROM `ImageTemplateBase` WHERE `image_id` = in_image_id) THEN

        UPDATE `ImageTemplateBase`
        SET
            `url`          = in_url,
            `restart`      = in_restart_policy,
            `host_network` = in_host_network,
            `privileged`   = in_privileged_mode
        WHERE
            `image_id` = in_image_id;

        SET var_rows_affected = ROW_COUNT();
        IF ROW_COUNT() = 0 THEN
            SET out_status = 'NoChange';
        ELSE
            SET out_status = 'Updated';
        END IF;

    ELSE

        INSERT INTO `ImageTemplateBase`
        (
            `image_id`,
            `url`,
            `restart`,
            `host_network`,
            `privileged`
        )
        VALUES
        (
            in_image_id,
            in_url,
            in_restart_policy,
            in_host_network,
            in_privileged_mode
        );

        SET out_status = 'Inserted';

    END IF;

END //

CREATE OR REPLACE PROCEDURE `Image_ClearTemplateData`
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

CREATE OR REPLACE PROCEDURE `Image_StoreTemplateVolume`
(
    in_image_id    INT,
    in_volume_name VARCHAR(255),
    in_volume_desc VARCHAR(255),
    in_volume_ro   BIT
)
BEGIN

   IF EXISTS(SELECT 1 FROM `ImageTemplateVolumes` WHERE `image_id` = in_image_id AND `volume` = in_volume_name) THEN

        UPDATE
            `ImageTemplateVolumes`
        SET
            `description` = in_volume_desc,
            `read_only`   = in_volume_ro
        WHERE
            `image_id` = in_image_id AND `volume` = in_volume_name;

   ELSE

       INSERT INTO `ImageTemplateVolumes`
       (
            `image_id`,
            `volume`,
            `description`,
            `read_only`
        )
       VALUES
       (
            in_image_id,
            in_volume_name,
            in_volume_desc,
            in_volume_ro
       );

   END IF;

END //

CREATE OR REPLACE PROCEDURE `Image_StoreTemplatePort`
(
    in_image_id   INT,
    in_port_name  INT,
    in_port_desc  VARCHAR(255),
    in_port_proto ENUM('tcp', 'udp')
)
BEGIN

    IF EXISTS(SELECT 1 FROM `ImageTemplatePorts` WHERE `image_id` = in_image_id AND `port` = in_port_name) THEN

        UPDATE `ImageTemplatePorts`
        SET
            `description` = in_port_desc,
            `protocol`    = in_port_proto
        WHERE
            `image_id` = in_image_id AND `port` = in_port_name;

    ELSE

        INSERT INTO `ImageTemplatePorts`
        (
            `image_id`,
            `port`,
            `description`,
            `protocol`
        )
        VALUES
        (
            in_image_id,
            in_port_name,
            in_port_desc,
            in_port_proto
        );

    END IF;

END //

CREATE OR REPLACE PROCEDURE `Image_StoreTemplateEnv`
(
    in_image_id  INT,
    in_env_key   VARCHAR(100),
    in_env_desc  VARCHAR(255)
)
BEGIN

    IF EXISTS(SELECT 1 FROM `ImageTemplateEnvironment` WHERE `image_id` = in_image_id AND `env_key` = in_env_key) THEN

        UPDATE `ImageTemplateEnvironment`
        SET
            `description` = in_env_desc
        WHERE
            `image_id` = in_image_id AND `env_key` = in_env_key;

    ELSE

        INSERT INTO `ImageTemplateEnvironment`
        (
            `image_id`,
            `env_key`,
            `description`
        )
        VALUES
        (
            in_image_id,
            in_env_key,
            in_env_desc
        );

    END IF;

END //

CREATE OR REPLACE PROCEDURE `Image_StoreTemplateDevice`
(
    in_image_id     INT,
    in_device_name  VARCHAR(100),
    in_device_desc  VARCHAR(255)
)
BEGIN

    IF EXISTS(SELECT 1 FROM `ImageTemplateDevices` WHERE `image_id` = in_image_id AND `device` = in_device_name) THEN

        UPDATE `ImageTemplateDevices`
        SET
            `description` = in_device_desc
        WHERE
            `image_id` = in_image_id AND `device` = in_device_name;

    ELSE

        INSERT INTO `ImageTemplateDevices`
        (
            `image_id`,
            `device`,
            `description`
        )
        VALUES
        (
            in_image_id,
            in_device_name,
            in_device_desc
        );

    END IF;

END //

CREATE OR REPLACE PROCEDURE `Image_StoreTemplateExtra`
(
    in_image_id    INT,
    in_extra_key   VARCHAR(100),
    in_extra_desc  VARCHAR(255)
)
BEGIN

    IF EXISTS(SELECT 1 FROM `ImageTemplateExtra` WHERE `image_id` = in_image_id AND `extra_key` = in_extra_key) THEN

        UPDATE `ImageTemplateExtra`
        SET
            `description` = in_extra_desc
        WHERE
            `image_id` = in_image_id AND `extra_key` = in_extra_key;

    ELSE

        INSERT INTO `ImageTemplateExtra`
        (
            `image_id`,
            `extra_key`,
            `description`
        )
        VALUES
        (
            in_image_id,
            in_extra_key,
            in_extra_desc
        );

    END IF;

END //

CREATE OR REPLACE PROCEDURE `Image_GetTemplateBase`
(
    in_image_id INT
)
BEGIN

    -- BASE
    SELECT
       `url`          AS `RepositoryUrl`,
       `restart`      AS `RestartPolicy`,
       `host_network` AS `HostNetworkEnabled`,
       `privileged`   AS `PrivilegedMode`
    FROM
        `ImageTemplateBase`
    WHERE
        `image_id` = in_image_id;

END //

CREATE OR REPLACE PROCEDURE `Image_GetTemplates`
(
    in_image_id INT
)
BEGIN

    SELECT
        'Port'        AS `ItemType`,
        `port`        AS `ItemName`,
        `description` AS `ItemDescription`,
        `protocol`    AS `ItemSecondary`
    FROM
        `ImageTemplatePorts`
    WHERE
        `image_id` = in_image_id

    UNION ALL

    SELECT
        'Volume'      AS `ItemType`,
        `volume`      AS `ItemName`,
        `description` AS `ItemDescription`,
        `read_only`   AS `ItemSecondary`
    FROM
        `ImageTemplateVolumes`
    WHERE
        `image_id` = in_image_id

    UNION ALL

    SELECT
        'Env'         AS `ItemType`,
        `env_key`     AS `ItemName`,
        `description` AS `ItemDescription`,
        NULL          AS `ItemSecondary`
    FROM
        `ImageTemplateEnvironment`
    WHERE
        `image_id` = in_image_id

    UNION ALL

    SELECT
        'Device'      AS `ItemType`,
        `device`      AS `ItemName`,
        `description` AS `ItemDescription`,
        NULL          AS `ItemSecondary`
    FROM
        `ImageTemplateDevices`
    WHERE
        `image_id` = in_image_id

    UNION ALL

    SELECT
        'Extra'       AS `ItemType`,
        `extra_key`   AS `ItemName`,
        `description` AS `ItemDescription`,
        NULL          AS `ItemSecondary`
    FROM
        `ImageTemplateExtra`
    WHERE
        `image_id` = in_image_id;

END //
