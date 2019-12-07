<#import "../ui/components/navbar.ftl" as navbar />

<#macro base title context showTitle=true>

<!DOCTYPE html>
<html>
<head>

    <title><#if showTitle>${title} | </#if>fleet</title>

    <link rel="shortcut icon" type="image/png" href="assets/images/favicon-32x32.png"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />

    <link href="https://fonts.googleapis.com/css?family=Pacifico|Nunito" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="assets/css/bulma.min.css" />
    <link rel="stylesheet" type="text/css" href="assets/css/app.css" />
    <link rel="stylesheet" type="text/css" href="assets/css/fontawesome-all.css" />

</head>

<body>

    <@navbar.navbar id="MainNav" hasShadow=false itemPlacement="end">

        <#if __AuthenticatedUser?has_content>
            <@navbar.dropdown displayText=__AuthenticatedUser.name>
                <@navbar.item displayText="Log Out" icon="sign-out-alt" />
            </@navbar.dropdown>
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