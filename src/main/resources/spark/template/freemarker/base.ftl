<#macro base title context>

    <!DOCTYPE html>
    <html>
        <head>

            <title>${title}</title>

            <link rel="shortcut icon" type="image/png" href="/images/favicon-32x32.png"/>

            <link href="https://fonts.googleapis.com/css?family=Pacifico|Nunito" rel="stylesheet" />
            <link rel="stylesheet" type="text/css" href="/css/fontawesome-all.css" />
            <link rel="stylesheet" type="text/css" href="/css/bootstrap.min.css" />
            <link rel="stylesheet" type="text/css" href="/css/fleet.css" />

        </head>

        <body>

            <nav class="navbar navbar-expand-lg navbar-white navbar-light mb-4">
                <div class="container">
                    <a class="navbar-brand" href="/">Fleet</a>

                    <#if __AUTHENTICATED_USER?has_content>
                        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                            <span class="navbar-toggler-icon"></span>
                        </button>

                        <div class="collapse navbar-collapse" id="navbarSupportedContent">
                            <ul class="navbar-nav ml-auto">
                                <li class="nav-item dropdown">
                                    <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                        <i class="fas fa-user-circle"></i> ${__AUTHENTICATED_USER.name}
                                    </a>
                                    <div class="dropdown-menu" aria-labelledby="navbarDropdown">
                                        <a class="dropdown-item" href="admin">Manage Repositories</a>
                                        <form action="/admin/logout" method="POST">
                                            <button class="dropdown-item">Log Out</button>
                                        </form>
                                    </div>
                                </li>
                            </ul>
                        </div>
                    </#if>
                </div>
            </nav>

            <#nested>

            <footer class="text-center my-5">
                Fleet &copy; 2019 LinuxServer.io
            </footer>

        </body>

        <script type="text/javascript" src="/js/jquery-3.3.1.min.js"></script>
        <script type="text/javascript" src="/js/jquery.tablesorter.js"></script>
        <script type="text/javascript" src="/js/bootstrap.bundle.min.js"></script>
        <script type="text/javascript" src="/js/fleet.js"></script>

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

            <#if context='admin'>
            repositoryManager.init();
            synchronisationManager.init();
            </#if>

            <#if context='home'>
            imageListManager.init();
            </#if>

        </script>

    </html>

</#macro>