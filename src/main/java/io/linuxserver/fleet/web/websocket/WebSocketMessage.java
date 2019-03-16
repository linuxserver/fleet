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

package io.linuxserver.fleet.web.websocket;

public class WebSocketMessage {

    public enum MessageType {
        FORCE_SYNC, CONNECTED, DISCONNECTED, SYNC_START, SYNC_END, SYNC_SKIP, REPOSITORIES_SCANNED, IMAGE_UPDATED
    }

    private final MessageType messageType;
    private final Object data;

    public WebSocketMessage(MessageType messageType, Object data) {

        this.messageType = messageType;
        this.data = data;
    }


    public MessageType getMessageType() {
        return messageType;
    }

    public Object getData() {
        return data;
    }
}
