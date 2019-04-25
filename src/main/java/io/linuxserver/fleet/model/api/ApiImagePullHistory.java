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

import io.linuxserver.fleet.model.Image;
import io.linuxserver.fleet.model.ImagePullStat;

import java.util.ArrayList;
import java.util.List;

public class ApiImagePullHistory {

    private int imageId;
    private String imageName;
    private String groupMode;
    private List<ApiImagePullStat> pullHistory = new ArrayList<>();

    public int getImageId() {
        return imageId;
    }

    public String getImageName() {
        return imageName;
    }

    public String getGroupMode() {
        return groupMode;
    }

    public List<ApiImagePullStat> getPullHistory() {
        return pullHistory;
    }

    public static ApiImagePullHistory fromPullStats(Image image, List<ImagePullStat> stats) {

        ApiImagePullHistory history = new ApiImagePullHistory();
        history.imageId = image.getId();
        history.imageName = image.getName();
        history.groupMode = stats.get(0).getGroupMode().toString();

        for (ImagePullStat stat : stats)
            history.pullHistory.add(new ApiImagePullStat(stat.getTimeGroup(), stat.getPullCount()));

        return history;
    }

    public static class ApiImagePullStat {

        private final String timeGroup;
        private final long pullCount;

        ApiImagePullStat(String timeGroup, long pullCount) {
            this.timeGroup = timeGroup;
            this.pullCount = pullCount;
        }

        public String getTimeGroup() {
            return timeGroup;
        }

        public long getPullCount() {
            return pullCount;
        }
    }
}
