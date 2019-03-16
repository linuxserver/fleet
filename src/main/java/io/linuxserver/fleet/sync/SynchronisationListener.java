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

/**
 * <p>
 * Allows for the receiving of messages from the synchronisation process at
 * specific stages.
 * </p>
 */
public interface SynchronisationListener {

    /**
     * <p>
     * Triggered when the synchronisation process starts
     * </p>
     */
    void onSynchronisationStart();

    /**
     * <p>
     * Triggered when the synchronisation process has successfully scanned for all known
     * repositories against the Docker Hub user.
     * </p>
     *
     * @param event
     *      The list of all found repositories
     */
    void onRepositoriesScanned(RepositoriesScannedEvent event);

    /**
     * <p>
     * Triggered when the synchronisation process as successfully scanned and updated a single
     * image from Docker Hub, and is about to save it back to the internal database. This will
     * contain the updated version of the image.
     * </p>
     *
     * @param event
     *      The new view of the image. This also contains positional information pertaining to
     *      how far in the overall list the image is.
     */
    void onImageUpdated(ImageUpdateEvent event);

    /**
     * <p>
     * Triggered when the synchronisation process has finished.
     * </p>
     */
    void onSynchronisationFinish();

    /**
     * <p>
     * Triggered when a calling task forces a synchronisation but a process is already running.
     * </p>
     */
    void onSynchronisationSkipped();
}
