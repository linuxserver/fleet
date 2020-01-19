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

import io.javalin.core.security.AccessManager;
import io.javalin.core.security.Role;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.linuxserver.fleet.auth.AuthenticatedUser;
import io.linuxserver.fleet.v2.web.AppRole;
import io.linuxserver.fleet.v2.web.Locations;
import io.linuxserver.fleet.v2.web.SessionAttributes;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class DefaultAccessManager implements AccessManager {

    @Override
    public void manage(@NotNull Handler handler, @NotNull Context ctx, @NotNull Set<Role> permittedRoles) throws Exception {

        if (permittedRoles.contains(AppRole.Anyone)) {
            handler.handle(ctx);
        } else {

            final AuthenticatedUser user = ctx.sessionAttribute(SessionAttributes.AuthenticatedUser);
            if (null == user) {
                ctx.redirect(Locations.Login);
            } else if (isUserRoleValid(user, permittedRoles)){
                handler.handle(ctx);
            } else {
                ctx.status(401);
            }
        }
    }

    private boolean isUserRoleValid(final AuthenticatedUser user, final Set<Role> permittedRoles) {

        for (Role role : permittedRoles) {
            if (user.getRoles().contains(role)) {
                return true;
            }
        }
        return false;
    }
}
