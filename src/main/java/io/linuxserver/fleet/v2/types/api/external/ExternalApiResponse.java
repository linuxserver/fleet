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

package io.linuxserver.fleet.v2.types.api.external;

public class ExternalApiResponse<T> {

    private ApiStatus status;
    private T         data;

    public ExternalApiResponse(final ApiStatus status, final T data) {
        this.status = status;
        this.data   = data;
    }

    public final ApiStatus getStatus() {
        return status;
    }

    public final T getData() {
        return data;
    }

    public enum ApiStatus {
        OK, Error
    }
}
