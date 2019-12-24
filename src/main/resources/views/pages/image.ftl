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

<#import "../prebuilt/base.ftl"        as base />
<#import "../prebuilt/fleet-title.ftl" as title />
<#import "../ui/layout/section.ftl"    as section />
<#import "../ui/layout/container.ftl"  as container />
<#import "../ui/elements/box.ftl"      as box />
<#import "../ui/elements/table.ftl"    as table />
<#import "../ui/elements/tag.ftl"      as tag />

<@base.base title="${image.fullName}" context="image">

    <@section.section>
        <@container.container>

            <div class="columns is-multiline">

                <div class="column is-12">

                    <@title.title icon="cube" thinValue=image.repositoryName boldValue=image.name separator="/">
                        <#if image.deprecated>
                            <@tag.tag colour="warning" value="Deprecated" />
                        </#if>
                    </@title.title>

                    <div class="tags is-right">

                        <#assign latestBranch=image.findTagBranchByName("latest") />
                        <#if latestBranch?has_content>
                            <#list latestBranch.latestTag.digests as digest>
                                <@tag.tag colour="light" value='<i class="fas fa-microchip"></i> ${digest.architecture}' extraAttributes='title="Architecture"' />
                            </#list>
                        </#if>

                        <@tag.tag colour="light" value='<i class="fas fa-download"></i> ${image.pullCount}' extraAttributes='title="Pulls"' />
                        <@tag.tag colour="light" value='<i class="fas fa-star"></i> ${image.starCount}' extraAttributes='title="Stars"' />

                    </div>

                </div>

                <div class="column is-6-desktop is-12-tablet">
                    <@box.box extraClasses="is-paddingless is-clipped">

                        <h2 class="title is-5 has-text-centered has-margin-top">Build Information</h2>
                        <@table.table isFullWidth=true isNarrow=true isStriped=true>
                            <tbody>
                                <@table.halfDisplayRow title="Repository"   value=image.repositoryName link="/?key=${image.repositoryKey}" />
                                <@table.halfDisplayRow title="Build Time"   value=image.lastUpdatedAsString />
                                <@table.halfDisplayRow title="Synchronised" value=image.syncEnabled?string("Yes", "No") />
                                <@table.halfDisplayRow title="Stable"       value=image.stable?string("Yes", "No") />
                                <@table.halfDisplayRow title="Deprecated"   value=image.deprecated?string("Yes", "No") />
                            </tbody>
                        </@table.table>

                        <h2 class="title is-5 has-text-centered has-margin-top">Tracked Tags</h2>
                        <@table.table isFullWidth=true isNarrow=true isStriped=true>
                            <tbody>
                                <#list image.tagBranches as tagBranch>
                                    <@table.halfDisplayRow title=tagBranch.branchName value='<i class="fas fa-tag"></i> ${image.getMaskedVersion(tagBranch.latestTag)}' />
                                </#list>
                            </tbody>
                        </@table.table>

                    </@box.box>
                </div>

            </div>

        </@container.container>
    </@section.section>

</@base.base>
