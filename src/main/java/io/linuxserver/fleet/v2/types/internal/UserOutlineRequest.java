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

package io.linuxserver.fleet.v2.types.internal;

import io.linuxserver.fleet.v2.web.AppRole;

public class UserOutlineRequest {

    public static final UserOutlineRequest InitialFirstLoadUser = new UserOutlineRequest("admin", "admin", AppRole.Admin);

    private final String  username;
    private final String  password;
    private final AppRole role;

    public UserOutlineRequest(final String username, final String password, final AppRole role) {

        this.username = username;
        this.password = password;
        this.role     = role;
    }

    public final String getUsername() {
        return username;
    }

    public final String getPassword() {
        return password;
    }

    public final AppRole getRole() {
        return role;
    }
}
