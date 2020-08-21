/*
 * Copyright (c)  2019 LinuxServer.io
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

package io.linuxserver.fleet.v2.client.docker.dockerhub;

import io.linuxserver.fleet.dockerhub.DockerHubException;
import io.linuxserver.fleet.dockerhub.model.DockerHubV2Image;
import io.linuxserver.fleet.dockerhub.model.DockerHubV2ImageListResult;
import io.linuxserver.fleet.dockerhub.model.DockerHubV2TagListResult;
import io.linuxserver.fleet.v2.client.docker.DockerApiClient;
import io.linuxserver.fleet.v2.client.rest.HttpException;
import io.linuxserver.fleet.v2.client.rest.RestClient;
import io.linuxserver.fleet.v2.client.rest.RestResponse;
import io.linuxserver.fleet.v2.types.docker.DockerImage;
import io.linuxserver.fleet.v2.types.docker.DockerTag;

import java.util.ArrayList;
import java.util.List;

public class DockerHubApiClient implements DockerApiClient {

    private static final String DockerHubApiUrl = "https://hub.docker.com/v2";
    private static final int    DefaultPageSize = 1000;

    private final RestClient restClient;

    private final DockerHubImageConverter imageConverter;
    private final DockerHubTagConverter   tagConverter;

    public DockerHubApiClient() {

        restClient     = new RestClient();
        imageConverter = new DockerHubImageConverter();
        tagConverter   = new DockerHubTagConverter();
    }

    @Override
    public List<DockerImage> fetchAllImages(String repositoryName) {

        final List<DockerImage> images = new ArrayList<>();

        try {

            String url = DockerHubApiUrl + "/repositories/" + repositoryName + "/?page_size=" + DefaultPageSize;
            while (url != null) {

                final RestResponse<DockerHubV2ImageListResult> response = doCall(url, DockerHubV2ImageListResult.class);

                if (isResponseOK(response)) {

                    DockerHubV2ImageListResult payload = response.getPayload();
                    payload.getResults().forEach(i -> {

                        final DockerImage converted = imageConverter.convert(i);
                        if (null != converted) {
                            images.add(converted);
                        }
                    });

                    url = payload.getNext();
                }
            }

            return images;

        } catch (HttpException e) {
            throw new DockerHubException("Unable to get images for " + repositoryName, e);
        }
    }

    @Override
    public boolean isRepositoryValid(String repositoryName) {

        try {
            return !fetchAllImages(repositoryName).isEmpty();
        } catch (HttpException e) {
            throw new DockerHubException("Unable to verify repository " + repositoryName, e);
        }
    }

    @Override
    public DockerImage fetchImage(String imageName) {

        try {

            final  String absoluteUrl = DockerHubApiUrl + "/repositories/" + imageName + "/";

            final RestResponse<DockerHubV2Image> restResponse = doCall(absoluteUrl, DockerHubV2Image.class);

            if (isResponseOK(restResponse)) {
                return imageConverter.convert(restResponse.getPayload());
            }

            return null;

        } catch (HttpException e) {
            throw new DockerHubException("Unable to get images for " + imageName, e);
        }
    }

    @Override
    public List<DockerTag> fetchImageTags(String imageName) {

        try {

            List<DockerTag> tags = new ArrayList<>();

            String absoluteUrl = DockerHubApiUrl + "/repositories/" + imageName + "/tags/?page_size=" + DefaultPageSize;
            while (absoluteUrl != null) {

                final RestResponse<DockerHubV2TagListResult> response = doCall(absoluteUrl, DockerHubV2TagListResult.class);

                if (isResponseOK(response)) {

                    final DockerHubV2TagListResult payload = response.getPayload();
                    payload.getResults().forEach(t -> {

                        final DockerTag converted = tagConverter.convert(t);
                        if (null != converted) {
                            tags.add(converted);
                        }
                    });

                    absoluteUrl = payload.getNext();
                }
            }

            return tags;

        } catch (HttpException e) {
            throw new DockerHubException("Unable to get tags for " + imageName, e);
        }
    }

    /**
     * <p>
     * Attempts to call DockerHub with the currently set credentials. If they have expired, it will refresh them and try again.
     * This process will only try again once, so if the refresh resulted in another stale token, it will need to be handled.
     * </p>
     */
    private <T> RestResponse<T> doCall(String url, Class<T> responseType) {
        return restClient.executeGet(url, null, null, responseType);
    }

    private boolean isResponseOK(final RestResponse<?> restResponse) {
        return restResponse.getStatusCode() == 200;
    }
}
