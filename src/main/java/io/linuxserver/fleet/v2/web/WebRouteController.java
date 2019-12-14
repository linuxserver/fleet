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
import io.linuxserver.fleet.v2.web.routes.DefaultAccessManager;
import io.linuxserver.fleet.v2.web.routes.HomeController;
import io.linuxserver.fleet.v2.web.routes.ImageController;
import io.linuxserver.fleet.v2.web.routes.LoginController;

public class WebRouteController {

    private final Javalin webInstance;

    public WebRouteController(final FleetAppController app) {

        final WebConfiguration webConfiguration = app.getWebConfiguration();

        webInstance = Javalin.create(config -> {

            config.showJavalinBanner = false;
            config.addStaticFiles(Locations.Static.Static);
            config.accessManager(new DefaultAccessManager());

        }).start(webConfiguration.getPort());

        Javalin.log.info(printBanner());

        webInstance.routes(() -> {

            webInstance.get(Locations.Login, new LoginController());
            webInstance.get(Locations.Home,  new HomeController(app.getRepositoryManager()));
            webInstance.get(Locations.Image, new ImageController(app.getRepositoryManager()));
        });
    }

    private static String printBanner() {

        return "\n / _| | ___  ___| |_\n" +
                "| |_| |/ _ \\/ _ | __|\n" +
                "|  _| |  __|  __| |_\n" +
                "|_| |_|\\___|\\___|\\__|";
    }
}
