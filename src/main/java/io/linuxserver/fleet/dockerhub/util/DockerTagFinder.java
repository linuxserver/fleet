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

package io.linuxserver.fleet.dockerhub.util;

import io.linuxserver.fleet.v2.types.docker.DockerTag;

import java.util.List;
import java.util.Optional;

public class DockerTagFinder {

    public DockerTag findVersionedTagMatchingBranch(List<DockerTag> tags, String namedBranch) {

        Optional<DockerTag> tagBranchName = tags.stream().filter(tag -> namedBranch.equals(tag.getName())).findFirst();

        if (tagBranchName.isPresent()) {

            DockerTag trueLatestTag = tagBranchName.get();
            Optional<DockerTag> versionedLatestTag = tags.stream()
                .filter(tag -> !tag.equals(trueLatestTag) && tag.getSize() == trueLatestTag.getSize()).findFirst();

            return versionedLatestTag.orElse(trueLatestTag);
        }

        return tags.get(0);
    }
}
