<#--
Reference: https://bulma.io/documentation/elements/notification/

Bold notification blocks, to alert your users of something

    id:String             - The unique identifier for this element
    isError:boolean       - Colours the notification with an error-like colour
    isWarning:boolean     - Colours the notification with a warning-like colour
    isSuccess:boolean     - Colours the notification with a success-like colour
    isInfo:boolean        - Colours the notification with an info-like colour
    isDismissable:boolean - Whether or not the notification may be removed via an "x" button. Default: true

Note on colour flags: The notification will always take the colour of the highest priority level. Therefore if isWarning=true and isInfo=true,
                      the resulting colour will be for isWarning as it has a higher priority.
-->
<#macro notification id="" isError=false isWarning=false isSuccess=false isInfo=true isDismissable=true>

    <div<#if id?has_content> id="${id}"</#if> class="notification is-<#if isError>danger<#elseif isWarning>warning<#elseif isSuccess>success<#elseif isInfo>info<#else>primary</#if>">
        <#if isDismissable><button class="delete"></button></#if>
        <#nested />
    </div>

</#macro>