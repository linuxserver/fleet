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

public class PropertyLoadedUserAuthenticator implements UserAuthenticator {

    private final String adminUsername;
    private final String adminPassword;

    public PropertyLoadedUserAuthenticator(String adminUsername, String adminPassword) {

        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    @Override
    public AuthenticationResult authenticate(UserCredentials userCredentials) {

        if (adminUsername.equals(userCredentials.getUsername()) && adminPassword.equals(userCredentials.getPassword()))
            return new AuthenticationResult(true, new AuthenticatedUser(userCredentials.getUsername()));

        return AuthenticationResult.notAuthenticated();
    }
}
