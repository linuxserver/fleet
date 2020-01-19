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

package io.linuxserver.fleet.core;

public interface FleetRuntime {

    /**
     * If set will switch specific properties to allow more streamlined development
     */
    boolean DEV_MODE                = System.getProperty("enable.dev") != null;

    /**
     * Base directory for the config file.
     */
    String CONFIG_BASE              = System.getProperty("fleet.config.base");

    /**
     * Whether or not logs should show passwords
     */
    boolean SHOW_PASSWORDS          = System.getProperty("fleet.show.passwords") != null;

    /**
     * Tells Fleet to completely wipe the database and recreate it.
     */
    boolean NUKE_DATABASE           = System.getProperty("fleet.nuke.database") != null;
}
