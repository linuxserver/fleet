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

<#import "../ui/form/input.ftl"            as input />
<#import "../ui/components/message.ftl"    as message />
<#import "../ui/elements/box.ftl"          as box />
<#import "../prebuilt/base.ftl"            as base />
<#import "../ui/elements/table.ftl"        as table />
<#import "../ui/elements/button.ftl"       as button />

<#import "../prebuilt/image-list-table-item.ftl" as imageListTableItem />
<#import "../prebuilt/image-list-item.ftl"       as imageListBoxItem />

<@base.base title="Images" context="home" showTitle=false>

    <section class="section is-paddingless-top">
        <div class="container">

            <#if selectedRepository?has_content>

                <div class="columns is-multiline">

                    <div class="column is-12 has-margin-top">
                        <h2 class="title is-3 repository-title">
                            <i class="fas fa-cubes"></i> ${selectedRepository.name}<span class="has-text-primary">.</span>
                        </h2>
                    </div>

                    <div class="column is-12 has-margin-top">
                        <@input.text id="SearchImages" icon="search" placeholder="Search..." />
                    </div>

                </div>
                <div class="columns has-margin-top">

                    <div class="column is-12">
                        <@button.buttons isGrouped=true isRightAligned=true>
                            <@button.button id="DisplayImageTable" colour="normal-colour" title="Display as table" extraClasses="is-active">
                                <i class="fas fa-table is-marginless"></i>
                            </@button.button>
                            <@button.button id="DisplayImageGrid" colour="normal-colour" title="Display as grid">
                                <i class="fas fa-th-large is-marginless"></i>
                            </@button.button>
                        </@button.buttons>
                    </div>
                </div>

                <#if selectedRepository.images?has_content>

                    <div id="ImageTableHolder" class="columns is-multiline">

                        <div class="column is-12">
                            <@table.table id="ImageTable" isFullWidth=true isScrollable=true extraClasses="table--sortable">
                                <thead>
                                <tr>
                                    <th>Name</th>
                                    <th></th>
                                    <th>Latest Version</th>
                                    <th class="sorter-pullCount">Pulls</th>
                                    <th>Stars</th>
                                    <th>Build Time</th>
                                </tr>
                                </thead>
                                <tbody>
                                <#list selectedRepository.images as image>
                                    <@imageListTableItem.imageListItem image=image />
                                </#list>
                                </tbody>
                            </@table.table>
                        </div>

                    </div>

                    <div id="ImageGridHolder" class="columns is-multiline is-hidden">
                        <#list selectedRepository.images as image>
                            <@imageListBoxItem.imageListItem image=image />
                        </#list>
                    </div>

                <#else>
                    <div class="column is-12">
                        No images
                    </div>
                </#if>

            </#if>

        </div>
    </section>

</@base.base>