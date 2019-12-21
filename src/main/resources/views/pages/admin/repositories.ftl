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

<#import "../../ui/components/message.ftl" as message />
<#import "../../ui/layout/section.ftl"     as section />
<#import "../../ui/layout/container.ftl"   as container />
<#import "../../ui/form/input.ftl"         as input />
<#import "../../ui/elements/button.ftl"    as button />
<#import "../../ui/elements/table.ftl"     as table />

<@base.base title="Repositories | Admin" context="admin_repositories">

    <@section.section id="ManageRepositories">
        <@container.container>

            <div class="columns is-multiline">
                <div class="column is-12">
                    <@title.title boldValue="Repositories" icon="cubes" subtitle="Manage the sync status of all added repositories" />
                </div>
                <div class="column is-12 has-margin-top">

                    <@table.table isFullWidth=true isHoverable=true isScrollable=true>
                        <thead>
                            <tr>
                                <th class="has-text-centered" style="width: 100px; max-width: 100px;"><abbr title="Is this repository synchronised?">Enabled</abbr></th>
                                <th>Name</th>
                                <th style="width: 300px; min-width: 300px;"><abbr title="Apply a mask against all tags under this repository">Version Mask</abbr></th>
                                <th style="width: 100px; min-width: 100px;"></th>
                            </tr>
                        </thead>
                        <tbody>
                            <#list repositories as repository>
                                <tr class="repository-row" data-repository-key="${repository.key}">
                                    <td class="editable-repository-enabled is-vcentered has-text-centered">
                                        <@input.toggle id="Enabled_${repository.key.id}" size="large" isToggled=repository.syncEnabled />
                                    </td>
                                    <td class="is-vcentered">
                                        <p>
                                            <strong>${repository.name}</strong>
                                        </p>
                                        <a href="/admin/images?repositoryKey=${repository.key}">${repository.images?size} images</a>
                                    </td>
                                    <td class="editable-repository-version-mask is-vcentered">
                                        <@input.switchable id="VersionMask_${repository.key.id}" icon="mask" value=repository.versonMask size="small" acceptClass="version-mask-switch" />
                                    </td>
                                    <td class="is-vcentered has-text-right">
                                        <@button.button size="small" colour="danger" extraAttributes='data-repository-key="${repository.key}"'>Delete</@button.button>
                                    </td>
                                </tr>
                            </#list>
                            <tr>
                                <td colspan="3" class="is-hovered">
                                    <@input.text id="NewRepositoryName" icon="cubes" placeholder="New Repository..." />
                                </td>
                                <td class="has-text-right is-hovered is-vcentered">
                                    <@button.button id="SubmitNewRepository" size="small" colour="success">
                                        <i class="fas fa-plus"></i> Add
                                    </@button.button>
                                </td>
                            </tr>
                        </tbody>
                    </@table.table>

                </div>
            </div>

        </@container.container>
    </@section.section>

</@base.base>
