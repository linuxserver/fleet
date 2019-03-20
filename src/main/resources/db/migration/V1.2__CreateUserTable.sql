CREATE TABLE Users (
    `id`                INT             NOT NULL auto_increment PRIMARY KEY,
    `username`          VARCHAR(255)    NOT NULL,
    `password`          VARCHAR(255)    DEFAULT NULL,
    `modified`          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY (`username`)
) ENGINE=InnoDB;