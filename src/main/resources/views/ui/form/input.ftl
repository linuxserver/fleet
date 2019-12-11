<#--
Reference: https://bulma.io/documentation/form/input/

    type:enum(text, password, number, date) - Determines the type of input and how its value is displayed
    id:String                               - A unique identifier for this element
    label:String                            - If provided, a text label is displayed next to the input
    placeholder:String                      - Places a hint into the input
    extraClasses:String                     - Any other classes the input should have. These are applied directly to the input
    extraAttributes:String                  - Any other attributes (e.g. data-*) the input should have. Applied directly to the input.
    icon:String                             - An optional icon to be displayed at the front of the input.
    required:boolean                        - Marks the input as being required if part of a form. Default: false
    readonly:boolean                        - Marks the input as being readonly. This will ensure the value cannot be changed.
    disabled:boolean                        - Marks the input as disabled. This will not only block value changes but will also omit the input from any form submissions.
    isInvalid:boolean                       - If the value is already known to be invalid at the time of rendering, it will display the requiredHelp if any is provided
    requiredHelp:String                     - If "required" is true, some help text to be displayed
    size:enum(small, normal, large)         - The size of the input
-->
<#macro input type id value="" label="" title="" placeholder="" extraClasses="" extraAttributes="" icon="" isRequired=false isReadonly=false isDisabled=false isInvalid=false requiredHelp="" size="normal">

    <div class="field">
        <#if label?has_content>
            <label class="label is-${size}" for="${id}">${label}</label>
        </#if>
        <div class="control is-${size}<#if icon?has_content> has-icons has-icons-left</#if>">
            <input type="${type}"<#if title?has_content> title="${title}"</#if> class="input<#if isReadonly> is-static</#if> is-${size}<#if extraClasses?has_content> ${extraClasses}</#if>" id="${id}" name="${id}"<#if isRequired> required</#if><#if isReadonly> readonly</#if><#if isDisabled> disabled</#if><#if value?has_content> value="${value}"</#if><#if placeholder?has_content> placeholder="${placeholder}"</#if><#if extraAttributes?has_content> ${extraAttributes}</#if>/>
            <#if icon?has_content>
                <span class="icon is-${size} is-left"><i class="fas fa-${icon}"></i></span>
            </#if>
            <#if isRequired && requiredHelp?has_content>
                <p class="help invalid-feedback is-danger">${requiredHelp}</p>
            </#if>
        </div>
    </div>

</#macro>

<#--
Reference: https://bulma.io/documentation/form/input/

Convenience macro to generate a text input

    id:String                               - A unique identifier for this element
    label:String                            - If provided, a text label is displayed next to the input
    placeholder:String                      - Places a hint into the input
    extraClasses:String                     - Any other classes the input should have. These are applied directly to the input
    extraAttributes:String                  - Any other attributes (e.g. data-*) the input should have. Applied directly to the input.
    icon:String                             - An optional icon to be displayed at the front of the input.
    required:boolean                        - Marks the input as being required if part of a form. Default: false
    readonly:boolean                        - Marks the input as being readonly. This will ensure the value cannot be changed.
    disabled:boolean                        - Marks the input as disabled. This will not only block value changes but will also omit the input from any form submissions.
    requiredHelp:String                     - If "required" is true, some help text to be displayed
    size:enum(small, normal, large)         - The size of the input
-->
<#macro text id value="" label="" title="" placeholder="" extraClasses="" extraAttributes="" icon="" isRequired=false isReadonly=false isDisabled=false isInvalid=false requiredHelp="" size="normal">
    <@input type="text" id=id value=value title=title label=label placeholder=placeholder extraAttributes=extraAttributes extraClasses=extraClasses icon=icon isRequired=isRequired isReadonly=isReadonly isDisabled=isDisabled isInvalid=isInvalid requiredHelp=requiredHelp size=size />
</#macro>

<#--
Reference: https://bulma.io/documentation/form/input/

Convenience macro to generate a password input

    id:String                               - A unique identifier for this element
    label:String                            - If provided, a text label is displayed next to the input
    placeholder:String                      - Places a hint into the input
    extraClasses:String                     - Any other classes the input should have. These are applied directly to the input
    extraAttributes:String                  - Any other attributes (e.g. data-*) the input should have. Applied directly to the input.
    icon:String                             - An optional icon to be displayed at the front of the input.
    required:boolean                        - Marks the input as being required if part of a form. Default: false
    readonly:boolean                        - Marks the input as being readonly. This will ensure the value cannot be changed.
    disabled:boolean                        - Marks the input as disabled. This will not only block value changes but will also omit the input from any form submissions.
    requiredHelp:String                     - If "required" is true, some help text to be displayed
    size:enum(small, normal, large)         - The size of the input
