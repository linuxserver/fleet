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

package io.linuxserver.fleet.v2.client.docker.dockerhub;

import io.linuxserver.fleet.v2.client.docker.converter.DockerResponseConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public abstract class AbstractDockerHubConverter<DOCKER_HUB, INTERNAL> implements DockerResponseConverter<DOCKER_HUB, INTERNAL> {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    public final INTERNAL convert(final DOCKER_HUB dockerHubV2Image) {

        try {

            if (null == dockerHubV2Image) {
                LOGGER.warn("Attempted to convert null image");
            } else {
                return doPlainConvert(dockerHubV2Image);
            }

        } catch (Exception e) {
            LOGGER.error("Unable to convert docker model to internal.", e);
        }

        return null;
    }

    protected abstract INTERNAL doPlainConvert(final DOCKER_HUB dockerHubV2Image);

    protected final LocalDateTime parseDockerHubDate(String date) {

        if (null == date) {
            return null;
        }

        try {

            final String dateToParse = (date.endsWith("Z") ? date.substring(0, date.length() - 1) : date);
            return LocalDateTime.parse(dateToParse);

        } catch (DateTimeParseException e) {

            LOGGER.error("parseDockerHubDate(" + date + ") unable to leniently parse date.", e);
            return null;
        }
    }
}
