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

package io.linuxserver.fleet.v2.types.docker;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Fleet's specifically required structure for an image coming from DockerHub. This object
 * is common within the app, and is immune to any API changes to DockerHub itself.
 * </p>
 */
public class DockerImage {

    private final String        name;
    private final String        repository;
    private final String        description;
    private final int           starCount;
    private final long          pullCount;
    private final LocalDateTime buildDate;

    private List<DockerTag> tags = new ArrayList<>();

    public DockerImage(String name, String repository, String description, int starCount, long pullCount, LocalDateTime buildDate) {

        this.name = name;
        this.repository = repository;
        this.description = description;
        this.starCount = starCount;
        this.pullCount = pullCount;
        this.buildDate = buildDate;
    }

    public final void addTag(final DockerTag tag) {
        tags.add(tag);
    }

    public final List<DockerTag> getTags() {
        return tags;
    }

    public final String getName() {
        return name;
    }

    public final String getRepository() {
        return repository;
    }

    public final String getDescription() {
        return description;
    }

    public final int getStarCount() {
        return starCount;
    }

    public final long getPullCount() {
        return pullCount;
    }

    public final LocalDateTime getBuildDate() {
        return buildDate;
    }

    @Override
    public final String toString() {
        return getRepository() + "/" + getName();
    }
}
