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

package io.linuxserver.fleet.model;

public class ImagePullStat {

    private final int       imageId;
    private final String    timeGroup;
    private final long      pullCount;
    private final GroupMode groupMode;

    public ImagePullStat(int imageId, String timeGroup, long pullCount, GroupMode groupMode) {

        this.imageId    = imageId;
        this.timeGroup  = timeGroup;
        this.pullCount  = pullCount;
        this.groupMode  = groupMode;
    }

    public int getImageId() {
        return imageId;
    }

    public String getTimeGroup() {
        return timeGroup;
    }

    public long getPullCount() {
        return pullCount;
    }

    public GroupMode getGroupMode() {
        return groupMode;
    }

    public enum GroupMode {

        HOUR, DAY, WEEK, MONTH, YEAR;

        @Override
        public String toString() {
            return name().toLowerCase();
        }

        public static boolean isValid(String value) {

            try {

                if (null == value) {
                    return false;
                }

                valueOf(value);
                return true;

            } catch (IllegalArgumentException e) {
                return false;
            }
        }
    }
}
