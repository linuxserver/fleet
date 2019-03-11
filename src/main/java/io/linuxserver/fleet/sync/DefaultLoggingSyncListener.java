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

package io.linuxserver.fleet.sync;

import io.linuxserver.fleet.sync.event.ImageUpdateEvent;
import io.linuxserver.fleet.sync.event.RepositoriesScannedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultLoggingSyncListener implements SynchronisationListener {

    private final Logger LOGGER = LoggerFactory.getLogger(DefaultLoggingSyncListener.class);

    @Override
    public void onSynchronisationStart() {
        LOGGER.info("Sync started");
    }

    @Override
    public void onRepositoriesScanned(RepositoriesScannedEvent event) {
        LOGGER.info("Found repositories: {}", event.getRepositories());
    }

    @Override
    public void onImageUpdated(ImageUpdateEvent event) {
        LOGGER.info("({}/{}) {}.", event.getCurrentPosition(), event.getTotalImages(), event.getImage().getName());
    }

    @Override
    public void onSynchronisationFinish() {
        LOGGER.info("Sync finished.");
    }
}
