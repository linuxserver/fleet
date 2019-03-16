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

package io.linuxserver.fleet.web.routes;

import io.linuxserver.fleet.delegate.TaskDelegate;
import io.linuxserver.fleet.model.api.ApiResponse;
import io.linuxserver.fleet.model.api.FleetApiException;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * <p>
 * Handles requests from an admin user to force a new synchronisation of the image list.
 * </p>
 */
public class ForceSyncApi implements Route {

    private final TaskDelegate taskDelegate;

    public ForceSyncApi(TaskDelegate taskDelegate) {
        this.taskDelegate = taskDelegate;
    }

    @Override
    public Object handle(Request request, Response response) {

        try {

            taskDelegate.runSynchronisationTask();
            return new ApiResponse<>("OK", "Synchronisation forced.");

        } catch (Exception e) {
            throw new FleetApiException(500, e.getMessage(), e);
        }
    }
}
