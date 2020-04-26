/*
 * Copyright (c)  2019 LinuxServer.io
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

package io.linuxserver.fleet.v2.web.freemarker;

import freemarker.ext.beans.StringModel;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModelException;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;

public class Java8DateTimeMethod implements CustomFreemarkerTemplate {

    @Override
    public String getName() {
        return "formatDate";
    }

    @Override
    public Object exec(List arguments) throws TemplateModelException {

        if (arguments.size() != 2) {
            throw new TemplateModelException("Wrong arguments");
        }

        final TemporalAccessor  time      = (TemporalAccessor) ((StringModel) arguments.get(0)).getWrappedObject();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(((SimpleScalar) arguments.get(1)).getAsString());

        return formatter.format(time);
    }
}
