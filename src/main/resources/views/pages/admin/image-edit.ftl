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

<@base.base title='Edit ${image.name} | Admin' context="admin_image_edit">

    <#if image?has_content>

        <input type="hidden" id="ImageKey" value="${image.key}" />

        <@section.section id="ManageImage">
            <@container.container>

                <div class="columns is-multiline">

                    <div class="column is-12">
                        <@title.title icon="cube" thinValue=image.repositoryName boldValue=image.name separator="/" subtitle="Update metadata and tracked branches" />
                    </div>

                    <#--
                    Tag branches
                    -->
                    <div class="column is-half-desktop is-full-mobile has-margin-top">

                        <h2 class="title is-4">Tracked Tag Branches</h2>
                        <@table.table id="ImageTrackedBranches" isScrollable=true isFullWidth=true>
                            <thead>
                            <tr>
                                <th>Branch Name</th>
                                <th></th>
                            </tr>
                            </thead>
                            <tbody>
                                <#list image.tagBranches as tagBranch>
                                    <tr>
                                        <td class="is-vcentered">${tagBranch.branchName}</td>
                                        <td>
                                            <#if !tagBranch.branchProtected>
                                                <@button.buttons isRightAligned=true>
                                                    <@button.button extraClasses="remove-tag-branch" colour="danger" size="small">
                                                        <i class="fas fa-trash"></i> Remove
                                                    </@button.button>
                                                </@button.buttons>
                                            <#else>
                                                <@button.buttons isRightAligned=true>
                                                    <@button.button colour="light" size="small" isDisabled=true>
                                                        Protected
                                                    </@button.button>
                                                </@button.buttons>
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

                    <#--
                    General base information which is to be added manually (data which can't necessarily be inferred from upstream)
                    -->
                    <div class="column is-12 has-margin-top">
                        <h2 class="title is-4">General</h2>
                    </div>
                    <div class="column is-half-desktop is-full-tablet is-full-mobile">
                        <@input.text id="ImageBase" label="Base Image" />
                    </div>
                    <div class="column is-half-desktop is-full-tablet is-full-mobile">
                        <@input.text id="ImageCategory" label="Category" />
                    </div>
                    <div class="column is-half-desktop is-full-tablet is-full-mobile">
                        <@input.text id="ImageSupportUrl" label="Support Url" />
                    </div>
                    <div class="column is-half-desktop is-full-tablet is-full-mobile">
                        <@input.text id="ImageAppUrl" label="Application Url" />
                    </div>

                    <#--
                    A display logo for the grid listing and main image display page
                    -->
                    <div class="column is-half-desktop is-full-tablet is-full-mobile has-margin-top">

                        <h2 class="title is-4">App Logo</h2>

                        <div class="file has-name is-boxed">
                            <label class="file-label">
                                <input class="file-input" type="file" name="ImageLogo" id="ImageLogo" />
                                <span class="file-cta">
                                    <span class="file-icon">
                                        <i class="fas fa-upload"></i>
                                    </span>
                                    <span class="file-label">
                                        Choose a file...
                                    </span>
                                </span>
                                <span class="file-name">
                                    Screen Shot 2017-07-29 at 15.54.25.png
                                </span>
                            </label>
                        </div>
                    </div>

                    <#--
                    Port/Volume mappings for containers created from this image
                    -->
                    <div class="column is-12 has-margin-top">

                        <h2 class="title is-4">Container Template</h2>
                        <h3 class="title is-5">Recommended Runtime</h3>
                    </div>
                    <div class="column is-12">
                        <@input.text id="ImageTemplateUpstreamUrl" label="Registry Url" />
                    </div>
                    <div class="column is-3-desktop is-12-tablet is-12-mobile">
                        <@input.dropdown label="Restart Policy" id="ImageTemplateRestartPolicy">
                            <option value="no">no</option>
                            <option value="always">always</option>
                            <option value="unless-stopped">unless-stopped</option>
                            <option value="on-failure">on-failure</option>
                        </@input.dropdown>
                    </div>
                    <div class="column is-3-desktop is-12-tablet is-12-mobile">
                        <@input.toggle id="ImageTemplateNetworkHost" label="Host Network" size="large" />
                    </div>
                    <div class="column is-3-desktop is-12-tablet is-12-mobile">
                        <@input.toggle id="ImageTemplatePrivileged"  label="Privileged"   size="large" />
                    </div>
                    <div class="column is-3-desktop is-12-tablet is-12-mobile">
                        <@input.dropdown id="ImageTemplateCapabilities" label="Capabilities" isMultiple=true>
                            <#list containerCapabilities as capability>
                                <option value="${capability}">${capability}</option>
                            </#list>
                        </@input.dropdown>
                    </div>
                    <div class="column is-12 has-margin-top">

                        <h3 class="title is-5">Ports</h3>
                        <@table.table id="ImageTemplatePorts" isScrollable=true isFullWidth=true>
                            <thead>
                                <tr>
                                    <th>Port</th>
                                    <th>Protocol</th>
                                    <th>Description</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                            </tbody>
                        </@table.table>
                        <@button.buttons isRightAligned=true>
                            <@button.button id="AddNewPort" colour="normal-colour" size="small">
                                <i class="fas fa-plus has-text-success"></i> Add
                            </@button.button>
                        </@button.buttons>

                    </div>
                    <div class="column is-12 has-margin-top">

                        <h3 class="title is-5">Volumes</h3>
                        <@table.table id="ImageTemplateVolumes" isScrollable=true isFullWidth=true>
                            <thead>
                            <tr>
                                <th>Volume</th>
                                <th>Read Only?</th>
                                <th>Description</th>
                                <th></th>
                            </tr>
                            </thead>
                            <tbody>
                            </tbody>
                        </@table.table>
                        <@button.buttons isRightAligned=true>
                            <@button.button id="AddNewVolume" colour="normal-colour" size="small">
                                <i class="fas fa-plus has-text-success"></i> Add
                            </@button.button>
                        </@button.buttons>

                    </div>
                    <div class="column is-12 has-margin-top">

                        <h3 class="title is-5">Environment</h3>
                        <@table.table id="ImageTemplateEnv" isScrollable=true isFullWidth=true>
                            <thead>
                            <tr>
                                <th>Environment Variable</th>
                                <th>Description</th>
                                <th></th>
                            </tr>
                            </thead>
                            <tbody>
                            </tbody>
                        </@table.table>
                        <@button.buttons isRightAligned=true>
                            <@button.button id="AddNewEnv" colour="normal-colour" size="small">
                                <i class="fas fa-plus has-text-success"></i> Add
                            </@button.button>
                        </@button.buttons>

                    </div>
                    <div class="column is-12 has-margin-top">

                        <h3 class="title is-5">Devices</h3>
                        <@table.table id="ImageTemplateDevices" isScrollable=true isFullWidth=true>
                            <thead>
                            <tr>
                                <th>Device</th>
                                <th>Description</th>
                                <th></th>
                            </tr>
                            </thead>
                            <tbody>
                            </tbody>
                        </@table.table>
                        <@button.buttons isRightAligned=true>
                            <@button.button id="AddNewDevice" colour="normal-colour" size="small">
                                <i class="fas fa-plus has-text-success"></i> Add
                            </@button.button>
                        </@button.buttons>

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
