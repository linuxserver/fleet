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

<#import "../prebuilt/base.ftl"                  as base />
<#import "../prebuilt/fleet-title.ftl"           as title />
<#import "../prebuilt/image-list-table-item.ftl" as imageListTableItem />
<#import "../prebuilt/image-list-item.ftl"       as imageListBoxItem />

<#import "../ui/form/input.ftl"            as input />
<#import "../ui/components/message.ftl"    as message />
<#import "../ui/elements/box.ftl"          as box />
<#import "../ui/elements/table.ftl"        as table />
<#import "../ui/elements/button.ftl"       as button />

<@base.base title="Images" context="home" showTitle=false availableRepositories=availableRepositories>

    <section class="section is-paddingless-top">
        <div class="container">

            <#if selectedRepository?has_content>

                <div class="columns is-multiline">

                    <div class="column is-12 has-margin-top">
                        <@title.title icon="cubes" boldValue=selectedRepository.name />
                    </div>

                    <div class="column is-12 has-margin-top">
                        <@input.text id="SearchImages" icon="search" placeholder="Search..." />
                    </div>

                </div>

                <#if selectedRepository.images?has_content>

                    <div id="ImageTableHolder" class="columns is-multiline">

                        <div class="column is-12">
                            <@table.table id="ImageTable" isFullWidth=true isScrollable=true extraClasses="table--sortable">
                                <thead>
                                <tr>
                                    <th></th>
                                    <th>Name</th>
                                    <th></th>
                                    <th>Latest Version</th>
                                    <th class="sorter-pullCount is-hidden-mobile">Pulls</th>
                                    <th class="is-hidden-mobile">Stars</th>
                                    <th class="is-hidden-mobile">Build Time</th>
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

                <#else>
                    <div class="column is-12">
                        No images
                    </div>
                </#if>

            </#if>

        </div>
    </section>

</@base.base>
