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

package io.linuxserver.fleet.v2.client.rest.proxy;

import io.linuxserver.fleet.v2.client.rest.HttpException;
import io.linuxserver.fleet.v2.client.rest.marshalling.MarshallingStrategy;

import java.io.IOException;

public class LazyLoadPayloadProxy<T> implements PayloadProxy<T> {

    private final MarshallingStrategy   marshallingStrategy;
    private final String                payload;
    private final Class<T>              payloadType;

    public LazyLoadPayloadProxy(MarshallingStrategy marshallingStrategy, String payload, Class<T> payloadType) {

        this.marshallingStrategy    = marshallingStrategy;
        this.payload                = payload;
        this.payloadType            = payloadType;
    }

    @Override
    public T get() {

        try {
            return marshallingStrategy.unmarshall(payload, payloadType);
        } catch (IOException e) {
            throw new HttpException("Unable to unmarshall response payload", e);
        }
    }
}
