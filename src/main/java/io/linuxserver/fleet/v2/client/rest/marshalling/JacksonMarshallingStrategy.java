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

package io.linuxserver.fleet.v2.client.rest.marshalling;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Jackson JSON implementation of the marshalling strategy. This will convert incoming
 * and outgoing messages formatted in JSON.
 */
public class JacksonMarshallingStrategy implements MarshallingStrategy {

    private static final ObjectMapper OBJECT_MAPPER;
    static {

        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public <T> T unmarshall(String value, Class<T> classType) throws IOException {
        return OBJECT_MAPPER.readValue(value, classType);
    }

    @Override
    public String marshall(Object value) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(value);
    }

    @Override
    public String getContentType() {
        return "application/json";
    }
}
