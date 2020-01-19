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
<#macro title thinValue="" boldValue="" separator="" icon="" imageIcon="" subtitle="">

    <h2 class="title is-size-3-desktop is-size-4-mobile">
        <#if imageIcon?has_content>
            <figure class="image is-32x32 is-pulled-left has-margin-right">
                <img src="${imageIcon}" alt="Title logo" />
            </figure>
        <#elseif icon?has_content>
            <i class="fas fa-${icon}"></i>
        </#if>
        <#if thinValue?has_content><span class="has-text-weight-light">${thinValue}</span>${separator}</#if>${boldValue}<span class="has-text-primary">.</span>
        <#nested />
    </h2>
    <#if subtitle?has_content>
        <h3 class="subtitle">
            ${subtitle}
        </h3>
    </#if>

</#macro>
