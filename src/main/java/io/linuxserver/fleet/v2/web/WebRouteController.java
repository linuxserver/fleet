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
import io.javalin.core.validation.JavalinValidation;
import io.javalin.http.staticfiles.Location;
import io.linuxserver.fleet.core.FleetAppController;
import io.linuxserver.fleet.core.config.WebConfiguration;
import io.linuxserver.fleet.v2.key.ImageKey;
import io.linuxserver.fleet.v2.key.ImageLookupKey;
import io.linuxserver.fleet.v2.key.RepositoryKey;
import io.linuxserver.fleet.v2.types.meta.history.ImagePullStatistic.StatGroupMode;
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
            config.addStaticFiles(app.getAppProperties().getStaticFilesPath().toString(), Location.EXTERNAL);
            config.accessManager(new DefaultAccessManager());

        }).start(webConfiguration.getPort());

        Javalin.log.info(printBanner());

        JavalinValidation.register(StatGroupMode.class,  StatGroupMode::valueOf);
        JavalinValidation.register(ImageKey.class,       ImageKey::parse);
        JavalinValidation.register(ImageLookupKey.class, ImageLookupKey::new);
        JavalinValidation.register(RepositoryKey.class,  RepositoryKey::parse);

        webInstance.exception(Exception.class, (e, ctx) -> {

            ctx.status(400);
            ctx.result(e.getMessage());
        });

        webInstance.routes(() -> {

            final LoginController loginController = new LoginController(app);
            get( Locations.Login, loginController, roles(AppRole.Anyone));
            post(Locations.Login, loginController, roles(AppRole.Anyone));

            get(Locations.Home,  new HomeController( app), roles(AppRole.Anyone));
            get(Locations.Image, new ImageController(app), roles(AppRole.Anyone));

            get(Locations.Admin.Repositories, new AdminRepositoryController(app), roles(AppRole.Admin));
            get(Locations.Admin.Images,       new AdminImageController(     app), roles(AppRole.Admin));
            get(Locations.Admin.Schedules,    new AdminScheduleController(  app), roles(AppRole.Admin));

            final AdminImageEditController imageEditController = new AdminImageEditController(app);
            get( Locations.Admin.ImageEdit, imageEditController, roles(AppRole.Admin));
            post(Locations.Admin.ImageEdit, imageEditController, roles(AppRole.Admin));

            path(Locations.Internal.Api, () -> {

                path(Locations.Internal.Repository, () -> {

                    put(   apiController::updateRepositorySpec, roles(AppRole.Admin));
                    post(  apiController::addNewRepository,     roles(AppRole.Admin));
                    delete(apiController::deleteRepository,     roles(AppRole.Admin));

                    path(Locations.Internal.Sync, () -> {
                       put(apiController::syncRepository, roles(AppRole.Admin));
                    });
                });

                path(Locations.Internal.Image, () -> {

                    put(apiController::updateImageSpec, roles(AppRole.Admin));

                    path(Locations.Internal.Sync, () -> {
                        put(apiController::syncImage, roles(AppRole.Admin));
                    });

                    path(Locations.Internal.Stats, () -> {
                        get(apiController::getImagePullHistory, roles(AppRole.Anyone));
                    });

                    path(Locations.Internal.Track, () -> {
                        put(apiController::trackNewBranch, roles(AppRole.Admin));
                    });
                });

                path(Locations.Internal.Schedule, () -> {
                   put(apiController::runSchedule, roles(AppRole.Admin));
                });
            });
        });

        Runtime.getRuntime().addShutdownHook(new Thread(webInstance::stop));
    }

    private static String printBanner() {

        return "\n / _| | ___  ___| |_\n" +
                "| |_| |/ _ \\/ _ | __|\n" +
                "|  _| |  __|  __| |_\n" +
                "|_| |_|\\___|\\___|\\__|";
    }
}
