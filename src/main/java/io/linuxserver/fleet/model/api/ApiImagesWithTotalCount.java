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

package io.linuxserver.fleet.model.api;

import java.util.List;
import java.util.Map;

public class ApiImagesWithTotalCount {

    private final long                          totalPullCount;
    private final Map<String, List<ApiImage>>   repositories;

    public ApiImagesWithTotalCount(long totalPullCount, Map<String, List<ApiImage>> repositories) {

        this.totalPullCount = totalPullCount;
        this.repositories = repositories;
    }

    public long getTotalPullCount() {
        return totalPullCount;
    }

    public Map<String, List<ApiImage>> getRepositories() {
        return repositories;
    }
}
