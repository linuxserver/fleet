<#--
 Copyright (c) 2019 LinuxServer.io
 
 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.
-->
<#import "../../prebuilt/base.ftl"        as base />
<#import "../../prebuilt/fleet-title.ftl" as title />

<#import "../../ui/layout/section.ftl"     as section />
<#import "../../ui/layout/container.ftl"   as container />
<#import "../../ui/elements/button.ftl"    as button />
<#import "../../ui/elements/table.ftl"     as table />

<@base.base title="Schedules | Admin" context="admin_schedules">

    <@section.section id="LoadedSchedules">
        <@container.container>

            <div class="columns is-multiline">
                <div class="column is-12">
                    <@title.title boldValue="Schedules" icon="clock" subtitle="All loaded scheduled tasks which run periodically" />
                </div>

                <div class="column is-12 has-margin-top">

                    <@table.table isFullWidth=true isHoverable=true isScrollable=true>
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th>Last Run</th>
                                <th>Next Run (Est.)</th>
                                <th>Interval</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <#list schedules as schedule>
                                <tr data-schedule-key="${schedule.key}">
                                    <td class="is-vcentered">${schedule.name}</td>
                                    <td class="is-vcentered">${formatDate(schedule.lastRunTime, 'dd MMM yyyy HH:mm:ss')}</td>
                                    <td class="is-vcentered">${formatDate(schedule.nextRunTime, 'dd MMM yyyy HH:mm:ss')}</td>
                                    <td class="is-vcentered">${schedule.interval.timeDuration} ${schedule.interval.timeUnitAsTimeUnit?lower_case}</td>
                                    <td>
                                        <@button.buttons isGrouped=true isRightAligned=true>
                                            <@button.button extraClasses="force-schedule-run" colour="normal-colour" size="small" title="Run this schedule now" extraAttributes='data-schedule-key="${schedule.key}"'>
                                                <i class="fas fa-play is-marginless"></i>
                                            </@button.button>
                                        </@button.buttons>
                                    </td>
                                </tr>
                            </#list>
                        </tbody>
                    </@table.table>
                </div>

            </div>

            <div class="columns is-multiline has-margin-top">

                <div class="column is-12">
                    <h3 class="title is-5">
                        Queued Items
                    </h3>
                    <h4 class="subtitle is-6">
                        The synchronisation queue contains individual sync requests for images.
                    </h4>
                    There ${(queueSize==1)?string('is', 'are')} currently #{queueSize} ${(queueSize==1)?string('item', 'items')} queued.
                </div>

                <div class="column is-12">
                    <h3 class="title is-5">
                        Request Consumer
                    </h3>
                    <h4 class="subtitle is-6">
                        Asynchronous thread which subscribes to the request queue.
                    </h4>
                    The request consumer is currently ${consumerRunning?string('<span class="has-text-success has-fontweight-bold">running</span>', '<span class="has-text-danger has-fontweight-bold">not running</span>')}.
                </div>
            </div>

        </@container.container>
    </@section.section>

</@base.base>
