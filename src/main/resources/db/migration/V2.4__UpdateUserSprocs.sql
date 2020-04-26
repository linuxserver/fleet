DELIMITER //

CREATE OR REPLACE VIEW `User_View` AS (

    SELECT
        `id`       AS `UserId`,
        `username` AS `Username`,
        `password` AS `UserPassword`,
        `modified` AS `ModifiedTime`,
        `role`     AS `UserRole`
    FROM
        Users
);
//

CREATE OR REPLACE PROCEDURE `User_CreateOutline`
(
    in_username     VARCHAR(255),
    in_password     VARCHAR(255),
    in_role         ENUM('Admin'),

    OUT out_status ENUM('Inserted', 'NoChange', 'Exists')
)
BEGIN

    IF NOT EXISTS(SELECT 1 FROM User_View WHERE `Username` = in_username) THEN

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

        SELECT * FROM User_View WHERE `UserId` = LAST_INSERT_ID();

    ELSE
        SET out_status = 'Exists';
    END IF;

END;
//

CREATE OR REPLACE PROCEDURE `User_Save`
(
    in_id           INT,
    in_username     VARCHAR(255),
    in_password     VARCHAR(255),
    in_role         ENUM('Admin'),

    OUT out_status ENUM('Updated', 'NoChange')
)
BEGIN

    IF EXISTS(SELECT 1 FROM User_View WHERE `Username` = in_username) THEN

        UPDATE Users
        SET
            `password` = in_password,
            `role`     = in_role
        WHERE
                `id` = in_id;

        SET out_status = 'Updated';

        SELECT * FROM User_View WHERE `UserId` = in_id;

    ELSE
        SET out_status = 'NoChange';
    END IF;

END;
//

CREATE OR REPLACE PROCEDURE `User_GetAll` ()
BEGIN

    SELECT
        *
    FROM
        User_View;

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
        `UserId` = in_id;

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
        `Username` = in_username;
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

DELIMITER ;
