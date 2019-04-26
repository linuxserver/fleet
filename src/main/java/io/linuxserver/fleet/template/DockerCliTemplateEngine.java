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

import io.linuxserver.fleet.model.template.*;

import java.io.IOException;
import java.io.Writer;

/**
 * <p>
 * Converts the image params into a runnable <code>docker create</code> command.
 * </p>
 */
public class DockerCliTemplateEngine extends AbstractTemplateEngine {

    private static final String NEWLINE = " \\\n";

    public DockerCliTemplateEngine() {
        super(null);
    }

    @Override
    public String getTemplateType() {
        return "docker-cli";
    }

    @Override
    public void renderTemplate(ImageTemplateParams params, Writer writer) throws IOException {

        writer.write("docker create" + NEWLINE);
        writer.write("    --name=" + params.getImageName() + NEWLINE);
        writer.write("    --restart " + params.getRestartPolicy() + NEWLINE);

        if (params.isHostNetwork()) {
            writer.write("    --network=host" + NEWLINE);
        }

        if (params.isPrivileged()) {
            writer.write("    --privileged" + NEWLINE);
        }

        for (ImageTemplateEnvironment env : params.getEnvironments()) {
            writer.write("    -e " + env.getKey() + "=" + env.getValue() + NEWLINE);
        }

        for (ImageTemplatePort port : params.getPorts()) {
            writer.write("    -p " + port.getHostValue() + ":" + port.getContainerValue() + "/" + port.getProtocol() + NEWLINE);
        }

        for (ImageTemplateVolume volume : params.getVolumes()) {
            writer.write("    -v " + volume.getHostValue() + ":" + volume.getContainerValue() + (volume.isReadOnly() ? ":ro" : "") + NEWLINE);
        }

        for (ImageTemplateExtraParam extra : params.getExtra()) {
            writer.write("    " + extra.getKey() + "=" + extra.getValue() + NEWLINE);
        }

        writer.write("    " + params.getRepositoryName() + "/" + params.getImageName());
    }
}
