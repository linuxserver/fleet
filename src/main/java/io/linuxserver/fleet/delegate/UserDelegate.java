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

package io.linuxserver.fleet.delegate;

import io.linuxserver.fleet.auth.security.PasswordEncoder;
import io.linuxserver.fleet.db.dao.UserDAO;
import io.linuxserver.fleet.db.query.InsertUpdateResult;
import io.linuxserver.fleet.db.query.InsertUpdateStatus;
import io.linuxserver.fleet.exception.SaveException;
import io.linuxserver.fleet.model.internal.User;

public class UserDelegate {

    private final PasswordEncoder   passwordEncoder;
    private final UserDAO           userDAO;

    public UserDelegate(PasswordEncoder passwordEncoder, UserDAO userDAO) {

        this.passwordEncoder = passwordEncoder;
        this.userDAO = userDAO;
    }

    public User fetchUser(int id) {
        return userDAO.fetchUser(id);
    }

    public User fetchUserByUsername(String username) {
        return userDAO.fetchUserByUsername(username);
    }

    public boolean isUserRepositoryEmpty() {
        return userDAO.fetchAllUsers().isEmpty();
    }

    public User createNewUser(String username, String password) throws SaveException {

        InsertUpdateResult<User> result = userDAO.saveUser(new User(username, passwordEncoder.encode(password)));

        if (result.getStatus() == InsertUpdateStatus.OK)
            return result.getResult();

        throw new SaveException(result.getStatusMessage());
    }

    public void removeUser(User user) {
        userDAO.removeUser(user);
    }
}
