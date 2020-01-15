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

<@base.base title="${(image.fullName)!'Unknown Image'}" context="image" hasHero=false>

    <#if image?has_content>

        <section class="hero">
            <@section.section extraClasses="is-paddingless-bottom">
                <@container.container extraClasses="has-margin-bottom">

                    <div class="columns is-multiline">

                        <div class="column is-full">

                            <@title.title icon="cube" thinValue=image.repositoryName boldValue=image.name separator="/" subtitle=image.description>
                                <#if image.deprecated>
                                    <@tag.tag colour="warning" value="Deprecated" />
                                </#if>
                            </@title.title>

                            <div class="tags">

                                <@tag.tag colour="light" value='<i class="fas fa-download"></i> ${image.pullCount}' extraAttributes='title="Pulls"' />
                                <@tag.tag colour="light" value='<i class="fas fa-star"></i> ${image.starCount}' extraAttributes='title="Stars"' />

                                <#assign latestBranch=image.findTagBranchByName("latest") />
                                <#if latestBranch?has_content>
                                    <#list latestBranch.latestTag.digests as digest>
                                        <@tag.tag colour="light" value='<i class="fas fa-microchip"></i> ${digest.architecture}' extraAttributes='title="Architecture"' />
                                    </#list>
                                </#if>

                            </div>

                        </div>
                    </div>

                </@container.container>
            </@section.section>
        </section>

        <@section.section>
            <@container.container>

                <div class="columns is-multiline">

                    <div class="column is-full has-margin-bottom">

                        <h2 class="title is-5">Build Information</h2>
                        <h3 class="subtitle is-6">General build information for this image</h3>

                        <@table.table isFullWidth=true isNarrow=false isStriped=true isScrollable=true>
                            <thead>
                                <tr>
                                    <th scope="row" colspan="2"></th>
                                </tr>
                            </thead>
                            <tbody>
                                <@table.halfDisplayRow title="Repository"   value=image.repositoryName link="/?key=${image.repositoryKey}" />
                                <@table.halfDisplayRow title="Build Time"   value=image.lastUpdatedAsString />
                                <@table.halfDisplayRow title="Synchronised" value=image.syncEnabled?string("Yes", "No") />
                                <@table.halfDisplayRow title="Stable"       value=image.stable?string("Yes", "No") />
                                <@table.halfDisplayRow title="Deprecated"   value=image.deprecated?string("Yes", "No") />
                            </tbody>
                        </@table.table>
                    </div>

                    <div class="column is-full has-margin-bottom">

                        <h2 class="title is-5">Tracked Tags</h2>
                        <h3 class="subtitle is-6">Known tags which link to a specific branched app version.</h3>

                        <@table.table isFullWidth=true isNarrow=false isStriped=true isScrollable=true>
                            <thead>
                                <tr>
                                    <th scope="row" colspan="2"></th>
                                </tr>
                            </thead>
                            <tbody>
                                <#list image.tagBranches as tagBranch>
                                    <@table.halfDisplayRow title=tagBranch.branchName?html value='<i class="fas fa-tag"></i> ${image.getMaskedVersion(tagBranch.latestTag)}' />
                                </#list>
                            </tbody>
                        </@table.table>
                    </div>

                    <div class="column is-full">
                        <div class="columns has-margin-top">
                            <div class="column is-full is-full-mobile">
                                <h2 class="title is-5">Daily Pull Statistics</h2>
                                <div class="chart-container" style="position: relative; width: 100%; height: 250px">
                                    <canvas id="ImagePullHistory"></canvas>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>

            </@container.container>
        </@section.section>

    <#else>

        <@section.section>
            <@container.container>
                Could not find image.
            </@container.container>
        </@section.section>

    </#if>

</@base.base>
