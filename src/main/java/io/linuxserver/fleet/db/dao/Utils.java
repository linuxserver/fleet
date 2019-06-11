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

package io.linuxserver.fleet.db.dao;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;

class Utils {

    static void setNullableInt(CallableStatement call, int position, Integer value) throws SQLException {

        if (null == value)
            call.setNull(position, Types.INTEGER);

        else
            call.setInt(position, value);
    }

    static void setNullableLong(CallableStatement call, int position, Long value) throws SQLException {

        if (null == value)
            call.setNull(position, Types.BIGINT);

        else
            call.setLong(position, value);
    }

    static void setNullableString(CallableStatement call, int position, String value) throws SQLException {

        if (null == value)
            call.setNull(position, Types.VARCHAR);

        else
            call.setString(position, value);
    }

    static void setNullableTimestamp(CallableStatement call, int position, LocalDateTime localDateTime) throws SQLException {

        if (null == localDateTime) {
            call.setNull(position, Types.TIMESTAMP);
        } else {
            call.setTimestamp(position, Timestamp.valueOf(localDateTime));
        }
    }

    static void safeClose(CallableStatement call) {

        try {

            if (null != call)
                call.close();

        } catch (SQLException e) {
            throw new RuntimeException("Unable to close call", e);
        }
    }
}
