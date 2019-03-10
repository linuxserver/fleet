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

import io.linuxserver.fleet.delegate.RepositoryDelegate;
import spark.ModelAndView;
import spark.Request;

import java.util.HashMap;
import java.util.Map;

public class ManageRepositoriesPage extends WebPage {

    private final RepositoryDelegate repositoryDelegate;

    public ManageRepositoriesPage(RepositoryDelegate repositoryDelegate) {
        this.repositoryDelegate = repositoryDelegate;
    }

    @Override
    protected ModelAndView handle(Request request) {

        Map<String, Object> model = new HashMap<>();
        model.put("repositories", repositoryDelegate.fetchAllRepositories());
        return new ModelAndView(model, "admin.ftl");
    }
}
