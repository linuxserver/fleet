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

package io.linuxserver.fleet.delegate;

import io.linuxserver.fleet.core.FleetBeans;
import io.linuxserver.fleet.thread.SynchroniseAllRepositoriesTask;
import io.linuxserver.fleet.thread.TaskManager;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Manager of the {@link TaskManager} as a means to keep a reference to all dependencies
 * that a task may need. Ensures that calling classes do not need to know the dependencies
 * of each specific task.
 * </p>
 */
public class TaskDelegate {

    private final TaskManager               taskManager;
    private final SynchronisationDelegate   synchronisationDelegate;

    public TaskDelegate(FleetBeans beans) {

        taskManager             = beans.getTaskManager();
        synchronisationDelegate = beans.getSynchronisationDelegate();
    }

    /**
     * <p>
     * Asynchronously triggers a new run of the synchronisation process.
     * </p>
     */
    public void runSynchronisationTask() {
        taskManager.runTaskOnce(new SynchroniseAllRepositoriesTask(synchronisationDelegate));
    }

    /**
     * <p>
     * Creates a new schedule for the synchronisation process.
     * </p>
     *
     * @param interval
     *      How often the process should run.
     * @param timeUnit
     *      The relative unit of time of the interval.
     */
    public void scheduleSynchronisationTask(int interval, TimeUnit timeUnit) {
        taskManager.scheduleRecurringTask(new SynchroniseAllRepositoriesTask(synchronisationDelegate), interval, timeUnit);
    }
}
