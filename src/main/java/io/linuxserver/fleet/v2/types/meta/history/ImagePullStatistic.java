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

package io.linuxserver.fleet.v2.types.meta.history;

public class ImagePullStatistic implements Comparable<ImagePullStatistic> {

    private final long          pullCount;
    private final String        groupedDateTime;
    private final StatGroupMode groupMode;

    public ImagePullStatistic(final long pullCount,
                              final String groupedDateTime,
                              final StatGroupMode groupMode) {

        this.pullCount       = pullCount;
        this.groupedDateTime = groupedDateTime;
        this.groupMode       = groupMode;
    }

    public final long getPullCount() {
        return pullCount;
    }

    public final String getGroupedDateTime() {
        return groupedDateTime;
    }

    public final boolean isGroupedBy(final StatGroupMode groupMode) {
        return this.groupMode == groupMode;
    }

    @Override
    public int compareTo(final ImagePullStatistic o) {

        final int dateComparison = groupedDateTime.compareTo(o.groupedDateTime);
        if (dateComparison == 0) {
            return groupMode.compareTo(o.groupMode);
        }
        return dateComparison;
    }

    @Override
    public int hashCode() {
        return groupMode.hashCode() + groupedDateTime.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {

        if (!(obj instanceof ImagePullStatistic)) {
            return false;
        }

        final ImagePullStatistic other = (ImagePullStatistic) obj;
        return other.groupedDateTime.equals(groupedDateTime) && other.isGroupedBy(groupMode);
    }

    @Override
    public String toString() {
        return pullCount + "@" + groupedDateTime + ":" + groupMode;
    }

    public enum StatGroupMode {

        Day("hour"),
        Week("day"),
        Month("day");

        private final String dataPoints;

        StatGroupMode(final String dataPoints) {
            this.dataPoints = dataPoints;
        }

        public final String getDataPoint() {
            return dataPoints;
        }
    }
}
