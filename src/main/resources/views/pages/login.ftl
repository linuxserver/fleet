<#--
 Copyright (c) 2019 LinuxServer.io

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.
-->
<#import "../prebuilt/base.ftl"         as base />
<#import "../prebuilt/fleet-title.ftl"  as title />
<#import "../ui/elements/box.ftl"       as box />
<#import "../ui/form/input.ftl"         as input />
<#import "../ui/elements/button.ftl"    as button />
<#import "../ui/components/message.ftl" as message />
<#import "../ui/layout/container.ftl"   as container />

<@base.base title="Log In" showNav=false context="login" hasHero=false>

    <section class="hero is-fullheight">
        <div class="hero-body">
            <@container.container isFluid=true>
                <div class="columns is-centered">
                    <div class="column is-one-third-desktop is-two-thirds-tablet is-full-mobile">

                        <form class="needs-validation" novalidate action="/login" method="post">
                            <@box.box>

                                <@title.title boldValue="fleet" />

                                <@input.text     id="username" icon="user" placeholder="Username" isRequired=true />
                                <@input.password id="password" icon="lock" placeholder="Password" isRequired=true />

                                <@button.submit  id="SubmitLogin" extraClasses="is-fullwidth">
                                    Log In
                                </@button.submit>

                                <#if loginFailed?has_content>
                                    <@message.message colour="danger" extraClasses="has-margin-top">
                                        ${loginFailed?html}
                                    </@message.message>
                                </#if>

                            </@box.box>
                        </form>

                    </div>
                </div>
            </@container.container>
        </div>
    </section>

</@base.base>
