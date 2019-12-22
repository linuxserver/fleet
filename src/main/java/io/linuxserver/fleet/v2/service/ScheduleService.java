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

package io.linuxserver.fleet.v2.service;

import io.linuxserver.fleet.core.FleetAppController;
import io.linuxserver.fleet.v2.cache.BasicItemCache;
import io.linuxserver.fleet.v2.db.ScheduleDAO;
import io.linuxserver.fleet.v2.key.AbstractHasKey;
import io.linuxserver.fleet.v2.key.ScheduleKey;
import io.linuxserver.fleet.v2.thread.schedule.AppSchedule;
import io.linuxserver.fleet.v2.thread.schedule.ScheduleSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

public class ScheduleService extends AbstractAppService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleService.class);

    private final BasicItemCache<ScheduleKey, ScheduleWrapper> scheduleCache;

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private final ScheduleDAO              scheduleDAO;

    public ScheduleService(final FleetAppController controller, final ScheduleDAO scheduleDAO) {
        super(controller);

        this.scheduleCache = new BasicItemCache<>();
        this.scheduleDAO   = scheduleDAO;
    }

    public final void initialiseSchedules() {

        final Set<ScheduleSpec> specs = scheduleDAO.fetchScheduleSpecs();
        for (ScheduleSpec spec : specs) {

            try {

                final AppSchedule schedule = loadSchedule(spec);
                LOGGER.info("Schedule loaded: {}", schedule);

                loadOneSchedule(schedule);

            } catch (Exception e) {
                LOGGER.error("Unable to load schedule", e);
            }
        }
    }

    public final AppSchedule forceRun(final ScheduleKey scheduleKey) {

        if (scheduleCache.isItemCached(scheduleKey)) {

            final ScheduleWrapper wrapper = scheduleCache.findItem(scheduleKey);

            LOGGER.info("Cancelling current run of schedule {}", wrapper.getName());
            wrapper.getFuture().cancel(false);

            LOGGER.info("Triggering re-run of schedule {}", wrapper.getName());
            loadOneSchedule(wrapper.getSchedule());

            return wrapper.getSchedule();

        } else {

            LOGGER.warn("Did not find cached schedule with key {}", scheduleKey);
            throw new IllegalArgumentException("No schedule found with key " + scheduleKey);
        }
    }

    public final List<AppSchedule> getLoadedSchedules() {
        return scheduleCache.getAllItems().stream().map(ScheduleWrapper::getSchedule).collect(Collectors.toList());
    }

    private AppSchedule loadSchedule(final ScheduleSpec spec) {

        try {

            final Constructor<? extends AppSchedule> scheduleConstructor = spec.getScheduleClass().getDeclaredConstructor(ScheduleSpec.class,
                                                                                                                          FleetAppController.class);
            return scheduleConstructor.newInstance(spec, getController());

        } catch (Exception e) {

            LOGGER.error("Unable to instantiate schedule for class {}", spec.getScheduleClass(), e);
            throw new RuntimeException(e);
        }
    }

    private void loadOneSchedule(AppSchedule schedule) {

        final ScheduledFuture<?> future = executorService.scheduleAtFixedRate(schedule,
                                                                             0,
                                                                              schedule.getInterval().getTimeDuration(),
                                                                              schedule.getInterval().getTimeUnitAsTimeUnit());

        scheduleCache.addItem(new ScheduleWrapper(schedule, future));
    }

    public static class ScheduleWrapper extends AbstractHasKey<ScheduleKey> {

        private final ScheduledFuture<?> future;
        private final AppSchedule        schedule;

        public ScheduleWrapper(final AppSchedule schedule, final ScheduledFuture<?> future) {
            super(schedule.getKey());
            this.future   = future;
            this.schedule = schedule;
        }

        public final ScheduledFuture<?> getFuture() {
            return future;
        }

        public final AppSchedule getSchedule() {
            return schedule;
        }

        public final String getName() {
            return getSchedule().getName();
        }
    }
}
