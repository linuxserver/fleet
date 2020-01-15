<#--
 Copyright (c) 2020 LinuxServer.io
 
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

<#import "../../../ui/form/input.ftl" as input />

<#macro misc templateHolder restartPolicies=[ 'no', 'always', 'unless-stopped', 'on-failure' ]>

    <div class="columns is-multiline">
        <div class="column is-full">
            <@input.text id="ImageTemplateUpstreamUrl" label="Registry Url" value=templateHolder.registryUrl!""?html />
        </div>
        <div class="column is-3-desktop is-full-tablet is-full-mobile">
            <@input.dropdown label="Restart Policy" id="ImageTemplateRestartPolicy">
                <option hidden disabled>Choose option...</option>
                <#list restartPolicies as policy>
                    <option<#if policy==templateHolder.restartPolicy!""> selected</#if> value="${policy}">${policy}</option>
                </#list>
            </@input.dropdown>
        </div>
        <div class="column is-3-desktop is-full-tablet is-full-mobile">
            <@input.toggle id="ImageTemplateNetworkHost" label="Host Network" size="large" isToggled=templateHolder.hostNetworkingEnabled />
        </div>
        <div class="column is-3-desktop is-full-tablet is-full-mobile">
            <@input.toggle id="ImageTemplatePrivileged"  label="Privileged"   size="large" isToggled=templateHolder.privilegedMode />
        </div>
        <div class="column is-3-desktop is-full-tablet is-full-mobile">
            <@input.dropdown id="ImageTemplateCapabilities" label="Capabilities" isMultiple=true>
                <#list containerCapabilities as capability>
                    <option<#if templateHolder.hasCapability(capability)> selected</#if> value="${capability}">${capability}</option>
                </#list>
            </@input.dropdown>
        </div>
    </div>

</#macro>
