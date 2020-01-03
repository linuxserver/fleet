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
import io.linuxserver.fleet.v2.key.UserKey;
import io.linuxserver.fleet.v2.types.User;
import io.linuxserver.fleet.v2.types.internal.UserOutlineRequest;
import io.linuxserver.fleet.v2.web.AppRole;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DefaultUserDAO extends AbstractDAO implements UserDAO {

    private static final String GetUser       = "{CALL User_Get(?)}";
    private static final String GetUserByName = "{CALL User_GetByName(?)}";
    private static final String GetAllUser    = "{CALL User_GetAll()}";
    private static final String CreateUser    = "{CALL User_Save(?,?,?,?,?)}";

    public DefaultUserDAO(DatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    @Override
    public User fetchUser(final UserKey userKey) {
        return null;
    }

    @Override
    public User lookUpUser(final String username) {
        return null;
    }

    @Override
    public InsertUpdateResult<User> createUser(final UserOutlineRequest request) {
        return null;
    }

    @Override
    public List<User> fetchAllUsers() {
        return null;
    }

    @Override
    public InsertUpdateResult<Void> removeUser(User user) {
        return null;
    }

    private User makeOneUser(final ResultSet results) throws SQLException {

        return new User(new UserKey(results.getInt("UserId")),
                                    results.getString("UserName"),
                                    results.getString("UserPassword"),
                                    results.getTimestamp("ModifiedTime").toLocalDateTime(),
                                    AppRole.valueOf(results.getString("UserRole")));
    }
}
