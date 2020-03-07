/*
 * Copyright (c)  2020 LinuxServer.io
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

package io.linuxserver.fleet.v2.types.internal;

import io.linuxserver.fleet.v2.types.meta.ExternalUrl;
import io.linuxserver.fleet.v2.types.meta.ExternalUrlKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImageUrlsUpdateRequest extends AbstractParamRequest {

    public ImageUrlsUpdateRequest(final Map<String, List<String>> params) {
        super(params);
    }

    public final List<ExternalUrl> getExternalUrls() {

        final List<String> urlKeys  = getParams("imageExternalUrlKey");
        final List<String> urlTypes = getParams("imageExternalUrlType");
        final List<String> urlNames = getParams("imageExternalUrlName");
        final List<String> urlPaths = getParams("imageExternalUrlPath");

        checkLists(urlKeys, urlTypes, urlNames, urlPaths);

        final List<ExternalUrl> urls = new ArrayList<>();
        if (null != urlKeys) {

            int i = 0;
            for (; i < urlKeys.size(); i++) {

                urls.add(new ExternalUrl(new ExternalUrlKey(Integer.parseInt(urlKeys.get(i))),
                                         ExternalUrl.ExternalUrlType.valueOf(urlTypes.get(i)),
                                         urlNames.get(i),
                                         urlPaths.get(i)));
            }
        }

        return urls;
    }
}
