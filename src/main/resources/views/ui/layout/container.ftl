<#--
Resource: https://bulma.io/documentation/layout/container/

A simple container to center your content horizontally

    id:String            - The unique identifier for this element
    extraClasses:String  - Any additional classes that this element should have
    isFluid:boolean      - Defines whether or not the container becomes "full width". Default: false
-->
<#macro container id="" extraClasses="" isFluid=false>

    <div <#if id?has_content>id="${id}"</#if> class="container<#if isFluid> is-fluid</#if><#if extraClasses?has_content> ${extraClasses}</#if>">
        <#nested />
    </div>

</#macro>