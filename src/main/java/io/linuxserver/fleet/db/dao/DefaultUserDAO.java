/*
 * Copyright (c) 2019 LinuxServer.io
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.linuxserver.fleet.db.dao;

import io.linuxserver.fleet.db.PoolingDatabaseConnection;
import io.linuxserver.fleet.db.query.InsertUpdateResult;
import io.linuxserver.fleet.db.query.InsertUpdateStatus;
import io.linuxserver.fleet.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static io.linuxserver.fleet.db.dao.Utils.setNullableInt;
import static io.linuxserver.fleet.db.dao.Utils.setNullableString;

public class DefaultUserDAO implements UserDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultUserDAO.class);

    private final PoolingDatabaseConnection databaseConnection;

    public DefaultUserDAO(PoolingDatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public User fetchUser(int id) {

        try (Connection connection = databaseConnection.getConnection()) {

            CallableStatement call = connection.prepareCall("{CALL User_Get(?)}");
            call.setInt(1, id);

            ResultSet results = call.executeQuery();
            if (results.next())
                return parseUserFromResultSet(results);

        } catch (SQLException e) {
            LOGGER.error("Unable to retrieve user", e);
        }

        return null;
    }

    @Override
    public User fetchUserByUsername(String username) {

        try (Connection connection = databaseConnection.getConnection()) {

            CallableStatement call = connection.prepareCall("{CALL User_GetByName(?)}");
            call.setString(1, username);

            ResultSet results = call.executeQuery();
            if (results.next())
                return parseUserFromResultSet(results);

        } catch (SQLException e) {
            LOGGER.error("Unable to retrieve user", e);
        }

        return null;
    }

    @Override
    public InsertUpdateResult<User> saveUser(User user) {

        try (Connection connection = databaseConnection.getConnection()) {

            CallableStatement call = connection.prepareCall("{CALL User_Save(?,?,?,?,?,?)}");
            setNullableInt(call, 1, user.getId());
            call.setString(2, user.getUsername());
            setNullableString(call, 3, user.getPassword());

            call.registerOutParameter(4, Types.INTEGER);
            call.registerOutParameter(5, Types.INTEGER);
            call.registerOutParameter(6, Types.VARCHAR);

            call.executeUpdate();

            int userId             = call.getInt(4);
            int status             = call.getInt(5);
            String statusMessage   = call.getString(6);

            if (InsertUpdateStatus.OK == status)
                return new InsertUpdateResult<>(fetchUser(userId), status, statusMessage);

            return new InsertUpdateResult<>(status, statusMessage);

        } catch (SQLException e) {

            LOGGER.error("Unable to save user", e);
            return new InsertUpdateResult<>(null, InsertUpdateStatus.OK, "Unable to save user");
        }
    }

    @Override
    public List<User> fetchAllUsers() {

        List<User> repositories = new ArrayList<>();

        try (Connection connection = databaseConnection.getConnection()) {

            CallableStatement call = connection.prepareCall("{CALL User_GetAll()}");

            ResultSet results = call.executeQuery();
            while (results.next())
                repositories.add(parseUserFromResultSet(results));

        } catch (SQLException e) {
            LOGGER.error("Unable to get all users", e);
        }

        return repositories;
    }

    @Override
    public void removeUser(User user) {

        try (Connection connection = databaseConnection.getConnection()) {

            CallableStatement call = connection.prepareCall("{CALL User_Delete(?)}");
            call.setInt(1, user.getId());

            call.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error("Error when removing user", e);
        }
    }

    private User parseUserFromResultSet(ResultSet results) throws SQLException {

        return new User(
            results.getInt("UserId"),
            results.getString("UserName"),
            results.getString("UserPassword")
        ).withModifiedTime(results.getTimestamp("ModifiedTime").toLocalDateTime());
    }
}
