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

<#import "../../ui/components/dropdown.ftl" as dropdown />
<#import "../../ui/layout/section.ftl"      as section />
<#import "../../ui/layout/container.ftl"    as container />
<#import "../../ui/form/input.ftl"          as input />
<#import "../../ui/elements/button.ftl"     as button />
<#import "../../ui/elements/table.ftl"      as table />

<@base.base title='Images (${(repository.name)!"Unknown Repository"}) | Admin' context="admin_images">

    <#if repository?has_content>

        <@section.section id="ManageImages">
            <@container.container>

                <div class="columns is-multiline">
                    <div class="column is-12">
                        <@title.title boldValue="Images under ${repository.name}" subtitle="Manage all synchronised images" />
                    </div>
                    <div class="column is-12 has-margin-top">

                        <@table.table isFullWidth=true isHoverable=true isScrollable=true extraClasses="table--sortable">
                            <thead>
                            <tr>
                                <th>Name</th>
                                <th><abbr title="Apply a mask against all tags under this repository">Version Mask</abbr></th>
                                <th class="has-text-centered"><abbr title="Should this image be included as part of the main repository sync?">Sync</abbr></th>
                                <th class="has-text-centered"><abbr title="Is this image deemed stable?">Stable</abbr></th>
                                <th class="has-text-centered"><abbr title="Should this image be displayed on the main page?">Hidden</abbr></th>
                                <th class="has-text-centered"><abbr title="Is this image deprecated?">Dep.</abbr></th>
                                <th></th>
                            </tr>
                            </thead>
                            <tbody>
                            <#list repository.images as image>
                                <tr class="image-row" data-repository-key="${repository.key}" data-image-key="${image.key}">
                                    <td class="is-vcentered">
                                        ${image.name}
                                    </td>
                                    <td class="editable-image-version-mask is-vcentered" style="width: 300px; min-width: 300px;">
                                        <@input.switchable id="VersionMask_${repository.key.id}" icon="mask" value=image.versionMask size="small" acceptClass="update-image-trigger" />
                                    </td>
                                    <td class="editable-image-sync-enabled is-vcentered has-text-centered" style="width: 100px; max-width: 100px;">
                                        <@input.toggle id="Enabled_${image.key.id}" size="small" inputClasses="update-image-trigger" isToggled=(repository.syncEnabled && image.syncEnabled) isDisabled=(!repository.syncEnabled) title="${(!repository.syncEnabled)?string('Repository sync has been disabled.', '')}" />
                                    </td>
                                    <td class="editable-image-stable is-vcentered has-text-centered" style="width: 100px; max-width: 100px;">
                                        <@input.toggle id="Stable_${image.key.id}" size="small" inputClasses="update-image-trigger" isToggled=image.stable />
                                    </td>
                                    <td class="editable-image-hidden is-vcentered has-text-centered" style="width: 100px; max-width: 100px;">
                                        <@input.toggle id="Hidden_${image.key.id}" colour="warning" size="small" inputClasses="update-image-trigger" isToggled=image.hidden />
                                    </td>
                                    <td class="editable-image-deprecated is-vcentered has-text-centered" style="width: 100px; max-width: 100px;">
                                        <@input.toggle id="Deprecated_${image.key.id}" colour="danger" size="small" inputClasses="update-image-trigger" isToggled=image.deprecated />
                                    </td>
                                    <td style="min-width: 100px; width: 100px;">
                                        <@button.buttons isGrouped=true isRightAligned=true>
                                            <@button.button id="ForceResync_${image.key.id}" size="small" title="Force resync" colour="normal-colour" extraAttributes='data-image-key="${image.key}"' extraClasses="sync-image">
                                                <i class="fas fa-sync-alt is-marginless"></i>
                                            </@button.button>
                                            <@button.link size="small" title="Edit image metadata" colour="normal-colour" link="/admin/image?imageKey=${image.key}">
                                                <i class="fas fa-pencil-alt is-marginless"></i>
                                            </@button.link>
                                        </@button.buttons>
                                    </td>
                                </tr>
                            </#list>
                            </tbody>
                        </@table.table>

                    </div>
                </div>

            </@container.container>
        </@section.section>

    <#else>

        <@section.section id="ManageImages">
            <@container.container>
                Unable to find repository.
            </@container.container>
        </@section.section>

    </#if>

</@base.base>
