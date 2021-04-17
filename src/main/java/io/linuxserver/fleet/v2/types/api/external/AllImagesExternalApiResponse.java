/*
 * Copyright (c)  2020 LinuxServer.io
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

package io.linuxserver.fleet.v2.types.api.external;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllImagesExternalApiResponse {

    private final Map<String, List<ExternalApiImage>> repositories;

    public AllImagesExternalApiResponse() {
        this.repositories   = new HashMap<>();
    }

    public final ExternalApiImage addImage(final String repositoryName,
                                           final String imageName,
                                           final long pullCount,
                                           final String version,
                                           final String category,
                                           final boolean stable) {

        if (!repositories.containsKey(repositoryName)) {
            repositories.put(repositoryName, new ArrayList<>());
        }

        final ExternalApiImage apiImage = new ExternalApiImage(imageName, pullCount, version, category, stable);
        repositories.get(repositoryName).add(apiImage);
        return apiImage;
    }

    public final long getTotalPullCount() {

        long totalPullCount = 0L;
        for (List<ExternalApiImage> repositoryImages : repositories.values()) {
            for (ExternalApiImage image : repositoryImages) {
                totalPullCount += image.getPullCount();
            }
        }
        return totalPullCount;
    }

    public final Map<String, List<ExternalApiImage>> getRepositories() {
        return repositories;
    }
}
