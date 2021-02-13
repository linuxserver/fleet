package io.linuxserver.fleet.v2.client.docker.dockerhub;

import io.linuxserver.fleet.dockerhub.DockerHubException;
import io.linuxserver.fleet.v2.client.rest.RestClient;
import io.linuxserver.fleet.v2.client.rest.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class DockerHubAuthenticator implements IDockerHubAuthenticator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DockerHubAuthenticator.class);

    private final RestClient           client;
    private final DockerHubCredentials credentials;

    private String token;

    public DockerHubAuthenticator(final DockerHubCredentials credentials, final RestClient client) {

        this.credentials = credentials;
        this.client = client;

        refreshToken();
    }

    /**
     * <p>
     * Re-authenticates with Docker Hub to obtain a fresh JWT.
     * </p>
     *
     * @return
     *      The new JWT to be used in authenticated requests.
     */
    public synchronized String refreshToken() {

        LOGGER.info("Refreshing token for Docker Hub authentication");

        final RestResponse<DockerHubTokenResponse> authenticationResponse = client.executePost(
                DockerHubApiClient.DockerHubApiUrl + "/users/login", null, null, credentials, DockerHubTokenResponse.class);

        if (authenticationResponse.getStatusCode() == 200) {

            LOGGER.info("Refresh successful");

            final String token = authenticationResponse.getPayload().getToken();
            this.token = token;

            return token;
        }

        LOGGER.info("Unable to refresh token.");

        throw new DockerHubException("Unable to authenticate with Docker Hub. Check credentials");
    }

    synchronized String getCurrentToken() {

        if (null == token) {
            return refreshToken();
        }
        return token;
    }

    @Override
    public Map<String, String> buildAuthHeaders() {

        final Map<String, String> authHeaders = new HashMap<>();
        authHeaders.put("Authorization", "JWT " + getCurrentToken());
        return authHeaders;
    }

    static class DockerHubTokenResponse {

        private String token;

        public String getToken() {
            return token;
        }
    }
}
