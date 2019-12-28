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

package io.linuxserver.fleet.v2.types;

import io.linuxserver.fleet.v2.key.AbstractHasKey;
import io.linuxserver.fleet.v2.key.AlertKey;

import java.time.LocalDateTime;
import java.util.UUID;

public class AppAlert extends AbstractHasKey<AlertKey> {

    private final AlertLevel    alertLevel;
    private final LocalDateTime alertDate;
    private final String        subject;
    private final String        alertMessage;

    private AppAlert(final AlertKey key,
                     final AlertLevel level,
                     final LocalDateTime alertDate,
                     final String subject,
                     final String alertMessage) {
        super(key);

        this.alertLevel   = level;
        this.alertDate    = LocalDateTime.of(alertDate.toLocalDate(), alertDate.toLocalTime());
        this.subject      = subject;
        this.alertMessage = alertMessage;
    }

    public static AppAlert makeAlert(final AlertLevel alertLevel, final String subject, final String alertMessage) {

        return new AppAlert(new AlertKey(UUID.randomUUID().toString()),
                            alertLevel,
                            LocalDateTime.now(),
                            subject,
                            alertMessage);
    }

    public final LocalDateTime getAlertDate() {
        return LocalDateTime.of(alertDate.toLocalDate(), alertDate.toLocalTime());
    }

    public final String getSubject() {
        return subject;
    }

    public final String getAlertMessage() {
        return alertMessage;
    }

    public final AlertLevel getAlertLevel() {
        return alertLevel;
    }

    public final boolean isSystemAlert() {
        return getAlertLevel().isSystem();
    }

    public enum AlertLevel {

        Info, Warning, Error, System;

        public final boolean isInfo() {
            return this == Info;
        }

        public final boolean isSystem() {
            return this == System;
        }
    }
}
