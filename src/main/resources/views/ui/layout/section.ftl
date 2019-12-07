<#--
Resource: https://bulma.io/documentation/layout/ection/

A simple container to divide your page into sections

    id:String            - The unique identifier for this element
    extraClasses:String  - Any additional classes that this element should have
-->
<#macro section id="" extraClasses="">

    <section <#if id?has_content>id="${id}"</#if> class="section<#if extraClasses?has_content> ${extraClasses}</#if>">
        <#nested />
    </section>

</#macro>