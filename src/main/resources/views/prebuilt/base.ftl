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

<#import "../ui/components/navbar.ftl" as navbar />
<#import "../ui/elements/button.ftl"   as button />

<#macro base title context showTitle=true backgroundColour="white">

<!DOCTYPE html>
<html lang="en">
<head>

    <title><#if showTitle>${title} | </#if>fleet</title>

    <link rel="shortcut icon" type="image/png" href="assets/images/favicon-32x32.png"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />

    <link href="https://fonts.googleapis.com/css?family=Pacifico|Nunito" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="assets/css/bulma-0.7.5.min.css" />
    <link rel="stylesheet" type="text/css" href="assets/css/app.css" />
    <link rel="stylesheet" type="text/css" href="assets/css/fontawesome-all.css" />

</head>

<body class="has-background-${backgroundColour} has-text-grey-dark">

    <@navbar.navbar id="MainNav" hasShadow=false itemPlacement="end">

        <#if __AuthenticatedUser?has_content>
            <@navbar.dropdown displayText=__AuthenticatedUser.name>
                <@navbar.item displayText="Log Out" icon="sign-out-alt" />
            </@navbar.dropdown>
        <#else>
            <@navbar.buttons size="small">
                <@button.link id="LogIn" link="/login" size="small">
                    <i class="fas fa-sign-in-alt"></i> Log In
                </@button.link>
            </@navbar.buttons>
        </#if>

    </@navbar.navbar>

    <#nested>

    <script type="text/javascript" src="assets/js/jquery-3.3.1.min.js"></script>
    <script type="text/javascript" src="assets/js/app.js"></script>
    <script type="text/javascript">

        (function() {
          'use strict';
          window.addEventListener('load', function() {

            formValidationManager.init();
            bulmaManager.init();

          }, false);
        })();

    </script>
</body>

</html>

</#macro>