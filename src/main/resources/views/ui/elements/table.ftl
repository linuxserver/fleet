<#--
Resource: https://bulma.io/documentation/elements/table/

The inevitable HTML table, with special case cells

    id:String              - The unique identifier for this element
    isBordered:boolean     - Places a border around the outer edges of the table
    isStriped:boolean      - Sets a background colour on all alternate rows
    isFullWidth:boolean    - Ensures the table fills the available horizontal space of its parent
    isHoverable:boolean    - Sets a hover colour for rows in the table
    isScrollable:boolean   - Allows the table to be horizontally scrolled if too wide
    extraClasses:String    - Any additional classes that this element should have
    extraAttributes:String - Any additional attributes that this element should have (e.g. disabled, data-*)
-->
<#macro table id="" isBordered=false isStriped=false isFullWidth=true isHoverable=false isNarrow=false isScrollable=false extraClasses="" extraAttributes="">

    <#if isScrollable>
    <div class="table-container">
    </#if>

        <table <#if id?has_content>id="${id}" </#if>class="table<#if isNarrow> is-narrow</#if><#if isBordered> is-bordered</#if><#if isStriped> is-striped</#if><#if isFullWidth> is-fullwidth</#if><#if isHoverable> is-hoverable</#if><#if extraClasses?has_content> ${extraClasses}</#if>"<#if extraAttributes?has_content> ${extraAttributes}</#if>>
            <#nested />
        </table>

    <#if isScrollable>
    </div>
    </#if>
</#macro>

<#--
Builds a single row with two columns of equal length (50%). The left side contains the title while
the right side contains the value.
-->
<#macro halfDisplayRow title value link="" id="">

    <tr>
        <td class="is-half has-text-grey-dark">
            ${title}
        </td>
        <td<#if id?has_content> id="${id}"</#if>>
            <#if link?has_content>
                <a href="${link}">
            </#if>
            ${value}
            <#if link?has_content>
                </a>
            </#if>
        </td>
    </tr>

</#macro>
