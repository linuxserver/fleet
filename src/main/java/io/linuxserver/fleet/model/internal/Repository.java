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

import io.linuxserver.fleet.v2.key.AbstractHasKey;
import io.linuxserver.fleet.v2.key.RepositoryKey;

import java.time.LocalDateTime;

public class Repository extends AbstractHasKey<RepositoryKey> {

    private String        versionMask;
    private boolean       syncEnabled;
    private LocalDateTime modifiedTime;

    public Repository(final RepositoryKey repositoryKey) {
        super(repositoryKey);
    }

    public static Repository copyOf(Repository repository) {
        return new Repository(repository.getKey()).withVersionMask(repository.versionMask).withSyncEnabled(repository.syncEnabled);
    }

    public Repository withVersionMask(String versionMask) {

        this.versionMask = versionMask;
        return this;
    }

    public Repository withSyncEnabled(boolean syncEnabled) {

        this.syncEnabled = syncEnabled;
        return this;
    }

    public Repository withModifiedTime(LocalDateTime modifiedTime) {

        this.modifiedTime = modifiedTime;
        return this;
    }

    public String getName() {
        return getKey().getName();
    }

    public String getVersionMask() {
        return versionMask;
    }

    public boolean isSyncEnabled() {
        return syncEnabled;
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
