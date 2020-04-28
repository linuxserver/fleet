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
                                        <@table.halfDisplayRow title="Docker Hub"   value='<i class="fab fa-docker"></i> ${image.fullName}' link="https://hub.docker.com/r/${image.fullName}" />
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

                            <#if image.metaData.populated || image.metaData.externalUrls?has_content>
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

                                            <#-- Any set external Urls -->
                                            <#list image.metaData.externalUrls as url>

                                                <@table.halfDisplayRow
                                                    title='<i class="fas fa-${url.type.icon}" title="${url.type}"></i> ${url.name}'
                                                    value=url.absoluteUrl link=url.absoluteUrl />

                                            </#list>

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
                                            <th class="has-text-small">Branch</th>
                                            <th class="has-text-small">Version</th>
                                            <th class="has-text-small">Built</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <#list image.tagBranches as tagBranch>
                                            <tr>
                                                <th scope="row">
                                                    ${tagBranch.branchName?html}
                                                </th>
                                                <td>
                                                    <i class="fas fa-tag"></i> ${image.getMaskedVersion(tagBranch.latestTag)}
                                                </td>
                                                <td>
                                                    ${formatDate(tagBranch.latestTag.buildDate, "dd MMM yyyy 'at' HH:mm:ss")}
                                                </td>
                                            </tr>
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

                                <#--
                                This is not ready yet
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
                                -->

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
