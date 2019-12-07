<#--
Reference: https://bulma.io/documentation/elements/tag/

Small tag labels to insert anywhere

    value:String                            - The primary text to display in the tag
    colour:enum(primary, warning, info etc) - The colour of the main text background
    size:enum(small, normal, large)         - The size of the tag
    isRounded:boolean                       - If true, rounds the edges. Only applied if isGrouped=false
    isGrouped:boolean                       - If true, generates a title for the tag
    groupTitle:String                       - A title for the tag, separate to the main text
    extraClasses:String                     - Any other classes the input should have. These are applied directly to the input
    extraAttributes:String                  - Any other attributes (e.g. data-*) the input should have. Applied directly to the input.
-->
<#macro tag value colour="primary" size="normal" isRounded=false isGrouped=false groupTitle="" extraClasses="" extraAttributes="">

    <#if isGrouped>
        <div class="tags has-addons">
            <span class="tag is-dark">${groupTitle}</span>
    </#if>

    <span class="tag is-${colour} is-${size}<#if !isGrouped && isRounded> is-rounded</#if><#if extraClasses?has_content> ${extraClasses}</#if>"<#if extraAttributes?has_content> ${extraAttributes}</#if>>
        ${value}
    </span>

    <#if isGrouped>
        </div>
    </#if>

</#macro>