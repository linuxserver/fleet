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
    FOREIGN KEY (`image_id`) REFERENCES Images(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;
//

CREATE TABLE ImageTemplateBase (
    `image_id`      INT             NOT NULL,
    `url`           VARCHAR(255)    DEFAULT NULL,
    `restart`       ENUM('no', 'always', 'unless-stopped', 'on-failure') NOT NULL,
    `host_network`  BIT             DEFAULT 0,
    `privileged`    BIT             DEFAULT 0,
    PRIMARY KEY (`image_id`),
    FOREIGN KEY (`image_id`) REFERENCES Images(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;
//

CREATE TABLE ImageTemplateEnvironment (
    `image_id`      INT             NOT NULL,
    `env_key`       VARCHAR(100)    NOT NULL,
    `env_value`     VARCHAR(255)    NOT NULL,
    PRIMARY KEY (`image_id`, `env_key`),
    FOREIGN KEY (`image_id`) REFERENCES Images(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;
//

CREATE TABLE ImageTemplateExtra (
    `image_id`      INT             NOT NULL,
    `extra_key`     VARCHAR(100)    NOT NULL,
    `extra_value`   VARCHAR(255)    NOT NULL,
    PRIMARY KEY (`image_id`, `extra_key`),
    FOREIGN KEY (`image_id`) REFERENCES Images(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;
//

CREATE TABLE ImageTemplateVolumes (
    `image_id`      INT             NOT NULL,
    `host_volume`   VARCHAR(500)    DEFAULT NULL,
    `cont_volume`   VARCHAR(500)    NOT NULL,
    `read_only`     BIT             DEFAULT 0,
    PRIMARY KEY (`image_id`, `cont_volume`),
    FOREIGN KEY (`image_id`) REFERENCES Images(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;
//

CREATE TABLE ImageTemplatePorts (
    `image_id`      INT                 NOT NULL,
    `host_port`     SMALLINT            DEFAULT NULL,
    `cont_port`     SMALLINT            NOT NULL,
    `protocol`      ENUM('tcp', 'udp')  DEFAULT 'tcp',
    PRIMARY KEY (`image_id`, `cont_port`),
    FOREIGN KEY (`image_id`) REFERENCES Images(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;
//

CREATE TABLE TemplateEngine (
    `id`                INT             NOT NULL auto_increment PRIMARY KEY,
    `name`              VARCHAR(100)    NOT NULL,
    `template`          TEXT            NOT NULL,
    `template_class`    VARCHAR(100)    NOT NULL
) ENGINE=InnoDB;
//