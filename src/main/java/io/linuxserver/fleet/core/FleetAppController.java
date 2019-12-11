/*
 * Copyright (c) 2019 LinuxServer.io
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

package io.linuxserver.fleet.core;

import io.linuxserver.fleet.core.config.WebConfiguration;
import io.linuxserver.fleet.v2.web.WebRouteController;

/**
 * <p>
 * Primary entry point for the application. All contexts and resources are loaded
 * through this class.
 * </p>
 */
public class FleetAppController extends AbstractAppController {

    private static FleetAppController instance;

    public static FleetAppController instance() {

        if (null == instance) {

            synchronized (FleetAppController.class) {

                if (null == instance) {
                    instance = new FleetAppController();
                }
            }
        }

        return instance;
    }

    @Override
    protected final void run() {
        super.run();
        configureWeb();
    }

    public final WebConfiguration getWebConfiguration() {
        return new WebConfiguration(getAppProperties());
    }

    private void configureWeb() {
        new WebRouteController(this);
    }
}
