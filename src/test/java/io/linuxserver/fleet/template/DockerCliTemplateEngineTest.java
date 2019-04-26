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

import io.linuxserver.fleet.model.template.ImageTemplateEnvironment;
import io.linuxserver.fleet.model.template.ImageTemplateParams;
import io.linuxserver.fleet.model.template.ImageTemplatePort;
import io.linuxserver.fleet.model.template.ImageTemplateVolume;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DockerCliTemplateEngineTest {

    private DockerCliTemplateEngine engine;

    @Before
    public void before() {
        engine = new DockerCliTemplateEngine();
    }

    @Test
    public void shouldGenerateCreateCommand() throws IOException {

        ImageTemplateParams params = new ImageTemplateParams("test", "image");
        params.setHostNetwork(true);
        params.setPrivileged(true);
        params.setRestartPolicy("always");

        params.withEnvironment(new ImageTemplateEnvironment("PUID", "1000"));
        params.withEnvironment(new ImageTemplateEnvironment("TZ", "Europe/London"));

        params.withPort(new ImageTemplatePort(1337, 1337, "tcp"));
        params.withPort(new ImageTemplatePort(1338, 1339, "udp"));

        params.withVolume(new ImageTemplateVolume("/home/user/config", "/config", false));
        params.withVolume(new ImageTemplateVolume("/storage/data", "/data", true));

        Writer writer = new StringWriter();
        engine.renderTemplate(params, writer);

        String expectedOutput = "docker create \\\n" +
            "    --name=image \\\n" +
            "    --restart always \\\n" +
            "    --network=host \\\n" +
            "    --privileged \\\n" +
            "    -e PUID=1000 \\\n" +
            "    -e TZ=Europe/London \\\n" +
            "    -p 1337:1337/tcp \\\n" +
            "    -p 1338:1339/udp \\\n" +
            "    -v /home/user/config:/config \\\n" +
            "    -v /storage/data:/data:ro \\\n" +
            "    test/image";

        assertThat(writer.toString(), is(equalTo(expectedOutput)));
    }
}
