<#--
Resource: https://bulma.io/documentation/elements/button/

The classic button, in different colors, sizes, and states

    id:String                                                         - The unique identifier for this element
    type:enum(button, submit)                                         - Sets the type of button. If "submit", it will interact with its parent form.
    title:String                                                      - A description of the button to give further context.
    extraClasses:String                                               - Any additional classes that this element should have
    extraAttributes:String                                            - Any additional attributes that this element should have (e.g. disabled, data-*)
    colour:enum(primary, warning, info, danger, success, light, dark) - Sets the main colour of the button. Default: primary
    size:enum(small, normal, medium, large)                           - Sets the size of the button.
    isWide:boolean                                                    - If set to true, will fill available horizontal space.
    isOutlined:boolean                                                - If set to true, will set colour on button outline rather than background.
    isInverted:boolean                                                - If set to true, will set the text colour rather than background.
    isRounded:boolean                                                 - If set to true, will fully round the edges of the button.
-->
<#macro button id="" type="button" title="" extraClasses="" extraAttributes="" colour="primary" size="normal" modal="" isDisabled=false isWide=false isOutlined=false isInverted=false isRounded=false>

    <button type="${type}"<#if id?has_content> id="${id}"</#if><#if title?has_content> title="${title}"</#if> class="button is-${size} is-${colour}<#if isWide> is-fullwidth</#if><#if isOutlined> is-outlined</#if><#if isInverted> is-inverted</#if><#if isRounded> is-rounded</#if><#if extraClasses?has_content> ${extraClasses}</#if><#if modal?has_content> is-modal-trigger</#if>"<#if modal?has_content> data-modal="${modal}"</#if><#if extraAttributes?has_content> ${extraAttributes}</#if><#if isDisabled> disabled</#if>>
        <#nested />
    </button>

</#macro>

<#--
A convenience macro to generate a submit button.
-->
<#macro submit id="" extraClasses="" title=""  extraAttributes="" colour="primary" size="normal" isWide=false isOutlined=false isInverted=false isRounded=false>

    <@button id=id type="submit" title=title extraClasses=extraClasses extraAttributes=extraAttributes colour=colour size=size isWide=isWide isOutlined=isOutlined isInverted=isInverted isRounded=isRounded>
        <#nested />
    </@button>

</#macro>

<#--
Generates an anchor link with the same styling as a button.
-->
<#macro link id="" link="" title="" extraClasses="" extraAttributes="" colour="primary" size="normal" modal="" isWide=false isOutlined=false isInverted=false isRounded=false>

    <a<#if link?has_content> href="${link}"</#if><#if title?has_content> title="${title}"</#if> class="button is-${size} is-${colour}<#if isWide> is-fullwidth</#if><#if isOutlined> is-outlined</#if><#if isInverted> is-inverted</#if><#if isRounded> is-rounded</#if><#if extraClasses?has_content> ${extraClasses}</#if><#if modal?has_content> is-modal-trigger</#if>"<#if modal?has_content> data-modal="${modal}"</#if><#if extraAttributes?has_content> ${extraAttributes}</#if>>
        <#nested />
    </a>

</#macro>

<#macro buttons isGrouped=false size="normal">

    <div class="buttons<#if isGrouped> has-addons</#if> are-${size}">
        <#nested />
    </div>

</#macro>