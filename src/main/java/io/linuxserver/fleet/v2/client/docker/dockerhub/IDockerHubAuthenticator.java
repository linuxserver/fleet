package io.linuxserver.fleet.v2.client.docker.dockerhub;

import java.util.Map;

public interface IDockerHubAuthenticator {

    Map<String, String> buildAuthHeaders();

    String refreshToken();
}
