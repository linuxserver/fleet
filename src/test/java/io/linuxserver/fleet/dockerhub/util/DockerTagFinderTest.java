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

package io.linuxserver.fleet.dockerhub.util;

import io.linuxserver.fleet.v2.types.docker.DockerTag;
import io.linuxserver.fleet.v2.types.docker.DockerTagManifestDigest;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DockerTagFinderTest {

    @Test
    public void shouldFindCorrectTagIfAllDigestsMatch() {

        final DockerTag named = new DockerTag("latest", 1234L, LocalDateTime.now());
        named.addDigest(new DockerTagManifestDigest(1234L, "digest1", "arch", "variant1"));
        named.addDigest(new DockerTagManifestDigest(1234L, "digest2", "arch", "variant2"));
        named.addDigest(new DockerTagManifestDigest(1234L, "digest3", "arch", "variant3"));

        final DockerTag shouldMatch = new DockerTag("v1234", 1234L, LocalDateTime.now());
        shouldMatch.addDigest(new DockerTagManifestDigest(1234L, "digest1", "arch", "variant1"));
        shouldMatch.addDigest(new DockerTagManifestDigest(1234L, "digest2", "arch", "variant2"));
        shouldMatch.addDigest(new DockerTagManifestDigest(1234L, "digest3", "arch", "variant3"));

        final DockerTag shouldNotMatch = new DockerTag("v1234", 1234L, LocalDateTime.now());
        shouldNotMatch.addDigest(new DockerTagManifestDigest(1234L, "digest1", "arch", "variant1"));
        shouldNotMatch.addDigest(new DockerTagManifestDigest(1234L, "digest4", "arch", "variant2"));
        shouldNotMatch.addDigest(new DockerTagManifestDigest(1234L, "digest3", "arch", "variant3"));

        final List<DockerTag> tags = new ArrayList<>();
        tags.add(named);
        tags.add(shouldMatch);
        tags.add(shouldNotMatch);

        assertThat(DockerTagFinder.findVersionedTagMatchingBranch(tags, "latest"), is(equalTo(shouldMatch)));
    }

    @Test
    public void shouldReturnFoundNamedTagIfNoOthersMatchFully() {

        final DockerTag named = new DockerTag("latest", 1234L, LocalDateTime.now());
        named.addDigest(new DockerTagManifestDigest(1234L, "digest1", "arch", "variant1"));
        named.addDigest(new DockerTagManifestDigest(1234L, "digest2", "arch", "variant2"));
        named.addDigest(new DockerTagManifestDigest(1234L, "digest3", "arch", "variant3"));

        final DockerTag shouldMatch = new DockerTag("v1234", 1234L, LocalDateTime.now());
        shouldMatch.addDigest(new DockerTagManifestDigest(1234L, "digest1", "arch", "variant1"));
        shouldMatch.addDigest(new DockerTagManifestDigest(1234L, "digest5", "arch", "variant2"));
        shouldMatch.addDigest(new DockerTagManifestDigest(1234L, "digest3", "arch", "variant3"));

        final DockerTag shouldNotMatch = new DockerTag("v1234", 1234L, LocalDateTime.now());
        shouldNotMatch.addDigest(new DockerTagManifestDigest(1234L, "digest1", "arch", "variant1"));
        shouldNotMatch.addDigest(new DockerTagManifestDigest(1234L, "digest4", "arch", "variant2"));
        shouldNotMatch.addDigest(new DockerTagManifestDigest(1234L, "digest3", "arch", "variant3"));

        final List<DockerTag> tags = new ArrayList<>();
        tags.add(named);
        tags.add(shouldMatch);
        tags.add(shouldNotMatch);

        assertThat(DockerTagFinder.findVersionedTagMatchingBranch(tags, "latest"), is(equalTo(named)));
    }
}
