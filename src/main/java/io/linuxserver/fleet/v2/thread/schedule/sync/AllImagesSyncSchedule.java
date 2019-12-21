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

package io.linuxserver.fleet.v2.thread.schedule.sync;

import io.linuxserver.fleet.core.FleetAppController;
import io.linuxserver.fleet.v2.thread.schedule.AbstractAppSchedule;
import io.linuxserver.fleet.v2.thread.schedule.ScheduleSpec;
import io.linuxserver.fleet.v2.types.Repository;

import java.util.List;

public final class AllImagesSyncSchedule extends AbstractAppSchedule {

    public AllImagesSyncSchedule(final ScheduleSpec spec,
                                 final FleetAppController controller) {
        super(spec, controller);
    }

    @Override
    public void executeSchedule() {

        final List<Repository> allRepositories = getController().getRepositoryService().getAllRepositories();
        for (Repository repository : allRepositories) {
            getController().getSynchronisationService().synchroniseCachedRepository(repository);
        }
    }
}
