DELIMITER //

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

    ELSE
        SET out_status = 'NoChange';
    END IF;

END;
//
