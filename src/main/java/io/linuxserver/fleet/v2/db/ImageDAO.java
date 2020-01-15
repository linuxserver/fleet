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

import io.linuxserver.fleet.db.query.InsertUpdateResult;
import io.linuxserver.fleet.v2.key.ImageKey;
import io.linuxserver.fleet.v2.key.RepositoryKey;
import io.linuxserver.fleet.v2.types.Image;
import io.linuxserver.fleet.v2.types.Repository;
import io.linuxserver.fleet.v2.types.TagBranch;
import io.linuxserver.fleet.v2.types.internal.ImageOutlineRequest;
import io.linuxserver.fleet.v2.types.internal.RepositoryOutlineRequest;
import io.linuxserver.fleet.v2.types.internal.TagBranchOutlineRequest;

import java.time.LocalDate;
import java.util.List;

public interface ImageDAO {

    Image fetchImage(final ImageKey imageKey);

    InsertUpdateResult<Image> storeImage(final Image image);

    InsertUpdateResult<Image> storeImageMetaData(final Image image);

    InsertUpdateResult<Image> createImageOutline(final ImageOutlineRequest request);

    InsertUpdateResult<TagBranch> createTagBranchOutline(final TagBranchOutlineRequest request);

    InsertUpdateResult<Void> removeImage(final Image image);

    Repository fetchRepository(final RepositoryKey repositoryKey);

    InsertUpdateResult<Repository> createRepositoryOutline(final RepositoryOutlineRequest request);

    List<Repository> fetchAllRepositories();

    InsertUpdateResult<Repository> storeRepository(Repository repository);

    InsertUpdateResult<Void> removeRepository(final Repository repository);
}
