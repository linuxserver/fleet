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

package io.linuxserver.fleet.db.dao;

import io.linuxserver.fleet.db.PoolingDatabaseConnection;
import io.linuxserver.fleet.db.query.InsertUpdateResult;
import io.linuxserver.fleet.db.query.InsertUpdateStatus;
import io.linuxserver.fleet.model.internal.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static io.linuxserver.fleet.db.dao.Utils.safeClose;
import static io.linuxserver.fleet.db.dao.Utils.setNullableInt;
import static io.linuxserver.fleet.db.dao.Utils.setNullableString;

public class DefaultRepositoryDAO implements RepositoryDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultImageDAO.class);

    private final PoolingDatabaseConnection databaseConnection;

    public DefaultRepositoryDAO(PoolingDatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Repository fetchRepository(int id) {

        CallableStatement call = null;

        try (Connection connection = databaseConnection.getConnection()) {

            call = connection.prepareCall("{CALL Repository_Get(?)}");
            call.setInt(1, id);

            ResultSet results = call.executeQuery();
            if (results.next())
                return parseRepositoryFromResultSet(results);

        } catch (SQLException e) {
            LOGGER.error("Unable to retrieve repository", e);
        } finally {
            safeClose(call);
        }

        return null;
    }

    @Override
    public InsertUpdateResult<Repository> saveRepository(Repository repository) {

        CallableStatement call = null;

        try (Connection connection = databaseConnection.getConnection()) {

            call = connection.prepareCall("{CALL Repository_Save(?,?,?,?,?,?,?)}");
            setNullableInt(call, 1, repository.getId());
            call.setString(2, repository.getName());
            setNullableString(call, 3, repository.getVersionMask());
            call.setBoolean(4, repository.isSyncEnabled());

            call.registerOutParameter(5, Types.INTEGER);
            call.registerOutParameter(6, Types.INTEGER);
            call.registerOutParameter(7, Types.VARCHAR);

            call.executeUpdate();

            int repositoryId        = call.getInt(5);
            int status              = call.getInt(6);
            String statusMessage    = call.getString(7);

            if (InsertUpdateStatus.OK == status)
                return new InsertUpdateResult<>(fetchRepository(repositoryId), status, statusMessage);

            return new InsertUpdateResult<>(status, statusMessage);

        } catch (SQLException e) {

            LOGGER.error("Unable to save repository", e);
            return new InsertUpdateResult<>(null, InsertUpdateStatus.OK, "Unable to save repository");

        } finally {
            safeClose(call);
        }
    }

    @Override
    public List<Repository> fetchAllRepositories() {

        List<Repository> repositories = new ArrayList<>();

        CallableStatement call = null;

        try (Connection connection = databaseConnection.getConnection()) {

            call = connection.prepareCall("{CALL Repository_GetAll()}");

            ResultSet results = call.executeQuery();
            while (results.next())
                repositories.add(parseRepositoryFromResultSet(results));

        } catch (SQLException e) {
            LOGGER.error("Unable to get all repositories", e);
        } finally {
            safeClose(call);
        }

        return repositories;
    }

    @Override
    public Repository findRepositoryByName(String name) {

        CallableStatement call = null;

        try (Connection connection = databaseConnection.getConnection()) {

            call = connection.prepareCall("{CALL Repository_GetByName(?)}");
            call.setString(1, name);

            ResultSet results = call.executeQuery();
            if (results.next())
                return parseRepositoryFromResultSet(results);

        } catch (SQLException e) {
            LOGGER.error("Unable to retrieve repository", e);
        } finally {
            safeClose(call);
        }

        return null;
    }

    @Override
    public void removeRepository(int id) {

        CallableStatement call = null;

        try (Connection connection = databaseConnection.getConnection()) {

            call = connection.prepareCall("{CALL Repository_Delete(?)}");
            call.setInt(1, id);

            call.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error("Error when removing repository", e);
        } finally {
            safeClose(call);
        }
    }

    private Repository parseRepositoryFromResultSet(ResultSet results) throws SQLException {

        Repository repository = new Repository(results.getInt("RepositoryId"), results.getString("RepositoryName"))
            .withSyncEnabled(results.getBoolean("SyncEnabled"))
            .withVersionMask(results.getString("RepositoryVersionMask"))
            .withModifiedTime(results.getTimestamp("ModifiedTime").toLocalDateTime());

        LOGGER.debug("Parsed repository: " + repository);
        return repository;
    }
}
