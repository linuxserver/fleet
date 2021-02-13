package io.linuxserver.fleet.v2.client.docker.dockerhub;

import io.linuxserver.fleet.v2.Utils;

public class DockerHubCredentials {

    private final String username;
    private final String password;

    public DockerHubCredentials(final String username,
                                final String password) {
        this.username = Utils.ensureNotNull(username);
        this.password = Utils.ensureNotNull(password);
    }

    public final String getUsername() {
        return username;
    }

    public final String getPassword() {
        return password;
    }
}
