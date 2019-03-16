DELIMITER //

CREATE PROCEDURE `Repository_Save`
(
    in_id               INT,
    in_name             VARCHAR(255),
    in_version_mask     VARCHAR(255),
    in_sync_enabled     TINYINT,

    OUT out_id      INT,
    OUT out_status  INT,
    OUT out_message VARCHAR(100)
)
BEGIN

    IF in_id IS NULL THEN

        INSERT INTO Repositories
        (
            `name`,
            `version_mask`,
            `sync_enabled`
        )
        VALUES
        (
            in_name,
            in_version_mask,
            in_sync_enabled
        );

        SET out_id = LAST_INSERT_ID();

    ELSE

        UPDATE Repositories
        SET
            `name`          = in_name,
            `version_mask`  = in_version_mask,
            `sync_enabled`  = in_sync_enabled
        WHERE
            `id` = in_id;

        SET out_id = in_id;

    END IF;

    SET out_status = 0;
    SET out_message = 'OK';

END;
//

CREATE PROCEDURE `Repository_Get`
(
    in_id   INT
)
BEGIN

    SELECT
        `id`            AS `RepositoryId`,
        `name`          AS `RepositoryName`,
        `version_mask`  AS `RepositoryVersionMask`,
        `sync_enabled`  AS `SyncEnabled`,
        `modified`      AS `ModifiedTime`
    FROM
        Repositories
    WHERE
        `id` = in_id;

END;
//

CREATE PROCEDURE `Repository_GetByName`
(
    in_name   VARCHAR(255)
)
BEGIN

    SELECT
        `id`            AS `RepositoryId`,
        `name`          AS `RepositoryName`,
        `version_mask`  AS `RepositoryVersionMask`,
        `sync_enabled`  AS `SyncEnabled`,
        `modified`      AS `ModifiedTime`
    FROM
        Repositories
    WHERE
        `name` = in_name;
END;
//

CREATE PROCEDURE `Repository_Delete`
(
    in_id   INT
)
BEGIN

    DELETE FROM Images          WHERE `repository` = in_id;
    DELETE FROM Repositories    WHERE `id` = in_id;

END;
//

CREATE PROCEDURE `Repository_GetAll` ()
BEGIN

    SELECT
        `id`            AS `RepositoryId`,
        `name`          AS `RepositoryName`,
        `version_mask`  AS `RepositoryVersionMask`,
        `sync_enabled`  AS `SyncEnabled`,
        `modified`      AS `ModifiedTime`
    FROM
        Repositories
    ORDER BY
        `name` ASC;

END;
//

CREATE PROCEDURE `Image_Get`
(
    in_id   INT
)
BEGIN

    SELECT
        images.`id`                                                 AS `ImageId`,
        images.`repository`                                         AS `RepositoryId`,
        images.`name`                                               AS `ImageName`,
        images.`pulls`                                              AS `ImagePullCount`,
        images.`latest_version`                                     AS `ImageVersion`,
        COALESCE(images.`version_mask`, repos.`version_mask`)       AS `ImageVersionMask`,
        images.`hidden`                                             AS `ImageHidden`,
        images.`unstable`                                           AS `ImageUnstable`,
        images.`modified`                                           AS `ModifiedTime`
    FROM
        Images images
    INNER JOIN
        Repositories repos ON repos.`id` = images.`repository`
    WHERE
        images.`id` = in_id;

END;
//

CREATE PROCEDURE `Image_GetByName`
(
    in_repo_id      INT,
    in_image_name   VARCHAR(255)
)
BEGIN

    SELECT
        images.`id`                                                 AS `ImageId`,
        images.`repository`                                         AS `RepositoryId`,
        images.`name`                                               AS `ImageName`,
        images.`pulls`                                              AS `ImagePullCount`,
        images.`latest_version`                                     AS `ImageVersion`,
        COALESCE(images.`version_mask`, repos.`version_mask`)       AS `ImageVersionMask`,
        images.`hidden`                                             AS `ImageHidden`,
        images.`unstable`                                           AS `ImageUnstable`,
        images.`modified`                                           AS `ModifiedTime`
    FROM
        Images images
    INNER JOIN
        Repositories repos ON repos.`id` = images.`repository`
    WHERE
        images.`name` = in_image_name AND repos.`id` = in_repo_id;

END;
//

CREATE PROCEDURE `Image_Save`
(
    in_id           INT,
    in_repository   INT,
    in_name         VARCHAR(255),
    in_pull_count   BIGINT,
    in_version      VARCHAR(100),
    in_version_mask VARCHAR(255),
    in_hidden       TINYINT,
    in_unstable     TINYINT,

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
            `unstable`
        )
        VALUES
        (
            in_repository,
            in_name,
            in_pull_count,
            in_version,
            in_version_mask,
            in_hidden,
            in_unstable
        );

        SET out_id = LAST_INSERT_ID();

    ELSE

        UPDATE Images
        SET
            `name`              = in_name,
            `pulls`             = in_pull_count,
            `latest_version`    = in_version,
            `version_mask`      = in_version_mask,
            `hidden`            = in_hidden,
            `unstable`          = in_unstable
        WHERE
            `id` = in_id;

        SET out_id = in_id;

    END IF;

    SET out_status  = 0;
    SET out_message = "OK";

END;
//

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
        images.`id`                                                 AS `ImageId`,
        images.`repository`                                         AS `RepositoryId`,
        images.`name`                                               AS `ImageName`,
        images.`pulls`                                              AS `ImagePullCount`,
        images.`latest_version`                                     AS `ImageVersion`,
        COALESCE(images.`version_mask`, repos.`version_mask`)       AS `ImageVersionMask`,
        images.`hidden`                                             AS `ImageHidden`,
        images.`unstable`                                           AS `ImageUnstable`,
        images.`modified`                                           AS `ModifiedTime`
    FROM
        Images images
    INNER JOIN
        Repositories repos ON repos.`id` = images.`repository`
    WHERE
        images.`repository` = in_repository
    ORDER BY
        images.`name` ASC;

END;
//

DELIMITER ;
