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

import io.linuxserver.fleet.web.WebServer;
import io.linuxserver.fleet.web.pages.HomePage;
import io.linuxserver.fleet.web.pages.LoginPage;
import io.linuxserver.fleet.web.pages.ManageRepositoriesPage;
import io.linuxserver.fleet.web.pages.SetupPage;
import io.linuxserver.fleet.web.routes.*;
import io.linuxserver.fleet.web.websocket.SynchronisationWebSocket;

import java.util.concurrent.TimeUnit;

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

        SynchronisationWebSocket synchronisationWebSocket = new SynchronisationWebSocket();
        beans.getSynchronisationDelegate().registerListener(synchronisationWebSocket);

        WebServer webServer = beans.getWebServer();

        webServer.addWebSocket("/admin/ws/sync", synchronisationWebSocket);
        webServer.addFilter(   "*",              new InitialUserFilterRoute(beans.getProperties().getAuthenticationType(), beans.getUserDelegate()));
        webServer.start();

        webServer.addPage(       "/",                        new HomePage(beans.getRepositoryDelegate(), beans.getImageDelegate()));
        webServer.addGetApi(     "/api/v1/images",           new AllImagesApi(beans.getRepositoryDelegate(), beans.getImageDelegate()));
        webServer.addPage(       "/admin",                   new ManageRepositoriesPage(beans.getRepositoryDelegate()));
        webServer.addPage(       "/admin/login",             new LoginPage());
        webServer.addPostRoute(  "/admin/login",             new LoginRoute(beans.getAuthenticationDelegate()));
        webServer.addPostRoute(  "/admin/logout",            new LogoutRoute());
        webServer.addPostApi(    "/admin/manageImage",       new ManageImageApi(beans.getImageDelegate()));
        webServer.addGetApi(     "/admin/getImage",          new GetImageApi(beans.getImageDelegate()));
        webServer.addPostApi(    "/admin/manageRepository",  new ManageRepositoryApi(beans.getRepositoryDelegate()));
        webServer.addPostApi(    "/admin/forceSync",         new ForceSyncApi(beans.getTaskDelegate()));

        if (initialUserNeedsConfiguring()) {

            webServer.addPage(       "/setup", new SetupPage());
            webServer.addPostRoute(  "/setup", new RegisterInitialUserRoute(beans.getUserDelegate()));
        }
    }

    private void scheduleSync() {
        beans.getTaskDelegate().scheduleSynchronisationTask(beans.getProperties().getRefreshIntervalInMinutes(), TimeUnit.MINUTES);
    }

    private boolean initialUserNeedsConfiguring() {

        String configured = System.getProperty(FLEET_USER_UNDEFINED);
        if (null == configured || "true".equalsIgnoreCase(configured)) {
            System.setProperty(FLEET_USER_UNDEFINED, String.valueOf(beans.getUserDelegate().isUserRepositoryEmpty()));
        }

        return "true".equalsIgnoreCase(System.getProperty(FLEET_USER_UNDEFINED));
    }
}
