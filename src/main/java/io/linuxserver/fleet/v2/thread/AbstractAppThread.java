/*
 * Copyright (c)  2019 LinuxServer.io
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

package io.linuxserver.fleet.v2.thread;

import io.linuxserver.fleet.core.FleetAppController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAppThread extends Thread {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private ThreadStatus status = ThreadStatus.Stopped;

    private final FleetAppController controller;

    public AbstractAppThread(final FleetAppController controller, final String name) {
        this.controller = controller;
        setName(name);
    }

    public final FleetAppController getController() {
        return controller;
    }

    @Override
    public synchronized void run() {

        try {

            LOGGER.info("Starting thread...");
            status = ThreadStatus.Running;
            while (isThreadRunning()) {
                doRunSinglePass();
            }

        } catch (Exception e) {

            LOGGER.error("Thread has encountered an exception it cannot handle", e);
            controller.handleException(e);

            LOGGER.info("Stopping thread...");
            status = ThreadStatus.Stopped;
        }
    }

    public final boolean isThreadRunning() {
        return status == ThreadStatus.Running;
    }

    protected final Logger getLogger() {
        return LOGGER;
    }

    protected abstract void doRunSinglePass() throws Exception;
}
