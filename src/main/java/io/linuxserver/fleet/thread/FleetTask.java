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

public abstract class FleetTask implements Runnable {

    private TaskListener taskListener;

    public void setTaskListener(TaskListener listener) {
        this.taskListener = listener;
    }

    protected TaskListener getTaskListener() {
        return taskListener;
    }

    @Override
    public void run() {

        onStart(toString() + " has started.");
        executeTask();
        onEnd(toString() + " has finished.");
    }

    protected abstract void executeTask();

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    private void onStart(String message) {

        if (taskListener != null)
            taskListener.onTaskStart(message);
    }

    private void onEnd(String message) {

        if (taskListener != null)
            taskListener.onTaskEnd(message);
    }
}
