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

package io.linuxserver.fleet.delegate;

import io.linuxserver.fleet.db.dao.RepositoryDAO;
import io.linuxserver.fleet.db.query.InsertUpdateResult;
import io.linuxserver.fleet.db.query.InsertUpdateStatus;
import io.linuxserver.fleet.exception.SaveException;
import io.linuxserver.fleet.model.Repository;

import java.util.List;

public class RepositoryDelegate {

    private final RepositoryDAO repositoryDAO;

    public RepositoryDelegate(RepositoryDAO repositoryDAO) {
        this.repositoryDAO = repositoryDAO;
    }

    public Repository fetchRepository(int id) {
        return repositoryDAO.fetchRepository(id);
    }

    public Repository saveRepository(Repository repository) throws SaveException {

        InsertUpdateResult<Repository> result = repositoryDAO.saveRepository(repository);

        if (result.getStatus() == InsertUpdateStatus.OK)
            return result.getResult();

        throw new SaveException(result.getStatusMessage());
    }

    public List<Repository> fetchAllRepositories() {
        return repositoryDAO.fetchAllRepositories();
    }

    public Repository findRepositoryByName(String name) {
        return repositoryDAO.findRepositoryByName(name);
    }

    public void removeRepository(int id) {
        repositoryDAO.removeRepository(id);
    }
}
