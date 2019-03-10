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

package io.linuxserver.fleet.rest;

import io.linuxserver.fleet.rest.marshalling.JacksonMarshallingStrategy;
import io.linuxserver.fleet.rest.marshalling.MarshallingStrategy;
import io.linuxserver.fleet.rest.proxy.LazyLoadPayloadProxy;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * <p>
 * Simple wrapper for base HttpClient, providing an easier way to marshall/unmarshall payloads
 * </p>
 */
public class RestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestClient.class);

    private final CloseableHttpClient   client;
    private final HttpClientContext     clientContext;

    private MarshallingStrategy         marshallingStrategy;

    public RestClient() {

        setMarshallingStrategy(new JacksonMarshallingStrategy());
        client = HttpClients.custom().setConnectionManager(new PoolingHttpClientConnectionManager()).build();
        clientContext = HttpClientContext.create();
    }

    private void setMarshallingStrategy(MarshallingStrategy marshallingStrategy) {

        LOGGER.debug("Configuring RestClient with " + marshallingStrategy.getClass().getName());
        this.marshallingStrategy = marshallingStrategy;
    }

    public <T> RestResponse<T> executeGet(String url, Map<String, String> queryParameters, Map<String, String> headers, Class<T> responseType) {

        try {

            return executeBaseRequest(responseType, headers, new HttpGet(url + parseQueryParameters(queryParameters)));

        } catch (IOException e) {

            LOGGER.error("Unable to perform GET", e);
            throw new HttpException("Unable to perform GET", e);
        }
    }

    public <T> RestResponse<T> executePost(String url, Map<String, String> queryParameters, Map<String, String> headers, Object payload, Class<T> responseType) {


        try {

            HttpPost post = new HttpPost(url + parseQueryParameters(queryParameters));
            post.setEntity(new StringEntity(marshallingStrategy.marshall(payload), Charset.forName("UTF-8")));
            post.setHeader("Content-Type", marshallingStrategy.getContentType());

            return executeBaseRequest(responseType, headers, post);

        } catch (IOException e) {

            LOGGER.error("Unable to perform GET", e);
            throw new HttpException("Unable to perform GET", e);
        }
    }

    private <T> RestResponse<T> executeBaseRequest(Class<T> responseType, Map<String, String> headers, HttpRequestBase request) throws IOException {

        LOGGER.debug("url             : " + request.getURI().toString());
        LOGGER.debug("headers         : " + headers);

        if (headers != null) {

            for (Map.Entry<String, String> header : headers.entrySet())
                request.setHeader(header.getKey(), header.getValue());
        }

        LOGGER.debug("Executing.");
        try (CloseableHttpResponse response = client.execute(request, clientContext)) {

            StatusLine statusLine = response.getStatusLine();
            LOGGER.debug("Response status: " + statusLine);

            int statusCode = statusLine.getStatusCode();

            HttpEntity content = response.getEntity();

            String responseBody = null;
            if (null != content) {

                responseBody = EntityUtils.toString(content);
                LOGGER.debug("Parsed response payload: " + responseBody);
            }

            if (null != responseBody)
                return new RestResponse<>(new LazyLoadPayloadProxy<>(marshallingStrategy, responseBody, responseType), statusCode);

            return new RestResponse<>(statusCode);

        } finally {
            request.releaseConnection();
        }
    }

    private String parseQueryParameters(Map<String, String> queryParameters) {

        if (null != queryParameters) {

            StringBuilder builtParameterString = new StringBuilder("?");

            for (Map.Entry<String, String> param : queryParameters.entrySet())
                builtParameterString.append(param.getKey()).append("=").append(param.getValue()).append("&");

            builtParameterString.setLength(builtParameterString.length() - 1);

            return builtParameterString.toString();
        }

        return "";
    }
}
