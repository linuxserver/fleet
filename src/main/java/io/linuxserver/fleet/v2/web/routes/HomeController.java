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
import io.linuxserver.fleet.v2.key.RepositoryKey;
import io.linuxserver.fleet.v2.service.RepositoryManager;
import io.linuxserver.fleet.v2.types.Repository;
import io.linuxserver.fleet.v2.web.PageModelSpec;

import java.util.Collections;

public class HomeController extends AbstractPageHandler {

    private final RepositoryManager repositoryManager;

    public HomeController(final RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    @Override
    protected PageModelSpec handlePageLoad(final Context ctx) {

        final PageModelSpec modelSpec = new PageModelSpec("views/pages/home.ftl");

        final String repositoryLookupParam = ctx.queryParam("key");
        if (null == repositoryLookupParam) {

            final Repository repository = repositoryManager.getFirstRepository();
            setSingleRepository(modelSpec, repository);

        } else {

            final RepositoryKey repositoryLookupKey = RepositoryKey.parse(repositoryLookupParam);
            final Repository repository = repositoryManager.getRepository(repositoryLookupKey);
            setSingleRepository(modelSpec, repository);
        }

        modelSpec.addModelAttribute("availableRepositories", repositoryManager.getAllSynchronisedRepositories());

        return modelSpec;
    }

    private void setSingleRepository(PageModelSpec modelSpec, Repository repository) {

        if (null != repository) {
            modelSpec.addModelAttribute("selectedRepository", repository);
        }
    }

    @Override
    protected PageModelSpec handleFormSubmission(final Context ctx) {
        return new PageModelSpec("views/pages/unsupported.ftl");
    }
}
