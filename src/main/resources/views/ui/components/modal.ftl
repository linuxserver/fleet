<#--
Reference: https://bulma.io/documentation/components/modal/

A classic modal overlay, in which you can include any content you want

    id:String              - An identifier for this element
    title:String           - An optional title for the modal
    isDismissable:boolean  - Whether this element can be closed by the user
    isWide:boolean         - Sets the modal to take up a larger proportion of the screen. Default: true
    extraClasses:String    - Any other classes the element should have.
    extraAttributes:String - Any other attributes (e.g. data-*) the element should have.
-->
<#macro modal id title="" isDismissable=true isWide=true extraClasses="" extraAttributes="">

    <div id="${id}" class="modal is-rounded<#if isWide> is-widescreen</#if><#if isDismissable> is-dismissable</#if><#if extraClasses?has_content> ${extraClasses}</#if>"<#if extraAttributes?has_content> ${extraAttributes}</#if>>
        <div class="modal-background<#if isDismissable> is-modal-cancel</#if>"></div>
        <div class="modal-card">
            <div class="modal-card-body">
                <#if title?has_content>
                    <h2 class="title is-4 has-text-centered">${title}</h2>
                </#if>
                <#nested />
            </div>
        </div>
        <#if isDismissable>
            <button class="modal-close is-large is-modal-cancel" aria-label="close"></button>
        </#if>
    </div>

</#macro>