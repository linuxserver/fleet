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

package io.linuxserver.fleet.v2.web.routes;

import io.javalin.http.Context;
import io.linuxserver.fleet.core.FleetAppController;
import io.linuxserver.fleet.v2.service.AbstractAppService;
import io.linuxserver.fleet.v2.types.Repository;
import io.linuxserver.fleet.v2.types.api.ApiRepositoryWrapper;
import io.linuxserver.fleet.v2.types.internal.RepositoryOutlineRequest;
import io.linuxserver.fleet.v2.web.ApiException;
import io.linuxserver.fleet.v2.web.request.NewRepositoryRequest;

public class InternalApiController extends AbstractAppService {

    public InternalApiController(final FleetAppController controller) {
        super(controller);
    }

    public final void updateRepository(final Context ctx) {

    }

    public void addNewRepository(final Context ctx) {

        try {

            final NewRepositoryRequest request = ctx.bodyValidator(NewRepositoryRequest.class)
                    .check(req -> req.getRepositoryName() != null).get();

            final Repository newlyCreatedRepository = getController()
                    .verifyRepositoryAndCreateOutline(new RepositoryOutlineRequest(request.getRepositoryName()));

            ctx.json(new ApiRepositoryWrapper(newlyCreatedRepository));

        } catch (IllegalArgumentException e) {
            throw new ApiException(e.getMessage(), e);
        }
    }
}
