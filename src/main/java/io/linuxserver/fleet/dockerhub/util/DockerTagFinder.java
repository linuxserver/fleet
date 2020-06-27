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
import io.linuxserver.fleet.v2.types.docker.DockerTagManifestDigest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DockerTagFinder {

    public static DockerTag findVersionedTagMatchingBranch(List<DockerTag> tags, String namedBranch) {

        Optional<DockerTag> tagBranchName = tags.stream().filter(tag -> namedBranch.equals(tag.getName())).findFirst();

        if (tagBranchName.isPresent()) {

            DockerTag namedTagForBranch = tagBranchName.get();
            Optional<DockerTag> versionedLatestTag = tags.stream()
                .filter(tag -> !tag.equals(namedTagForBranch) && allManifestsMatch(namedTagForBranch, tag)).findFirst();

            return versionedLatestTag.orElse(namedTagForBranch);
        }

        return tags.isEmpty() ? null : tags.get(0);
    }

    private static boolean allManifestsMatch(final DockerTag namedTag, final DockerTag toCheck) {

        final List<DockerTagManifestDigest> namedDigests   = namedTag.getDigests();
        final List<DockerTagManifestDigest> digestsToCheck = toCheck.getDigests();

        boolean allMatch = true;

        if (namedDigests.size() == digestsToCheck.size()) {

            final Map<String, String> namedDigestsAsMap = toMapKeyedByArch(namedDigests);

            for (DockerTagManifestDigest digestToCheck : digestsToCheck) {

                final String archPlusVariant = digestToCheck.getArchitecture() + digestToCheck.getArchVariant();
                final String foundDigest     = namedDigestsAsMap.get(archPlusVariant);

                allMatch = allMatch && (null != foundDigest) && foundDigest.equals(digestToCheck.getDigest());
            }

        } else {
            allMatch = false;
        }

        return allMatch;
    }

    private static Map<String, String> toMapKeyedByArch(final List<DockerTagManifestDigest> initialList) {

        final Map<String, String> map = new HashMap<>();
        for (DockerTagManifestDigest digest : initialList) {
            map.put(digest.getArchitecture() + digest.getArchVariant(), digest.getDigest());
        }
        return map;
    }
}
