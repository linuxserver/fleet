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

package io.linuxserver.fleet.web.pages;

import io.linuxserver.fleet.web.SessionAttribute;
import spark.*;

import java.util.Map;

public abstract class WebPage implements TemplateViewRoute {

    @Override
    @SuppressWarnings("unchecked")
    public ModelAndView handle(Request request, Response response) {

        ModelAndView modelAndView = handle(request);

        Session session = request.session(false);
        if (null != session && null != session.attribute(SessionAttribute.USER))
            ((Map<String, Object>) modelAndView.getModel()).put("__AUTHENTICATED_USER", session.attribute(SessionAttribute.USER));

        return modelAndView;
    }

    protected abstract ModelAndView handle(Request request);
}
