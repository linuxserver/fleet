DELIMITER //

ALTER TABLE Images
ADD COLUMN `latest_version_raw`         VARCHAR(200)    DEFAULT NULL,
ADD COLUMN `latest_version_buildtime`   TIMESTAMP       NULL DEFAULT NULL;
//

CREATE OR REPLACE VIEW `Image_View` AS (

    SELECT
        images.`id`                        AS `ImageId`,
        images.`repository`                AS `RepositoryId`,
        images.`name`                      AS `ImageName`,
        images.`pulls`                     AS `ImagePullCount`,
        images.`latest_version`            AS `LatestTagVersion`,
        images.`latest_version_raw`        AS `LatestMaskedTagVersion`,
        images.`latest_version_buildtime`  AS `LatestTagBuildDate`,
        images.`version_mask`              AS `ImageVersionMask`,
        images.`hidden`                    AS `ImageHidden`,
        images.`unstable`                  AS `ImageUnstable`,
        images.`deprecated`                AS `ImageDeprecated`,
        images.`deprecation_reason`        AS `ImageDeprecationReason`,
        images.`modified`                  AS `ModifiedTime`
    FROM
        Images images
);
//

DROP PROCEDURE `Image_Save`//
CREATE PROCEDURE `Image_Save`
(
    in_id                   INT,
    in_repository           INT,
    in_name                 VARCHAR(255),
    in_pull_count           BIGINT,
    in_version              VARCHAR(100),
    in_version_mask         VARCHAR(255),
    in_hidden               TINYINT,
    in_unstable             TINYINT,
    in_deprecated           TINYINT,
    in_deprecation_reason   VARCHAR(255),
    in_version_raw          VARCHAR(200),
    in_version_buildtime    TIMESTAMP,

    OUT out_id      INT,
    OUT out_status  INT,
    OUT out_message VARCHAR(100)
)
BEGIN

    IF in_id IS NULL THEN

        INSERT INTO Images
        (
            `repository`,
            `name`,
            `pulls`,
            `latest_version`,
            `version_mask`,
            `hidden`,
            `unstable`,
            `deprecated`,
            `deprecation_reason`,
            `latest_version_raw`,
            `latest_version_buildtime`
        )
        VALUES
        (
            in_repository,
            in_name,
            in_pull_count,
            in_version,
            in_version_mask,
            in_hidden,
            in_unstable,
            in_deprecated,
            in_deprecation_reason,
            in_version_raw,
            in_version_buildtime
        );

        SET out_id      = LAST_INSERT_ID();
        SET out_status  = 0;
        SET out_message = 'OK';

    ELSE

        UPDATE Images
        SET
            `name`                     = in_name,
            `pulls`                    = in_pull_count,
            `latest_version`           = in_version,
            `version_mask`             = in_version_mask,
            `hidden`                   = in_hidden,
            `unstable`                 = in_unstable,
            `deprecated`               = in_deprecated,
            `deprecation_reason`       = in_deprecation_reason,
            `latest_version_raw`       = in_version_raw,
            `latest_version_buildtime` = in_version_buildtime
        WHERE
            `id` = in_id;

        SET out_id = in_id;

        CALL Image_SavePullHistory(out_id, in_pull_count, out_status, out_message);

    END IF;

END;
//

DROP PROCEDURE `Image_GetAll`//
CREATE PROCEDURE `Image_GetAll`
(
    in_repository       INT,

    OUT out_total_count INT
)
BEGIN

    SELECT
        COUNT(*)
    INTO
        out_total_count
    FROM
        Images
    WHERE
        `repository` = in_repository;

    SELECT
        *
    FROM
        Image_View images
    INNER JOIN
        Repositories repos ON repos.`id` = images.`RepositoryId`
    WHERE
        images.`RepositoryId` = in_repository
    ORDER BY
        images.`ImageName` ASC;

END;
//

DROP PROCEDURE `Image_Get`//
CREATE PROCEDURE `Image_Get`
(
    in_id   INT
)
BEGIN

    SELECT
        *
    FROM
        Image_View images
    WHERE
        images.`ImageId` = in_id;

END;
//

DROP PROCEDURE `Image_GetByName`//
CREATE PROCEDURE `Image_GetByName`
(
    in_repo_id      INT,
    in_image_name   VARCHAR(255)
)
BEGIN

    SELECT
        *
    FROM
        Image_View images
    INNER JOIN
        Repositories repos ON repos.`id` = images.`RepositoryId`
    WHERE
        images.`ImageName` = in_image_name AND repos.`id` = in_repo_id;

END;
//