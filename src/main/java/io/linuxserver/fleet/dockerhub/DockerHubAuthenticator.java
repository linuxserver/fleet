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

package io.linuxserver.fleet.dockerhub;

import io.linuxserver.fleet.rest.RestClient;
import io.linuxserver.fleet.rest.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DockerHubAuthenticator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DockerHubAuthenticator.class);

    private final RestClient            client;
    private final DockerHubCredentials  credentials;

    private String token;

    DockerHubAuthenticator(DockerHubCredentials credentials, RestClient client) {

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
    synchronized String refreshToken() {

        LOGGER.info("Refreshing token for Docker Hub authentication");

        RestResponse<DockerHubTokenResponse> authenticationResponse = client.executePost(
            DockerHubV2Client.DOCKERHUB_BASE_URI + "/users/login", null, null, credentials, DockerHubTokenResponse.class);

        if (authenticationResponse.getStatusCode() == 200) {

            LOGGER.info("Refresh successful");

            String token = authenticationResponse.getPayload().getToken();
            this.token = token;

            return token;
        }

        LOGGER.info("Unable to refresh token.");

        throw new DockerHubException("Unable to authenticate with Docker Hub. Check credentials");
    }

    synchronized String getCurrentToken() {

        if (null == token)
            return refreshToken();

        return token;
    }

    static class DockerHubTokenResponse {

        private String token;

        public String getToken() {
            return token;
        }
    }
}
