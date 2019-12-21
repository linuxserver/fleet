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

package io.linuxserver.fleet.v2.web.routes;

import io.javalin.http.Context;
import io.linuxserver.fleet.v2.service.ScheduleService;
import io.linuxserver.fleet.v2.web.PageModelSpec;

public class AdminScheduleController extends AbstractPageHandler {

    private final ScheduleService scheduleService;

    public AdminScheduleController(final ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Override
    protected PageModelSpec handlePageLoad(final Context ctx) {

        final PageModelSpec modelSpec = new PageModelSpec("views/pages/admin/schedules.ftl");
        modelSpec.addModelAttribute("schedules", scheduleService.getLoadedSchedules());
        return modelSpec;
    }

    @Override
    protected PageModelSpec handleFormSubmission(Context ctx) {
        return null;
    }
}
