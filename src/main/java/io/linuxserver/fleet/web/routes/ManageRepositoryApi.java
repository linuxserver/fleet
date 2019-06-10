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

import io.linuxserver.fleet.delegate.RepositoryDelegate;
import io.linuxserver.fleet.model.internal.Repository;
import io.linuxserver.fleet.model.api.ApiResponse;
import io.linuxserver.fleet.model.api.FleetApiException;
import spark.Request;
import spark.Response;
import spark.Route;

public class ManageRepositoryApi implements Route {

    private final RepositoryDelegate repositoryDelegate;

    public ManageRepositoryApi(RepositoryDelegate repositoryDelegate) {
        this.repositoryDelegate = repositoryDelegate;
    }

    @Override
    public Object handle(Request request, Response response) {

        try {

            int repositoryId = Integer.parseInt(request.queryParams("repositoryId"));

            Repository repository = repositoryDelegate.fetchRepository(repositoryId);
            if (null == repository) {

                response.status(404);
                return new ApiResponse<>("Error", "Repository not found.");
            }

            Action action = Action.valueOf(request.queryParams("action"));
            switch (action) {

                case ENABLE_SYNC:
                case DISABLE_SYNC:
                    repository.withSyncEnabled(Action.ENABLE_SYNC.equals(action));
                    break;

                case MASK:
                    String versionMask = cleanParam(request.queryParams("versionMask"));
                    repository.withVersionMask(versionMask);
                    break;
            }

            repositoryDelegate.saveRepository(repository);

            return new ApiResponse<>("OK", "Repository updated.");

        } catch (Exception e) {
            throw new FleetApiException(500, e.getMessage(), e);
        }
    }

    private String cleanParam(String param) {

        if ("".equalsIgnoreCase(param))
            return null;

        return param;
    }

    public enum Action {
        ENABLE_SYNC, DISABLE_SYNC, MASK
    }
}
