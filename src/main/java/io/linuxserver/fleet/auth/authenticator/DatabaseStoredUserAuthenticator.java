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

package io.linuxserver.fleet.auth.authenticator;

import io.linuxserver.fleet.auth.AuthenticatedUser;
import io.linuxserver.fleet.auth.AuthenticationResult;
import io.linuxserver.fleet.auth.UserCredentials;
import io.linuxserver.fleet.auth.security.PasswordEncoder;
import io.linuxserver.fleet.delegate.UserDelegate;
import io.linuxserver.fleet.v2.types.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseStoredUserAuthenticator implements UserAuthenticator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseStoredUserAuthenticator.class);

    private final PasswordEncoder   passwordEncoder;
    private final UserDelegate      userDelegate;

    public DatabaseStoredUserAuthenticator(PasswordEncoder passwordEncoder, UserDelegate userDelegate) {

        this.passwordEncoder    = passwordEncoder;
        this.userDelegate       = userDelegate;
    }

    @Override
    public AuthenticationResult authenticate(UserCredentials userCredentials) {

        User user = userDelegate.fetchUserByUsername(userCredentials.getUsername());
        if (null == user) {

            LOGGER.warn("Attempt to log in with user '{}' failed. Not found.", userCredentials.getUsername());
            return AuthenticationResult.notAuthenticated();
        }

        boolean authenticated = passwordEncoder.matches(userCredentials.getPassword(), user.getPassword());
        if (authenticated) {
            return new AuthenticationResult(authenticated, new AuthenticatedUser(user.getUsername()));
        }

        LOGGER.warn("Unable to verify user credentials for user {}", userCredentials.getUsername());
        return AuthenticationResult.notAuthenticated();
    }
}
