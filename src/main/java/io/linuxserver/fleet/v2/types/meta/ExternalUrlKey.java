/*
 * Copyright (c)  2020 LinuxServer.io
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

package io.linuxserver.fleet.v2.types.meta;

import io.linuxserver.fleet.v2.Utils;
import io.linuxserver.fleet.v2.key.AbstractDatabaseKey;

public class ExternalUrlKey extends AbstractDatabaseKey {

    public static final ExternalUrlKey NewNotPersistedYet = new ExternalUrlKey(-1);

    public ExternalUrlKey(final Integer id) {
        super(Utils.ensureNotNull(id));
    }
}
