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

package io.linuxserver.fleet.v2.types.meta.template;

public abstract class AbstractTemplateItem<T extends Comparable<T>, ITEM extends AbstractTemplateItem<T, ITEM>> implements TemplateItem<T>, Comparable<ITEM> {

    private final T      name;
    private final String description;

    protected AbstractTemplateItem(final T name, final String description) {

        this.name        = name;
        this.description = description;
    }

    @Override
    public final T getName() {
        return name;
    }

    @Override
    public final String getDescription() {
        return description;
    }

    @Override
    public int compareTo(final ITEM o) {
        return o.getName().compareTo(getName());
    }
}
