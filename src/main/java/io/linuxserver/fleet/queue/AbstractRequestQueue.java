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

package io.linuxserver.fleet.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class AbstractRequestQueue<T extends FleetRequest<? extends FleetResponse>> implements RequestQueue<T> {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass().getName());

    private final BlockingQueue<T> requestQueue;

    public AbstractRequestQueue() {
        this.requestQueue = new LinkedBlockingDeque<>();
    }

    @Override
    public synchronized void enqueueRequest(final T request) {

        try {

            if (!requestQueue.contains(request)) {

                final boolean added = requestQueue.add(request);
                if (!added) {
                    LOGGER.warn("enqueueRequest unable to add request to queue");
                }
            }

        } catch (Exception e) {
            LOGGER.error("enqueueRequest caught unhandled exception {}", e.getMessage());
        }
    }

    @Override
    public synchronized T takeOneRequest() {

        try {
            return requestQueue.take();
        } catch (InterruptedException e) {
            LOGGER.error("takeOneRequest was interrupted: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
