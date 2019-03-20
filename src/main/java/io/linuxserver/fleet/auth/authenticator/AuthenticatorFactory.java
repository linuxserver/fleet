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

package io.linuxserver.fleet.auth.authenticator;

import io.linuxserver.fleet.auth.security.PKCS5S2PasswordEncoder;
import io.linuxserver.fleet.core.FleetBeans;
import io.linuxserver.fleet.core.FleetProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticatorFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticatorFactory.class);

    public static UserAuthenticator getAuthenticator(FleetBeans beans) {

        FleetProperties properties = beans.getProperties();

        AuthenticationType authType = AuthenticationType.valueOf(properties.getAuthenticationType().toUpperCase());
        switch (authType) {

            case DATABASE:

                LOGGER.info("Configuring new authenticator: DatabaseStoredUserAuthenticator");
                return new DatabaseStoredUserAuthenticator(beans.getPasswordEncoder(), beans.getUserDelegate());

            case PROPERTIES:
            default:

                LOGGER.info("Configuring new authenticator: PropertyLoadedUserAuthenticator");
                return new PropertyLoadedUserAuthenticator(properties.getAppUsername(), properties.getAppPassword());
        }
    }

    public enum AuthenticationType {
        PROPERTIES, DATABASE
    }
}
