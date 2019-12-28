/*
 * Copyright (c)  2019 LinuxServer.io
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

package io.linuxserver.fleet.v2.thread.schedule;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class TimeWithUnit {

    public static final TimeWithUnit Zero = new TimeWithUnit(0, TimeUnit.SECONDS);

    private final long     timeDuration;
    private final TimeUnit timeUnit;

    public TimeWithUnit(final long timeDuration, final TimeUnit timeUnit) {

        this.timeDuration = timeDuration;
        this.timeUnit     = timeUnit;
    }

    public static TimeWithUnit valueOf(final String value) {

        if (value.matches("\\d+:(seconds|minutes|hours|days)")) {

            final String[] values = value.split(":");
            return new TimeWithUnit(Integer.parseInt(values[0]), TimeUnit.valueOf(values[1].toUpperCase()));
        }

        throw new IllegalArgumentException("Invalid TimeWithUnit value " + value);
    }

    public final TimeWithUnit convertToLowestUnit(final TimeWithUnit otherUnit) {

        if (getTimeUnit().compareTo(otherUnit.getTimeUnit()) < 0) {
            return this;
        }

        return new TimeWithUnit(otherUnit.getTimeUnit().convert(getTimeDuration(), getTimeUnit()),
                                otherUnit.getTimeUnit());
    }

    public final long getTimeDuration() {
        return timeDuration;
    }

    public final ChronoUnit getChronoUnit() {
        return timeUnit.toChronoUnit();
    }

    public final TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public final boolean isGreaterThanZero() {
        return getTimeDuration() > 0;
    }

    @Override
    public final String toString() {
        return getTimeDuration() + ":" + getTimeUnit().name().toLowerCase();
    }
}
