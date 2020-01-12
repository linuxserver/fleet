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

package io.linuxserver.fleet.v2.types.meta.template;

public class PortTemplateItem extends AbstractTemplateItem<Integer, PortTemplateItem> {

    private final Protocol protocol;

    public PortTemplateItem(final Integer port, final String description, final Protocol protocol) {
        super(port, description);
        this.protocol = protocol;
    }

    public final Integer getPort() {
        return getName();
    }

    public final String getProtocol() {
        return getProtocolAsProtocol().protocolName;
    }

    public final Protocol getProtocolAsProtocol() {
        return protocol;
    }

    public enum Protocol {

        Tcp("tcp"),
        Udp("udp");

        private final String protocolName;

        Protocol(final String protocolName) {
            this.protocolName = protocolName;
        }

        public static Protocol fromName(final String protocolName) {

            for (Protocol protocol : values()) {
                if (protocol.protocolName.equals(protocolName)) {
                    return protocol;
                }
            }

            throw new IllegalArgumentException("Unknown protocol " + protocolName);
        }
    }
}
