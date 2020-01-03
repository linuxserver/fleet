DELIMITER //

CREATE TABLE Repository (
    `id`                INT             NOT NULL auto_increment PRIMARY KEY,
    `name`              VARCHAR(255)    NOT NULL,
    `sync_enabled`      TINYINT         NOT NULL DEFAULT 1,
    `version_mask`      VARCHAR(255)    DEFAULT NULL,
    `hidden`            TINYINT         NOT NULL DEFAULT 0,
    `stable`            TINYINT         NOT NULL DEFAULT 1,
    `deprecated`        TINYINT         NOT NULL DEFAULT 0,
    `modified`          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
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
    `sync_enabled`              TINYINT         NOT NULL DEFAULT 1,
    `version_mask`              VARCHAR(255)    DEFAULT NULL,
    `hidden`                    TINYINT         NOT NULL DEFAULT 0,
    `stable`                    TINYINT         NOT NULL DEFAULT 1,
    `deprecated`                TINYINT         NOT NULL DEFAULT 0,
    `modified`                  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
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
    `build_date`     TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
    UNIQUE KEY (`image_id`, `name`),
    FOREIGN KEY (`image_id`) REFERENCES Image(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;
//

CREATE TABLE TagDigest (
    `branch_id` INT NOT NULL,
    `size`      BIGINT NOT NULL,
    `arch`      VARCHAR(100) NOT NULL,
    `digest`    VARCHAR(255) NOT NULL,
    `variant`   VARCHAR(50) DEFAULT CURRENT_TIMESTAMP(),
    UNIQUE KEY (`branch_id`, `digest`),
    FOREIGN KEY (`branch_id`) REFERENCES TagBranch(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;
//

CREATE TABLE Schedule (
    `id`         INT          NOT NULL auto_increment PRIMARY KEY,
    `name`       VARCHAR(100) NOT NULL,
    `java_class` VARCHAR(255) NOT NULL,
    `interval`   VARCHAR(50)  NOT NULL DEFAULT '1:hours',
    `delay`      VARCHAR(50)  NOT NULL DEFAULT '0:seconds'
) ENGINE=InnoDB;
//

CREATE TABLE AppSetting (
    `id`          INT          NOT NULL auto_increment PRIMARY KEY,
    `name`        VARCHAR(100) NOT NULL,
    `string_val`  VARCHAR(255) DEFAULT NULL,
    `int_val`     INT          DEFAULT NULL,
    `double_val`  DOUBLE(6,2)  DEFAULT NULL,
    `boolean_val` TINYINT      DEFAULT NULL,
    UNIQUE KEY (`name`)
) ENGINE=InnoDB;
//

ALTER TABLE Users
    ADD COLUMN `role` enum('Admin') DEFAULT 'Admin';
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

CREATE OR REPLACE VIEW `RepositoryKey_View` AS
(
    SELECT
        `id`     AS `RepositoryId`,
        `name`   AS `RepositoryName`
    FROM
        Repository
);
//

CREATE OR REPLACE VIEW `Repository_View` AS
(
    SELECT

        -- General
        `id`     AS `RepositoryId`,
        `name`   AS `RepositoryName`,

        -- Spec
        `sync_enabled` AS `SyncEnabled`,
        `version_mask` AS `VersionMask`,
        `hidden`       AS `Hidden`,
        `stable`       AS `Stable`,
        `deprecated`   AS `Deprecated`,
        `modified`     AS `LastUpdated`

    FROM
        Repository
);
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
        tag_branch.`image_id`  AS `ImageId`,
        tag_branch.`id`        AS `BranchId`,
        tag_digest.`size`      AS `DigestSize`,
        tag_digest.`digest`    AS `DigestSha`,
        tag_digest.`arch`      AS `DigestArch`,
        tag_digest.`variant`   AS `DigestVariant`
    FROM
        TagDigest tag_digest
    JOIN
        TagBranch tag_branch ON tag_branch.`id` = tag_digest.`branch_id`
);
//

CREATE OR REPLACE PROCEDURE `Repository_Get`
(
    in_id INT
)
BEGIN

    SELECT
        *
    FROM
         Repository_View
    WHERE
        `RepositoryId` = in_id;

END //

CREATE OR REPLACE PROCEDURE `Repository_GetRepositoryKeys` ()
BEGIN
    SELECT * FROM RepositoryKey_View;
END //

CREATE OR REPLACE PROCEDURE `Repository_GetImageKeys`
(
    in_id INT
)
BEGIN
    SELECT
        *
    FROM
         ImageKey_View
    WHERE
        `RepositoryId` = in_id;
END //

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
        `ImageId` = in_image_id;

END;
//

CREATE OR REPLACE PROCEDURE `Image_GetTagDigests`
(
    in_branch_id INT
)
BEGIN

    SELECT
        *
    FROM
        TagDigest_View tag_digest
    WHERE
        tag_digest.`BranchId` = in_branch_id;

END //

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

CREATE OR REPLACE PROCEDURE `Image_StoreTagBranch`
(
    in_image_id       INT,
    in_branch_id      INT,
    in_latest_version VARCHAR(255),
    in_build_date     TIMESTAMP
)
BEGIN

   UPDATE
       TagBranch
   SET
        `latest_version` = in_latest_version,
        `build_date`     = in_build_date
   WHERE
        `id` = in_branch_id
   AND
        `image_id` = in_image_id;

   -- Updating a tag branch should come hand-in-hand with updating digests.
   DELETE FROM TagDigest WHERE `branch_id` = in_branch_id;

END;
//

CREATE OR REPLACE PROCEDURE `Image_StoreTagDigest`
(
    in_branch_id INT,
    in_size      BIGINT,
    in_digest    VARCHAR(255),
    in_arch      VARCHAR(255),
    in_variant   VARCHAR(100)
)
BEGIN

    INSERT INTO TagDigest (`branch_id`, `size`, `digest`, `arch`, `variant`)
    VALUES
    (
        in_branch_id,
        in_size,
        in_digest,
        in_arch,
        in_variant
    );

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

    IF EXISTS(SELECT `id` FROM Image WHERE `id` = in_id) THEN

        DELETE FROM Image WHERE `id` = in_id;
        SET out_status = 'Updated';

    ELSE
        SET out_status = 'NoChange';
    END IF;

END //

CREATE OR REPLACE PROCEDURE `Repository_Delete`
(
    in_id   INT,

    OUT out_status enum('Updated', 'NoChange')
)
BEGIN

    IF EXISTS(SELECT `id` FROM Repository WHERE `id` = in_id) THEN

        DELETE FROM Repository WHERE `id` = in_id;
        SET out_status = 'Updated';

    ELSE
        SET out_status = 'NoChange';
    END IF;

END //

CREATE OR REPLACE PROCEDURE `Repository_Store`
(
    in_id           INT,
    in_synchronised TINYINT,
    in_version_mask VARCHAR(255),

    OUT out_status enum('NoChange', 'Updated')
)
BEGIN

    IF NOT(EXISTS(SELECT `id` FROM Repository WHERE `id` = in_id)) THEN
       SET out_status = 'NoChange';
    ELSE

        UPDATE
            Repository
        SET
            `sync_enabled` = in_synchronised,
            `version_mask` = in_version_mask
        WHERE
            `id` = in_id;

        SET out_status = 'Updated';

    END IF;

END //

CREATE OR REPLACE PROCEDURE `Repository_CreateOutline`
(
    in_name VARCHAR(255),
    in_modified     TIMESTAMP,
    in_deprecated   TINYINT,
    in_hidden       TINYINT,
    in_stable       TINYINT,
    in_synchronised TINYINT,
    in_version_mask VARCHAR(255),

    OUT out_status enum('Inserted', 'Exists')
)
BEGIN

    IF EXISTS(SELECT `id` FROM Repository WHERE `name` = in_name) THEN
        SET out_status = 'Exists';
    ELSE

        INSERT INTO Repository (`name`, `modified`, `deprecated`, `hidden`, `stable`, `sync_enabled`, `version_mask`)
        VALUES
        (
            in_name,
            in_modified,
            in_deprecated,
            in_hidden,
            in_stable,
            in_synchronised,
            in_version_mask
        );

        SET out_status = 'Inserted';

        SELECT * FROM RepositoryKey_View WHERE RepositoryId = LAST_INSERT_ID();

    END IF;

END //

CREATE OR REPLACE PROCEDURE `Image_StorePullHistory`
(
    in_image_id     INT,
    in_image_pulls  BIGINT,

    OUT out_status  ENUM('Updated', 'NoChange')
)
BEGIN

    IF EXISTS(SELECT 1 FROM Image WHERE `id` = in_image_id) THEN

        INSERT INTO ImagePullHistory
        (
            `image_id`,
            `pull_timestamp`,
            `pull_count`
        )
        VALUES
        (
            in_image_id,
            UNIX_TIMESTAMP(NOW()),
            in_image_pulls
        );

        SET out_status = 'Updated';
    ELSE
        SET out_status = 'NoChange';
    END IF;

END;
//

CREATE OR REPLACE PROCEDURE `Image_GetStats` (
    in_image_id   INT
)
BEGIN

    SELECT
        `image_id`                                AS ImageId,
        MAX(`pull_count`)                         AS ImagePulls,
        FROM_UNIXTIME(`pull_timestamp`, '%Y%m%d') AS TimeGroup,
        'Week'                                    AS GroupMode
    FROM
        ImagePullHistory
    WHERE
        `image_id` = in_image_id
    AND
        `pull_timestamp` > unix_timestamp(now() - interval 7 day)
    GROUP BY
        TimeGroup

    UNION ALL

    SELECT
        `image_id`                                AS ImageId,
        MAX(`pull_count`)                         AS ImagePulls,
        FROM_UNIXTIME(`pull_timestamp`, '%Y%m%d') AS TimeGroup,
        'Month'                                    AS GroupMode
    FROM
        ImagePullHistory
    WHERE
        `image_id` = in_image_id
    AND
        `pull_timestamp` > unix_timestamp(now() - interval 1 month)
    GROUP BY
        TimeGroup

    UNION ALL

    SELECT
        `image_id`                                  AS ImageId,
        MAX(`pull_count`)                           AS ImagePulls,
        FROM_UNIXTIME(`pull_timestamp`, '%Y%m%d%h') AS TimeGroup,
        'Day'                                       AS GroupMode
    FROM
        ImagePullHistory
    WHERE
        `image_id` = in_image_id
    AND
        `pull_timestamp` > unix_timestamp(date(now()))

    ORDER BY
        GroupMode, TimeGroup;

END;
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

CREATE OR REPLACE PROCEDURE `Image_CreateOutline`
(
    in_repository   INT,
    in_name         VARCHAR(255),
    in_description  TEXT,
    in_modified     TIMESTAMP,
    in_deprecated   TINYINT,
    in_hidden       TINYINT,
    in_stable       TINYINT,
    in_synchronised TINYINT,
    in_version_mask VARCHAR(255),

    OUT out_status enum('Inserted', 'Exists')
)
BEGIN

    DECLARE var_imageId INT;

    IF EXISTS(SELECT `id` FROM Image WHERE `repository` = in_repository AND `name` = in_name) THEN
        SET out_status = 'Exists';
    ELSE

        INSERT INTO Image (`repository`, `name`, `description`, `modified`, `deprecated`, `hidden`, `stable`, `sync_enabled`, `version_mask`)
        VALUES
        (
            in_repository,
            in_name,
            in_description,
            in_modified,
            in_deprecated,
            in_hidden,
            in_stable,
            in_synchronised,
            in_version_mask
        );

        SET var_imageId = LAST_INSERT_ID();

        INSERT INTO TagBranch (`image_id`, `name`, `protected`)
        VALUES
        (
            var_imageId,
            'latest',
            1
        );

        SET out_status = 'Inserted';

        SELECT * FROM ImageKey_View image_key WHERE image_key.`ImageId` = var_imageId;

    END IF;

END;
//

CREATE OR REPLACE PROCEDURE `Schedule_GetSpecs` ()
BEGIN

    SELECT
        `id`         AS `ScheduleId`,
        `name`       AS `ScheduleName`,
        `interval`   AS `ScheduleInterval`,
        `delay`      AS `ScheduleDelayOffset`,
        `java_class` AS `ScheduleClass`
    FROM
         Schedule;
END;
//

CREATE OR REPLACE VIEW `User_View` AS (

    SELECT
        `id`        AS `UserId`,
        `username`  AS `UserName`,
        `password`  AS `UserPassword`,
        `role`      AS `UserRole`,
        `modified`  AS `ModifiedTime`
    FROM
        Users
);
//

CREATE OR REPLACE PROCEDURE `User_Save`
(
    in_id           INT,
    in_username     VARCHAR(255),
    in_password     VARCHAR(255),
    in_role         ENUM('Admin'),

    OUT out_status  ENUM('Exists', 'Inserted')
)
BEGIN

    IF in_id IS NULL THEN

        INSERT INTO Users
        (
            `username`,
            `password`,
            `role`
        )
        VALUES
        (
            in_username,
            in_password,
            in_role
        );

        SET out_status = 'Inserted';

        SELECT * FROM User_View WHERE `id` = LAST_INSERT_ID();

    ELSE
       SET out_status = 'Exists';
    END IF;

END;
//

CREATE OR REPLACE PROCEDURE `User_GetAll` ()
BEGIN

    SELECT * FROM User_View;

END;
//

CREATE OR REPLACE PROCEDURE `User_Get`
(
    in_id   INT
)
BEGIN

    SELECT
        *
    FROM
         User_View
    WHERE
        UserId = in_id;

END;
//

CREATE OR REPLACE PROCEDURE `User_GetByName`
(
    in_username   VARCHAR(255)
)
BEGIN

    SELECT
        *
    FROM
        User_View
    WHERE
        UserName = in_username;
END;
//

CREATE OR REPLACE PROCEDURE `User_Delete`
(
    in_id   INT
)
BEGIN

    DELETE FROM Users WHERE `id` = in_id;

END;
//