-->
<#macro password id value="" label="" placeholder="" extraClasses="" extraAttributes="" icon="" isRequired=false isReadonly=false isDisabled=false isInvalid=false requiredHelp="" size="normal">
    <@input type="password" id=id value=value label=label placeholder=placeholder extraAttributes=extraAttributes extraClasses=extraClasses icon=icon isRequired=isRequired isReadonly=isReadonly isDisabled=isDisabled isInvalid=isInvalid requiredHelp=requiredHelp size=size />
</#macro>

<#--
Reference: https://bulma.io/documentation/form/input/

Convenience macro to generate a number input

    id:String                               - A unique identifier for this element
    label:String                            - If provided, a text label is displayed next to the input
    placeholder:String                      - Places a hint into the input
    extraClasses:String                     - Any other classes the input should have. These are applied directly to the input
    extraAttributes:String                  - Any other attributes (e.g. data-*) the input should have. Applied directly to the input.
    icon:String                             - An optional icon to be displayed at the front of the input.
    required:boolean                        - Marks the input as being required if part of a form. Default: false
    readonly:boolean                        - Marks the input as being readonly. This will ensure the value cannot be changed.
    disabled:boolean                        - Marks the input as disabled. This will not only block value changes but will also omit the input from any form submissions.
    requiredHelp:String                     - If "required" is true, some help text to be displayed
    size:enum(small, normal, large)         - The size of the input
-->
<#macro number id value="" label="" placeholder="" extraClasses="" extraAttributes="" icon="" isRequired=false isReadonly=false isDisabled=false isInvalid=false requiredHelp="" size="normal">
    <@input type="number" id=id value=value label=label placeholder=placeholder extraAttributes=extraAttributes extraClasses=extraClasses icon=icon isRequired=isRequired isReadonly=isReadonly isDisabled=isDisabled isInvalid=isInvalid requiredHelp=requiredHelp size=size />
</#macro>

<#macro dropdown id label="" extraClasses="" extraAttributes="" icon="" colour="" isInline=false isMultiple=false isRequired=false isReadonly=false isDisabled=false requiredHelp="" size="normal">

    <div class="field<#if isInline> is-inline</#if>">
        <#if label?has_content>
            <label class="label is-${size}" for="${id}">${label}</label>
        </#if>
        <div class="control<#if icon?has_content> has-icons has-icons-left</#if><#if isInline> is-inline</#if><#if extraClasses?has_content> ${extraClasses}</#if>">
            <div class="select is-${size}<#if isMultiple> is-multiple</#if><#if colour?has_content> is-${colour}</#if>">
                <select id="${id}" name="${id}"<#if isMultiple> multiple</#if><#if isRequired> required</#if><#if isReadonly> readonly</#if><#if isDisabled> disabled</#if><#if extraAttributes?has_content> ${extraAttributes}</#if>>
                    <#nested />
                </select>
            </div>
            <#if icon?has_content>
                <span class="icon is-${size} is-left"><i class="fas fa-${icon}"></i></span>
            </#if>
        </div>
        <#if isRequired && requiredHelp?has_content>
            <p class="help is-danger is-hidden">${requiredHelp}</p>
        </#if>
    </div>

</#macro>

<#--
Reference: https://bulma.io/documentation/form/input/

Creates a neat toggle element on the page in the place of a checkbox.

    id:String                                         - A unique identifier for this element
    title:String                                      - If provided, a text label is displayed next to the input
    toggled:boolean                                   - The initial toggle state of the toggle.
    extraClasses:String                               - Any other classes the input should have. These are applied to the parent span
    inputClasses:String                               - Any other classes the input should have. These are applied directly to the input
    extraAttributes:String                            - Any other attributes (e.g. data-*) the input should have. Applied directly to the input.
    disabled:boolean                                  - Marks the input as disabled. This will not only block value changes but will also omit the input from any form submissions.
    colour:enum(danger, warning, info, primary, link) - The colour the toggle will be when selected.
    size:enum(small, normal, large)                   - The size of the input
-->
<#macro toggle id="" title="" label="" isToggled=false isDisabled=false size="normal" colour="primary" inputClasses="" extraClasses="" extraAttributes="">

    <#if label?has_content>
        <div class="field">
            <label class="label is-${(size=='large')?string('normal', size)}">${label}</label>
            <div class="control">
    </#if>
    <label <#if id?has_content>for="${id}"</#if> class="switch is-${size} is-${colour}<#if extraClasses?has_content> ${extraClasses}</#if>" title="${title}">
        <input<#if id?has_content> id="${id}" name="${id}"</#if> type="checkbox"<#if isDisabled> disabled</#if><#if isToggled> checked</#if><#if extraAttributes?has_content> ${extraAttributes}</#if><#if inputClasses?has_content> class="${inputClasses}"</#if> />
        <span class="slider round"></span>
    </label>
    <#if label?has_content>
            </div>
        </div>
    </#if>

