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

import io.linuxserver.fleet.delegate.SynchronisationDelegate;

public class SynchroniseAllRepositoriesTask extends FleetTask {

    private final SynchronisationDelegate synchronisationDelegate;

    public SynchroniseAllRepositoriesTask(SynchronisationDelegate synchronisationDelegate) {
        this.synchronisationDelegate = synchronisationDelegate;
    }

    @Override
    protected void executeTask() {

        TaskListener listener = getTaskListener();

        if (listener == null)
            synchronisationDelegate.synchronise();

        else
            synchronisationDelegate.synchronise((event) -> listener.onTaskOutput(event.toString()));
    }
}
