/*
 * Copyright (c)  2020 LinuxServer.io
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

package io.linuxserver.fleet.v2.types.internal;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class AbstractParamRequest {

    private final Map<String, List<String>> params;

    public AbstractParamRequest(final Map<String, List<String>> params) {
        this.params = params;
    }

    protected final List<String> getParams(final String key) {
        return params.get(key);
    }

    protected final String getOrNull(final String value) {
        return "".equalsIgnoreCase(value.trim()) ? null : value;
    }

    protected final String getFirstOrNull(final String key) {

        final List<String> strings = params.get(key);
        if (null == strings || strings.isEmpty()) {
            return null;
        }
        return strings.get(0);
    }

    protected final boolean getAsBoolean(final String value) {
        return "true".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value);
    }

    @SafeVarargs
    protected final void checkLists(final List<String>... lists) {

        boolean containsDifferent = false;

        boolean allNull  = Stream.of(lists).allMatch(Objects::isNull);
        boolean noneNull = Stream.of(lists).allMatch(Objects::nonNull);

        if (allNull || noneNull) {

            if (allNull) {
                return;
            }

        } else {
            containsDifferent = true;
        }

        if (!containsDifferent) {

            int prevSize = -1;
            for (List<String> list : lists) {

                if (prevSize != -1 && list.size() != prevSize) {

                    containsDifferent = true;
                    break;

                } else {
                    prevSize = list.size();
                }
            }
        }

        if (containsDifferent) {
            throw new IllegalArgumentException("One or more values are null when others are not, or sizes mismatch");
        }
    }
}
