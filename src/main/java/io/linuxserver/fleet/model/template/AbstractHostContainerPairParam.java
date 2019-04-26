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

package io.linuxserver.fleet.model.template;

public abstract class AbstractHostContainerPairParam<HOST_V, CONTAINER_V> {

    private final HOST_V        hostValue;
    private final CONTAINER_V   containerValue;

    AbstractHostContainerPairParam(HOST_V hostValue, CONTAINER_V containerValue) {

        this.hostValue = hostValue;
        this.containerValue = containerValue;
    }

    public HOST_V getHostValue() {
        return hostValue;
    }

    public CONTAINER_V getContainerValue() {
        return containerValue;
    }
}
