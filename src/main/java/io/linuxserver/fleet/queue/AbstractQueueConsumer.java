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

public abstract class AbstractQueueConsumer<R extends FleetResponse, T extends FleetRequest<R>> extends Thread implements QueueConsumer<T> {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass().getName());

    private final RequestQueue<T> requestQueue;

    public AbstractQueueConsumer(final RequestQueue<T> requestQueue, final String name) {
        super(name + "-Consumer");
        this.requestQueue = requestQueue;
    }

    @Override
    public void run() {

        while (true) {
            consume();
        }
    }

    @Override
    public void start() {

        getLogger().info("Starting...");
        super.start();
    }

    @Override
    public void consume() {

        try {

            T request = requestQueue.takeOneRequest();
            R response = request.execute();

            handleResponse(response);

        } catch (Exception e) {
            getLogger().error("consume caught unhandled exception", e);
        }
    }

    protected abstract void handleResponse(R response);

    public Logger getLogger() {
        return LOGGER;
    }
}
