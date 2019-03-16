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

import io.linuxserver.fleet.web.JsonTransformer;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class AbstractWebSocket {

    private static final JsonTransformer JSON = new JsonTransformer();

    private static final Queue<Session> SESSIONS = new ConcurrentLinkedQueue<>();

    protected void broadcast(WebSocketMessage message) {
        SESSIONS.forEach(s -> broadcast(s, message));
    }

    protected void broadcast(Session user, WebSocketMessage message) {

        try {
            user.getRemote().sendString(JSON.render(message));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnWebSocketConnect
    public void onConnect(Session user) {

        SESSIONS.add(user);
        broadcast(user, new WebSocketMessage(WebSocketMessage.MessageType.CONNECTED, "Connected"));
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        SESSIONS.remove(user);
    }
}
