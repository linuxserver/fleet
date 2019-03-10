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

import io.linuxserver.fleet.auth.AuthenticatedUser;
import io.linuxserver.fleet.auth.AuthenticationResult;

public class PropertiesAuthenticationDelegate implements AuthenticationDelegate {

    private final String adminUsername;
    private final String adminPassword;

    public PropertiesAuthenticationDelegate(String username, String password) {

        this.adminUsername = username;
        this.adminPassword = password;
    }

    @Override
    public AuthenticationResult authenticate(String username, String password) {

        if (adminUsername.equals(username) && adminPassword.equals(password))
            return new AuthenticationResult(true, new AuthenticatedUser(username));

        return AuthenticationResult.notAuthenticated();
    }
}
