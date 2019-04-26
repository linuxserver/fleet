<#macro base title context>

    <!DOCTYPE html>
    <html>
        <head>

            <title>${title}</title>

            <link rel="shortcut icon" type="image/png" href="/images/favicon-32x32.png"/>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />

            <link href="https://fonts.googleapis.com/css?family=Pacifico|Nunito" rel="stylesheet" />
            <link rel="stylesheet" type="text/css" href="/css/all.min.css" />

        </head>

        <body>

            <nav class="navbar navbar-expand-lg navbar-white navbar-light mb-4">
                <div class="container">
                    <a class="navbar-brand" href="/">Fleet</a>

                    <#if __AUTHENTICATED_USER?has_content>
                        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                            <span class="navbar-toggler-icon align-middle">
                                <i class="fas fa-bars"></i>
                            </span>
                        </button>

                        <div class="collapse navbar-collapse" id="navbarSupportedContent">
                            <ul class="navbar-nav ml-auto">
                                <li class="nav-item dropdown">
                                    <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                        <i class="fas fa-user-circle"></i> ${__AUTHENTICATED_USER.name}
                                    </a>
                                    <div class="dropdown-menu" aria-labelledby="navbarDropdown">
                                        <a class="dropdown-item" href="admin">Manage Repositories</a>
                                        <a class="dropdown-item" href="admin/templates">Image Templates</a>
                                        <form action="/logout" method="POST">
                                            <button class="dropdown-item">Log Out</button>
                                        </form>
                                    </div>
                                </li>
                            </ul>
                        </div>
                    </#if>
                </div>
            </nav>

            <div class="fleet-notifications"></div>

            <#nested>

            <footer class="text-center my-5">
                Fleet &copy; 2019 LinuxServer.io
            </footer>

        </body>

        <script type="text/javascript" src="/js/all.min.js"></script>
        <script type="text/javascript">

            jQuery.tablesorter.addParser({
                id: "pullCount",
                is: function(s) {
                    return /^[0-9]?[0-9,\.]*$/.test(s);
                },
                format: function(s) {
                    return jQuery.tablesorter.formatFloat( s.replace(/,/g,'') );
                },
                type: "numeric"
            });

            $(function() {
                $("table.table--sortable").tablesorter();
            });

            <#if context="admin">
            repositoryManager.init();
            synchronisationManager.init();
            </#if>

            <#if context="home">
            imageListManager.init();
            </#if>

            <#if context="setup" || context="login">
            (function() {
              'use strict';
              window.addEventListener('load', function() {

                var forms = document.getElementsByClassName('needs-validation');

                var validation = Array.prototype.filter.call(forms, function(form) {
                  form.addEventListener('submit', function(event) {
                    if (form.checkValidity() === false) {
                      event.preventDefault();
                      event.stopPropagation();
                    }
                    form.classList.add('was-validated');
                  }, false);
                });
              }, false);
            })();

            passwordValidationManager.init();
            </#if>

        </script>

    </html>

</#macro>