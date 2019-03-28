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

import freemarker.core.HTMLOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.Version;
import io.linuxserver.fleet.web.SessionAttribute;
import spark.*;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.Map;

public abstract class WebPage implements Route {

    private static final Configuration CONFIGURATION;
    static {

        CONFIGURATION = new Configuration(new Version(2, 3, 23));
        CONFIGURATION.setClassForTemplateLoading(FreeMarkerEngine.class, "");
        CONFIGURATION.setOutputFormat(HTMLOutputFormat.INSTANCE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object handle(Request request, Response response) {

        ModelAndView modelAndView = handle(request);

        Session session = request.session(false);
        if (null != session && null != session.attribute(SessionAttribute.USER))
            ((Map<String, Object>) modelAndView.getModel()).put("__AUTHENTICATED_USER", session.attribute(SessionAttribute.USER));

        return new FreeMarkerEngine(CONFIGURATION).render(modelAndView);
    }

    protected abstract ModelAndView handle(Request request);
}
