DELIMITER //

ALTER TABLE ImageTemplateEnvironment
    ADD COLUMN `example` VARCHAR(255);
//

CREATE OR REPLACE PROCEDURE `Image_StoreTemplateEnv`
(
    in_image_id    INT,
    in_env_key     VARCHAR(100),
    in_env_desc    VARCHAR(255),
    in_env_example VARCHAR(255)
)
BEGIN

    IF EXISTS(SELECT 1 FROM `ImageTemplateEnvironment` WHERE `image_id` = in_image_id AND `env_key` = in_env_key) THEN

        UPDATE `ImageTemplateEnvironment`
        SET
            `description` = in_env_desc,
            `example`     = in_env_example
        WHERE
            `image_id` = in_image_id AND `env_key` = in_env_key;

    ELSE

        INSERT INTO `ImageTemplateEnvironment`
        (
            `image_id`,
            `env_key`,
            `description`,
            `example`
        )
        VALUES
        (
            in_image_id,
            in_env_key,
            in_env_desc,
            in_env_example
        );

    END IF;

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
        `example`     AS `ItemSecondary`
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
