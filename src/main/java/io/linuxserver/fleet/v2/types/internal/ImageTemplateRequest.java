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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class ImageTemplateRequest {

    private final Map<String, List<String>> rawTemplateParams;

    public ImageTemplateRequest(final Map<String, List<String>> rawTemplateParams) {
        this.rawTemplateParams = rawTemplateParams;
    }

    public final String getRegistryUrl() {
        return getFirstOrNull("ImageTemplateUpstreamUrl");
    }

    public final String getRestartPolicy() {
        return getFirstOrNull("ImageTemplateRestartPolicy");
    }

    public final boolean isHostNetworkEnabled() {
        return getAsBoolean(getFirstOrNull("ImageTemplateNetworkHost"));
    }

    public final boolean isPrivilegedMode() {
        return getAsBoolean(getFirstOrNull("ImageTemplatePrivileged"));
    }

    public final List<String> getCapabilities() {
        return rawTemplateParams.get("ImageTemplateCapabilities");
    }

    public final List<TemplateItem<String>> getPorts() {

        final List<String> portNumbers      = rawTemplateParams.get("imageTemplatePort");
        final List<String> portProtocols    = rawTemplateParams.get("imageTemplatePortProtocol");
        final List<String> portDescriptions = rawTemplateParams.get("imageTemplatePortDescription");

        checkLists(portNumbers, portProtocols, portDescriptions);

        final List<TemplateItem<String>> ports = new ArrayList<>();

        if (null != portNumbers) {

            int i = 0;
            for (; i < portNumbers.size(); i++) {

                ports.add(new TemplateItem<>(portNumbers.get(i),
                                             getOrNull(portDescriptions.get(i)),
                                             portProtocols.get(i)));
            }
        }

        return ports;
    }

    public final List<TemplateItem<Boolean>> getVolumes() {

        final List<String> volumeNames        = rawTemplateParams.get("imageTemplateVolume");
        final List<String> volumeReadOnlys    = rawTemplateParams.get("imageTemplateVolumeReadonly");
        final List<String> volumeDescriptions = rawTemplateParams.get("imageTemplateVolumeDescription");

        checkLists(volumeNames, volumeReadOnlys, volumeDescriptions);

        final List<TemplateItem<Boolean>> volumes = new ArrayList<>();

        if (null != volumeNames) {

            int i = 0;
            for (; i < volumeNames.size(); i++) {

                volumes.add(new TemplateItem<>(volumeNames.get(i),
                                               getOrNull(volumeDescriptions.get(i)),
                                               "readonly".equalsIgnoreCase(volumeReadOnlys.get(i))));
            }
        }

        return volumes;
    }

    public final List<TemplateItem<Void>> getEnvironment() {

        final List<String> envNames        = rawTemplateParams.get("imageTemplateEnv");
        final List<String> envDescriptions = rawTemplateParams.get("imageTemplateEnvDescription");

        checkLists(envNames, envDescriptions);

        final List<TemplateItem<Void>> env = new ArrayList<>();

        if (null != envNames) {

            int i = 0;
            for (; i < envNames.size(); i++) {
                env.add(new TemplateItem<>(envNames.get(i), getOrNull(envDescriptions.get(i)),null));
            }
        }

        return env;
    }

    public final List<TemplateItem<Void>> getDevices() {

        final List<String> deviceNames        = rawTemplateParams.get("imageTemplateDevice");
        final List<String> deviceDescriptions = rawTemplateParams.get("imageTemplateDeviceDescription");

        checkLists(deviceNames, deviceDescriptions);

        final List<TemplateItem<Void>> env = new ArrayList<>();

        if (null != deviceNames) {

            int i = 0;
            for (; i < deviceNames.size(); i++) {
                env.add(new TemplateItem<>(deviceNames.get(i), getOrNull(deviceDescriptions.get(i)),null));
            }
        }

        return env;
    }

    private String getOrNull(final String value) {
        return "".equalsIgnoreCase(value.trim()) ? null : value;
    }

    private String getFirstOrNull(final String key) {

        final List<String> strings = rawTemplateParams.get(key);
        if (null == strings || strings.isEmpty()) {
            return null;
        }
        return strings.get(0);
    }

    private boolean getAsBoolean(final String value) {
        return "true".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value);
    }

    @SafeVarargs
    private final void checkLists(final List<String>... lists) {

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

    public static class TemplateItem<T> {

        private final String  name;
        private final String  description;
        private final T       secondaryField;

        public TemplateItem(final String name, final String description, final T secondaryField) {

            this.name           = name;
            this.description    = description;
            this.secondaryField = secondaryField;
        }

        public final String getName() {
            return name;
        }

        public final String getDescription() {
            return description;
        }

        public final T getSecondaryField() {
            return secondaryField;
        }
    }
}
