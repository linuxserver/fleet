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
<#macro compose fullName containerName templates latest="">

    <div class="content">
        <pre><code class="language-yaml">---
version: "2"
services:
  ${containerName}:
    image: ${fullName}<#if latest?has_content>:${latest}</#if>
    container_name: ${containerName}
<#if templates.hostNetworkingEnabled>    network_mode: host
</#if>
<#if templates.restartPolicy?has_content>
    restart: ${templates.restartPolicy}</#if>
<#if templates.capabilities?has_content>    cap_add:
    <#list templates.capabilities as cap>
        - ${cap}
    </#list>
</#if>
<#if templates.env?has_content>    environment:
  <#list templates.env as env>
      - ${env.name}<#if env.exampleValue?has_content>=${env.exampleValue}</#if><#if env.description?has_content> # ${env.description}</#if>
  </#list>
</#if>
<#if templates.volumes?has_content>    volumes:
  <#list templates.volumes as volume>
      - /host/path/to${volume.name}:${volume.name}<#if volume.readonly>:ro</#if> <#if volume.description?has_content># ${volume.description}</#if>
  </#list>
</#if>
<#if templates.ports?has_content && !templates.hostNetworkingEnabled>    ports:
  <#list templates.ports as port>
      - ${port.name?string["##0"]}:${port.name?string["##0"]}/${port.protocol} <#if port.description?has_content># ${port.description}</#if>
  </#list>
</#if>
<#if templates.devices?has_content>    devices:
  <#list templates.devices as device>
      - ${device.name}:${device.name} <#if device.description?has_content># ${device.description}</#if>
  </#list>
</#if></code></pre>
    </div>

</#macro>

<#macro cli fullName containerName templates latest="">

    <div class="content">
        <pre><code class="language-bash">docker create \
  --name=${containerName} \<#if templates.hostNetworkingEnabled>
  --net=host \</#if><#if templates.env?has_content>
<#list templates.env as env>
  -e ${env.name}<#if env.exampleValue?has_content>=${env.exampleValue}</#if><#if env.description?has_content> `# ${env.description}`</#if> \
</#list>
</#if>
<#if templates.volumes?has_content>
<#list templates.volumes as volume>
  -v /host/path/to${volume.name}:${volume.name}<#if volume.readonly>:ro</#if><#if volume.description?has_content> `# ${volume.description}`</#if> \
</#list>
</#if>
<#if templates.ports?has_content && !templates.hostNetworkingEnabled>
<#list templates.ports as port>
  -p ${port.name?string["##0"]}:${port.name?string["##0"]}/${port.protocol}<#if port.description?has_content> `# ${port.description}`</#if> \
</#list>
</#if>
<#if templates.devices?has_content>
<#list templates.devices as device>
  --device ${device.name}:${device.name}<#if device.description?has_content> `# ${device.description}`</#if> \
</#list>
</#if>
<#if templates.capabilities?has_content>
<#list templates.capabilities as cap>
  --cap-add=${cap} \
</#list>
</#if>
<#if templates.restartPolicy?has_content>  --restart ${templates.restartPolicy} \</#if>
  ${fullName}<#if latest?has_content>:${latest}</#if></code></pre>
    </div>

</#macro>
