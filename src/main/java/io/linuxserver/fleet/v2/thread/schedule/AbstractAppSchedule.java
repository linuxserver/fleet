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

import io.linuxserver.fleet.core.FleetAppController;
import io.linuxserver.fleet.v2.LoggerOwner;
import io.linuxserver.fleet.v2.key.AbstractHasKey;
import io.linuxserver.fleet.v2.key.ScheduleKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractAppSchedule extends AbstractHasKey<ScheduleKey> implements AppSchedule, LoggerOwner {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final FleetAppController            controller;
    private final AtomicReference<ScheduleSpec> spec;

    public AbstractAppSchedule(final ScheduleSpec spec,
                               final FleetAppController controller) {
        super(spec.getKey());

        this.controller = controller;
        this.spec = new AtomicReference<>(spec);
    }

    protected final FleetAppController getController() {
        return controller;
    }

    public final ScheduleSpec getSpec() {
        return spec.get();
    }

    @Override
    public final String getName() {
        return getSpec().getScheduleName();
    }

    @Override
    public final LocalDateTime getLastRunTime() {
        return getSpec().getLastRun();
    }

    @Override
    public final LocalDateTime getNextRunTime() {

        final ScheduleSpec scheduleSpec = getSpec();
        final TimeWithUnit interval     = scheduleSpec.getInterval();

        return scheduleSpec.getLastRun().plus(interval.getTimeDuration(), interval.getTimeUnit());
    }

    @Override
    public final Duration getLastRunDuration() {
        return getSpec().getLastRunDuration();
    }

    @Override
    public final TimeWithUnit getInterval() {
        return getSpec().getInterval();
    }

    @Override
    public final Logger getLogger() {
        return logger;
    }

    @Override
    public void run() {

        final LocalDateTime startTime    = LocalDateTime.now();
        final String        scheduleName = getSpec().getScheduleName();

        try {

            logger.info("Starting run of schedule {}", scheduleName);
            executeSchedule();
            logger.info("Run of schedule {} finished.", scheduleName);

        } catch (Exception e) {
            logger.error("Caught unhandled exception during running of schedule {}", scheduleName, e);
        }

        final LocalDateTime endTime = LocalDateTime.now();
        spec.set(spec.get().cloneWithLastRun(startTime, endTime));
    }

    @Override
    public String toString() {
        return "Name=" + getName() + ", Interval=" + getInterval();
    }
}
