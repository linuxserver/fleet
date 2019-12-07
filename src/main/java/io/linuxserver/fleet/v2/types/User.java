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

package io.linuxserver.fleet.v2.types;

import io.linuxserver.fleet.v2.key.AbstractHasKey;
import io.linuxserver.fleet.v2.key.UserKey;

import java.time.LocalDateTime;

public class User extends AbstractHasKey<UserKey> {

    private final String username;
    private final String password;

    private LocalDateTime modifiedTime;

    public User(String username, String password) {
        super(new UserKey());

        this.username = username;
        this.password = password;
    }

    public User(Integer id, String username, String password) {
        super(new UserKey(id));

        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public User withModifiedTime(LocalDateTime modifiedTime) {

        this.modifiedTime = modifiedTime;
        return this;
    }

    public LocalDateTime getModifiedTime() {

        if (null != modifiedTime) {

            return LocalDateTime.of(
                modifiedTime.getYear(),
                modifiedTime.getMonth(),
                modifiedTime.getDayOfMonth(),
                modifiedTime.getHour(),
                modifiedTime.getMinute(),
                modifiedTime.getSecond()
            );
        }

        return null;
    }
}
