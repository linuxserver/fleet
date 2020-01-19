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

package io.linuxserver.fleet.v2.service;

import io.linuxserver.fleet.auth.AuthenticationDelegate;
import io.linuxserver.fleet.auth.AuthenticationResult;
import io.linuxserver.fleet.auth.DefaultAuthenticationDelegate;
import io.linuxserver.fleet.auth.authenticator.DefaultUserAuthenticator;
import io.linuxserver.fleet.auth.security.PBKDF2PasswordEncoder;
import io.linuxserver.fleet.core.FleetAppController;
import io.linuxserver.fleet.db.query.InsertUpdateResult;
import io.linuxserver.fleet.v2.db.UserDAO;
import io.linuxserver.fleet.v2.key.UserKey;
import io.linuxserver.fleet.v2.types.User;
import io.linuxserver.fleet.v2.types.internal.UserOutlineRequest;

import java.util.List;

public class UserService extends AbstractAppService {

    private final UserDAO                userDAO;
    private final AuthenticationDelegate authDelegate;

    public UserService(final FleetAppController controller,
                       final UserDAO userDAO) {
        super(controller);
        this.userDAO      = userDAO;
        this.authDelegate = new DefaultAuthenticationDelegate(new DefaultUserAuthenticator(this,
                                                              new PBKDF2PasswordEncoder(getProperties().getAppSecret())));
        createInitialAdminUser();
    }

    public final AuthenticationResult authenticateCredentials(final String username, final String password) {
        return authDelegate.authenticate(username, password);
    }

    public final User lookUpUser(final String username) {
        return userDAO.lookUpUser(username);
    }

    public final User fetchUser(final UserKey userKey) {
        return userDAO.fetchUser(userKey);
    }

    public final List<User> fetchAllUsers() {
        return userDAO.fetchAllUsers();
    }

    public final User createUser(final UserOutlineRequest userOutlineRequest) {

        final InsertUpdateResult<User> result = userDAO.createUser(userOutlineRequest);
        if (result.isError()) {

            getLogger().error("Unable to create new user: {}", result.getStatusMessage());
            throw new RuntimeException("Unable to create new user: " + result.getStatusMessage());
        }
        return result.getResult();
    }

    private void createInitialAdminUser() {

        if (fetchAllUsers().isEmpty()) {

            getLogger().info("There are no users! Creating initial user with default credentials");
            final UserOutlineRequest encodedUser = UserOutlineRequest.InitialFirstLoadUser.cloneWithPassword(
                authDelegate.encodePassword(UserOutlineRequest.InitialFirstLoadUser.getPassword())
            );

            createUser(encodedUser);

            getLogger().warn("!!!!!!!!");
            getLogger().warn("DEFAULT USER CREATED. CHANGE THE PASSWORD OR CREATE A NEW USER!");
            getLogger().warn("!!!!!!!!");
        }
    }
}
