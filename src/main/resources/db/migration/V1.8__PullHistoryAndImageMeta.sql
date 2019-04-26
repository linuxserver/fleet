DELIMITER //

CREATE TABLE ImagePullHistory (
    `image_id`            INT     NOT NULL,
    `pull_timestamp`      BIGINT  NOT NULL,
    `pull_count`          BIGINT  NOT NULL,
    PRIMARY KEY (`image_id`, `pull_timestamp`)
) ENGINE=InnoDB;
//

CREATE PROCEDURE `Image_SavePullHistory`
(
    in_image_id     INT,
    in_image_pulls  BIGINT,

    OUT out_status  INT,
    OUT out_message VARCHAR(100)
)
BEGIN

    IF EXISTS(SELECT 1 FROM Images WHERE `id` = in_image_id) THEN

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

    END IF;

    SET out_status  = 0;
    SET out_message = "OK";

END;
//

CREATE PROCEDURE `Image_GetPullHistory`
(
    in_image_id         INT,
    in_grouping_mode    ENUM('hour', 'day', 'week', 'month', 'year')
)
BEGIN

    IF in_grouping_mode = 'hour' THEN

        SELECT
            `image_id`                                  AS ImageId,
            MAX(`pull_count`)                           AS ImagePulls,
            FROM_UNIXTIME(`pull_timestamp`, '%Y%m%d%H') AS TimeGroup
        FROM
            ImagePullHistory
        WHERE
            `image_id` = in_image_id
        GROUP BY `image_id`, TimeGroup
        ORDER BY TimeGroup;

    ELSEIF in_grouping_mode = 'day' THEN

        SELECT
            `image_id`                                AS ImageId,
            MAX(`pull_count`)                         AS ImagePulls,
            FROM_UNIXTIME(`pull_timestamp`, '%Y%m%d') AS TimeGroup
        FROM
            ImagePullHistory
        WHERE
            `image_id` = in_image_id
        GROUP BY `image_id`, TimeGroup
        ORDER BY TimeGroup;

    ELSEIF in_grouping_mode = 'week' THEN

        SELECT
            `image_id`                              AS ImageId,
            MAX(`pull_count`)                       AS ImagePulls,
            FROM_UNIXTIME(`pull_timestamp`, '%Y%v') AS TimeGroup
        FROM
            ImagePullHistory
        WHERE
            `image_id` = in_image_id
        GROUP BY `image_id`, TimeGroup
        ORDER BY TimeGroup;

    ELSEIF in_grouping_mode = 'month' THEN

        SELECT
            `image_id`                              AS ImageId,
            MAX(`pull_count`)                       AS ImagePulls,
            FROM_UNIXTIME(`pull_timestamp`, '%Y%m') AS TimeGroup
        FROM
            ImagePullHistory
        WHERE
            `image_id` = in_image_id
        GROUP BY `image_id`, TimeGroup
        ORDER BY TimeGroup;

    ELSEIF in_grouping_mode = 'year' THEN

        SELECT
            `image_id`                            AS ImageId,
            MAX(`pull_count`)                     AS ImagePulls,
            FROM_UNIXTIME(`pull_timestamp`, '%Y') AS TimeGroup
        FROM
            ImagePullHistory
        WHERE
            `image_id` = in_image_id
        GROUP BY `image_id`, TimeGroup
        ORDER BY TimeGroup;

    END IF;

END;
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
            `deprecation_reason`
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
            in_deprecation_reason
        );

        SET out_id      = LAST_INSERT_ID();
        SET out_status  = 0;
        SET out_message = 'OK';

    ELSE

        UPDATE Images
        SET
            `name`                  = in_name,
            `pulls`                 = in_pull_count,
            `latest_version`        = in_version,
            `version_mask`          = in_version_mask,
            `hidden`                = in_hidden,
            `unstable`              = in_unstable,
            `deprecated`            = in_deprecated,
            `deprecation_reason`    = in_deprecation_reason
        WHERE
            `id` = in_id;

        SET out_id = in_id;

        CALL Image_SavePullHistory(out_id, in_pull_count, out_status, out_message);

    END IF;

END;
//

DROP PROCEDURE `Repository_Delete`//
CREATE PROCEDURE `Repository_Delete`
(
    in_id   INT
)
BEGIN

    DELETE FROM ImagePullHistory WHERE `image_id` IN (SELECT `id` FROM Images WHERE `repository` = in_id);
    DELETE FROM Images           WHERE `repository` = in_id;
    DELETE FROM Repositories     WHERE `id` = in_id;

END;
//

CREATE PROCEDURE `Image_Delete`
(
    in_id   INT
)
BEGIN

    DELETE FROM ImagePullHistory WHERE `image_id` = in_id;
    DELETE FROM Images           WHERE `id` = in_id;

END;
//