<#--
Resource: https://bulma.io/documentation/elements/box/

Constructs a visible container for content on a page.

    id:String              - The unique identifier for this element
    extraClasses:String    - Any additional classes that this element should have
    extraAttributes:String - Any additional attributes that this element should have (e.g. disabled, data-*)
-->
<#macro media id="" extraClasses="" extraAttributes="">

    <article<#if id?has_content> id="${id}"</#if> class="media<#if extraClasses?has_content> ${extraClasses}</#if>"<#if extraAttributes?has_content> ${extraAttributes}</#if>>
        <div class="media-content">
            <div class="content">
                <#nested />
            </div>
        </div>
    </article>

</#macro>