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

package io.linuxserver.fleet.v2.db;

import io.linuxserver.fleet.core.db.DatabaseProvider;
import io.linuxserver.fleet.v2.key.ScheduleKey;
import io.linuxserver.fleet.v2.thread.schedule.AppSchedule;
import io.linuxserver.fleet.v2.thread.schedule.ScheduleSpec;
import io.linuxserver.fleet.v2.thread.schedule.TimeWithUnit;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class DefaultScheduleDAO extends AbstractDAO implements ScheduleDAO {

    private static final String GetScheduleSpecs = "{CALL Schedule_GetSpecs()}";

    public DefaultScheduleDAO(final DatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    @Override
    public Set<ScheduleSpec> fetchScheduleSpecs() {

        final Set<ScheduleSpec> specs = new HashSet<>();

        try (final Connection connection = getConnection()) {

            try (final CallableStatement call = connection.prepareCall(GetScheduleSpecs)) {

                final ResultSet results = call.executeQuery();
                while (results.next()) {
                    specs.add(makeOneScheduleSpec(results));
                }

            } catch (ClassNotFoundException e) {
                getLogger().error("No class found for schedule. Ignoring this schedule.", e);
            }

        } catch (SQLException e) {

            getLogger().error("Error caught when executing SQL: fetchScheduleSpecs", e);
            throw new RuntimeException("fetchScheduleSpecs", e);
        }

        return specs;
    }

    @SuppressWarnings("unchecked")
    private ScheduleSpec makeOneScheduleSpec(final ResultSet results) throws SQLException, ClassNotFoundException {

        return ScheduleSpec.makeInitial(new ScheduleKey(results.getInt("ScheduleId")),
                                        results.getString("ScheduleName"),
                                        TimeWithUnit.valueOf(results.getString("ScheduleInterval")),
                                        (Class<? extends AppSchedule>) Class.forName(results.getString("ScheduleClass")));
    }
}
