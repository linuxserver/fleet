<#--
Resource: https://bulma.io/documentation/layout/hero/

An imposing hero banner to showcase something

    id:String            - The unique identifier for this element
    isFullHeight:boolean - Ensures the hero fills the entire screen
    extraClasses:String  - Any additional classes that this element should have
-->
<#macro hero id="" isFullHeight=false extraClasses="">

    <section <#if id?has_content>id="${id}"</#if> class="hero<#if isFullHeight> is-fullheight</#if><#if extraClasses?has_content> ${extraClasses}</#if>">
        <div class="hero-body">
            <#nested />
        </div>
    </section>

</#macro>