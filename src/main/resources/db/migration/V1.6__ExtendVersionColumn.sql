DELIMITER //

ALTER TABLE Images MODIFY `latest_version` VARCHAR(255) DEFAULT NULL//

DROP PROCEDURE `Image_Save`//
CREATE PROCEDURE `Image_Save`
(
    in_id                   INT,
    in_repository           INT,
    in_name                 VARCHAR(255),
    in_pull_count           BIGINT,
    in_version              VARCHAR(255),
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

        SET out_id = LAST_INSERT_ID();

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

    END IF;

    SET out_status  = 0;
    SET out_message = "OK";

END;
//