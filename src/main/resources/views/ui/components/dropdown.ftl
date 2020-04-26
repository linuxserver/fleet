
<#macro dropdown triggerText id title="" size="normal" isDisabled=false extraClasses="" extraAttributes="">

    <div class="dropdown">
        <div class="dropdown-trigger"<#if title?has_content> title="${title}"</#if>>
            <button class="button is-${size}" id="${id}" aria-haspopup="true" aria-controls="${id}_Menu"<#if isDisabled> disabled</#if>>
                <span>${triggerText}</span>
                <span class="icon is-small">
                    <i class="fas fa-angle-down" aria-hidden="true"></i>
                </span>
            </button>
        </div>
        <div class="dropdown-menu" id="${id}_Menu" role="menu">
            <div class="dropdown-content">
                <#nested />
            </div>
        </div>
    </div>

</#macro>

<#macro item id="" link="" modal="" isActive=false extraClasses="" extraAttributes="">
    <a<#if id?has_content> id="${id}"</#if> class="dropdown-item<#if isActive> is-active</#if><#if modal?has_content> is-modal-trigger</#if><#if extraClasses?has_content> ${extraClasses}</#if>"<#if extraAttributes?has_content> ${extraAttributes}</#if><#if modal?has_content> data-modal="${modal}"</#if><#if link?has_content> href="${link}"</#if>>
        <#nested />
    </a>
</#macro>