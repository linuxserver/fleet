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

package io.linuxserver.fleet.v2.client.docker.queue;

import io.linuxserver.fleet.v2.thread.AsyncTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskQueue<TASK extends AsyncTask<?, ?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskQueue.class);

    private final BlockingQueue<TASK> activeTaskQueue;

    public TaskQueue() {
        activeTaskQueue = new LinkedBlockingQueue<>();
    }

    public final boolean submitTask(final TASK task) {

        LOGGER.info("Task submitted: {}", task);
        if (activeTaskQueue.contains(task)) {

            LOGGER.warn("Task {} is already queued so will not duplicate the request.", task);
            return false;
        }

        return activeTaskQueue.add(task);
    }

    public final int size() {
        return activeTaskQueue.size();
    }

    public final TASK retrieveNextTask() throws InterruptedException {
        return activeTaskQueue.take();
    }

    public final boolean isEmpty() {
        return activeTaskQueue.isEmpty();
    }
}
