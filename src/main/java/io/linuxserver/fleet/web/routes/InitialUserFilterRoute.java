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

import io.linuxserver.fleet.auth.authenticator.AuthenticatorFactory.AuthenticationType;
import io.linuxserver.fleet.delegate.UserDelegate;
import spark.Filter;
import spark.Request;
import spark.Response;

import static spark.Spark.halt;

/**
 * <p>
 * Forces the maintainer of Fleet to set up an initial user before running Fleet.
 * </p>
 */
public class InitialUserFilterRoute implements Filter {

    private static final String FLEET_USER_UNDEFINED = "fleet.user.undefined";

    private static final String[] PATH_EXEMPTIONS = {
        "/assets", "/setup"
    };

    private final UserDelegate userDelegate;
    private final boolean      databaseAuthenticationEnabled;

    public InitialUserFilterRoute(String authenticationType, UserDelegate userDelegate) {

        this.userDelegate                   = userDelegate;
        this.databaseAuthenticationEnabled  = AuthenticationType.DATABASE.equals(AuthenticationType.valueOf(authenticationType.toUpperCase()));
    }

    @Override
    public void handle(Request request, Response response) {

        if (databaseAuthenticationEnabled) {

            if (!initialUserNeedsConfiguring() && "/setup".equals(request.pathInfo())) {
                halt(401);
            }

            else if (initialUserNeedsConfiguring() && !pathIsExempted(request.pathInfo())) {
                response.redirect("/setup");
            }
        }
    }

    private boolean initialUserNeedsConfiguring() {

        String configured = System.getProperty(FLEET_USER_UNDEFINED);
        if (null == configured || "true".equalsIgnoreCase(configured)) {
            System.setProperty(FLEET_USER_UNDEFINED, String.valueOf(userDelegate.isUserRepositoryEmpty()));
        }

        return "true".equalsIgnoreCase(System.getProperty(FLEET_USER_UNDEFINED));
    }

    private boolean pathIsExempted(String path) {

        for (String exemption : PATH_EXEMPTIONS) {

            if (path.startsWith(exemption)) {
                return true;
            }
        }

        return false;
    }
}
