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

import io.linuxserver.fleet.v2.key.AbstractHasKey;
import io.linuxserver.fleet.v2.key.ScheduleKey;

public final class ScheduleSpec extends AbstractHasKey<ScheduleKey> {

    private final String                       scheduleName;
    private final TimeWithUnit                 interval;
    private final TimeWithUnit                 delayOffset;
    private final Class<? extends AppSchedule> specForClass;

    private ScheduleSpec(final ScheduleKey key,
                         final String scheduleName,
                         final TimeWithUnit interval,
                         final TimeWithUnit delayOffset,
                         final Class<? extends AppSchedule> specForClass) {
        super(key);

        this.scheduleName    = scheduleName;
        this.interval        = interval;
        this.delayOffset     = delayOffset;
        this.specForClass    = specForClass;
    }

    public static ScheduleSpec makeInitial(final ScheduleKey key,
                                           final String scheduleName,
                                           final TimeWithUnit interval,
                                           final TimeWithUnit delayOffset,
                                           final Class<? extends AppSchedule> specForClass) {
        return new ScheduleSpec(key,
                                scheduleName,
                                interval,
                                delayOffset,
                                specForClass);
    }

    public final String getScheduleName() {
        return scheduleName;
    }

    public final TimeWithUnit getInterval() {
        return interval;
    }

    public final TimeWithUnit getDelayOffset() {
        return delayOffset;
    }

    public final Class<? extends AppSchedule> getScheduleClass() {
        return specForClass;
    }
}
