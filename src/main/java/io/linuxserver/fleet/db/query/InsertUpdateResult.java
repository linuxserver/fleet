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

package io.linuxserver.fleet.db.query;

public class InsertUpdateResult<T> {

    private final T         result;
    private final int       status;
    private final String    statusMessage;

    public InsertUpdateResult(T result, int status, String statusMessage) {

        this.result = result;
        this.status = status;
        this.statusMessage = statusMessage;
    }

    public InsertUpdateResult(int status, String statusMessage) {
        this(null, status, statusMessage);
    }

    public T getResult() {
        return result;
    }

    public int getStatus() {
        return status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
