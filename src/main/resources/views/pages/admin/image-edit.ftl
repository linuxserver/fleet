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
<#import "../../ui/elements/tag.ftl"        as tag />
<#import "../../ui/components/message.ftl"  as message />

<#import "template-components/image-template-ports.ftl"       as templatePorts />
<#import "template-components/image-template-volumes.ftl"     as templateVolumes />
<#import "template-components/image-template-environment.ftl" as templateEnvironment />
<#import "template-components/image-template-devices.ftl"     as templateDevices />
<#import "template-components/image-template-misc.ftl"        as templateMisc />

<@base.base title='Edit ${image.name} | Admin' context="admin_image_edit">

    <#if image?has_content>

        <input type="hidden" id="ImageKey" value="${image.key}" />

        <@section.section id="ManageImage">
            <@container.container>

                <nav class="breadcrumb" aria-label="breadcrumbs">
                    <ul>
                        <li>
                            <a href="/admin/images?repositoryKey=${image.repositoryKey}">
                                <i class="fas fa-arrow-left"></i> Back to ${image.repositoryKey.name}
                            </a>
                        </li>
                    </ul>
                </nav>

                <div class="columns is-multiline">

                    <div class="column is-full">
                        <@title.title icon="cube" thinValue=image.repositoryName boldValue=image.name separator="/" subtitle="Update metadata and tracked branches" />
                    </div>

                    <#--
                    General base information which is to be added manually (data which can't necessarily be inferred from upstream)
                    -->
                    <div class="column is-full has-margin-top">
                        <form class="needs-validation" novalidate action="/admin/image" method="post" enctype="multipart/form-data">
                            <div class="columns is-multiline">
                                <div class="column is-full">
                                    <h2 class="title is-4">General</h2>
                                </div>
                                <div class="column is-full">
                                    <div class="field is-horizontal">
                                        <div class="field-label">
                                            <label class="label" for="ImageAppLogo">App Logo</label>
                                        </div>
                                        <div class="field-body">

                                            <#if image.metaData.appImagePath?has_content>
                                                <div class="is-fullwidth">
                                                    <figure class="image is-128x128">
                                                        <img src="${image.metaData.appImagePath}" alt="${image.name} logo" />
                                                    </figure>
                                                </div>
                                            </#if>

                                            <input type="file" name="ImageAppLogo" id="ImageAppLogo" />
                                        </div>
                                    </div>
                                </div>
                                <div class="column is-full">

                                    <@input.text id="ImageBase" label="Base Image" isInline=true value=image.metaData.baseImage
                                        infoText="The name of the base image this image pulls from." />

                                </div>
                                <div class="column is-full">

                                    <@input.text id="ImageCategory" label="Category" isInline=true value=image.metaData.category
                                        infoText="The application category for this image (e.g Home Automation)." />

                                </div>
                            </div>
                            <div class="columns">
                                <div class="column is-full">

                                    <input type="hidden" name="imageKey" value="${image.key}" />
                                    <input type="hidden" name="updateType" value="GENERAL" />

                                    <@button.buttons isRightAligned=true>
                                        <@button.submit colour="success" extraClasses="is-fullwidth-mobile">
                                            <i class="fas fa-save"></i> Save General Changes
                                        </@button.submit>
                                    </@button.buttons>
                                </div>
                            </div>
                        </form>
                    </div>

                    <div class="column is-full has-margin-top">
                        <form class="needs-validation" novalidate action="/admin/image" method="post">

                            <div class="columns is-multiline">
                                <div class="column is-full">
                                    <h2 class="title is-4">External Urls</h2>
                                </div>
                                <div class="column is-full">
                                    <@table.table id="ImageExternalUrls">
                                        <thead>
                                            <tr>
                                                <th>Type</th>
                                                <th>Name</th>
                                                <th>Absolute Path</th>
                                                <th></th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                             <#list image.metaData.externalUrls as url>
                                                <tr>
                                                    <td>
                                                        <div class="select is-small">
                                                            <select title="Url Type" name="imageExternalUrlType" required>
                                                                <#list imageUrlTypes as imageUrlType>
                                                                    <option<#if url.type==imageUrlType> selected</#if> title="${imageUrlType.description}" value="${imageUrlType}">${imageUrlType}</option>
                                                                </#list>
                                                            </select>
                                                        </div>
                                                    </td>
                                                    <td>
                                                        <input type="hidden" value="${url.key.id}" name="imageExternalUrlKey" />
                                                        <input title="Descriptive name of URL" type="text" class="input is-small" name="imageExternalUrlName" value="${url.name?html}" required />
                                                    </td>
                                                    <td>
                                                        <input title="Full path of URL" type="text" class="input is-small" name="imageExternalUrlPath" value="${url.absoluteUrl?html}" required />
                                                    </td>
                                                    <td>
                                                        <@button.buttons isRightAligned=true>
                                                            <@button.button colour="danger" size="small" extraClasses="remove-image-external-url">
                                                                <i class="fas fa-trash is-marginless"></i>
                                                            </@button.button>
                                                        </@button.buttons>
                                                    </td>
                                                </tr>
                                             </#list>
                                        </tbody>
                                    </@table.table>

                                    <@button.buttons isRightAligned=true>
                                        <@button.button id="AddNewExternalUrl" colour="normal-colour" size="small">
                                            <i class="fas fa-plus has-text-success"></i> Add Url
                                        </@button.button>
                                    </@button.buttons>
                                </div>
                            </div>

                            <div class="columns">
                                <div class="column is-full">

                                    <input type="hidden" name="imageKey" value="${image.key}" />
                                    <input type="hidden" name="updateType" value="EXTERNAL_URLS" />

                                    <@button.buttons isRightAligned=true>
                                        <@button.submit colour="success" extraClasses="is-fullwidth-mobile">
                                            <i class="fas fa-save"></i> Update External Urls
                                        </@button.submit>
                                    </@button.buttons>
                                </div>
                            </div>
                        </form>
                    </div>

                    <#--
                    Tag branches
                    -->
                    <div class="column is-full has-margin-top">
                        <h2 class="title is-4">Tracked Tag Branches</h2>
                        <@message.message colour="info">
                            Recorded branches here will be used to look up latest linked versions. The <strong>latest</strong> tag is
                            default and cannot be deleted.
                        </@message.message>
                    </div>

                    <div class="column is-half-desktop is-full-tablet is-full-mobile has-margin-top">
                        <@table.table id="ImageTrackedBranches" isScrollable=true isFullWidth=true>
                            <thead>
                            <tr>
                                <th>Branch Name</th>
                                <th style="width: 100px;"></th>
                            </tr>
                            </thead>
                            <tbody>
                            <#list image.tagBranches as tagBranch>
                                <tr class="tracked-branch" data-branch-name="${tagBranch.branchName?html}">
                                    <td class="is-vcentered">
                                        ${tagBranch.branchName?html}
                                    </td>
                                    <td>
                                        <#if !tagBranch.branchProtected>
                                            <@button.buttons isRightAligned=true>
                                                <@button.button extraClasses="remove-tag-branch" colour="white" size="small" title="Stop tracking this branch.">
                                                    <i class="fas fa-trash has-text-danger is-marginless"></i>
                                                </@button.button>
                                            </@button.buttons>
                                        <#else>
                                            <div class="tags is-right">
                                                <@tag.tag colour="light" value="Protected" extraAttributes='title="This branch can\'t be removed."' />
                                            </div>
                                        </#if>
                                    </td>
                                </tr>
                            </#list>
                            <tr>
                                <td>
                                    <@input.text id="NewTrackedBranch" icon="sitemap" size="small" />
                                </td>
                                <td>
                                    <@button.buttons isRightAligned=true>
                                        <@button.button id="TrackNewBranch" size="small" colour="success">
                                            <i class="fas fa-plus"></i> Track
                                        </@button.button>
                                    </@button.buttons>
                                </td>
                            </tr>
                            </tbody>
                        </@table.table>

                    </div>
                </div>

                <form class="needs-validation" novalidate action="/admin/image" method="post">
                    <div class="columns is-multiline has-margin-top">

                        <#--
                        Port/Volume mappings for containers created from this image
                        -->
                        <div class="column is-full has-margin-top">
                            <h2 class="title is-4">Container Template</h2>
                            <@message.message colour="info">
                                This information is for display purposes and has no impact on this image's synchronisation processes. The
                                data stored here will be used to generate various templates for downstream systems to consume, as well as
                                for the display page to provide run commands and Compose snippets
                            </@message.message>
                        </div>

                        <div class="column is-full">
                            <div class="tabs" data-tabs-for="#ImageTemplateTabContent">
                                <ul>
                                    <li data-tab-for="#ImageTemplatePorts" class="is-active">
                                        <a><i class="fas fa-ethernet"></i> Ports</a>
                                    </li>
                                    <li data-tab-for="#ImageTemplateVolumes">
                                        <a><i class="fas fa-folder"></i>Volumes</a>
                                    </li>
                                    <li data-tab-for="#ImageTemplateEnv">
                                        <a><i class="fas fa-code"></i> Environment</a>
                                    </li>
                                    <li data-tab-for="#ImageTemplateDevices">
                                        <a><i class="fas fa-microchip"></i> Devices</a>
                                    </li>
                                    <li data-tab-for="#ImageTemplateMisc">
                                        <a><i class="fas fa-tag"></i> Misc</a>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>

                    <div id="ImageTemplateTabContent" class="columns has-tabs-content">
                        <div id="ImageTemplatePorts" class="column tab-content is-active is-full has-margin-top">
                            <@templatePorts.ports ports=image.metaData.templates.ports />
                        </div>
                        <div id="ImageTemplateVolumes" class="column tab-content is-full has-margin-top">
                            <@templateVolumes.volumes volumes=image.metaData.templates.volumes />
                        </div>
                        <div id="ImageTemplateEnv" class="column tab-content is-full has-margin-top">
                            <@templateEnvironment.environment environment=image.metaData.templates.env />
                        </div>
                        <div id="ImageTemplateDevices" class="column tab-content is-full has-margin-top">
                            <@templateDevices.devices devices=image.metaData.templates.devices />
                        </div>
                        <div id="ImageTemplateMisc" class="column tab-content is-full has-margin-top">
                            <@templateMisc.misc templateHolder=image.metaData.templates />
                        </div>
                    </div>

                    <div class="columns">
                        <div class="column is-full">

                            <input type="hidden" name="imageKey" value="${image.key}" />
                            <input type="hidden" name="updateType" value="TEMPLATE" />

                            <@button.buttons isRightAligned=true>
                                <@button.submit id="SaveTemplateChanges" colour="success" extraClasses="is-fullwidth-mobile">
                                    <i class="fas fa-save"></i> Save Template Changes
                                </@button.submit>
                            </@button.buttons>

                        </div>
                    </div>
                </form>

            </@container.container>
        </@section.section>

    <#else>

        <@section.section id="ManageImages">
            <@container.container>
                Unable to find repository.
            </@container.container>
        </@section.section>

    </#if>

    <script type="text/javascript">
        var externalUrlTypes = [<#list imageUrlTypes as imageUrlType><#if imageUrlType?index &gt; 0>,</#if>'${imageUrlType}'</#list>];
    </script>

</@base.base>
