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

/**
 * <p>
 * Provides a mechanism for a password to be encoded using a strong cryptographic algorithm.
 * The general idea of this interface has been taken from Spring's own implementation of this.
 * <a href="https://docs.spring.io/spring-security/site/docs/4.2.4.RELEASE/apidocs/org/springframework/security/crypto/password/PasswordEncoder.html">PasswordEncoder</a>
 * </p>
 */
public interface PasswordEncoder {

    /**
     * <p>
     * Encodes the raw password into a one-way encrypted hash. The result of which should be stored.
     * </p>
     *
     * @param rawPassword
     *      The raw unencrypted password.
     *
     * @return
     *      The hashed result.
     */
    String encode(String rawPassword);

    /**
     * <p>
     * Determines if the provided raw password, when encoded, matches the stored encoded password.
     * </p>
     *
     * @param rawPassword
     *      The raw password to check
     * @param encodedPassword
     *      The originally encoded and stored password
     * @return
     *      true if the passwords match, false if not.
     */
    boolean matches(String rawPassword, String encodedPassword);
}
