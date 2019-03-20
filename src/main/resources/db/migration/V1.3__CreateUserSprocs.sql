DELIMITER //

CREATE PROCEDURE `User_Save`
(
    in_id           INT,
    in_username     VARCHAR(255),
    in_password     VARCHAR(255),

    OUT out_id      INT,
    OUT out_status  INT,
    OUT out_message VARCHAR(100)
)
BEGIN

    IF in_id IS NULL THEN

        INSERT INTO Users
        (
            `username`,
            `password`
        )
        VALUES
        (
            in_username,
            in_password
        );

        SET out_id = LAST_INSERT_ID();

    ELSE

        UPDATE Repositories
        SET
            `username`  = in_username,
            `password`  = in_password
        WHERE
            `id` = in_id;

        SET out_id = in_id;

    END IF;

    SET out_status = 0;
    SET out_message = 'OK';

END;
//

CREATE PROCEDURE `User_GetAll` ()
BEGIN

    SELECT
        `id`        AS `UserId`,
        `username`  AS `UserName`,
        `password`  AS `UserPassword`,
        `modified`  AS `ModifiedTime`
    FROM
        Users;

END;
//

CREATE PROCEDURE `User_Get`
(
    in_id   INT
)
BEGIN

    SELECT
        `id`        AS `UserId`,
        `username`  AS `UserName`,
        `password`  AS `UserPassword`,
        `modified`  AS `ModifiedTime`
    FROM
        Users
    WHERE
        `id` = in_id;

END;
//

CREATE PROCEDURE `User_GetByName`
(
    in_username   VARCHAR(255)
)
BEGIN

    SELECT
        `id`        AS `UserId`,
        `username`  AS `UserName`,
        `password`  AS `UserPassword`,
        `modified`  AS `ModifiedTime`
    FROM
        Users
    WHERE
        `username` = in_username;
END;
//

CREATE PROCEDURE `User_Delete`
(
    in_id   INT
)
BEGIN

    DELETE FROM Users WHERE `id` = in_id;

END;
//

DELIMITER ;
