package io.linuxserver.fleet.db.dao;

import io.linuxserver.fleet.db.query.InsertUpdateResult;
import io.linuxserver.fleet.model.internal.Repository;

import java.util.List;

public interface RepositoryDAO {

    Repository fetchRepository(int id);

    InsertUpdateResult<Repository> saveRepository(Repository repository);

    List<Repository> fetchAllRepositories();

    Repository findRepositoryByName(String name);

    void removeRepository(int id);
}
