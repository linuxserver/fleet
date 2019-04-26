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

package io.linuxserver.fleet.template;

import io.linuxserver.fleet.model.template.ImageTemplateParams;

import java.io.IOException;
import java.io.Writer;

/**
 * <p>
 * Provides the contract for all template engines for rendering a template
 * based on the Docker parameters for a single image.
 * </p>
 */
public interface ImageTemplateEngine {

    /**
     * <p>
     * Writes a rendered view of the image Docker parameters into a specific view.
     * </p>
     *
     * @param params All necessary params for a Docker command.
     * @param writer The output destination. This will be closed when finished.
     */
    void renderTemplate(ImageTemplateParams params, Writer writer) throws IOException;
}
