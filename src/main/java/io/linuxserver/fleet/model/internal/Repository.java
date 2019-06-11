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

package io.linuxserver.fleet.model.internal;

public class Repository extends PersistableItem<Repository> {

    private final String name;

    private String       versionMask;
    private boolean      syncEnabled;

    public Repository(Integer id, String name) {
        super(id);

        this.name = name;
    }

    public Repository(String name) {
        super();

        this.name = name;
    }

    public Repository withVersionMask(String versionMask) {

        this.versionMask = versionMask;
        return this;
    }

    public Repository withSyncEnabled(boolean syncEnabled) {

        this.syncEnabled = syncEnabled;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getVersionMask() {
        return versionMask;
    }

    public boolean isSyncEnabled() {
        return syncEnabled;
    }
}
