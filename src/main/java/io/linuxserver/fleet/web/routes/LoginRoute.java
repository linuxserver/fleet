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

import io.linuxserver.fleet.auth.AuthenticationResult;
import io.linuxserver.fleet.delegate.AuthenticationDelegate;
import io.linuxserver.fleet.web.SessionAttribute;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

public class LoginRoute implements Route {

    private final AuthenticationDelegate authenticationDelegate;

    public LoginRoute(AuthenticationDelegate authenticationDelegate) {
        this.authenticationDelegate = authenticationDelegate;
    }

    @Override
    public Object handle(Request request, Response response) {

        Session session = request.session(false);
        if (isUserAlreadyLoggedIn(session)) {

            response.redirect("/");
            return null;
        }

        String username = request.queryParams("username");
        String password = request.queryParams("password");

        AuthenticationResult authResult = authenticationDelegate.authenticate(username, password);
        if (!authResult.isAuthenticated()) {

            response.redirect("/login?fail=true");
            return null;
        }

        final Session newSession = request.session();
        newSession.attribute(SessionAttribute.USER, authResult.getUser());

        response.redirect("/");
        return "OK";
    }

    private boolean isUserAlreadyLoggedIn(Session session) {
        return null != session && null != session.attribute(SessionAttribute.USER);
    }
}
