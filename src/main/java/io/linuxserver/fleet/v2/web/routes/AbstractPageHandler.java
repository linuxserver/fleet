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

package io.linuxserver.fleet.v2.web.routes;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.linuxserver.fleet.core.FleetAppController;
import io.linuxserver.fleet.v2.service.AbstractAppService;
import io.linuxserver.fleet.v2.web.PageModelAttributes;
import io.linuxserver.fleet.v2.web.PageModelSpec;
import io.linuxserver.fleet.v2.web.SessionAttributes;
import io.linuxserver.fleet.v2.web.freemarker.CustomFreemarkerTemplate;
import io.linuxserver.fleet.v2.web.freemarker.Java8DateTimeMethod;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static io.javalin.plugin.rendering.template.TemplateUtil.model;

public abstract class AbstractPageHandler extends AbstractAppService implements Handler {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private static final List<CustomFreemarkerTemplate> CUSTOM_TEMPLATES;
    static {

        CUSTOM_TEMPLATES = new ArrayList<>();
        CUSTOM_TEMPLATES.add(new Java8DateTimeMethod());
    }

    AbstractPageHandler(final FleetAppController controller) {
        super(controller);
        LOGGER.info("Registering web route.");
    }

    @Override
    public final void handle(@NotNull Context ctx) {

        try {

            PageModelSpec spec;
            if ("get".equalsIgnoreCase(ctx.method())) {
                spec = handlePageLoad(ctx);
            } else if ("post".equalsIgnoreCase(ctx.method())) {
                spec = handleFormSubmission(ctx);
            } else {

                ctx.render("views/pages/error.ftl", model("error", "You can't load the page this way."));
                return;
            }

            injectCustomMethods(spec);
            injectTopLevelModelAttributes(ctx, spec);
            checkViewForRedirect(ctx, spec);

        } catch (Exception e) {

            LOGGER.error("Unexpected error occurred when loading page.", e);
            ctx.render("views/pages/error.ftl", model("error", "Something unexepected happened", "exception", e));
        }
    }

    protected abstract PageModelSpec handlePageLoad(Context ctx);

    protected abstract PageModelSpec handleFormSubmission(Context ctx);

    protected void injectCustomMethods(final PageModelSpec spec) {

        for (CustomFreemarkerTemplate template : CUSTOM_TEMPLATES) {
            spec.addModelAttribute(template.getName(), template);
        }
    }

    private void injectTopLevelModelAttributes(final Context ctx, final PageModelSpec spec) {

        spec.addModelAttribute(PageModelAttributes.AuthenticatedUser, ctx.sessionAttribute(SessionAttributes.AuthenticatedUser));
        spec.addModelAttribute(PageModelAttributes.SystemAlerts,      getController().getSystemAlerts());
    }

    private void checkViewForRedirect(final Context ctx, final PageModelSpec spec) {

        if (isRedirect(spec.getViewName())) {
            ctx.redirect(spec.getViewName().split(":", 2)[1]);
        } else {
            ctx.render(spec.getViewName(), spec.getModel());
        }
    }

    private boolean isRedirect(final String view) {
        return null != view && view.startsWith("redirect:");
    }
}
