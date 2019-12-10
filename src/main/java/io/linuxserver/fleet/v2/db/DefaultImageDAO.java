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

package io.linuxserver.fleet.v2.db;

import io.linuxserver.fleet.db.DefaultDatabaseConnection;
import io.linuxserver.fleet.db.query.InsertUpdateResult;
import io.linuxserver.fleet.db.query.InsertUpdateStatus;
import io.linuxserver.fleet.v2.key.ImageKey;
import io.linuxserver.fleet.v2.key.RepositoryKey;
import io.linuxserver.fleet.v2.key.TagBranchKey;
import io.linuxserver.fleet.v2.types.*;
import io.linuxserver.fleet.v2.types.internal.ImageOutlineRequest;
import io.linuxserver.fleet.v2.types.internal.TagBranchOutlineRequest;
import io.linuxserver.fleet.v2.types.meta.ItemSyncSpec;
import sun.util.locale.provider.LocaleServiceProviderPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DefaultImageDAO extends AbstractDAO implements ImageDAO {

    private static final String GetRepository      = "{CALL Repository_Get(?)}";
    private static final String GetImageKeys       = "{CALL Repository_GetImageKeys(?)}";

    private static final String StoreImage         = "{CALL Image_Store(?,?,?,?,?,?,?,?,?,?)}";
    private static final String StoreTagBranch     = "{CALL Image_StoreTagBranch(?,?,?,?)}";
    private static final String StoreTagDigest     = "{CALL Image_StoreTagDigest(?,?,?,?,?)}";
    private static final String CreateImageOutline = "{CALL Image_CreateOutline(?,?,?,?)}";
    private static final String GetImage           = "{CALL Image_Get(?)}";
    private static final String GetTagBranches     = "{CALL Image_GetTagBranches(?)}";
    private static final String GetTagDigests      = "{CALL Image_GetTagDigests(?)}";
    private static final String DeleteImage        = "{CALL Image_Delete(?)}";

    public DefaultImageDAO(final DefaultDatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    @Override
    public Image fetchImage(final ImageKey imageKey) {

        try (final Connection connection = getConnection()) {

            return makeImage(imageKey, connection);

        } catch (SQLException e) {

            getLogger().error("Error caught when executing SQL: fetchImage", e);
            throw new RuntimeException("fetchImage", e);
        }
    }

    @Override
    public InsertUpdateResult<Image> storeImage(final Image image) {

        try (final Connection connection = getConnection()) {

            try (final CallableStatement call = connection.prepareCall(StoreImage)) {

                int i = 1;

                call.setInt(i++, image.getKey().getId());
                call.setLong(i++, image.getPullCount());
                call.setInt(i++, image.getStarCount());
                Utils.setNullableString(call, i++, image.getDescription());
                Utils.setNullableTimestamp(call, i++, image.getLastUpdated());

                call.setBoolean(i++, image.isDeprecated());
                call.setBoolean(i++, image.isHidden());
                call.setBoolean(i++, image.isStable());
                call.setBoolean(i++, image.isSyncEnabled());
                Utils.setNullableString(call, i, image.getVersionMask());

                final ResultSet results = call.executeQuery();
                if (results.next()) {
                    return new InsertUpdateResult<>(makeImage(makeImageKey(results), connection));
                }

                return new InsertUpdateResult<>(InsertUpdateStatus.FAILED, "storeImage did not return anything.");
            }

        } catch (SQLException e) {

            getLogger().error("Error caught when executing SQL: storeImage", e);
            return new InsertUpdateResult<>(InsertUpdateStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public InsertUpdateResult<Image> createImageOutline(final ImageOutlineRequest request) {

        try (final Connection connection = getConnection()) {

            try (final CallableStatement call = connection.prepareCall(CreateImageOutline)) {

                int i = 1;
                call.setInt(i++, request.getRepositoryKey().getId());
                call.setString(i++, request.getImageName());
                call.setString(i++, request.getImageDescription());
                Utils.setNullableTimestamp(call, i, request.getImageLastUpdated());

                final ResultSet results = call.executeQuery();
                if (results.next()) {
                    return new InsertUpdateResult<>(makeImage(makeImageKey(results), connection));
                }

                return new InsertUpdateResult<>(InsertUpdateStatus.FAILED, "createImageOutline did not return anything.");
            }

        } catch (SQLException e) {

            getLogger().error("Error caught when executing SQL: createImageOutline", e);
            return new InsertUpdateResult<>(InsertUpdateStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public InsertUpdateResult<TagBranch> storeTagBranchOutline(final TagBranchOutlineRequest request) {


        try (final Connection connection = getConnection()) {

            try (final CallableStatement call = connection.prepareCall(StoreTagBranch)) {

                int i = 1;
                call.setInt(i++, request.getImageKey().getId());
                call.setString(i++, request.getBranchName());
                call.setString(i++, request.getLatestTag().getVersion());
                Utils.setNullableTimestamp(call, i, request.getLatestTag().getBuildDate());

                final ResultSet results = call.executeQuery();
                if (results.next()) {

                    storeTagDigests(connection, makeTagBranchKey(results, request.getImageKey()), request.getLatestTag().getDigests());
                    return new InsertUpdateResult<>(makeTagBranch(results, connection, request.getImageKey()));
                }

                return new InsertUpdateResult<>(InsertUpdateStatus.FAILED, "storeTagBranchOutline did not return anything.");
            }

        } catch (SQLException e) {

            getLogger().error("Error caught when executing SQL: storeTagBranchOutline", e);
            return new InsertUpdateResult<>(InsertUpdateStatus.FAILED, e.getMessage());
        }
    }

    private void storeTagDigests(final Connection connection, final TagBranchKey branchKey, final List<TagDigest> digests) throws SQLException {

        try (final CallableStatement call = connection.prepareCall(StoreTagDigest)) {

            for (TagDigest digest : digests) {

                int i = 1;
                call.setInt(i++,    branchKey.getId());
                call.setLong(i++,   digest.getSize());
                call.setString(i++, digest.getDigest());
                call.setString(i++, digest.getArchitecture());
                call.setString(i,   digest.getArchVariant());

                call.addBatch();
            }

            call.executeBatch();
        }
    }

    @Override
    public void removeImage(final Image image) {


        try (final Connection connection = getConnection()) {

            try (final CallableStatement call = connection.prepareCall(DeleteImage)) {

                call.setInt(1, image.getKey().getId());
                call.registerOutParameter(2, Types.VARCHAR);
                call.executeUpdate();

                final DbUpdateStatus status = DbUpdateStatus.valueOf(call.getString(2));
                if (status.isNoChange()) {
                    getLogger().warn("removeImage attempted to remove an image which did not exist in the database: {}", image);
                }
            }

        } catch (SQLException e) {

            getLogger().error("Error caught when executing SQL: removeImage", e);
            throw new RuntimeException("removeImage unable to delete image", e);
        }
    }

    @Override
    public Repository fetchRepository(final RepositoryKey repositoryKey) {

        try (final Connection connection = getConnection()) {
            return makeRepository(repositoryKey, connection);
        } catch (SQLException e) {

            getLogger().error("Error caught when executing SQL: fetchRepository", e);
            throw new RuntimeException("fetchRepository", e);
        }
    }

    private Image makeImage(final ImageKey imageKey, final Connection connection) throws SQLException {

        try (final CallableStatement call = connection.prepareCall(GetImage)) {

            call.setInt(1, imageKey.getId());

            final Image image = makeOneImage(connection, call);

            if (null == image) {
                getLogger().info("No image with key {} found", imageKey);
            }

            return image;
        }
    }

    private List<Image> makeImages(final List<ImageKey> imageKeys, final Connection connection) throws SQLException {

        final List<Image> images = new ArrayList<>();
        try (final CallableStatement call = connection.prepareCall(GetImage)) {

            for (ImageKey key : imageKeys) {

                call.setInt(1, key.getId());

                final Image image = makeOneImage(connection, call);

                if (null != image) {
                    images.add(image);
                } else {
                    getLogger().warn("makeImages attempted to make image for key {} but none exists. Skipping.", key);
                }
            }
        }
        return images;
    }

    private Image makeOneImage(final Connection connection, final CallableStatement call) throws SQLException {

        final ResultSet results = call.executeQuery();

        if (results.next()) {

            final Image image = new Image(makeImageKey(results),
                                          makeSyncSpec(results),
                                          makeCountData(results),
                                          results.getString("Description"),
                                          results.getTimestamp("LastUpdated").toLocalDateTime());

            enrichImageWithTagBranches(image, connection);

            return image;
        }

        return null;
    }

    private ImageKey makeImageKey(final ResultSet results) throws SQLException {

        return new ImageKey(results.getInt("ImageId"),
                            results.getString("ImageName"),
                            makeRepositoryKey(results));
    }

    private Repository makeRepository(final RepositoryKey repositoryKey, final Connection connection) throws SQLException {

        try (final CallableStatement call = connection.prepareCall(GetRepository)) {

            call.setInt(1, repositoryKey.getId());
            return makeOneRepository(connection, call);
        }
    }

    private Repository makeOneRepository(final Connection connection, final CallableStatement call) throws SQLException {

        final ResultSet results = call.executeQuery();

        if (results.next()) {

            final Repository repository = new Repository(makeRepositoryKey(results),
                                                         makeSyncSpec(results));

            enrichRepositoryWithImages(repository, connection);

            return repository;
        }

        return null;
    }

    private void enrichRepositoryWithImages(final Repository repository, final Connection connection) throws SQLException {

        try (final CallableStatement call = connection.prepareCall(GetImageKeys)) {

            call.setInt(1, repository.getKey().getId());

            final List<ImageKey> repositoryImageKeys = new ArrayList<>();

            final ResultSet results = call.executeQuery();
            while (results.next()) {
                repositoryImageKeys.add(makeImageKey(results));
            }

            makeImages(repositoryImageKeys, connection).forEach(repository::addImage);
        }
    }

    private RepositoryKey makeRepositoryKey(final ResultSet results) throws SQLException {

        return new RepositoryKey(results.getInt("RepositoryId"),
                                 results.getString("RepositoryName"));
    }

    private ItemSyncSpec makeSyncSpec(final ResultSet results) throws SQLException {

        return new ItemSyncSpec(results.getBoolean("Deprecated"),
                                results.getBoolean("Hidden"),
                                results.getBoolean("Stable"),
                                results.getBoolean("SyncEnabled"),
                                results.getString("VersionMask"));
    }

    private ImageCountData makeCountData(final ResultSet results) throws SQLException {

        return new ImageCountData(results.getLong("LatestPullCount"),
                                  results.getInt("LatestStarCount"));
    }

    private void enrichImageWithTagBranches(final Image image, final Connection connection) throws SQLException {

        try (final CallableStatement call = connection.prepareCall(GetTagBranches)) {

            call.setInt(1, image.getKey().getId());

            final ResultSet results = call.executeQuery();
            while (results.next()) {
                image.addTagBranch(makeTagBranch(results, connection, image.getKey()));
            }
        }
    }

    private TagBranch makeTagBranch(final ResultSet results, final Connection connection, final ImageKey imageKey) throws SQLException {

        final TagBranchKey branchKey = makeTagBranchKey(results, imageKey);

        return new TagBranch(branchKey,
                             results.getString("BranchName"),
                             makeTag(results, connection, branchKey));
    }

    private TagBranchKey makeTagBranchKey(ResultSet results, ImageKey imageKey) throws SQLException {
        return new TagBranchKey(results.getInt("BranchId"), imageKey);
    }

    private Tag makeTag(final ResultSet results, final Connection connection, final TagBranchKey tagBranchKey) throws SQLException {

        return new Tag(results.getString("TagVersion"),
                       results.getTimestamp("TagBuildDate").toLocalDateTime(),
                       makeTagDigests(tagBranchKey, connection));
    }

    private Set<TagDigest> makeTagDigests(final TagBranchKey tagBranchKey, final Connection connection) throws SQLException {

        final Set<TagDigest> digests = new HashSet<>();

        try (final CallableStatement call = connection.prepareCall(GetTagDigests)) {

            call.setInt(1, tagBranchKey.getId());

            final ResultSet results = call.executeQuery();
            while (results.next()) {
                digests.add(makeTagDigest(results));
            }
        }

        return digests;
    }

    private TagDigest makeTagDigest(final ResultSet results) throws SQLException {

        return new TagDigest(results.getLong("DigestSize"),
                             results.getString("DigestSha"),
                             results.getString("DigestArch"),
                             results.getString("DigestVariant"));
    }
}
