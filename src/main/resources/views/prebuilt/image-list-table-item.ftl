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

<#import "../ui/elements/tag.ftl" as tag />

<#macro imageListItem image>

    <#if !image.hidden>

        <tr data-image-name="${image.name}">
            <td class="is-vcentered">
                <h4 class="title is-6">
                    <a class="has-text-grey-dark" href="/image?name=${image.fullName}">
                        <span class="has-text-weight-light">${image.repositoryKey.name}/</span><span class="has-text-weight-500">${image.name}</span>
                    </a>
                </h4>
            </td>
            <td>
                <#if image.deprecated>
                    <@tag.tag colour="warning" value='<i class="fas fa-exclamation-circle"></i> Deprecated' />
                </#if>
                <#if !image.stable>
                    <@tag.tag colour="danger" value='<i class="fas fa-exclamation-triangle"></i> Unstable!' />
                </#if>
            </td>
            <td class="is-vcentered">
                <@tag.tag colour="light" value='<i class="fas fa-tag"></i> ${image.latestTag.version}' extraAttributes='title="Latest Version"' />
            </td>
            <td class="is-vcentered">
                ${image.lastUpdatedAsString!""}
            </td>
            <td class="is-vcentered">
                ${image.pullCount}
            </td>
            <td class="is-vcentered">
                ${image.starCount}
            </td>
        </tr>
    </#if>

</#macro>