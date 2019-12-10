DELIMITER //

CREATE TABLE Repository (
    `id`                INT             NOT NULL auto_increment PRIMARY KEY,
    `name`              VARCHAR(255)    NOT NULL,
    `sync_enabled`      TINYINT         NOT NULL DEFAULT 1,
    `version_mask`      VARCHAR(255)    DEFAULT NULL,
    `hidden`            TINYINT         NOT NULL DEFAULT 0,
    `stable`            TINYINT         NOT NULL DEFAULT 1,
    `deprecated`        TINYINT         NOT NULL DEFAULT 0,
    `modified`          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY (`name`)
) ENGINE=InnoDB;
//

CREATE TABLE Image (
    `id`                        INT             NOT NULL auto_increment PRIMARY KEY,
    `repository`                INT             NOT NULL,
    `name`                      VARCHAR(255)    NOT NULL,
    `description`               TEXT            DEFAULT NULL,
    `pulls`                     BIGINT          DEFAULT 0,
    `stars`                     BIGINT          DEFAULT 0,
    `latest_version`            VARCHAR(255)    DEFAULT NULL,
    `sync_enabled`              TINYINT         NOT NULL DEFAULT 1,
    `version_mask`              VARCHAR(255)    DEFAULT NULL,
    `hidden`                    TINYINT         NOT NULL DEFAULT 0,
    `stable`                    TINYINT         NOT NULL DEFAULT 1,
    `deprecated`                TINYINT         NOT NULL DEFAULT 0,
    `modified`                  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY (`repository`, `name`),
    FOREIGN KEY (`repository`) REFERENCES Repository(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;
//

CREATE TABLE TagBranch (
    `id`             INT NOT NULL auto_increment PRIMARY KEY,
    `image_id`       INT NOT NULL,
    `name`           VARCHAR(255) NOT NULL,
    `latest_version` VARCHAR(255) NOT NULL DEFAULT 'Unknown',
    `protected`      TINYINT NOT NULL DEFAULT 0,
    `build_date`     TIMESTAMP DEFAULT NULL,
    UNIQUE KEY (`image_id`, `name`),
    FOREIGN KEY (`image_id`) REFERENCES Image(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;
//

CREATE TABLE TagDigest (
    `branch_id` INT NOT NULL,
    `size`      BIGINT NOT NULL,
    `arch`      VARCHAR(100) NOT NULL,
    `digest`    VARCHAR(255) NOT NULL,
    `variant`   VARCHAR(50) DEFAULT NULL,
    UNIQUE KEY (`branch_id`, `digest`),
    FOREIGN KEY (`branch_id`) REFERENCES TagBranch(`id`)
) ENGINE=InnoDB;
//

CREATE OR REPLACE VIEW `ImageKey_View` AS (

    SELECT

        image_view.`ImageId`,
        image_view.`ImageName`,
        image_view.`RepositoryId`,
        image_view.`RepositoryName`

    FROM
        Image_View image_view
);
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
        images.`modified`    AS `LastUpdated`

    FROM
        Image images
    JOIN
        Repository repositories ON repositories.`id` = images.`repository`
);
//

CREATE OR REPLACE VIEW `TagBranch_View` AS (

    SELECT
        `image_id`       AS `ImageId`,
        `id`             AS `BranchId`,
        `name`           AS `BranchName`,
        `latest_version` AS `TagVersion`,
        `protected`      AS `BranchProtected`,
        `build_date`     AS `TagBuildDate`
    FROM
        TagBranch
);
//

CREATE OR REPLACE VIEW `TagDigest_View` AS (

    SELECT
        tag_branch.`image_id` AS `ImageId`,
        tag_branch.`branch_id AS `BranchId`,
        tag_digest.`size`     AS `DigestSize`,
        tag_digest.`digest`   AS `DigestSha`,
        tag_digest.`arch`     AS `DigestArch`,
        tag_digest.`variant`  AS `DigestVariant`,
    FROM
        TagDigest tag_digest
    JOIN
        TagBranch tag_branch ON tag_branch.`id` = tag_digest.`branch_id`
);
//

CREATE OR REPLACE PROCEDURE `Image_GetTagBranches`
(
    in_image_id INT
)
BEGIN

    -- Top level branch info
    SELECT *
    FROM
        TagBranch_View
    WHERE
        `image_id` = in_image_id;

    -- All digests for the given tag in the branch.
    SELECT *
    FROM
        TagDigest_View tag_digest
    WHERE
        tag_digest.`ImageId` = in_image_id;

END;
//

CREATE OR REPLACE PROCEDURE `Image_CreateTagBranchOutline`
(
    in_image_id    INT,
    in_branch_name VARCHAR(255)
)
BEGIN

    INSERT INTO TagBranch (`image_id`, `name`)
    VALUES
    (
        in_image_id,
        in_branch_name
    );

    -- Top level branch info
    SELECT *
    FROM
        TagBranch_View
    WHERE
        `BranchId` = LAST_INSERT_ID();

END;
//

CREATE OR REPLACE PROCEDURE `Image_StoreTagDigest`
(
    in_branch_id INT,
    in_size      BIGINT,
    in_digest    VARCHAR(255)
    in_arch      VARCHAR(255),
    in_variant   VARCHAR(100),

    OUT out_status enum('Inserted', 'NoChange')
)
BEGIN

    IF EXISTS(SELECT `branch_id` FROM TagDigest WHERE `branch_id` = in_branch_id AND `digest` = in_digest) THEN

        -- Digests are immutable and cannot be changed. Only new digest can be added.
        SET out_status = 'NoChange';

    ELSE

        INSERT INTO TagDigest (`branch_id`, `size`, `digest`, `arch`, `variant`)
        VALUES
        (
            in_branch_id,
            in_size,
            in_digest,
            in_arch,
            in_variant
        );

        SET out_status = 'Inserted';

    END IF;

END;
//

CREATE OR REPLACE PROCEDURE `Image_Get`
(
    in_id   INT
)
BEGIN

    SELECT * FROM Image_View image_view WHERE image_view.`ImageId` = in_id;

END;
//

CREATE OR REPLACE PROCEDURE `Image_Delete`
(
    in_id   INT,

    OUT out_status enum('Updated', 'NoChange')
)
BEGIN

    IF EXISTS(SELECT `id` FROM ImagesV2 WHERE `id` = in_id`) THEN

        DELETE FROM ImagesV2 WHERE `id` = in_id;
        SET out_status = 'Updated';

    ELSE
        SET out_status = 'NoChange';
    END IF;

END;
//

CREATE OR REPLACE PROCEDURE `Image_CreateOutline`
(
    in_repository  INT,
    in_name        VARCHAR(255),
    in_description TEXT,
    in_modified    TIMESTAMP,

    OUT out_status enum('Inserted', 'NoChange', 'Exists')
)
BEGIN

    IF EXISTS(SELECT `id` FROM ImagesV2 WHERE `id` = in_id`) THEN
        SET out_status = 'Exists';
    ELSE

        INSERT INTO ImagesV2 (`repository`, `name`, `description`, `modified`)
        VALUES
        (
            in_repository,
            in_name,
            in_description,
            in_modified
        );

        SELECT * FROM ImageKey_View image_key WHERE image_key.`ImageId` = LAST_INSERT_ID();

    END IF;

END;
//