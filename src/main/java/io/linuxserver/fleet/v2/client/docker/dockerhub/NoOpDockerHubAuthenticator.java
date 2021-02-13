package io.linuxserver.fleet.v2.client.docker.dockerhub;

import java.util.HashMap;
import java.util.Map;

public class NoOpDockerHubAuthenticator implements IDockerHubAuthenticator {

    @Override
    public Map<String, String> buildAuthHeaders() {
        return new HashMap<>();
    }

    @Override
    public String refreshToken() {
        return null;
    }
}
