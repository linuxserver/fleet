<#--
Resource: https://bulma.io/documentation/components/navbar/

Constructs a navigation bar for display (usually) at the top of the page.

    id:String                          - The unique identifier for this element
    hasShadow:boolean                  - Whether or not the nav should have a drop shadow. Default: true
    itemPlacement:enum("start", "end") - Informs the navbar where to place the items.
    extraClasses:String                - Any additional classes that this element should have
-->
<#macro navbar id="" hasShadow=true itemPlacement="start" extraClasses="">

    <nav <#if id?has_content>id="${id}"</#if> class="navbar<#if hasShadow> has-shadow</#if><#if extraClasses?has_content> ${extraClasses}</#if>" role="navigation" aria-label="main navigation">
        <div class="navbar-brand">

            <a class="navbar-item" href="/">
                <span class="navbar-title">fleet<span class="has-text-primary">.</span></span>
            </a>

            <a role="button" class="navbar-burger burger" aria-label="menu" aria-expanded="false" data-target="navbarBasicExample">
                <span aria-hidden="true"></span>
                <span aria-hidden="true"></span>
                <span aria-hidden="true"></span>
            </a>
        </div>

        <div class="navbar-menu">
            <div class="navbar-${itemPlacement}">
                <#nested />
            </div>
        </div>
    </nav>

</#macro>

<#--
Resource: https://bulma.io/documentation/components/navbar/

Constructs a single item within a navbar.

    id:String             - The unique identifier for this element
    displayText:String    - The text which will be viewable on the screen. This may be HTML.
    icon:String           - An icon to display next to the text
    notificationCount:int - If there is a linked notification count assigned to this item, it will be displayed as a badge.
    extraClasses:String   - Any additional classes that this element should have
-->
<#macro item displayText id="" link="" icon="" title="" isActive=false extraClasses="">

    <a <#if id?has_content>id="${id}"</#if> class="navbar-item is-relative<#if isActive> is-active</#if><#if extraClasses?has_content> ${extraClasses}</#if>" <#if link?has_content>href="${link}"</#if><#if title?has_content> title="${title}"</#if>>
        <#if icon?has_content><i class="fas fa-${icon}"></i> </#if>${displayText}
    </a>

</#macro>

<#macro buttons isGrouped=false size="normal">

    <div class="navbar-item">
        <div class="buttons<#if isGrouped> has-addons</#if> are-${size}">
            <#nested />
        </div>
    </div>

</#macro>

<#--
Resource: https://bulma.io/documentation/components/navbar/

Constructs a wrapper for a dropdown item, including its text.

    id:String             - The unique identifier for this element
    displayText:String    - The text which will be viewable on the screen. This may be HTML.
    icon:String           - An icon to display next to the text
    notificationCount:int - If there is a linked notification count assigned to this item, it will be displayed as a badge.
    isHoverable:boolean   - Sets whether or not the contents of the dropdown are displayed when the mouse hovers over it. Default: true
    extraClasses:String   - Any additional classes that this element should have
-->
<#macro dropdown displayText="" icon="" notificationCount=0 isHoverable=true isRight=false id="">

    <div <#if id?has_content>id="${id}"</#if> class="navbar-item has-dropdown<#if isHoverable> is-hoverable</#if>">

        <a class="navbar-link is-relative">
            <#if icon?has_content><i class="fas fa-${icon}"></i> </#if>${displayText}
            <span id="<#if id?has_content>${id}-</#if>notification-count" class="tag is-badge is-rounded is-danger"><#if notificationCount &gt; 0>#{notificationCount}</#if></span>
        </a>

        <div class="navbar-dropdown<#if isRight> is-right</#if>">
            <#nested />
        </div>
    </div>

</#macro>