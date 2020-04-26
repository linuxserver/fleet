/*
 * Copyright (c)  2019 LinuxServer.io
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

package io.linuxserver.fleet.v2.db;

import io.linuxserver.fleet.core.db.DatabaseProvider;
import io.linuxserver.fleet.db.query.InsertUpdateResult;
import io.linuxserver.fleet.db.query.InsertUpdateStatus;
import io.linuxserver.fleet.v2.key.UserKey;
import io.linuxserver.fleet.v2.types.User;
import io.linuxserver.fleet.v2.types.internal.UserOutlineRequest;
import io.linuxserver.fleet.v2.web.AppRole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DefaultUserDAO extends AbstractDAO implements UserDAO {

    private static final String GetUser       = "{CALL User_Get(?)}";
    private static final String GetUserByName = "{CALL User_GetByName(?)}";
    private static final String GetAllUsers   = "{CALL User_GetAll()}";
    private static final String CreateUser    = "{CALL User_CreateOutline(?,?,?,?)}";
    private static final String UpdateUser    = "{CALL User_Save(?,?,?,?,?)}";
    private static final String DeleteUser    = "{CALL User_Delete(?)}";

    public DefaultUserDAO(DatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    @Override
    public User fetchUser(final UserKey userKey) {

        try (final Connection connection = getConnection()) {

            try (final CallableStatement call = connection.prepareCall(GetUser)) {

                call.setInt(1, userKey.getId());

                final ResultSet results = call.executeQuery();
                if (results.next()) {
                    return makeOneUser(results);
                }
            }

            return null;

        } catch (SQLException e) {

            getLogger().error("fetchAllUsers unable to complete request", e);
            throw new RuntimeException("fetchAllUsers", e);
        }
    }

    @Override
    public User lookUpUser(final String username) {

        try (final Connection connection = getConnection()) {

            try (final CallableStatement call = connection.prepareCall(GetUserByName)) {

                call.setString(1, username);

                final ResultSet results = call.executeQuery();
                if (results.next()) {
                    return makeOneUser(results);
                }
            }

            return null;

        } catch (SQLException e) {

            getLogger().error("fetchAllUsers unable to complete request", e);
            throw new RuntimeException("fetchAllUsers", e);
        }
    }

    @Override
    public InsertUpdateResult<User> createUser(final UserOutlineRequest request) {

        try (final Connection connection = getConnection()) {

            try (final CallableStatement call = connection.prepareCall(CreateUser)) {

                int i = 1;
                call.setString(i++, request.getUsername());
                call.setString(i++, request.getPassword());
                call.setString(i++, request.getRole().name());

                final int statusIndex = i;
                call.registerOutParameter(statusIndex, Types.VARCHAR);

                final ResultSet results = call.executeQuery();

                final DbUpdateStatus status = DbUpdateStatus.valueOf(call.getString(statusIndex));
                if (results.next() && status.isInserted()) {
                    return new InsertUpdateResult<>(makeOneUser(results));
                }

                if (status.isExists()) {
                    return new InsertUpdateResult<>(InsertUpdateStatus.FAILED, "User already exists");
                }
            }

            return new InsertUpdateResult<>(InsertUpdateStatus.FAILED, "Unknown error");

        } catch (SQLException e) {

            getLogger().error("createUser unable to complete request", e);
            throw new RuntimeException("createUser", e);
        }
    }

    @Override
    public List<User> fetchAllUsers() {

        final List<User> users = new ArrayList<>();

        try (final Connection connection = getConnection()) {

            try (final CallableStatement call = connection.prepareCall(GetAllUsers)) {

                final ResultSet results = call.executeQuery();
                while (results.next()) {
                    users.add(makeOneUser(results));
                }
            }

        } catch (SQLException e) {

            getLogger().error("fetchAllUsers unable to complete request", e);
            throw new RuntimeException("fetchAllUsers", e);
        }

        return users;
    }

    @Override
    public InsertUpdateResult<Void> removeUser(final User user) {

        try (final Connection connection = getConnection()) {

            try (final CallableStatement call = connection.prepareCall(DeleteUser)) {

                call.setInt(1, user.getKey().getId());
                call.executeUpdate();
            }

            return new InsertUpdateResult<>(InsertUpdateStatus.OK, "OK");

        } catch (SQLException e) {

            getLogger().error("updateUser unable to complete request", e);
            throw new RuntimeException("updateUser", e);
        }
    }

    @Override
    public InsertUpdateResult<User> updateUser(final User updatedUser) {

        try (final Connection connection = getConnection()) {

            try (final CallableStatement call = connection.prepareCall(UpdateUser)) {

                int i = 1;
                call.setInt(   i++, updatedUser.getKey().getId());
                call.setString(i++, updatedUser.getUsername());
                call.setString(i++, updatedUser.getPassword());
                call.setString(i++, updatedUser.getRole().name());

                final int statusIndex = i;
                call.registerOutParameter(statusIndex, Types.VARCHAR);

                final ResultSet results = call.executeQuery();

                final DbUpdateStatus status = DbUpdateStatus.valueOf(call.getString(statusIndex));
                if (results.next() && status.isUpdated()) {
                    return new InsertUpdateResult<>(makeOneUser(results));
                }

                if (status.isNoChange()) {
                    return new InsertUpdateResult<>(InsertUpdateStatus.FAILED, "No user found");
                }
            }

            return new InsertUpdateResult<>(InsertUpdateStatus.FAILED, "Unknown error");

        } catch (SQLException e) {

            getLogger().error("updateUser unable to complete request", e);
            throw new RuntimeException("updateUser", e);
        }
    }

    private User makeOneUser(final ResultSet results) throws SQLException {

        return new User(new UserKey(results.getInt("UserId")),
                        results.getString("Username"),
                        results.getString("UserPassword"),
                        results.getTimestamp("ModifiedTime").toLocalDateTime(),
                        AppRole.valueOf(results.getString("UserRole")));
    }
}
