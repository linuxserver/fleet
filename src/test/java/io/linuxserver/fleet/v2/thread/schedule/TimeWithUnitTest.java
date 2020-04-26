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

package io.linuxserver.fleet.v2.thread.schedule;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class TimeWithUnitTest {

    @Test
    public void shouldConvertToLowestUnit() {

        final TimeWithUnit minutes = new TimeWithUnit(30, TimeUnit.MINUTES);
        final TimeWithUnit hours   = new TimeWithUnit(3, TimeUnit.HOURS);

        assertThat(minutes.convertToLowestUnit(hours).getTimeUnit(), is(equalTo(TimeUnit.MINUTES)));
        assertThat(minutes.convertToLowestUnit(hours).getTimeDuration(), is(equalTo(30L)));

        assertThat(hours.convertToLowestUnit(minutes).getTimeUnit(), is(equalTo(TimeUnit.MINUTES)));
        assertThat(hours.convertToLowestUnit(minutes).getTimeDuration(), is(equalTo(180L)));
    }
}
