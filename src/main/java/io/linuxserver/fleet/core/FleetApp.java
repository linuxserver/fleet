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

package io.linuxserver.fleet.core;

import io.linuxserver.fleet.auth.AuthenticatedUser;
import io.linuxserver.fleet.auth.authenticator.AuthenticatorFactory.AuthenticationType;
import io.linuxserver.fleet.model.api.ApiResponse;
import io.linuxserver.fleet.model.api.FleetApiException;
import io.linuxserver.fleet.web.JsonTransformer;
import io.linuxserver.fleet.web.SessionAttribute;
import io.linuxserver.fleet.web.pages.HomePage;
import io.linuxserver.fleet.web.pages.LoginPage;
import io.linuxserver.fleet.web.pages.ManageRepositoriesPage;
import io.linuxserver.fleet.web.pages.SetupPage;
import io.linuxserver.fleet.web.routes.*;
import io.linuxserver.fleet.web.websocket.SynchronisationWebSocket;
import spark.Session;

import java.util.concurrent.TimeUnit;

import static spark.Spark.*;

/**
 * <p>
 * Primary entry point for the application. All contexts and resources are loaded
 * through this class.
 * </p>
 */
public class FleetApp {

    private static final String FLEET_USER_UNDEFINED = "fleet.user.undefined";

    private static FleetApp instance;

    public static FleetApp instance() {

        if (null == instance) {

            synchronized (FleetApp.class) {

                if (null == instance) {
                    instance = new FleetApp();
                }
            }
        }

        return instance;
    }

    private final FleetBeans beans;

    private FleetApp() {
        beans = new FleetBeans();
    }

    void run() {

        migrateDatabase();
        configureWeb();
        scheduleSync();
    }

    private void migrateDatabase() {
        beans.getDatabaseVersion().migrate();
    }

    private void configureWeb() {

        port(beans.getProperties().getAppPort());

        staticFiles.location("/assets");
        staticFiles.expireTime(600);

        SynchronisationWebSocket synchronisationWebSocket = new SynchronisationWebSocket();
        beans.getSynchronisationDelegate().registerListener(synchronisationWebSocket);

        webSocket("/admin/ws/sync", synchronisationWebSocket);
        init();

        /* -----------------------
         * Set Up
         * -----------------------
         */
        if (initialUserNeedsConfiguring()) {

            path("/setup", () -> {

                before("", (request, response) -> {

                    if (!initialUserNeedsConfiguring()) {
                        halt(401);
                    }
                });

                get("",     new SetupPage());
                post("",    new RegisterInitialUserRoute(beans.getUserDelegate()));
            });
        }

        /* -----------------------
         * Image List and Log In
         * -----------------------
         */
        path("/", () -> {

            get("",         new HomePage(beans.getRepositoryDelegate(), beans.getImageDelegate()));
            get("/login",   new LoginPage());
            post("/login",  new LoginRoute(beans.getAuthenticationDelegate()));
            post("/logout", new LogoutRoute());
        });

        /* -----------------------
         * API
         * -----------------------
         */
        path("/api/v1", () -> {

            get("/images", new AllImagesApi(beans.getRepositoryDelegate(), beans.getImageDelegate()), new JsonTransformer());

            after("/*", (request, response) -> {

                response.header("Access-Control-Allow-Origin", "*");
                response.header("Access-Control-Allow-Methods", "GET");
                response.header("Content-Type","application/json");
            });
        });

        /* -----------------------
         * Admin
         * -----------------------
         */
        path("/admin", () -> {

            before("", (request, response) -> {

                Session session = request.session(false);

                if (null == session)
                    response.redirect("/login");

                else {

                    AuthenticatedUser user = session.attribute(SessionAttribute.USER);
                    if (null == user)
                        response.redirect("/login");
                }
            });

            before("/*", (request, response) -> {

                Session session = request.session(false);

                if (null == session)
                    response.redirect("/login");

                else {

                    AuthenticatedUser user = session.attribute(SessionAttribute.USER);
                    if (null == user)
                        response.redirect("/login");
                }
            });

            get("",                         new ManageRepositoriesPage(beans.getRepositoryDelegate()));

            get("/api/getImage",            new GetImageApi(beans.getImageDelegate()),              new JsonTransformer());
            post("/api/manageImage",        new ManageImageApi(beans.getImageDelegate()),           new JsonTransformer());
            post("/api/manageRepository",   new ManageRepositoryApi(beans.getRepositoryDelegate()), new JsonTransformer());
            post("/api/forceSync",          new ForceSyncApi(beans.getTaskDelegate()),              new JsonTransformer());

            after("/api/*", (request, response) -> response.header("Content-Type", "application/json"));
        });

        /* -----------------------
         * API Error Handling
         * -----------------------
         */
        exception(FleetApiException.class, (exception, request, response) -> {

            response.body(new JsonTransformer().render(new ApiResponse<>("ERROR", exception.getMessage())));
            response.header("Content-Type", "application/json");
            response.status(exception.getStatusCode());
        });
    }

    private void scheduleSync() {
        beans.getTaskDelegate().scheduleSynchronisationTask(beans.getProperties().getRefreshIntervalInMinutes(), TimeUnit.MINUTES);
    }

    private boolean initialUserNeedsConfiguring() {

        String configured = System.getProperty(FLEET_USER_UNDEFINED);
        if (null == configured || "true".equalsIgnoreCase(configured)) {
            System.setProperty(FLEET_USER_UNDEFINED, String.valueOf(beans.getUserDelegate().isUserRepositoryEmpty()));
        }

        return "true".equalsIgnoreCase(System.getProperty(FLEET_USER_UNDEFINED)) && databaseAuthenticationEnabled();
    }

    private boolean databaseAuthenticationEnabled() {
        return AuthenticationType.DATABASE == AuthenticationType.valueOf(beans.getProperties().getAuthenticationType());
    }
}
