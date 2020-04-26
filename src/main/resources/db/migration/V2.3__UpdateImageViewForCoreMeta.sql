DELIMITER //

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
       meta.category      AS `CoreMetaCategory`,
       meta.support       AS `CoreMetaSupportUrl`,
       meta.app_url       AS `CoreMetaAppUrl`

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
    in_category     VARCHAR(255),
    in_support      VARCHAR(500),
    in_app_url      VARCHAR(500),
    in_base_image   VARCHAR(255),
    in_icon_url     VARCHAR(1000),

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

        -- Only add core metadata if it has been provided with at least one value
        IF
        (
            in_category IS NOT NULL OR
            in_support IS NOT NULL OR
            in_app_url IS NOT NULL OR
            in_base_image IS NOT NULL OR
            in_icon_url IS NOT NULL
        )
        THEN

            IF NOT EXISTS(SELECT 1 FROM ImageMetadata WHERE `image_id` = in_id) THEN

                INSERT INTO ImageMetadata
                (
                    `image_id`,
                    `category`,
                    `support`,
                    `app_url`,
                    `base_image`,
                    `icon_url`
                )
                VALUES
                (
                    in_id,
                    in_category,
                    in_support,
                    in_app_url,
                    in_base_image,
                    in_icon_url
                );

            ELSE

                UPDATE ImageMetadata
                SET
                    `category`   = in_category,
                    `support`    = in_support,
                    `app_url`    = in_app_url,
                    `base_image` = in_base_image,
                    `icon_url`   = in_icon_url
                WHERE
                    `image_id` = in_id;

            END IF;

        END IF;

        IF ROW_COUNT() <> 1 THEN
            SET out_status = 'NoChange';
        ELSE
            SET out_status = 'Updated';
        END IF;

        CALL Image_StorePullHistory(in_id, in_pulls, out_status);

        SELECT * FROM ImageKey_View WHERE `ImageId` = in_id;

    END IF;

END //
