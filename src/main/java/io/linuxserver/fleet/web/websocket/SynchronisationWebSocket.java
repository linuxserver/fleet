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

import io.linuxserver.fleet.sync.SynchronisationListener;
import io.linuxserver.fleet.sync.event.ImageUpdateEvent;
import io.linuxserver.fleet.sync.event.RepositoriesScannedEvent;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class SynchronisationWebSocket extends AbstractWebSocket implements SynchronisationListener {

    @Override
    public void onSynchronisationStart() {
        broadcast(new WebSocketMessage(WebSocketMessage.MessageType.SYNC_START, "Sync started."));
    }

    @Override
    public void onRepositoriesScanned(RepositoriesScannedEvent event) {
        broadcast(new WebSocketMessage(WebSocketMessage.MessageType.REPOSITORIES_SCANNED, event));
    }

    @Override
    public void onImageUpdated(ImageUpdateEvent event) {
        broadcast(new WebSocketMessage(WebSocketMessage.MessageType.IMAGE_UPDATED, event));
    }

    @Override
    public void onSynchronisationFinish() {
        broadcast(new WebSocketMessage(WebSocketMessage.MessageType.SYNC_END, "Sync finished."));
    }

    @Override
    public void onSynchronisationSkipped() {
        broadcast(new WebSocketMessage(WebSocketMessage.MessageType.SYNC_SKIP, "Sync skipped."));
    }
}
