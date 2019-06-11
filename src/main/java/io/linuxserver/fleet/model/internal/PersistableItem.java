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

package io.linuxserver.fleet.model.internal;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;

public abstract class PersistableItem<T extends PersistableItem> {

    private Integer       id;
    private LocalDateTime modifiedTime;

    PersistableItem() { }

    PersistableItem(Integer id) {
        this.id = id;
    }

    @SuppressWarnings("unchecked")
    public T withModifiedTime(LocalDateTime modifiedTime) {

        this.modifiedTime = modifiedTime;
        return (T) this;
    }

    public Integer getId() {
        return id;
    }

    public LocalDateTime getModifiedTime() {
        return modifiedTime;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
