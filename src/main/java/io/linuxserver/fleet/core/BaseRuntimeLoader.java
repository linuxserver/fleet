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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Prints out all runtime arguments passed in as JVM arguments (<i>-D</i>).
 * </p>
 *
 * @author Josh Stark
 */
abstract class BaseRuntimeLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseRuntimeLoader.class);

    BaseRuntimeLoader() {

        LOGGER.info("Initalising...");
        LOGGER.info("Config base    : " + FleetRuntime.CONFIG_BASE);
        LOGGER.info("Show Passwords : " + FleetRuntime.SHOW_PASSWORDS);
    }
}
