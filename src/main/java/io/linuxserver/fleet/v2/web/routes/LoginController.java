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

package io.linuxserver.fleet.v2.web.routes;

import io.javalin.http.Context;
import io.linuxserver.fleet.auth.AuthenticationResult;
import io.linuxserver.fleet.core.FleetAppController;
import io.linuxserver.fleet.v2.web.PageModelSpec;
import io.linuxserver.fleet.v2.web.SessionAttributes;

public class LoginController extends AbstractPageHandler {

    public LoginController(final FleetAppController controller) {
        super(controller);
    }

    @Override
    protected PageModelSpec handlePageLoad(final Context ctx) {

        if (null != ctx.queryParam("invalidate")) {
            doLogOut(ctx);
        }

        return new PageModelSpec("views/pages/login.ftl");
    }

    @Override
    protected PageModelSpec handleFormSubmission(final Context ctx) {

        if (null != ctx.formParam("invalidate")) {
            doLogOut(ctx);
        } else {

            final boolean loggedIn = doLogIn(ctx);
            if (!loggedIn) {

                final PageModelSpec pageModelSpec = new PageModelSpec("views/pages/login.ftl");
                pageModelSpec.addModelAttribute("loginFailed", "Username or password was incorrect.");
                return pageModelSpec;
            }
        }

        return new PageModelSpec("redirect:/");
    }

    private boolean doLogIn(final Context ctx) {

        final String username = ctx.formParam("username");
        final String password = ctx.formParam("password");

        final AuthenticationResult result = getController().authenticateCredentials(username, password);
        if (result.isAuthenticated()) {

            ctx.sessionAttribute(SessionAttributes.AuthenticatedUser, result.getUser());
            return true;

        } else {
            return false;
        }
    }

    private void doLogOut(final Context ctx) {
        ctx.req.getSession(false).invalidate();
    }
}
