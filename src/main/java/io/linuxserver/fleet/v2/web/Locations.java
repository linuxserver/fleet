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

public interface Locations {

    interface Static {

        String Static  = "/static";
        String Assets  = "/assets";
    }

    // Error handling
    String UnhandledError   = "/error";
    String MethodNotAllowed = "/error";

    // All pages
    String Home  = "/";
    String Login = "/login";
    String Image = "/image";

    interface Api {
        String Images = "/api/v1/images";
    }

    interface Internal {
        String Api        = "/internalapi";
        String Repository = "repository";
        String Image      = "image";
        String Schedule   = "schedule";
        String Sync       = "sync";
        String Stats      = "stats";
    }

    interface Admin {

        String Repositories = "/admin/repositories";
        String Images       = "/admin/images";
        String Schedules    = "/admin/schedules";
        String Users        = "/admin/users";
    }
}
