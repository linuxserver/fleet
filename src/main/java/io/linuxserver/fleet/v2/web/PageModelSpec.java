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

import java.util.HashMap;
import java.util.Map;

public class PageModelSpec {

    private final String              viewName;
    private final Map<String, Object> model;

    public PageModelSpec(final String viewName) {

        this.viewName = viewName;
        this.model    = new HashMap<>();
    }

    public final String getViewName() {
        return viewName;
    }

    public final Map<String, Object> getModel() {
        return model;
    }

    public void addModelAttribute(final String key, final Object value) {
        model.put(key, value);
    }
}
