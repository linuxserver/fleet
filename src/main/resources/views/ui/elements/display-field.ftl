<#import "../../templates/helpers/type-safe.ftl" as typeSafe />

<#macro displayField value label="" size="normal" isInline=false extraClasses="" extraAttributes="">

    <div class="field<#if extraClasses?has_content> ${extraClasses}</#if>"<#if extraAttributes?has_content> ${extraAttributes}</#if>>
        <#if label?has_content>
            <label class="label is-${size}<#if isInline> is-inline</#if>">
                ${label}
            </label>
        </#if>
        <div class="control">
            <span class="display-field is-${size}">
                <@typeSafe.render value=value />
            </span>
        </div>
    </div>

</#macro>