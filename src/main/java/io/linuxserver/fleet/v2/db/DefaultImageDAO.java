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

import io.linuxserver.fleet.core.db.DatabaseProvider;
import io.linuxserver.fleet.db.query.InsertUpdateResult;
import io.linuxserver.fleet.db.query.InsertUpdateStatus;
import io.linuxserver.fleet.v2.key.HasKey;
import io.linuxserver.fleet.v2.key.ImageKey;
import io.linuxserver.fleet.v2.key.RepositoryKey;
import io.linuxserver.fleet.v2.key.TagBranchKey;
import io.linuxserver.fleet.v2.types.*;
import io.linuxserver.fleet.v2.types.internal.ImageOutlineRequest;
import io.linuxserver.fleet.v2.types.internal.RepositoryOutlineRequest;
import io.linuxserver.fleet.v2.types.internal.TagBranchOutlineRequest;
import io.linuxserver.fleet.v2.types.meta.*;
import io.linuxserver.fleet.v2.types.meta.history.ImagePullHistory;
import io.linuxserver.fleet.v2.types.meta.history.ImagePullStatistic;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultImageDAO extends AbstractDAO implements ImageDAO {

    private static final String GetRepositoryKeys        = "{CALL Repository_GetRepositoryKeys()}";
    private static final String GetRepository            = "{CALL Repository_Get(?)}";
    private static final String DeleteRepository         = "{CALL Repository_Delete(?,?)}";
    private static final String GetImageKeys             = "{CALL Repository_GetImageKeys(?)}";
    private static final String CreateRepositoryOutline  = "{CALL Repository_CreateOutline(?,?,?,?,?,?,?,?)}";
    private static final String StoreRepository          = "{CALL Repository_Store(?,?,?,?)}";

    private static final String StoreImage             = "{CALL Image_Store(?,?,?,?,?,?,?,?,?,?,?)}";
    private static final String CreateTagBranchOutline = "{CALL Image_CreateTagBranchOutline(?,?)}";
    private static final String RemoveOrphanBranches   = "{CALL Image_RemoveOrphanBranches(?,?)}";
    private static final String StoreTagBranch         = "{CALL Image_StoreTagBranch(?,?,?,?)}";
    private static final String StoreTagDigest         = "{CALL Image_StoreTagDigest(?,?,?,?,?)}";
    private static final String GetTagBranches         = "{CALL Image_GetTagBranches(?)}";
    private static final String GetTagDigests          = "{CALL Image_GetTagDigests(?)}";
    private static final String CreateImageOutline     = "{CALL Image_CreateOutline(?,?,?,?,?,?,?,?,?,?)}";
    private static final String GetImage               = "{CALL Image_Get(?)}";
    private static final String DeleteImage            = "{CALL Image_Delete(?)}";
    private static final String GetImageStats          = "{CALL Image_GetStats(?)}";
    private static final String GetExternalUrls        = "{CALL Image_GetExternalUrls(?)}";
    private static final String RemoveOrphanUrls       = "{CALL Image_RemoveOrphanUrls(?, ?)}";
    private static final String StoreCoreMetaData      = "{CALL Image_StoreCoreMetaData(?,?,?,?,?)}";
    private static final String StoreExternalUrl       = "{CALL Image_StoreExternalUrl(?,?,?,?,?)}";

    private final ImageTemplateFactory templateFactory;

    public DefaultImageDAO(final DatabaseProvider databaseConnection) {
        super(databaseConnection);
        this.templateFactory = new ImageTemplateFactory();
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
                Utils.setNullableString(call, i++, image.getVersionMask());

                call.registerOutParameter(i, Types.VARCHAR);

                final ResultSet results = call.executeQuery();

                final DbUpdateStatus status = DbUpdateStatus.valueOf(call.getString(i));
                if (status.isNoChange()) {
                    getLogger().warn("storeImage attempted to update an image which did not exist in the database: {}", image);
                } else if (results.next()) {

                    storeTagBranches(connection, image);
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
    public InsertUpdateResult<Image> storeImageMetaData(final Image image) {

        try (final Connection connection = getConnection()) {

            storeCoreMetaData(connection, image);
            storeExternalUrls(connection, image);

            templateFactory.storeImageTemplates(connection, image);

            return new InsertUpdateResult<>(makeImage(image.getKey(), connection));

        } catch (SQLException e) {

            getLogger().error("Error caught when executing SQL: storeImageMetaData", e);
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
                Utils.setNullableTimestamp(call, i++, request.getImageLastUpdated());
                call.setBoolean(i++, ItemSyncSpec.Default.isDeprecated());
                call.setBoolean(i++, ItemSyncSpec.Default.isHidden());
                call.setBoolean(i++, ItemSyncSpec.Default.isStable());
                call.setBoolean(i++, ItemSyncSpec.Default.isSynchronised());
                call.setString(i++, ItemSyncSpec.Default.getVersionMask());

                final int statusIndex = i;
                call.registerOutParameter(statusIndex, Types.VARCHAR);

                final ResultSet results = call.executeQuery();

                final DbUpdateStatus status = DbUpdateStatus.valueOf(call.getString(statusIndex));
                if (status.isExists()) {
                    return new InsertUpdateResult<>(InsertUpdateStatus.FAILED, "Image already exists");
                }

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
    public InsertUpdateResult<TagBranch> createTagBranchOutline(final TagBranchOutlineRequest request) {

        try (final Connection connection = getConnection()) {

            try (final CallableStatement call = connection.prepareCall(CreateTagBranchOutline)) {

                int i = 1;
                call.setInt(i++, request.getImageKey().getId());
                call.setString(i, request.getBranchName());

                final ResultSet results = call.executeQuery();
                if (results.next()) {
                    return new InsertUpdateResult<>(makeTagBranch(results, connection, request.getImageKey()));
                }

                return new InsertUpdateResult<>(InsertUpdateStatus.FAILED, "createTagBranchOutline did not return anything.");
            }

        } catch (SQLException e) {

            getLogger().error("Error caught when executing SQL: createTagBranchOutline", e);
            return new InsertUpdateResult<>(InsertUpdateStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public InsertUpdateResult<Void> removeImage(final Image image) {

        try (final Connection connection = getConnection()) {

            try (final CallableStatement call = connection.prepareCall(DeleteImage)) {

                call.setInt(1, image.getKey().getId());
                call.registerOutParameter(2, Types.VARCHAR);
                call.executeUpdate();

                final DbUpdateStatus status = DbUpdateStatus.valueOf(call.getString(2));
                if (status.isNoChange()) {

                    getLogger().warn("removeImage attempted to remove an image which did not exist in the database: {}", image);
                    return new InsertUpdateResult<>(InsertUpdateStatus.FAILED, "Unable to remove image " + image);
                }

                return new InsertUpdateResult<>(null);
            }

        } catch (SQLException e) {

            getLogger().error("Error caught when executing SQL: removeImage", e);
            return new InsertUpdateResult<>(InsertUpdateStatus.FAILED, e.getMessage());
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

    @Override
    public InsertUpdateResult<Repository> createRepositoryOutline(final RepositoryOutlineRequest request) {

        try (final Connection connection = getConnection()) {

            try (final CallableStatement call = connection.prepareCall(CreateRepositoryOutline)) {

                int i = 1;
                call.setString(   i++,   request.getRepositoryName());
                call.setTimestamp(i++, Timestamp.valueOf(LocalDateTime.now()));
                call.setBoolean(  i++, ItemSyncSpec.Default.isDeprecated());
                call.setBoolean(  i++, ItemSyncSpec.Default.isHidden());
                call.setBoolean(  i++, ItemSyncSpec.Default.isStable());
                call.setBoolean(  i++, ItemSyncSpec.Default.isSynchronised());
                call.setString(   i++, ItemSyncSpec.Default.getVersionMask());

                final int statusIndex = i;
                call.registerOutParameter(statusIndex, Types.VARCHAR);

                final ResultSet results = call.executeQuery();

                final DbUpdateStatus status = DbUpdateStatus.valueOf(call.getString(statusIndex));
                if (status.isExists()) {
                    return new InsertUpdateResult<>(InsertUpdateStatus.FAILED, "Repository already exists");
                }

                if (results.next()) {
                    return new InsertUpdateResult<>(makeRepository(makeRepositoryKey(results), connection));
                }

                return new InsertUpdateResult<>(InsertUpdateStatus.FAILED, "createRepositoryOutline did not return anything.");

            }

        } catch (SQLException e) {

            getLogger().error("Error caught when executing SQL: createRepositoryOutline", e);
            return new InsertUpdateResult<>(InsertUpdateStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public List<Repository> fetchAllRepositories() {

        try (final Connection connection = getConnection()) {

            try (final CallableStatement call = connection.prepareCall(GetRepositoryKeys)) {

                final ResultSet results = call.executeQuery();

                final List<RepositoryKey> repositoryKeys = new ArrayList<>();
                while (results.next()) {
                    repositoryKeys.add(makeRepositoryKey(results));
                }
                return makeRepositories(repositoryKeys, connection);
            }

        } catch (SQLException e) {

            getLogger().error("Error caught when executing SQL: fetchAllRepositories", e);
            throw new RuntimeException("fetchAllRepositories", e);
        }
    }

    @Override
    public InsertUpdateResult<Repository> storeRepository(Repository repository) {

        try (final Connection connection = getConnection()) {

            try (final CallableStatement call = connection.prepareCall(StoreRepository)) {

                int i = 1;
                call.setInt(i++,     repository.getKey().getId());
                call.setBoolean(i++, repository.getSpec().isSynchronised());
                call.setString(i++,  repository.getSpec().getVersionMask());

                final int statusIndex = i;
                call.registerOutParameter(statusIndex, Types.VARCHAR);

                call.executeUpdate();

                final DbUpdateStatus status = DbUpdateStatus.valueOf(call.getString(statusIndex));
                if (status.isUpdated()) {
                    return new InsertUpdateResult<>(makeRepository(repository.getKey(), connection));
                }

                return new InsertUpdateResult<>(InsertUpdateStatus.FAILED, "Repository was not updated.");
            }

        } catch (SQLException e) {

            getLogger().error("Error caught when executing SQL: storeRepository", e);
            return new InsertUpdateResult<>(InsertUpdateStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public InsertUpdateResult<Void> removeRepository(final Repository repository) {

        try (final Connection connection = getConnection()) {

            try (final CallableStatement call = connection.prepareCall(DeleteRepository)) {

                call.setInt(1, repository.getKey().getId());
                call.registerOutParameter(2, Types.VARCHAR);

                call.executeUpdate();

                final DbUpdateStatus status = DbUpdateStatus.valueOf(call.getString(2));
                if (status.isNoChange()) {

                    getLogger().warn("Attempted to delete repository {} but resulted in no change", repository);
                    return new InsertUpdateResult<>(InsertUpdateStatus.FAILED, "Unable to remove repository " + repository.getName());
                }

                return new InsertUpdateResult<>(null);
            }

        } catch (SQLException e) {

            getLogger().error("Error caught when executing SQL: removeRepository.", e);
            return new InsertUpdateResult<>(InsertUpdateStatus.FAILED, e.getMessage());
        }
    }

    private void storeCoreMetaData(final Connection connection, final Image image) throws SQLException {

        try (final CallableStatement call = connection.prepareCall(StoreCoreMetaData)) {

            int i = 1;
            call.setInt(i++, image.getKey().getId());

            Utils.setNullableString(call, i++, image.getMetaData().getCategory());
            Utils.setNullableString(call, i++, image.getMetaData().getBaseImage());
            Utils.setNullableString(call, i++, image.getMetaData().getAppImagePath());

            final int statusIndex = i;
            call.registerOutParameter(statusIndex, Types.VARCHAR);
            call.executeUpdate();

            final DbUpdateStatus status = DbUpdateStatus.valueOf(call.getString(statusIndex));
            getLogger().info("storeCoreMetaData stored with result " + status);
        }
    }

    private void storeExternalUrls(final Connection connection, final Image image) throws SQLException {

        final List<ExternalUrl> urls = image.getMetaData().getExternalUrls();
        removeOrphans(connection, image.getKey(), urls, RemoveOrphanUrls);

        try (final CallableStatement call = connection.prepareCall(StoreExternalUrl)) {

            for (ExternalUrl url : urls) {

                int i = 1;
                call.setInt(   i++, image.getKey().getId());
                call.setInt(   i++, url.getKey().getId());
                call.setString(i++, url.getType().name());
                call.setString(i++, url.getName());
                call.setString(i,   url.getAbsoluteUrl());

                call.addBatch();
            }

            call.executeBatch();
        }
    }

    private void storeTagBranches(final Connection connection, final Image image) throws SQLException {

        final List<TagBranch> tagBranches = image.getTagBranches();
        removeOrphans(connection, image.getKey(), tagBranches, RemoveOrphanBranches);

        try (final CallableStatement call = connection.prepareCall(StoreTagBranch)) {

            for (TagBranch tagBranch : tagBranches) {

                int i = 1;
                call.setInt(i++,     tagBranch.getKey().getImageKey().getId());
                call.setInt(i++,     tagBranch.getKey().getId());
                call.setString(i++,  tagBranch.getLatestTag().getVersion());
                call.setTimestamp(i, Timestamp.valueOf(tagBranch.getLatestTag().getBuildDate()));

                call.addBatch();
            }

            call.executeBatch();
        }

        for (TagBranch tagBranch : tagBranches) {
            storeTagDigests(connection, tagBranch);
        }
    }

    private void removeOrphans(final Connection connection,
                               final ImageKey imageKey,
                               final List<? extends HasKey<?>> possibleOrphans,
                               final String sprocSpec) throws SQLException {

        final String ids = possibleOrphans.stream().map(o -> String.valueOf(o.getKey().getId())).collect(Collectors.joining(","));

        try (final CallableStatement call = connection.prepareCall(sprocSpec)) {

            call.setInt(   1, imageKey.getId());
            call.setString(2, ids);
            call.executeUpdate();
        }
    }

    private void storeTagDigests(final Connection connection, final TagBranch tagBranch) throws SQLException {

        try (final CallableStatement call = connection.prepareCall(StoreTagDigest)) {

            for (TagDigest digest : tagBranch.getLatestTag().getDigests()) {

                int i = 1;
                call.setInt(i++,    tagBranch.getKey().getId());
                call.setLong(i++,   digest.getSize());
                call.setString(i++, digest.getDigest());
                call.setString(i++, digest.getArchitecture());
                call.setString(i,   digest.getArchVariant());

                call.addBatch();
            }

            call.executeBatch();
        }
    }

    private List<Repository> makeRepositories(final List<RepositoryKey> repositoryKeys, final Connection connection) throws SQLException {

        final List<Repository> repositories = new ArrayList<>();
        try (final CallableStatement call = connection.prepareCall(GetRepository)) {

            for (RepositoryKey key : repositoryKeys) {

                call.setInt(1, key.getId());

                final Repository repository = makeOneRepository(connection, call);
                if (null != repository) {
                    repositories.add(repository);
                } else {
                    getLogger().warn("makeRepositories attempted to make repository for key {} but none exists. Skipping.", key);
                }
            }
        }
        return repositories;
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

            final ImageKey imageKey = makeImageKey(results);

            final Image image = new Image(imageKey,
                                          makeSyncSpec(results),
                                          makeImageMetaData(connection, imageKey, results),
                                          makeCountData(results),
                                          results.getString("Description"),
                                          results.getTimestamp("LastUpdated").toLocalDateTime());

            enrichImageWithTagBranches(image, connection);

            return image;
        }

        return null;
    }

    private ImageMetaData makeImageMetaData(final Connection connection, final ImageKey imageKey, final ResultSet mainImageResults) throws SQLException {

        return new ImageMetaData(makeCoreMeta(connection, imageKey, mainImageResults),
                                 makePullHistory(connection, imageKey),
                                 templateFactory.makeTemplateHolder(connection, imageKey));
    }

    private ImageCoreMeta makeCoreMeta(final Connection connection, final ImageKey imageKey, final ResultSet mainImageResults) throws SQLException {

        final ImageCoreMeta coreMeta = new ImageCoreMeta(mainImageResults.getString("CoreMetaImagePath"),
                                                         mainImageResults.getString("CoreMetaBaseImage"),
                                                         mainImageResults.getString("CoreMetaCategory"));

        makeExternalUrls(connection, imageKey).forEach(coreMeta::addExternalUrl);
        return coreMeta;
    }

    private List<ExternalUrl> makeExternalUrls(final Connection connection, final ImageKey imageKey) throws SQLException {

        final List<ExternalUrl> externalUrls = new ArrayList<>();

        try (final CallableStatement call = connection.prepareCall(GetExternalUrls)) {

            call.setInt(1, imageKey.getId());

            final ResultSet results = call.executeQuery();
            while (results.next()) {

                externalUrls.add(new ExternalUrl(new ExternalUrlKey(results.getInt("UrlId")),
                                                 ExternalUrl.ExternalUrlType.valueOf(results.getString("UrlType")),
                                                 results.getString("UrlName"),
                                                 results.getString("UrlPath")));
            }
        }

        return externalUrls;
    }

    private ImagePullHistory makePullHistory(final Connection connection, final ImageKey imageKey) throws SQLException {

        final ImagePullHistory pullHistory = new ImagePullHistory();
        try (final CallableStatement call = connection.prepareCall(GetImageStats)) {

            call.setInt(1, imageKey.getId());
            final ResultSet results = call.executeQuery();

            while (results.next()) {

                final long imagePulls = results.getLong("ImagePulls");
                if (!results.wasNull()) {

                    final String timeGroup = results.getString("TimeGroup");
                    final String groupMode = results.getString("GroupMode");

                    final ImagePullStatistic statistic = new ImagePullStatistic(imagePulls,
                            timeGroup,
                            ImagePullStatistic.StatGroupMode.valueOf(groupMode));

                    final boolean added = pullHistory.addStatistic(statistic);
                    if (!added) {
                        getLogger().warn("Unable to add pull history {} to image {}", statistic, imageKey);
                    }
                }
            }
        }
        return pullHistory;
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

        final TagBranchKey tagBranchKey = makeTagBranchKey(results, imageKey);

        return new TagBranch(tagBranchKey,
                             results.getString("BranchName"),
                             results.getBoolean("BranchProtected"),
                             makeTag(results, connection, tagBranchKey));
    }

    private TagBranchKey makeTagBranchKey(ResultSet results, ImageKey imageKey) throws SQLException {
        return new TagBranchKey(results.getInt("BranchId"), imageKey);
    }

    private Tag makeTag(final ResultSet results, final Connection connection, final TagBranchKey tagBranchKey) throws SQLException {

        return new Tag(results.getString("TagVersion"),
                       results.getTimestamp("TagBuildDate").toLocalDateTime(),
                       makeTagDigests(connection, tagBranchKey));
    }

    private Set<TagDigest> makeTagDigests(final Connection connection, final TagBranchKey tagBranchKey) throws SQLException {

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
