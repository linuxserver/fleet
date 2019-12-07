<#--
Reference: https://bulma.io/documentation/components/pagination/

A responsive, usable, and flexible pagination

    isRight:boolean                  - If set to true, the list will be on the right
    currentPage:int                  - The current page to be highlighted
    allPages:list(int)               - A list of all pages to render. A NULL value will be rendered as an ellipsis.
    additionalQueryParameters:String - Any additional params to be appended to the end of each link.
-->
<#macro pagination isRight=true currentPage=1 allPages=[] additionalQueryParameters="">

    <nav class="pagination has-margin-bottom<#if isRight> is-right</#if>" role="navigation" aria-label="pagination">
        <ul class="pagination-list">
            <#list allPages as page>
                <#if page?has_content>
                    <li>
                        <a aria-label="Goto page #{page}" class="pagination-link<#if page==currentPage> is-current</#if>" href="?page=#{page}${additionalQueryParameters}">#{page}</a>
                    </li>
                <#else>
                    <span class="pagination-ellipsis">&hellip;</span>
                </#if>
            </#list>
        </ul>
    </nav>

</#macro>