</#macro>

<#--
Reference: https://bulma.io/documentation/form/input/

Creates a basic text input with a surrounding container which also holds an autocomplete placeholder.
You will need to provide the relevant logic to populate this placeholder.

    id:String                                         - A unique identifier for this element
    title:String                                      - If provided, a text label is displayed next to the input
    toggled:boolean                                   - The initial toggle state of the toggle.
    extraClasses:String                               - Any other classes the input should have. These are applied to the parent span
    inputClasses:String                               - Any other classes the input should have. These are applied directly to the input
    extraAttributes:String                            - Any other attributes (e.g. data-*) the input should have. Applied directly to the input.
    disabled:boolean                                  - Marks the input as disabled. This will not only block value changes but will also omit the input from any form submissions.
    colour:enum(danger, warning, info, primary, link) - The colour the toggle will be when selected.
    size:enum(small, normal, large)                   - The size of the input
-->
<#macro autocomplete id value="" label="" placeholder="" extraClasses="" extraAttributes="" icon="" isRequired=false isReadonly=false isDisabled=false isInvalid=false requiredHelp="" size="normal">

    <div class="has-autocomplete is-relative">

        <@text
            id=id
            value=value
            label=label
            placeholder=placeholder
            extraClasses=extraClasses
            extraAttributes=extraAttributes
            icon=icon
            isRequired=isRequired
            isReadonly=isReadonly
            isDisabled=isDisabled
            isInvalid=isInvalid
            requiredHelp=requiredHelp
            size=size />

        <div class="dropdown is-block">
            <div class="dropdown-menu is-autocomplete" role="menu">
                <div class="dropdown-content"></div>
            </div>
        </div>

    </div>

</#macro>

<#--
Reference: https://bulma.io/documentation/form/input/

Creates a placeholder field with the ability to "switch" it into an input which can then be actioned.

    id:String                                         - A unique identifier for this element
    title:String                                      - If provided, a text label is displayed next to the input
    acceptClass:String                                - A classifier to be attached to the "Accept" button. Useful for JS triggering.
    isSwitched:boolean                                - If true, the input will be displayed initially rather than the placeholder
    extraClasses:String                               - Any other classes the input should have. These are applied to the parent span
    inputClasses:String                               - Any other classes the input should have. These are applied directly to the input
    extraAttributes:String                            - Any other attributes (e.g. data-*) the input should have. Applied directly to the input.
    disabled:boolean                                  - Marks the input as disabled. This will not only block value changes but will also omit the input from any form submissions.
    colour:enum(danger, warning, info, primary, link) - The colour the toggle will be when selected.
    size:enum(small, normal, large)                   - The size of the input
-->
<#macro switchable id isSwitched=false value="" placeholder="" acceptClass="" extraClasses="" extraAttributes="" icon="" isRequired=false isReadonly=false isDisabled=false isInvalid=false requiredHelp="" size="normal">

    <div class="has-switchable<#if isSwitched> is-active</#if>">

        <div class="switchable field has-addons is-marginless">

            <div class="control is-expanded is-${size}<#if icon?has_content> has-icons has-icons-left</#if>">
                <input type="text" data-switchable-original="${value}" class="input is-${size}<#if extraClasses?has_content> ${extraClasses}</#if>" id="${id}" name="${id}"<#if isRequired> required</#if><#if isReadonly> readonly</#if><#if isDisabled> disabled</#if><#if value?has_content> value="${value}"</#if> placeholder="${placeholder}"<#if extraAttributes?has_content> ${extraAttributes}</#if>/>
                <#if icon?has_content>
                    <span class="icon is-${size} is-left"><i class="fas fa-${icon}"></i></span>
                </#if>
            </div>
            <div class="control is-${size}">
                <div class="buttons has-addons">
                    <button title="Cancel edit"    class="button is-cancel-switchable is-normal-colour is-${size}"><i class="fas fa-times has-text-danger is-marginless"></i></button>
                    <button title="Accept changes" id="Accept_${id}" class="button is-accept-switchable<#if acceptClass?has_content> ${acceptClass}</#if> is-normal-colour is-${size}"><i class="fas fa-check has-text-success is-marginless"></i></button>
                </div>
            </div>
            <#if isRequired && requiredHelp?has_content>
                <p class="help is-danger<#if !isInvalid> is-hidden</#if>">${requiredHelp}</p>
            </#if>

        </div>

        <span class="switchable plaintext is-pointer is-${size}" title="Click to edit">
            ${value}
        </span>

    </div>

</#macro>