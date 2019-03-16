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

package io.linuxserver.fleet.web;

import io.linuxserver.fleet.auth.AuthenticatedUser;
import io.linuxserver.fleet.model.api.ApiResponse;
import io.linuxserver.fleet.model.api.FleetApiException;
import spark.Route;
import spark.RouteGroup;
import spark.Session;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

import static spark.Spark.*;

public class WebServer {

    private boolean started;

    public WebServer(int appPort) {

        port(appPort);

        staticFiles.location("/assets");
        staticFiles.expireTime(600);
    }

    public void start() {

        started = true;

        path("/admin", configureAuthorisationRoute(""));
        path("/admin", configureAuthorisationRoute("/repositories"));
        path("/admin", configureAuthorisationRoute("/images"));
        path("/admin", configureAuthorisationRoute("/manageImage"));
        path("/admin", configureAuthorisationRoute("/manageRepository"));
        path("/admin", configureAuthorisationRoute("/forceSync"));
        path("/admin", configureAuthorisationRoute("/getImage"));

        after("/api/v1/*", (request, response) -> {

            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET");
            response.header("Content-Type", "application/json");
        });

        after("/admin/getImage", (request, response) -> {
            response.header("Content-Type", "application/json");
        });

        exception(FleetApiException.class, (exception, request, response) -> {

            response.body(new JsonTransformer().render(new ApiResponse<>("ERROR", exception.getMessage())));
            response.header("Content-Type", "application/json");
            response.status(exception.getStatusCode());
        });
    }

    public void addWebSocket(String path, Object object) {

        if (started) {
            throw new IllegalStateException("Server has already started! Add a web socket before starting");
        }

        webSocket(path, object);
    }

    public void addPage(String path, TemplateViewRoute page) {
        get(path, page, new FreeMarkerEngine());
    }

    public void addPostRoute(String path, Route route) {
        post(path, route);
    }

    public void addGetApi(String path, Route route) {
        get(path, route, new JsonTransformer());
    }

    public void addPostApi(String path, Route route) {
        post(path, route, new JsonTransformer());
    }

    private RouteGroup configureAuthorisationRoute(String path) {

        return () -> before(path, (request, response) -> {

            Session session = request.session(false);

            if (null == session)
                response.redirect("/admin/login");

            else {

                AuthenticatedUser user = session.attribute(SessionAttribute.USER);
                if (null == user)
                    response.redirect("/admin/login");
            }
        });
    }
}
