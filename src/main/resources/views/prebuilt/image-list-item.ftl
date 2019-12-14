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
        <div class="column has-margin-top is-12 is-12-tablet is-8-desktop">
            <article class="media">
                <figure class="media-left">
                    <p class="icon has-text-centered">
                        <i class="fas fa-cube"></i>
                    </p>
                </figure>
                <div class="media-content">
                    <div class="content">

                        <h4 class="title is-5">
                            <span class="has-text-weight-light">${image.repositoryKey.name}/</span><span class="has-text-weight-500">${image.name}</span>
                        </h4>

                        ${image.description!""}

                        <div class="tags has-margin-top">
                            <#list image.tagBranches as tagBranch>
                                <@tag.tag colour="light" value='<i class="fas fa-tag"></i> ${tagBranch.latestTag.version}' extraAttributes='title="Latest Version"' />
                            </#list>

                            <@tag.tag colour="light" value='<i class="fas fa-download"></i> ${image.pullCount}' extraAttributes='title="Pulls"' />
                            <@tag.tag colour="light" value='<i class="fas fa-star"></i> ${image.starCount}' />

                            <#if image.deprecated>
                                <@tag.tag colour="warning" value='<i class="fas fa-exclamation-circle"></i> Deprecated' />
                            </#if>
                        </div>

                    </div>
                </div>
            </article>
        </div>
    </#if>

</#macro>