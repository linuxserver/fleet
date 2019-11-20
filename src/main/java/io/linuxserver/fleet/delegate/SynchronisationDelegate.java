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

import io.linuxserver.fleet.sync.DefaultLoggingSyncListener;
import io.linuxserver.fleet.sync.DefaultSynchronisationState;
import io.linuxserver.fleet.sync.SynchronisationContext;
import io.linuxserver.fleet.sync.SynchronisationListener;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Handles the one-way synchronisation of Docker Hub repositories and images over to the Fleet
 * database. Any newly created image in Docker Hub will be automatically picked up and stored, but any
 * new repositories will be marked as skipped until someone manually sets it to be synchronised.
 * </p>
 */
public class SynchronisationDelegate implements SynchronisationContext {

    private final ImageDelegate         imageDelegate;
    private final RepositoryDelegate    repositoryDelegate;
    private final DockerHubDelegate     dockerHubDelegate;

    private List<SynchronisationListener> listeners;

    private boolean fullRmProtected;

    public SynchronisationDelegate(ImageDelegate imageDelegate, RepositoryDelegate repositoryDelegate, DockerHubDelegate dockerHubDelegate) {

        this.imageDelegate = imageDelegate;
        this.repositoryDelegate = repositoryDelegate;
        this.dockerHubDelegate = dockerHubDelegate;

        this.listeners = new ArrayList<>();
        registerListener(new DefaultLoggingSyncListener());
    }

    public final void setFullRmProtected(final boolean fullRmProtected) {
        this.fullRmProtected = fullRmProtected;
    }

    /**
     * <p>
     * Starts a new synchronisation. This uses the statically assigned synchronisation state
     * to determine whether or not a synchronisation will actually take place. Consider triggering
     * this via {@link io.linuxserver.fleet.thread.SynchroniseAllRepositoriesTask}.
     * </p>
     */
    @Override
    public void synchronise() {
        DefaultSynchronisationState.instance().synchronise(this);
    }

    @Override
    public void registerListener(SynchronisationListener listener) {
        listeners.add(listener);
    }

    @Override
    public List<SynchronisationListener> getListeners() {
        return listeners;
    }

    @Override
    public ImageDelegate getImageDelegate() {
        return imageDelegate;
    }

    @Override
    public RepositoryDelegate getRepositoryDelegate() {
        return repositoryDelegate;
    }

    @Override
    public DockerHubDelegate getDockerHubDelegate() {
        return dockerHubDelegate;
    }

    @Override
    public boolean isFullRmProtected() {
        return fullRmProtected;
    }
}
