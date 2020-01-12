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

<#macro ports ports protocols=[ 'tcp', 'udp' ]>

    <@table.table isScrollable=true isFullWidth=true>
        <thead>
            <tr>
                <th class="template-port">Port</th>
                <th class="template-port-protocol">Protocol</th>
                <th class="template-port-description">Description</th>
                <th class="template-port-delete"></th>
            </tr>
        </thead>
        <tbody>
            <#list ports as port>
                <tr>
                    <td>
                        <input title="Port mapping" type="number" class="input is-small" name="imageTemplatePort" value="#{port.port}" required />
                    </td>
                    <td>
                        <div class="select is-small">
                            <select title="Port protocol" name="imageTemplatePortProtocol">
                                <#list protocols as protocol>
                                    <option<#if port.protocol==protocol> selected</#if> value="${port.protocol}">${port.protocol}</option>
                                </#list>
                            </select>
                        </div>
                    </td>
                    <td>
                        <input title="Port mapping description" type="text" class="input is-small" name="imageTemplatePortDescription" value="${port.description}" required />
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
        <@button.button id="AddNewPort" colour="normal-colour" size="small">
            <i class="fas fa-plus has-text-success"></i> Add
        </@button.button>
    </@button.buttons>

</#macro>
