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

package io.linuxserver.fleet.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TaskManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskManager.class);

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

    public void scheduleRecurringTask(FleetTask task, int interval, TimeUnit timeUnit) {

        LOGGER.info("Scheduling task " + task);
        executorService.scheduleAtFixedRate(task, 0, interval, timeUnit);
    }

    public void runTaskOnce(FleetTask task) {
        start(task);
    }

    private void start(FleetTask task) {
        new Thread(task).start();
    }
}
