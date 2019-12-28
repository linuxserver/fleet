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

package io.linuxserver.fleet.auth.authenticator;

import io.linuxserver.fleet.auth.AuthenticatedUser;
import io.linuxserver.fleet.auth.AuthenticationResult;
import io.linuxserver.fleet.auth.UserCredentials;
import io.linuxserver.fleet.auth.security.PasswordEncoder;
import io.linuxserver.fleet.v2.service.UserService;
import io.linuxserver.fleet.v2.types.User;

public class DefaultUserAuthenticator implements UserAuthenticator {

    private final UserService     userService;
    private final PasswordEncoder passwordEncoder;

    public DefaultUserAuthenticator(final UserService userService,
                                    final PasswordEncoder passwordEncoder) {

        this.userService     = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthenticationResult authenticate(final UserCredentials userCredentials) {

        final User user = userService.lookUpUser(userCredentials.getUsername());

        if (null != user && getPasswordEncoder().matches(userCredentials.getPassword(), user.getPassword())) {
            return new AuthenticationResult(true, new AuthenticatedUser(user.getUsername()));
        }

        return AuthenticationResult.notAuthenticated();
    }

    @Override
    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }
}
