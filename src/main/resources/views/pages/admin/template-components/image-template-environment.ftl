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

<#import "../../../ui/elements/table.ftl"  as table />
<#import "../../../ui/form/input.ftl"      as input />
<#import "../../../ui/elements/button.ftl" as button />

<#macro environment environment>

    <@table.table isScrollable=true isFullWidth=true>
        <thead>
            <tr>
                <th>Environment Variable</th>
                <th>Example Value</th>
                <th>Description</th>
                <th></th>
            </tr>
        </thead>
        <tbody>
            <#list environment as env>
                <tr>
                    <td>
                        <input title="Environment mapping" type="text" class="input is-small" value="${env.env}" name="imageTemplateEnv" required />
                    </td>
                    <td>
                        <input title="Environment mapping example value" type="text" class="input is-small" value="${env.exampleValue!""}" name="imageTemplateEnvExample" />
                    </td>
                    <td>
                        <input title="Environment mapping description" type="text" class="input is-small" value="${env.description!""}" name="imageTemplateEnvDescription" />
                    </td>
                    <td>
                        <@button.buttons isRightAligned=true>
                            <@button.button colour="danger" size="small" extraClasses="remove-image-template-item">
                                <i class="fas fa-trash is-marginless"></i>
                            </@button.button>
                        </@button.buttons>
                    </td>
                </tr>
            </#list>
        </tbody>
    </@table.table>

    <@button.buttons isRightAligned=true>
        <@button.button id="AddNewEnv" colour="normal-colour" size="small">
            <i class="fas fa-plus has-text-success"></i> Add
        </@button.button>
    </@button.buttons>

</#macro>
