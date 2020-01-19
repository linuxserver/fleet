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

<#import "../ui/components/navbar.ftl"  as navbar />
<#import "../ui/layout/container.ftl"   as container />
<#import "../ui/elements/button.ftl"    as button />
<#import "../ui/form/input.ftl"         as input />
<#import "../prebuilt/system-alert.ftl" as systemAlert />

<#macro base title context showTitle=true showNav=true backgroundColour="white" hasHero=false availableRepositories=[]>

<!DOCTYPE html>
<html lang="en">
<head>

    <title><#if showTitle>${title} | </#if>fleet</title>

    <link rel="shortcut icon" type="image/png" href="/assets/images/favicon-32x32.png"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />

    <link href="https://fonts.googleapis.com/css?family=Pacifico|Nunito" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="/assets/css/bulma-0.8.0.min.css" />
    <link rel="stylesheet" type="text/css" href="/assets/css/app.css" />
    <link rel="stylesheet" type="text/css" href="/assets/css/fontawesome-all.min.css" />
    <link rel="stylesheet" type="text/css" href="/assets/css/prism.css" />

</head>

<body class="has-background-${backgroundColour} has-text-grey-dark">

    <#if showNav>
    <@navbar.navbar id="MainNav" hasShadow=false itemPlacement="end" colour=(hasHero?string('dark', 'normal-colour')) extraClasses=''>

        <#if context="home">
            <div class="navbar-item">
                <#if availableRepositories?has_content && availableRepositories?size &gt; 0>
                    <@input.dropdown id="RepositorySelection" size="normal" icon="cubes">
                        <#list availableRepositories as repository>
                            <option value="${repository.key}"<#if repository.name==selectedRepository.name> selected</#if>>${repository.name}</option>
                        </#list>
                    </@input.dropdown>
                <#else>
                    There are currently no available repositories.
                </#if>
            </div>
        </#if>

        <#if __AuthenticatedUser?has_content>

            <@navbar.dropdown icon="shield-alt" displayText="Admin" isRight=true>
                <@navbar.item displayText="Repositories"    icon="cubes" link="/admin/repositories" isActive=(context=='admin_repositories') />
                <@navbar.item displayText="Schedules"       icon="clock" link="/admin/schedules"    isActive=(context=='admin_schedules') />
                <#-- <@navbar.item displayText="Users"           icon="users" link="/admin/users"        isActive=(context=='admin_users') />-->
                <#-- <@navbar.item displayText="System Settings" icon="cog"   link="/admin/system"       isActive=(context=='admin_system') />-->
            </@navbar.dropdown>

            <@navbar.dropdown displayText=__AuthenticatedUser.name isRight=true icon="user">
                <@navbar.item displayText="Log Out" icon="sign-out-alt" icon="sign-out-alt" link="/login?invalidate=true" />
            </@navbar.dropdown>

        <#else>
            <@navbar.buttons size="small">
                <@button.link id="LogIn" link="/login" size="small" colour="light">
                    <i class="fas fa-sign-in-alt"></i> Log In
                </@button.link>
            </@navbar.buttons>
        </#if>

    </@navbar.navbar>
    </#if>

    <div id="NotificationWrapper">
        <div id="Notifications"></div>
    </div>

    <#if __SystemAlerts?has_content && __SystemAlerts?size &gt; 0>
        <@container.container isFluid=true>
            <#list __SystemAlerts as alert>
                <@systemAlert.alert specificAlert=alert />
            </#list>
        </@container.container>
    </#if>

    <#nested>

    <script type="text/javascript" src="/assets/js/jquery-3.3.1.min.js"></script>
    <script type="text/javascript" src="/assets/js/Chart.bundle.min.js"></script>
    <script type="text/javascript" src="/assets/js/jquery.tablesorter.js"></script>
    <script type="text/javascript" src="/assets/js/app.js"></script>
    <script type="text/javascript" src="/assets/js/prism.js"></script>
    <#if context?starts_with("admin")>
        <script type="text/javascript" src="/assets/js/admin.js"></script>
    </#if>
    <script type="text/javascript">

        (function() {
          'use strict';
          window.addEventListener('load', function() {

              App.init();
              Search.init();
              FormValidation.init();

              <#if context=='image'>
              PullChart.populateChart('${image.key}', 'Month');
              </#if>

              <#if context?starts_with("admin")>
              Admin.init();
              </#if>

              jQuery.tablesorter.addParser({
                  id: "pullCount",
                  is: function(s) {
                      return /^[0-9]?[0-9,.]*$/.test(s);
                  },
                  format: function(s) {
                      return jQuery.tablesorter.formatFloat( s.replace(/,/g,'') );
                  },
                  type: "numeric"
              });

              $(function() {
                  $("table.table--sortable").tablesorter();
              });

          }, false);

        })();

    </script>
</body>

</html>

</#macro>
