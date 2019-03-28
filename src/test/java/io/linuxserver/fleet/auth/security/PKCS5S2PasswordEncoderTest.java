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

package io.linuxserver.fleet.auth.security;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PKCS5S2PasswordEncoderTest {

    private static final String HASH_FOR_PASSWORD = "CYvjZH/RPa+6Y4JVQiKjOTEIrdntD+6DJDmolYpO0gta8gjI5yRiN/dRoHr0PhpJB0EoTmqR4gJakqk6HElT2yRaLp6WPG4U0GZIYjQSUo8=";

    private PKCS5S2PasswordEncoder encoder = new PKCS5S2PasswordEncoder("superSecret");

    @Test
    public void shouldGenerateHash() {
        assertThat(encoder.matches("password", HASH_FOR_PASSWORD), is(equalTo(true)));
    }
}
