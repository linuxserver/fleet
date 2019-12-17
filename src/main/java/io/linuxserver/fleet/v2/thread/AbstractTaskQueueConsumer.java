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
import io.linuxserver.fleet.v2.client.docker.queue.TaskQueue;

public abstract class AbstractTaskQueueConsumer<DELEGATE extends AsyncTaskDelegate, R extends AsyncTaskResponse, T extends AsyncTask<DELEGATE, R>>
        extends AbstractAppThread {

    private final TaskQueue<T> taskQueue;
    private final DELEGATE     taskDelegate;

    public AbstractTaskQueueConsumer(final FleetAppController controller,
                                     final DELEGATE delegate,
                                     final TaskQueue<T> queue,
                                     final String consumerThreadName) {
        super(controller, consumerThreadName);
        taskQueue = queue;
        taskDelegate = delegate;
    }

    @Override
    protected void doRunSinglePass() throws Exception {

        final T task = taskQueue.retrieveNextTask();

        try {

            getLogger().info("Processing single task {}", task);
            final R response = task.performTaskOn(taskDelegate);

            handleTaskResponse(response);

        } catch (TaskExecutionException e) {
            getLogger().error("Unable to complete the processing of task {}", task, e);
        }
    }

    protected abstract void handleTaskResponse(final R response);
}
