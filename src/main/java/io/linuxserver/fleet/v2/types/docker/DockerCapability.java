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

package io.linuxserver.fleet.v2.types.docker;

public enum DockerCapability {

    AUDIT_CONTROL,
    AUDIT_WRITE,
    BLOCK_SUSPEND,
    CHOWN,
    DAC_OVERRIDE,
    DAC_READ_SEARCH,
    FOWNER,
    FSETID,
    IPC_LOCK,
    IPC_OWNER,
    KILL,
    LEASE,
    LINUX_IMMUTABLE,
    MAC_ADMIN,
    MAC_OVERRIDE,
    MKNOD,
    NET_ADMIN,
    NET_BIND_SERVICE,
    NET_BROADCAST,
    NET_RAW,
    SETFCAP,
    SETGID,
    SETPCAP,
    SETUID,
    SYSLOG,
    SYS_ADMIN,
    SYS_BOOT,
    SYS_CHROOT,
    SYS_MODULE,
    SYS_NICE,
    SYS_PACCT,
    SYS_PTRACE,
    SYS_RAWIO,
    SYS_RESOURCE,
    SYS_TIME,
    SYS_TTY_CONFIG,
    WAKE_ALARM;
}
