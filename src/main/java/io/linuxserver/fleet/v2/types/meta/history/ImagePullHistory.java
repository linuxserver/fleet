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

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ImagePullHistory {

    private final Set<ImagePullStatistic> historicalPulls;

    public ImagePullHistory() {
        historicalPulls = new TreeSet<>();
    }

    public final boolean addStatistic(final ImagePullStatistic statistic) {
        return historicalPulls.add(statistic);
    }

    public final List<ImagePullStatistic> getHistoryFor(final ImagePullStatistic.StatGroupMode groupMode) {
        return historicalPulls.stream().filter(s -> s.isGroupedBy(groupMode)).collect(Collectors.toList());
    }
}
