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
import io.linuxserver.fleet.db.query.LimitedResult;
import io.linuxserver.fleet.model.Image;
import io.linuxserver.fleet.model.ImagePullStat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static io.linuxserver.fleet.db.dao.Utils.*;

public class DefaultImageDAO implements ImageDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultImageDAO.class);

    private final PoolingDatabaseConnection databaseConnection;

    public DefaultImageDAO(PoolingDatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Image findImageByRepositoryAndImageName(int repositoryId, String imageName) {

        CallableStatement call = null;

        try (Connection connection = databaseConnection.getConnection()) {

            call = connection.prepareCall("{CALL Image_GetByName(?,?)}");
            call.setInt(1, repositoryId);
            call.setString(2, imageName);

            ResultSet results = call.executeQuery();
            if (results.next())
                return parseImageFromResultSet(results);

        } catch (SQLException e) {
            LOGGER.error("Unable to fetch image", e);
        } finally {
            safeClose(call);
        }

        return null;
    }

    @Override
    public Image fetchImage(Integer id) {

        LOGGER.debug("Fetching image by ID: " + id);

        CallableStatement call = null;

        try (Connection connection = databaseConnection.getConnection()) {

            call = connection.prepareCall("{CALL Image_Get(?)}");
            call.setInt(1, id);

            ResultSet results = call.executeQuery();
            if (results.next())
                return parseImageFromResultSet(results);

        } catch (SQLException e) {
            LOGGER.error("Unable to fetch image.", e);
        } finally {
            safeClose(call);
        }

        return null;
    }

    @Override
    public LimitedResult<Image> fetchImagesByRepository(int repositoryId) {

        List<Image> images = new ArrayList<>();

        CallableStatement call = null;

        try (Connection connection = databaseConnection.getConnection()) {

            call = connection.prepareCall("{CALL Image_GetAll(?,?)}");
            call.setInt(1, repositoryId);
            call.registerOutParameter(2, Types.INTEGER);

            ResultSet results = call.executeQuery();
            while (results.next())
                images.add(parseImageFromResultSet(results));

            int totalRecords = call.getInt(2);
            return new LimitedResult<>(images, totalRecords);

        } catch (SQLException e) {
            LOGGER.error("Unable to get all images", e);
        } finally {
            safeClose(call);
        }

        return new LimitedResult<>(images, images.size());
    }

    @Override
    public InsertUpdateResult<Image> saveImage(Image image) {

        CallableStatement call = null;

        try (Connection connection = databaseConnection.getConnection()) {

            call = connection.prepareCall("{CALL Image_Save(?,?,?,?,?,?,?,?,?,?,?,?,?)");
            setNullableInt(call, 1, image.getId());
            call.setInt(2, image.getRepositoryId());
            call.setString(3, image.getName());
            setNullableLong(call, 4, image.getPullCount());
            call.setString(5, image.getVersion());
            setNullableString(call, 6, image.getVersionMask());
            call.setBoolean(7, image.isHidden());
            call.setBoolean(8, image.isUnstable());
            call.setBoolean(9, image.isDeprecated());
            setNullableString(call, 10, image.getDeprecationReason());

            call.registerOutParameter(11, Types.INTEGER);
            call.registerOutParameter(12, Types.INTEGER);
            call.registerOutParameter(13, Types.VARCHAR);

            call.executeUpdate();

            int imageId             = call.getInt(11);
            int status              = call.getInt(12);
            String statusMessage    = call.getString(13);

            if (InsertUpdateStatus.OK == status)
                return new InsertUpdateResult<>(fetchImage(imageId), status, statusMessage);

            return new InsertUpdateResult<>(status, statusMessage);

        } catch (SQLException e) {

            LOGGER.error("Unable to save image", e);
            return new InsertUpdateResult<>(null, InsertUpdateStatus.OK, "Unable to save image");

        } finally {
            safeClose(call);
        }
    }

    @Override
    public void removeImage(Integer id) {

        CallableStatement call = null;

        try (Connection connection = databaseConnection.getConnection()) {

            call = connection.prepareCall("{CALL Image_Delete(?)}");
            call.setInt(1, id);

            call.executeUpdate();
            call.close();

        } catch (SQLException e) {
            LOGGER.error("Error when removing image", e);
        } finally {
            safeClose(call);
        }
    }

    @Override
    public List<ImagePullStat> fetchImagePullHistory(Integer imageId, ImagePullStat.GroupMode groupMode) {

        List<ImagePullStat> pullHistory = new ArrayList<>();

        CallableStatement call = null;

        try (Connection connection = databaseConnection.getConnection()) {

            call = connection.prepareCall("CALL Image_GetPullHistory(?, ?)");
            call.setInt(1, imageId);
            call.setString(2, groupMode.toString());

            ResultSet results = call.executeQuery();
            while (results.next())
                pullHistory.add(parseImagePullHistoryFromResultSet(results, groupMode));

            call.close();

        } catch (SQLException e) {
            LOGGER.error("Error when fetching image pull history", e);
        } finally {
            safeClose(call);
        }

        return pullHistory;
    }

    private ImagePullStat parseImagePullHistoryFromResultSet(ResultSet results, ImagePullStat.GroupMode groupMode) throws SQLException {

        return new ImagePullStat(
            results.getInt("ImageId"),
            results.getString("TimeGroup"),
            results.getLong("ImagePulls"),
            groupMode
        );
    }

    private Image parseImageFromResultSet(ResultSet results) throws SQLException {

        Image image = new Image(
            results.getInt("ImageId"),
            results.getInt("RepositoryId"),
            results.getString("ImageName")
        );

        return image
            .withVersion(results.getString("ImageVersion"))
            .withPullCount(results.getLong("ImagePullCount"))
            .withVersionMask(results.getString("ImageVersionMask"))
            .withModifiedTime(results.getTimestamp("ModifiedTime").toLocalDateTime())
            .withHidden(results.getBoolean("ImageHidden"))
            .withUnstable(results.getBoolean("ImageUnstable"))
            .withDeprecated(results.getBoolean("ImageDeprecated"))
            .withDeprecationReason(results.getString("ImageDeprecationReason"));
    }
}
