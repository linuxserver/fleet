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

<#import "../prebuilt/base.ftl"           as base />
<#import "../prebuilt/fleet-title.ftl"    as title />
<#import "../prebuilt/docker-example.ftl" as dockerExample />

<#import "../ui/components/message.ftl" as message />
<#import "../ui/elements/box.ftl"       as box />
<#import "../ui/elements/button.ftl"    as button />
<#import "../ui/elements/table.ftl"     as table />
<#import "../ui/elements/tag.ftl"       as tag />
<#import "../ui/layout/container.ftl"   as container />
<#import "../ui/layout/section.ftl"     as section />

<@base.base title="${(image.fullName)!'Unknown Image'}" context="image" hasHero=false>

    <#if image?has_content>

        <section class="hero">
            <@section.section extraClasses="is-paddingless-bottom">
                <@container.container extraClasses="has-margin-bottom">

                    <div class="columns is-multiline">

                        <div class="column is-full">

                            <@title.title
                                icon="cube"
                                imageIcon=image.metaData.appImagePath
                                thinValue=image.repositoryName
                                boldValue=image.name separator="/"
                                subtitle=image.description />

                            <div class="tags">

                                <@tag.tag colour="light" value='<i class="fas fa-download"></i> ${image.pullCount}' extraAttributes='title="Pulls"' />
                                <@tag.tag colour="light" value='<i class="fas fa-star"></i> ${image.starCount}' extraAttributes='title="Stars"' />

                                <#assign latestBranch=image.findTagBranchByName("latest") />
                                <#if latestBranch?has_content>
                                    <#list latestBranch.latestTag.digests as digest>
                                        <@tag.tag colour="light" value='<i class="fas fa-microchip"></i> ${digest.architecture}' extraAttributes='title="Architecture"' />
                                    </#list>
                                </#if>

                                <#if image.deprecated>
                                    <@tag.tag colour="warning" value="Deprecated" />
                                </#if>
                                <#if !image.stable>
                                    <@tag.tag colour="danger" value="Unstable" />
                                </#if>

                            </div>

                        </div>

                    </div>

                </@container.container>
            </@section.section>
        </section>

        <@section.section>
            <@container.container>

                <div class="columns">
                    <div class="column is-full">
                        <div class="tabs" data-tabs-for="#ImageViewTabContent">
                            <ul>
                                <li data-tab-for="#GeneralInfo" class="is-active">
                                    <a><i class="fas fa-info"></i> General</a>
                                </li>
                                <li data-tab-for="#PullStatsInfo">
                                    <a><i class="fas fa-chart-line"></i> Statistics</a>
                                </li>
                                <li data-tab-for="#TemplateInfo">
                                    <a><i class="fas fa-layer-group"></i> Container Info</a>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>

                <div id="ImageViewTabContent" class="columns has-tabs-content is-multiline has-margin-top">

                    <div id="GeneralInfo" class="column tab-content is-full is-active">

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
                                        <#if image.metaData.baseImage?has_content>
                                            <@table.halfDisplayRow title="Base Image" value=image.metaData.baseImage?html />
                                        </#if>
                                        <@table.halfDisplayRow title="Synchronised" value=image.syncEnabled?string("Yes", "No") />
                                        <@table.halfDisplayRow title="Stable"       value=image.stable?string("Yes", "No") />
                                        <@table.halfDisplayRow title="Deprecated"   value=image.deprecated?string("Yes", "No") />
                                    </tbody>
                                </@table.table>

                            </div>

                            <#if image.metaData.populated>
                                <div class="column is-full has-margin-bottom">

                                    <h2 class="title is-5">Support Information</h2>
                                    <h3 class="subtitle is-6">External links and support</h3>

                                    <@table.table isFullWidth=true isNarrow=false isStriped=true isScrollable=true>
                                        <thead>
                                        <tr>
                                            <th scope="row" colspan="2"></th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                            <#if image.metaData.category?has_content>
                                                <@table.halfDisplayRow title="Category" value=image.metaData.category />
                                            </#if>
                                            <#if image.metaData.appUrl?has_content>
                                                <@table.halfDisplayRow title="Application Home" value=image.metaData.appUrl?html link=image.metaData.appUrl />
                                            </#if>
                                            <#if image.metaData.supportUrl?has_content>
                                                <@table.halfDisplayRow title="Support" value=image.metaData.supportUrl?html link=image.metaData.supportUrl />
                                            </#if>
                                        </tbody>
                                    </@table.table>

                                </div>
                            </#if>

                            <div class="column is-full">

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

                        </div>

                    </div>

                    <div id="PullStatsInfo" class="column tab-content is-full">
                        <div class="columns">
                            <div class="column is-full is-full-mobile">
                                <h2 class="title is-5">Daily Pull Statistics</h2>
                                <div class="chart-container" style="position: relative; width: 100%; height: 250px">
                                    <canvas id="ImagePullHistory"></canvas>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div id="TemplateInfo" class="column tab-content is-full">
                        <div id="TemplateInfo" class="columns is-multiline">

                            <div class="column is-full">
                                <h2 class="title is-5">Running this as a container</h2>
                                <h3 class="subtitle is-6">
                                    Basic examples for getting this image running as a container
                                </h3>

                                <@message.message colour="info">

                                    These examples <strong>do not</strong> include the relevant values for volume mappings or environment variables. You will
                                    need to review these snippets and fill in the gaps based on your own needs. If you would like to generate a compose
                                    block or CLI run command with your mappings included, you can also use the template generator:

                                    <div class="has-text-centered has-margin-top">
                                        <@button.link id="TemplateGeneratorLink" colour="info">
                                            <i class="fas fa-layer-group"></i> Template Generator
                                        </@button.link>
                                    </div>

                                </@message.message>
                            </div>

                            <div class="column is-full has-margin-bottom">
                                <h2 class="title is-6">Docker Compose</h2>
                                <@dockerExample.compose fullName=image.fullName containerName=image.name templates=image.metaData.templates latest=image.latestTag.version />
                            </div>

                            <div class="column is-full has-margin-bottom">
                                <h2 class="title is-6">CLI</h2>
                                <@dockerExample.cli fullName=image.fullName containerName=image.name templates=image.metaData.templates latest=image.latestTag.version />
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
