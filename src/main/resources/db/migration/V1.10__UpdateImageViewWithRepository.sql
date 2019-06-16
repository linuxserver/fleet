DELIMITER //

CREATE OR REPLACE VIEW `Image_View` AS (

    SELECT
        images.`id`                        AS `ImageId`,
        images.`repository`                AS `RepositoryId`,
        repositories.`name`                AS `RepositoryName`,
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
    JOIN Repositories repositories ON repositories.`id` = images.`repository`
);
//