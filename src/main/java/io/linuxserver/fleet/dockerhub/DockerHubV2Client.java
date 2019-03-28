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

import io.linuxserver.fleet.dockerhub.model.*;
import io.linuxserver.fleet.rest.HttpException;
import io.linuxserver.fleet.rest.RestClient;
import io.linuxserver.fleet.rest.RestResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Client class to interface with Docker Hub's V2 API.
 */
public class DockerHubV2Client implements DockerHubClient {

    static final String DOCKERHUB_BASE_URI = "https://hub.docker.com/v2";

    private final RestClient             restClient;
    private final DockerHubAuthenticator authenticator;

    public DockerHubV2Client(DockerHubCredentials credentials) {

        this.restClient     = new RestClient();
        this.authenticator  = new DockerHubAuthenticator(credentials, restClient);
    }

    @Override
    public DockerHubV2NamespaceLookupResult fetchAllRepositories() {

        try {

            String url = DOCKERHUB_BASE_URI + "/repositories/namespaces";

            RestResponse<DockerHubV2NamespaceLookupResult> response = doCall(url, DockerHubV2NamespaceLookupResult.class);

            if (isResponseOK(response)) {
                return response.getPayload();
            }

            return new DockerHubV2NamespaceLookupResult();

        } catch (HttpException e) {
            throw new DockerHubException("Unable to get repositories", e);
        }
    }

    @Override
    public List<DockerHubV2Image> fetchImagesFromRepository(String repositoryName) {

        List<DockerHubV2Image> images = new ArrayList<>();

        try {

            String url = DOCKERHUB_BASE_URI + "/repositories/" + repositoryName;

            while (url != null) {

                RestResponse<DockerHubV2ImageListResult> response = doCall(url, DockerHubV2ImageListResult.class);

                if (isResponseOK(response)) {

                    DockerHubV2ImageListResult payload = response.getPayload();

                    images.addAll(payload.getResults());
                    url = payload.getNext();
                }
            }

            return images;

        } catch (HttpException e) {
            throw new DockerHubException("Unable to get images for " + repositoryName, e);
        }
    }

    @Override
    public DockerHubV2Image fetchImageFromRepository(String repositoryName, String imageName) {

        try {

            String absoluteUrl = DOCKERHUB_BASE_URI + "/repositories/" + repositoryName + "/" + imageName;

            RestResponse<DockerHubV2Image> restResponse = doCall(absoluteUrl, DockerHubV2Image.class);
            return restResponse.getPayload();

        } catch (HttpException e) {
            throw new DockerHubException("Unable to get images for " + repositoryName + "/" + imageName, e);
        }
    }

    @Override
    public DockerHubV2Tag fetchLatestTagForImage(String repositoryName, String imageName) {

        try {

            String absoluteUrl = DOCKERHUB_BASE_URI + "/repositories/" + repositoryName + "/" + imageName + "/tags" ;

            RestResponse<DockerHubV2TagListResult> restResponse = doCall(absoluteUrl, DockerHubV2TagListResult.class);

            List<DockerHubV2Tag> results = restResponse.getPayload().getResults();
            if (!results.isEmpty())
                return results.get(0);

            return null;

        } catch (HttpException e) {
            throw new DockerHubException("Unable to get tags for " + repositoryName + "/" + imageName, e);
        }
    }

    /**
     * <p>
     * Attempts to call DockerHub with the currently set credentials. If they have expired, it will refresh them and try again.
     * This process will only try again once, so if the refresh resulted in another stale token, it will need to be handled.
     * </p>
     */
    private <T> RestResponse<T> doCall(String url, Class<T> responseType) {

        RestResponse<T> restResponse = restClient.executeGet(url, null, buildHeaders(), responseType);

        if (isResponseUnauthorised(restResponse)) {

            authenticator.refreshToken();
            restResponse = restClient.executeGet(url, null, buildHeaders(), responseType);
        }

        return restResponse;
    }

    private Map<String, String> buildHeaders() {

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "JWT " + authenticator.getCurrentToken());

        return headers;
    }

    private boolean isResponseOK(RestResponse restResponse) {
        return restResponse.getStatusCode() == 200;
    }

    private boolean isResponseUnauthorised(RestResponse restResponse) {
        return restResponse.getStatusCode() == 401;
    }
}
