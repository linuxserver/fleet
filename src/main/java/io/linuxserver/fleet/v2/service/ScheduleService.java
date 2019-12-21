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
import io.linuxserver.fleet.v2.cache.ScheduleCache;
import io.linuxserver.fleet.v2.db.ScheduleDAO;
import io.linuxserver.fleet.v2.key.ScheduleKey;
import io.linuxserver.fleet.v2.thread.schedule.AppSchedule;
import io.linuxserver.fleet.v2.thread.schedule.ScheduleSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ScheduleService extends AbstractAppService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleService.class);

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private final ScheduleCache            scheduleCache;
    private final ScheduleDAO              scheduleDAO;

    public ScheduleService(final FleetAppController controller, final ScheduleDAO scheduleDAO) {
        super(controller);

        this.scheduleCache = new ScheduleCache();
        this.scheduleDAO   = scheduleDAO;
    }

    public final void initialiseSchedules() {

        final Set<ScheduleSpec> specs = scheduleDAO.fetchScheduleSpecs();
        for (ScheduleSpec spec : specs) {

            final AppSchedule schedule = loadSchedule(spec);

            LOGGER.info("Schedule loaded: {}", schedule);
            executorService.scheduleAtFixedRate(schedule,
                                                0,
                                                schedule.getInterval().getTimeDuration(),
                                                schedule.getInterval().getTimeUnitAsTimeUnit());

            scheduleCache.addItem(schedule);
        }
    }

    public final void forceRun(final ScheduleKey scheduleKey) {

        if (scheduleCache.isScheduleRunning(scheduleKey)) {
            scheduleCache.findItem(scheduleKey).executeSchedule();
        } else {
            throw new IllegalArgumentException("No schedule found with key " + scheduleKey);
        }
    }

    public final List<AppSchedule> getLoadedSchedules() {
        return new ArrayList<>(scheduleCache.getAllItems());
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
}
