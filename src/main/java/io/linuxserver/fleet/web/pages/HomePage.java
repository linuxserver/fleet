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

import io.linuxserver.fleet.delegate.ImageDelegate;
import io.linuxserver.fleet.delegate.RepositoryDelegate;
import io.linuxserver.fleet.model.Repository;
import io.linuxserver.fleet.model.RepositoryWithImages;
import spark.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomePage extends WebPage {

    private final RepositoryDelegate repositoryDelegate;
    private final ImageDelegate      imageDelegate;

    public HomePage(RepositoryDelegate repositoryDelegate, ImageDelegate imageDelegate) {

        this.repositoryDelegate = repositoryDelegate;
        this.imageDelegate      = imageDelegate;
    }

    @Override
    protected ModelAndView handle(Request request) {

        Map<String, Object> model = new HashMap<>();

        List<RepositoryWithImages> populatedRepositories = new ArrayList<>();

        List<Repository> repositories = repositoryDelegate.fetchAllRepositories();
        for (Repository repository : repositories) {

            if (repository.isSyncEnabled())
                populatedRepositories.add(new RepositoryWithImages(repository, imageDelegate.fetchImagesByRepository(repository.getId())));
        }

        model.put("populatedRepositories", populatedRepositories);

        return new ModelAndView(model, "home.ftl");
    }
}
