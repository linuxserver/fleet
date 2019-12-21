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

    private final int        timeDuration;
    private final ChronoUnit timeUnit;

    public TimeWithUnit(final int timeDuration, final ChronoUnit timeUnit) {

        this.timeDuration = timeDuration;
        this.timeUnit     = timeUnit;
    }

    public static TimeWithUnit valueOf(final String value) {

        if (value.matches("\\d+:(seconds|minutes|hours|days)")) {

            final String[] values = value.split(":");
            return new TimeWithUnit(Integer.parseInt(values[0]), ChronoUnit.valueOf(values[1].toUpperCase()));
        }

        throw new IllegalArgumentException("Invalid TimeWithUnit value " + value);
    }

    public final int getTimeDuration() {
        return timeDuration;
    }

    public final ChronoUnit getTimeUnit() {
        return timeUnit;
    }

    public final TimeUnit getTimeUnitAsTimeUnit() {
        return TimeUnit.valueOf(getTimeUnit().name());
    }

    @Override
    public final String toString() {
        return getTimeDuration() + ":" + getTimeUnitAsTimeUnit().name().toLowerCase();
    }
}
