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

    private final LocalDateTime                  intantiatedAt;
    private final AtomicReference<LocalDateTime> lastRun;
    private final AtomicReference<Duration>      lastRunDuration;

    public AbstractAppSchedule(final ScheduleSpec spec,
                               final FleetAppController controller) {
        super(spec.getKey());

        this.intantiatedAt   = LocalDateTime.now();
        this.controller      = controller;
        this.spec            = new AtomicReference<>(spec);
        this.lastRun         = new AtomicReference<>();
        this.lastRunDuration = new AtomicReference<>(Duration.ZERO);
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
        return lastRun.get();
    }

    @Override
    public final LocalDateTime getNextRunTime() {

        final ScheduleSpec scheduleSpec = getSpec();
        final TimeWithUnit interval     = scheduleSpec.getInterval();

        if (null == getLastRunTime()) {

            if (getSpec().getDelayOffset().isGreaterThanZero()) {
                return intantiatedAt.plus(scheduleSpec.getDelayOffset().getTimeDuration(), scheduleSpec.getDelayOffset().getChronoUnit());
            } else {
                return intantiatedAt.plus(interval.getTimeDuration(), interval.getChronoUnit());
            }
        }

        return getLastRunTime().plus(interval.getTimeDuration(), interval.getChronoUnit());
    }

    @Override
    public final Duration getLastRunDuration() {
        return lastRunDuration.get();
    }

    @Override
    public final TimeWithUnit getInterval() {
        return getSpec().getInterval();
    }

    @Override
    public TimeWithUnit getDelay() {
        return getSpec().getDelayOffset();
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

            if (isAllowedToExecute()) {

                logger.info("Starting run of schedule {}", scheduleName);
                executeSchedule();
                logger.info("Run of schedule {} finished.", scheduleName);

            } else {
                logger.info("Schedule is currently not allowed to run. Will log run but will skip until next time.");
            }

        } catch (Exception e) {
            logger.error("Caught unhandled exception during running of schedule {}", scheduleName, e);
        }

        final LocalDateTime endTime = LocalDateTime.now();
        lastRun.set(endTime);
        lastRunDuration.set(Duration.between(startTime, endTime));
    }

    @Override
    public String toString() {
        return "Name=" + getName() + ", Interval=" + getInterval() + ", InitialDelay=" + getDelay();
    }

    protected boolean isAllowedToExecute() {
        return true;
    }
}
