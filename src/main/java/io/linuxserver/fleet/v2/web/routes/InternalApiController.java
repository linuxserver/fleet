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

package io.linuxserver.fleet.v2.web.routes;

import io.javalin.http.Context;
import io.linuxserver.fleet.core.FleetAppController;
import io.linuxserver.fleet.v2.key.ImageKey;
import io.linuxserver.fleet.v2.key.RepositoryKey;
import io.linuxserver.fleet.v2.key.ScheduleKey;
import io.linuxserver.fleet.v2.service.AbstractAppService;
import io.linuxserver.fleet.v2.thread.schedule.AppSchedule;
import io.linuxserver.fleet.v2.types.Image;
import io.linuxserver.fleet.v2.types.Repository;
import io.linuxserver.fleet.v2.types.api.ApiImagePullHistoryWrapper;
import io.linuxserver.fleet.v2.types.api.ApiImageWrapper;
import io.linuxserver.fleet.v2.types.api.ApiRepositoryWrapper;
import io.linuxserver.fleet.v2.types.api.ApiScheduleWrapper;
import io.linuxserver.fleet.v2.types.internal.RepositoryOutlineRequest;
import io.linuxserver.fleet.v2.types.meta.ItemSyncSpec;
import io.linuxserver.fleet.v2.types.meta.history.ImagePullStatistic.StatGroupMode;
import io.linuxserver.fleet.v2.web.ApiException;
import io.linuxserver.fleet.v2.web.request.json.NewRepositoryRequest;
import io.linuxserver.fleet.v2.web.request.json.UpdateImageSpecRequest;
import io.linuxserver.fleet.v2.web.request.json.UpdateRepositoryRequest;

public class InternalApiController extends AbstractAppService {

    public InternalApiController(final FleetAppController controller) {
        super(controller);
    }

    public final void updateImageSpec(final Context ctx) {

        try {

            final UpdateImageSpecRequest request = ctx.bodyValidator(UpdateImageSpecRequest.class)
                    .check(req -> req.getImageKey() != null).get();

            final ItemSyncSpec spec = ItemSyncSpec.Default.copyOf();
            spec.setSynchronised(request.isSyncEnabled());
            spec.setStable(request.isStable());
            spec.setHidden(request.isHidden());
            spec.setDeprecated(request.isDeprecated());
            spec.setVersionMask(request.getVersionMask());

            final Image updated = getController().getImageService()
                    .updateImageSpec(ImageKey.parse(request.getImageKey()), spec);

            ctx.json(new ApiImageWrapper(updated));

        } catch (IllegalArgumentException e) {
            throw new ApiException(e.getMessage(), e);
        }
    }

    public final void updateRepositorySpec(final Context ctx) {

        try {

            final UpdateRepositoryRequest request = ctx.bodyValidator(UpdateRepositoryRequest.class)
                    .check(req -> req.getRepositoryKey() != null).get();

            final ItemSyncSpec spec = ItemSyncSpec.Default.copyOf();
            spec.setSynchronised(request.isSyncEnabled());
            spec.setVersionMask(request.getVersionMask());

            final Repository updated = getController().getImageService()
                    .updateRepositorySpec(RepositoryKey.parse(request.getRepositoryKey()), spec);

            ctx.json(new ApiRepositoryWrapper(updated));

        } catch (IllegalArgumentException e) {
            throw new ApiException(e.getMessage(), e);
        }
    }

    public final void addNewRepository(final Context ctx) {

        try {

            final NewRepositoryRequest request = ctx.bodyValidator(NewRepositoryRequest.class)
                    .check(req -> req.getRepositoryName() != null).get();

            final Repository newlyCreatedRepository = getController()
                    .verifyRepositoryAndCreateOutline(new RepositoryOutlineRequest(request.getRepositoryName()));

            ctx.json(new ApiRepositoryWrapper(newlyCreatedRepository));

        } catch (IllegalArgumentException e) {
            throw new ApiException(e.getMessage(), e);
        }
    }

    public final void runSchedule(final Context ctx) {

        try {

            final Integer scheduleKey = ctx.formParam("scheduleKey", Integer.class).get();
            final AppSchedule schedule = getController().getScheduleService().forceRun(new ScheduleKey(scheduleKey));

            ctx.json(new ApiScheduleWrapper(schedule));

        } catch (IllegalArgumentException e) {
            throw new ApiException(e.getMessage(), e);
        }
    }

    public final void syncRepository(final Context ctx) {

        try {

            final RepositoryKey repositoryKey = ctx.formParam("repositoryKey", RepositoryKey.class).get();
            final Repository    repository    = getController().getImageService().getRepository(repositoryKey);

            getController().synchroniseRepository(repository);

            ctx.json(new ApiRepositoryWrapper(repository));

        } catch (IllegalArgumentException e) {
            throw new ApiException(e.getMessage(), e);
        }
    }

    public final void syncImage(final Context ctx) {

        try {

            final ImageKey imageKeyParam = ctx.formParam("imageKey", ImageKey.class).get();
            getController().synchroniseImage(imageKeyParam);

            ctx.json("OK");

        } catch (IllegalArgumentException e) {
            throw new ApiException(e.getMessage(), e);
        }
    }

    public void deleteRepository(final Context ctx) {

        try {

            final RepositoryKey repositoryKeyParam = ctx.queryParam("repositoryKey", RepositoryKey.class).get();
            getController().getImageService().removeRepository(repositoryKeyParam);

            ctx.result("OK");

        } catch (IllegalArgumentException e) {
            throw new ApiException(e.getMessage(), e);
        }
    }

    public void getImagePullHistory(final Context ctx) {

        try {

            final ImageKey      imageKeyParam = ctx.queryParam("imageKey", ImageKey.class).get();
            final StatGroupMode groupMode     = ctx.queryParam("groupMode", StatGroupMode.class).get();
            final Image         cachedImage   = getController().getImageService().getImage(imageKeyParam);

            ctx.json(new ApiImagePullHistoryWrapper(cachedImage.getMetaData().getHistoryFor(groupMode), groupMode));

        } catch (IllegalArgumentException e) {
            throw new ApiException(e.getMessage(), e);
        }
    }

    public void trackNewBranch(final Context ctx) {

        try {

            final ImageKey imageKey   = ctx.formParam("imageKey",   ImageKey.class).get();
            final String   branchName = ctx.formParam("branchName", String.class).get();

            getController().trackBranch(imageKey, branchName);

        } catch (IllegalArgumentException e) {
            throw new ApiException(e.getMessage(), e);
        }
    }

    public void removeTrackedBranch(final Context ctx) {

        try {

            final ImageKey imageKey   = ctx.queryParam("imageKey",   ImageKey.class).get();
            final String   branchName = ctx.queryParam("branchName", String.class).get();

            getController().getImageService().removeTrackedBranch(imageKey, branchName);

        } catch (IllegalArgumentException e) {
            throw new ApiException(e.getMessage(), e);
        }
    }
}
