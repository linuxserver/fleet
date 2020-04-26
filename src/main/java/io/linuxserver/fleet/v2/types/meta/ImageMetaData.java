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

package io.linuxserver.fleet.v2.types.meta;

import io.linuxserver.fleet.v2.types.meta.history.ImagePullHistory;
import io.linuxserver.fleet.v2.types.meta.history.ImagePullStatistic;
import io.linuxserver.fleet.v2.types.meta.template.ImageTemplateHolder;

import java.util.List;

public class ImageMetaData {

    private final ImagePullHistory    pullHistory;
    private final ImageTemplateHolder templateHolder;
    private final ImageCoreMeta       coreMeta;

    public ImageMetaData(final ImageCoreMeta coreMeta,
                         final ImagePullHistory pullHistory,
                         final ImageTemplateHolder templateHolder) {
        this.coreMeta       = coreMeta;
        this.pullHistory    = pullHistory;
        this.templateHolder = templateHolder;
    }

    public final ImageMetaData cloneWithTemplate(final ImageTemplateHolder templateHolder) {
        return new ImageMetaData(getCoreMeta(), pullHistory, templateHolder);
    }

    public final ImageMetaData cloneWithCoreMeta(final ImageCoreMeta coreMeta) {
        return new ImageMetaData(coreMeta, pullHistory, getTemplates());
    }

    public final List<ImagePullStatistic> getHistoryFor(final ImagePullStatistic.StatGroupMode groupMode) {
        return pullHistory.getHistoryFor(groupMode);
    }

    public final ImageTemplateHolder getTemplates() {
        return templateHolder;
    }

    public final ImageCoreMeta getCoreMeta() {
        return coreMeta;
    }

    public final String getAppImagePath() {
        return getCoreMeta().getAppImagePath();
    }

    public final String getBaseImage() {
        return getCoreMeta().getBaseImage();
    }

    public final String getCategory() {
        return getCoreMeta().getCategory();
    }

    public final List<ExternalUrl> getExternalUrls() {
        return getCoreMeta().getExternalUrls();
    }

    public final boolean isPopulated() {
        return (null != getCategory() && !getCategory().isBlank()) || !getExternalUrls().isEmpty();
    }
}
