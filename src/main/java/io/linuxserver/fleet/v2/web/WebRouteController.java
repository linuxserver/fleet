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

package io.linuxserver.fleet.v2.web;

import io.javalin.Javalin;
import io.linuxserver.fleet.core.FleetAppController;
import io.linuxserver.fleet.core.config.WebConfiguration;
import io.linuxserver.fleet.v2.web.routes.*;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.core.security.SecurityUtil.roles;

public class WebRouteController {

    private final Javalin               webInstance;
    private final InternalApiController apiController;

    public WebRouteController(final FleetAppController app) {

        apiController = new InternalApiController(app);

        final WebConfiguration webConfiguration = app.getWebConfiguration();

        webInstance = Javalin.create(config -> {

            config.showJavalinBanner = false;
            config.addStaticFiles(Locations.Static.Static);
            config.accessManager(new DefaultAccessManager());

        }).start(webConfiguration.getPort());

        Javalin.log.info(printBanner());

        webInstance.routes(() -> {

            get(Locations.Login, new LoginController(),                           roles(FleetRole.Anyone));
            get(Locations.Home,  new HomeController( app.getRepositoryService()), roles(FleetRole.Anyone));
            get(Locations.Image, new ImageController(app.getRepositoryService()), roles(FleetRole.Anyone));

            get(Locations.Admin.Repositories, new AdminRepositoryController(app.getRepositoryService()), roles(FleetRole.Anyone));
            get(Locations.Admin.Images,       new AdminImageController(     app.getRepositoryService()), roles(FleetRole.Anyone));
            get(Locations.Admin.Schedules,    new AdminScheduleController(  app.getScheduleService()),   roles(FleetRole.Anyone));

            path(Locations.Internal.Api, () -> {

                path(Locations.Internal.Repository, () -> {
                    put(apiController::updateRepository,  roles(FleetRole.AdminOnly));
                    post(apiController::addNewRepository, roles(FleetRole.AdminOnly));
                });
            });
        });
    }

    private static String printBanner() {

        return "\n / _| | ___  ___| |_\n" +
                "| |_| |/ _ \\/ _ | __|\n" +
                "|  _| |  __|  __| |_\n" +
                "|_| |_|\\___|\\___|\\__|";
    }
}
