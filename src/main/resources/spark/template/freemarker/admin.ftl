<#import "./base.ftl" as base>

<@base.base title="Manage Repositories" context="admin">

    <div class="container mt-3">

        <div class="row">
            <div class="col-12 p-3">
                <h2>Manage Repositories</h2>
                <p>
                    Fleet only has access to the repositories against your Docker Hub account. All repositories will be
                    stored in the local database, but only those explicitly set to be synchronised will have their images
                    updated. You may update the status of each repository to be synchronised or skipped, as well as updating
                    the repository-level version mask for all images.
                </p>

                <div class="fleet-alert fleet-alert--info">
                    <i class="fas fa-info-circle text-info"></i> Any non-synchronised repositories will <i>not</i> be displayed on the main status page, nor will they appear in the API.
                </div>
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
                                    <input type="text" class="form-control form-control-sm version-mask" value="${repository.versionMask!""}" />
                                </td>
                                <td>
                                    <label class="switch">
                                        <input type="checkbox" class="sync-repository" <#if repository.syncEnabled>checked</#if> />
                                        <span class="slider round"></span>
                                    </label>
                                </td>
                            </tr>
                        </#list>

                    </tbody>
                </table>

            </div>
        </div>

        <div class="row">
            <div class="col-12 p-3">
                <h3>Synchronise</h3>
                <p>
                    This will trigger a synchronisation process and will update all images for all repositories.
                </p>
                <p>
                    <button type="button" id="force-sync" class="btn btn-warning"><i class="fas fa-sync-alt"></i> Force Synchronisation</button>
                </p>
                <div class="progress progress--sync" style="display: none">
                    <div class="progress--sync__bar progress-bar progress-bar-striped progress-bar-animated" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100"></div>
                </div>
                <div class="progress--sync__currentImage"></div>
            </div>
        </div>
        
    </div>

</@base.base>