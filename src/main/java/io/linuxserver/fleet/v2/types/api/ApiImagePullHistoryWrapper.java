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

package io.linuxserver.fleet.v2.types.api;

import io.linuxserver.fleet.v2.types.meta.history.ImagePullStatistic;

import java.util.*;
import java.util.stream.Collectors;

public class ApiImagePullHistoryWrapper extends AbstractApiWrapper<List<ImagePullStatistic>> {

    private final ImagePullStatistic.StatGroupMode groupMode;

    public ApiImagePullHistoryWrapper(final List<ImagePullStatistic> originalObject,
                                      final ImagePullStatistic.StatGroupMode groupMode) {
        super(originalObject);
        this.groupMode = groupMode;
    }

    public final String getGroupModeDataPoint() {
        return groupMode.getDataPoint();
    }

    public final List<String> getLabels() {
        return getOriginalObject().stream().map(ImagePullStatistic::getGroupedDateTime).collect(Collectors.toList());
    }

    public final List<Long> getPulls() {
        return getOriginalObject().stream().map(ImagePullStatistic::getPullCount).collect(Collectors.toList());
    }

    public final long getMean() {
        return (long) getPullDifferential().getPulls().stream().mapToLong(Long::longValue).average().orElse(0.0);
    }

    public final PullDifferentialsWithLabels getPullDifferential() {

        final PullDifferentialsWithLabels differentialsWithLabels = new PullDifferentialsWithLabels();
        int i;
        for (i = 1; i < getOriginalObject().size(); i++) {

            final ImagePullStatistic previousStat = getOriginalObject().get(i - 1);
            final ImagePullStatistic currentStat  = getOriginalObject().get(i);

            differentialsWithLabels.addDifferential(currentStat.getGroupedDateTime(), (currentStat.getPullCount() - previousStat.getPullCount()));
        }
        return differentialsWithLabels;
    }

    public static class PullDifferentialsWithLabels {

        private final Map<String, Long> differentials;

        public PullDifferentialsWithLabels() {
            differentials = new TreeMap<>();
        }

        public final void addDifferential(final String label, final Long pulls) {
            differentials.put(label, pulls);
        }

        public final Set<String> getLabels() {
            return differentials.keySet();
        }

        public final Collection<Long> getPulls() {
            return differentials.values();
        }
    }
}
