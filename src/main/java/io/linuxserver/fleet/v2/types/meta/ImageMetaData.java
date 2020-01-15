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

    public ImageMetaData(final ImagePullHistory pullHistory, final ImageTemplateHolder templateHolder) {
        this.pullHistory    = pullHistory;
        this.templateHolder = templateHolder;
    }

    public final ImageMetaData cloneWithTemplate(final ImageTemplateHolder templateHolder) {
        return new ImageMetaData(pullHistory, templateHolder);
    }

    public final List<ImagePullStatistic> getHistoryFor(final ImagePullStatistic.StatGroupMode groupMode) {
        return pullHistory.getHistoryFor(groupMode);
    }

    public final ImageTemplateHolder getTemplates() {
        return templateHolder;
    }
}
