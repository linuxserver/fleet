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

import io.linuxserver.fleet.web.pages.HomePage;
import io.linuxserver.fleet.web.pages.LoginPage;
import io.linuxserver.fleet.web.pages.ManageRepositoriesPage;
import io.linuxserver.fleet.web.routes.*;
import io.linuxserver.fleet.web.websocket.SynchronisationWebSocket;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Primary entry point for the application. All contexts and resources are loaded
 * through this class.
 * </p>
 */
class FleetApp {

    private final FleetBeans beans;

    FleetApp() {
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

        beans.getWebServer().addWebSocket("/admin/ws/sync", synchronisationWebSocket);
        beans.getWebServer().start();

        beans.getWebServer().addPage(       "/",                        new HomePage(beans.getRepositoryDelegate(), beans.getImageDelegate()));
        beans.getWebServer().addGetApi(     "/api/v1/images",           new AllImagesApi(beans.getRepositoryDelegate(), beans.getImageDelegate()));
        beans.getWebServer().addPage(       "/admin",                   new ManageRepositoriesPage(beans.getRepositoryDelegate()));
        beans.getWebServer().addPage(       "/admin/login",             new LoginPage());
        beans.getWebServer().addPostRoute(  "/admin/login",             new LoginRoute(beans.getAuthenticationDelegate()));
        beans.getWebServer().addPostRoute(  "/admin/logout",            new LogoutRoute());
        beans.getWebServer().addPostApi(    "/admin/manageImage",       new ManageImageApi(beans.getImageDelegate()));
        beans.getWebServer().addGetApi(     "/admin/getImage",          new GetImageApi(beans.getImageDelegate()));
        beans.getWebServer().addPostApi(    "/admin/manageRepository",  new ManageRepositoryApi(beans.getRepositoryDelegate()));
        beans.getWebServer().addPostApi(    "/admin/forceSync",         new ForceSyncApi(beans.getTaskDelegate()));
    }

    private void scheduleSync() {
        beans.getTaskDelegate().scheduleSynchronisationTask(beans.getProperties().getRefreshIntervalInMinutes(), TimeUnit.MINUTES);
    }
}
