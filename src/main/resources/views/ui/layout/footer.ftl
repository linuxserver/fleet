<#--
Reference: https://bulma.io/documentation/layout/footer/

A simple responsive footer which can include anything: lists, headings, columns, icons, buttons, etc.

    serviceInfo:AtassServiceInfo - An optional param which will be used to display the service name and other build info
-->
<#macro footer serviceInfo="">

    <footer class="footer has-margin-top">
        <div class="content has-text-centered has-text-grey">
            <#if serviceInfo?has_content>
                ${serviceInfo.serviceFullName}
                <p>
                    ${serviceInfo.version}
                </p>
            </#if>
        </div>
    </footer>

</#macro>