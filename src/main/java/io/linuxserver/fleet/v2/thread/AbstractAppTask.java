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

import io.linuxserver.fleet.v2.LoggerOwner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAppTask<DELEGATE extends AsyncTaskDelegate, RESPONSE extends AsyncTaskResponse> implements AsyncTask<DELEGATE, RESPONSE>, LoggerOwner {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public final String name;

    public AbstractAppTask(final String name) {
        this.name = name;
    }

    @Override
    public RESPONSE performTaskOn(final DELEGATE delegate) {

        try {
            return performTaskInternal(delegate);
        } catch (Exception e) {
            LOGGER.error("Unable to complete task", e);
            throw new TaskExecutionException(e);
        }
    }

    @Override
    public final Logger getLogger() {
        return LOGGER;
    }

    protected abstract RESPONSE performTaskInternal(final DELEGATE delegate);

    @Override
    public String toString() {
        return name;
    }
}
