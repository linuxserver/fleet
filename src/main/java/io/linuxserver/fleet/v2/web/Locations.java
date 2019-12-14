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

    // Api endpoints
    String ApiImages = "/api/v1/images";

    // Pages/endpoints which do not require authentication
    String[] UnAuthenticated = {
        Home, Image, ApiImages, Login, Static.Assets
    };
}
