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

package io.linuxserver.fleet.delegate;

import io.linuxserver.fleet.sync.*;

/**
 * <p>
 * Handles the one-way synchronisation of Docker Hub repositories and images over to the Fleet
 * database. Any newly created image in Docker Hub will be automatically picked up and stored, but any
 * new repositories will be marked as skipped until someone manually sets it to be synchronised.
 * </p>
 */
public class SynchronisationDelegate implements SynchronisationContext {

    private SynchronisationState    state;
    private SynchronisationListener listener;

    public SynchronisationDelegate(ImageDelegate imageDelegate, RepositoryDelegate repositoryDelegate, DockerHubDelegate dockerHubDelegate) {

        this.state = new SyncIdleState(imageDelegate, repositoryDelegate, dockerHubDelegate);
        setListener(new DefaultLoggingSyncListener());
    }

    @Override
    public void synchronise() {
        state.synchronise(this);
    }

    @Override
    public void setState(SynchronisationState state) {
        this.state = state;
    }

    @Override
    public SynchronisationState getState() {
        return state;
    }

    @Override
    public void setListener(SynchronisationListener listener) {
        this.listener = listener;
    }

    @Override
    public SynchronisationListener getListener() {
        return listener;
    }
}
