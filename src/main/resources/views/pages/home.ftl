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
<#import "../prebuilt/image-list-item.ftl" as imageListItem />

<@base.base title="Images" context="home" showTitle=false>

    <section class="section">
        <div class="container">

            <div class="columns is-multiline is-centered">
                <div class="column is-8-desktop">
                    <@input.text id="SearchImages" icon="search" placeholder="Search..." />
                </div>
            </div>

        </div>
    </section>

    <section class="section is-paddingless-top">
        <div class="container">

            <div class="columns">
                <div class="column is-12">
                    <#if availableRepositories?has_content && availableRepositories?size &gt; 0>
                        <@input.dropdown id="RepositorySelection" size="normal" icon="cubes">
                            <#list availableRepositories as repository>
                                <option value="${repository.key}"<#if repository.name==selectedRepository.name> selected</#if>>${repository.name}</option>
                            </#list>
                        </@input.dropdown>
                    <#else>
                        There are currently no available repositories.
                    </#if>
                </div>
            </div>

            <#if selectedRepository?has_content>

                <div class="columns is-multiline">
                    <div class="column is-12">
                        <h2 class="title is-4 repository-title">
                            <i class="fas fa-cubes"></i> ${selectedRepository.name}<span class="has-text-primary">.</span>
                        </h2>
                    </div>

                    <div class="column is-12">
                        <nav class="level">
                            <div class="level-item has-text-centered">
                                <div>
                                    <p class="heading">Images</p>
                                    <p class="title">${selectedRepository.images?size}</p>
                                </div>
                            </div>
                            <div class="level-item has-text-centered">
                                <div>
                                    <p class="heading">Aggregated Pulls</p>
                                    <p class="title">${selectedRepository.totalPulls}</p>
                                </div>
                            </div>
                            <div class="level-item has-text-centered">
                                <div>
                                    <p class="heading">Aggregated Stars</p>
                                    <p class="title">${selectedRepository.totalStars}</p>
                                </div>
                            </div>
                        </nav>
                    </div>
                </div>
                <div class="columns is-centered is-multiline has-margin-top">
                    <#if selectedRepository.images?has_content>
                        <#list selectedRepository.images as image>
                            <@imageListItem.imageListItem image=image />
                        </#list>
                    <#else>
                        No images
                    </#if>
                </div>

            </#if>

        </div>
    </section>

</@base.base>