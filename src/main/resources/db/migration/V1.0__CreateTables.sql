CREATE TABLE Repositories (
    `id`                INT             NOT NULL auto_increment PRIMARY KEY,
    `name`              VARCHAR(255)    NOT NULL,
    `version_mask`      VARCHAR(255)    DEFAULT NULL,
    `sync_enabled`      TINYINT         NOT NULL DEFAULT 0,
    `modified`          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY (`name`)
) ENGINE=InnoDB;

CREATE TABLE Images (
    `id`                        INT             NOT NULL auto_increment PRIMARY KEY,
    `repository`                INT             NOT NULL,
    `name`                      VARCHAR(255)    NOT NULL,
    `pulls`                     BIGINT          DEFAULT NULL,
    `latest_version`            VARCHAR(100)    DEFAULT NULL,
    `version_mask`              VARCHAR(255)    DEFAULT NULL,
    `hidden`                    TINYINT         NOT NULL DEFAULT 0,
    `unstable`                  TINYINT         NOT NULL DEFAULT 0,
    `modified`                  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY (`repository`, `name`),
    FOREIGN KEY (`repository`)  REFERENCES Repositories(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;