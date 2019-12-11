<#--
Reference: https://bulma.io/documentation/components/message/

Colored message blocks, to emphasize part of your page

    title:String                                             - An optional heading for the message. This affects the style slightly if provided.
    colour:enum(light, dark, danger, warning, info, success) - The colour scheme of the element.
    extraClasses:String                                      - Any other classes the element should have.
    extraAttributes:String                                   - Any other attributes (e.g. data-*) the element should have.
-->
<#macro message title="" colour="" extraClasses="" extraAttributes="">

    <article class="message<#if colour?has_content> is-${colour}</#if><#if extraClasses?has_content> ${extraClasses}</#if>"<#if extraAttributes?has_content> ${extraAttributes}</#if>>
        <#if title?has_content>
            <div class="message-header">
                ${title}
            </div>
        </#if>
        <div class="message-body">
            <#nested />
        </div>
    </article>

</#macro>