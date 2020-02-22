DELIMITER //

CREATE OR REPLACE PROCEDURE `Image_RemoveOrphanBranches`
(
    in_image_id  VARCHAR(255),
    in_branchIds VARCHAR(255)
)
BEGIN

    DELETE FROM TagBranch WHERE `image_id` = in_image_id AND NOT FIND_IN_SET(`id`, in_branchIds);

END;
//

CREATE OR REPLACE VIEW `Image_View` AS (

    SELECT

       -- Key
       images.`id`         AS `ImageId`,
       images.`name`       AS `ImageName`,
       images.`repository` AS `RepositoryId`,
       repositories.`name` AS `RepositoryName`,

       -- Counts
       images.`pulls` AS `LatestPullCount`,
       images.`stars` AS `LatestStarCount`,

       -- Spec
       images.`sync_enabled` AS `SyncEnabled`,
       images.`version_mask` AS `VersionMask`,
       images.`hidden`       AS `Hidden`,
       images.`stable`       AS `Stable`,
       images.`deprecated`   AS `Deprecated`,

       -- General
       images.`description` AS `Description`,
       images.`modified`    AS `LastUpdated`,

       -- Core Meta
       meta.icon_url      AS `CoreMetaImagePath`,
       meta.base_image    AS `CoreMetaBaseImage`,
       meta.category      AS `CoreMetaCategory`

    FROM
       Image images
   JOIN
       Repository repositories ON repositories.`id` = images.`repository`
   LEFT JOIN
       ImageMetadata meta on meta.`image_id` = images.`id`
);
//

CREATE OR REPLACE PROCEDURE `Image_Store`
(
    in_id           INT,
    in_pulls        BIGINT,
    in_stars        INT,
    in_description  TEXT,
    in_modified     TIMESTAMP,
    in_deprecated   TINYINT,
    in_hidden       TINYINT,
    in_stable       TINYINT,
    in_synchronised TINYINT,
    in_version_mask VARCHAR(255),

    OUT out_status enum('Updated', 'NoChange')
)
BEGIN

    IF NOT EXISTS(SELECT `id` FROM Image WHERE `id` = in_id) THEN
        SET out_status = 'NoChange';
    ELSE

        UPDATE
            Image
        SET
            `pulls`        = in_pulls,
            `stars`        = in_stars,
            `description`  = in_description,
            `modified`     = in_modified,
            `deprecated`   = in_deprecated,
            `hidden`       = in_hidden,
            `stable`       = in_stable,
            `sync_enabled` = in_synchronised,
            `version_mask` = in_version_mask
        WHERE
            `id` = in_id;

        IF ROW_COUNT() <> 1 THEN
            SET out_status = 'NoChange';
        ELSE
            SET out_status = 'Updated';
        END IF;

        CALL Image_StorePullHistory(in_id, in_pulls, out_status);

        SELECT * FROM ImageKey_View WHERE `ImageId` = in_id;

    END IF;

END //

CREATE OR REPLACE PROCEDURE Image_StoreCoreMetaData
(
    in_id           INT,
    in_category     VARCHAR(255),
    in_base_image   VARCHAR(255),
    in_icon_url     VARCHAR(1000),

    OUT out_status ENUM('Inserted', 'Updated', 'NoChange')
)
BEGIN

    -- Only add core metadata if it has been provided with at least one value
    IF
    (
        in_category   IS NOT NULL OR
        in_base_image IS NOT NULL OR
        in_icon_url   IS NOT NULL
    )
    THEN

        IF NOT EXISTS(SELECT 1 FROM ImageMetadata WHERE `image_id` = in_id) THEN

            INSERT INTO ImageMetadata
            (
                `image_id`,
                `category`,
                `base_image`,
                `icon_url`
            )
            VALUES
            (
                in_id,
                in_category,
                in_base_image,
                in_icon_url
            );

            SET out_status = 'Inserted';

        ELSE

            UPDATE ImageMetadata
            SET
                `category`   = in_category,
                `base_image` = in_base_image,
                `icon_url`   = in_icon_url
            WHERE
                `image_id` = in_id;

            IF ROW_COUNT() = 0 THEN
                SET out_status = 'NoChange';
            ELSE
                SET out_status = 'Updated';
            END IF;

        END IF;

    END IF;

END;
//

CREATE TABLE ExternalUrl (
    `id`       INT NOT NULL auto_increment PRIMARY KEY,
    `image_id` INT NOT NULL,
    `type`     ENUM('Support', 'Application', 'Donation', 'Misc'),
    `name`     VARCHAR(255) NOT NULL,
    `path`     VARCHAR(1000),
    UNIQUE KEY (`image_id`, `id`),
    FOREIGN KEY (`image_id`) REFERENCES Image(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;
//

CREATE OR REPLACE PROCEDURE Image_StoreExternalUrl
(
    in_image_id INT,
    in_id       INT,
    in_type     ENUM('Support', 'Application', 'Donation', 'Misc'),
    in_name     VARCHAR(255),
    in_path     VARCHAR(1000)
)
BEGIN

    IF in_id IS NULL OR in_id = -1 THEN

        INSERT INTO ExternalUrl (image_id, type, name, path)
        VALUES
        (
            in_image_id,
            in_type,
            in_name,
            in_path
        );

    ELSE

        UPDATE ExternalUrl
        SET
            `type` = in_type,
            `name` = in_name,
            `path` = in_path
        WHERE
            `image_id` = in_image_id AND `id` = in_id;

    END IF;

END;
//

CREATE OR REPLACE PROCEDURE Image_GetExternalUrls
(
    in_id INT
)
BEGIN

    SELECT
        `id`   AS UrlId,
        `type` AS UrlType,
        `name` AS `UrlName`,
        `path` AS `UrlPath`
    FROM
         ExternalUrl
    WHERE
        `image_id` = in_id;

END;
//

CREATE OR REPLACE PROCEDURE `Image_RemoveOrphanUrls`
(
    in_image_id  VARCHAR(255),
    in_urlIds    VARCHAR(255)
)
BEGIN

    DELETE FROM ExternalUrl WHERE `image_id` = in_image_id AND NOT FIND_IN_SET(`id`, in_urlIds);

END;
//

DELIMITER ;
