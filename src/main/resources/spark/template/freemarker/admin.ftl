<#import "./base.ftl" as base>

<@base.base title="Manage Repositories">

    <div class="container container--white mt-3">

        <div class="row">
            <div class="col-12 p-3">
                <h2>Manage Repositories</h2>
                <p>
                    Fleet only has access to the repositories against your Docker Hub account. All repositories will be
                    stored in the local database, but only those explicitly set to be synchronised will have their images
                    updated.
                </p>
            </div>
            <div class="col-12 p-3">
                <h3>Synchronise</h3>
                <p>
                    This will trigger a synchronisation process and will update all images for all repositories.
                </p>
                <p>
                    <button type="button" class="btn btn-warning"><i class="fas fa-sync-alt"></i> Force Synchronisation</button>
                </p>
            </div>
        </div>

        <div class="row">
            <div class="col-12">

                <table class="table">
                    <thead>
                        <tr>
                            <th>Name</th>
                            <th>Version Mask</th>
                            <th>Synchronise</th>
                        </tr>
                    </thead>
                    <tbody>

                        <#list repositories as repository>
                            <tr data-repository-id="#{repository.id}">
                                <td>
                                    ${repository.name}
                                </td>
                                <td>
                                    <input type="text" class="form-control form-control-sm version-mask" value="${repository.versionMask!""}">
                                </td>
                                <td>
                                    <label class="switch">
                                        <input type="checkbox" class="sync-repository" <#if repository.syncEnabled>checked</#if> />
                                        <span class="slider round"></span>
                                    </div>
                                </td>
                            </tr>
                        </#list>

                    </tbody>
                </table>

            </div>
        </div>
    </div>

</@base.base>