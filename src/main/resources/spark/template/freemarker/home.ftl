<#import "./base.ftl" as base>

<@base.base title="Image Status">

    <#if populatedRepositories?size &gt; 0>
        <#list populatedRepositories as populatedRepository>

            <div class="container container--white mt-3">

                <div class="row">
                    <div class="col-12 p-3">
                        <h2><a href="https://hub.docker.com/r/${populatedRepository.repository.name}">${populatedRepository.repository.name}</a></h2>
                    </div>
                </div>

                <#if populatedRepository.everyImageStable>
                    <div class="row">
                        <div class="col-12 p-3">
                            <div class="alert alert-success">
                                <i class="fas fa-check"></i>&nbsp; No issues reported
                            </div>
                        </div>
                    </div>
                <#else>
                    <div class="row">
                        <div class="col-12 p-3">
                            <div class="alert alert-warning">
                                <i class="fas fa-exclamation-triangle"></i>&nbsp; Some instability reported
                            </div>
                        </div>
                    </div>
                </#if>

                <div class="row">
                    <div class="col-12">

                        <div class="table-responsive">
                            <table class="table table--sortable">
                                <thead>
                                    <tr>
                                        <th>Name</th>
                                        <th>Version</th>
                                        <th class="sorter-pullCount">Pull Count</th>
                                        <th class="text-center">Status</th>
                                        <#if __AUTHENTICATED_USER?has_content>
                                            <th>Admin</th>
                                        </#if>
                                    </tr>
                                </thead>
                                <tbody>

                                    <#list populatedRepository.images as image>
                                        <#if !image.hidden || __AUTHENTICATED_USER?has_content>
                                            <tr <#if image.hidden>class="hidden-image"</#if> data-image-id="#{image.id}">
                                                <td class="image-name">
                                                    <a target="_blank" href="https://hub.docker.com/r/${populatedRepository.repository.name}/${image.name}">${image.name}</a>
                                                </td>
                                                <td>
                                                    <#if image.version?has_content>
                                                        <code>${image.version}</code>
                                                    </#if>
                                                </td>
                                                <td>
                                                    <span class="number">${image.pullCount}</span>
                                                </td>
                                                <td class="text-center image-status">

                                                    <#if image.unstable>
                                                        <i class="fas fa-exclamation-triangle text-warning" title="Potentially unstable"></i>
                                                    <#else>
                                                        <i class="fas fa-check-circle text-success" title="No issues reported"></i>
                                                    </#if>

                                                </td>
                                                <#if __AUTHENTICATED_USER?has_content>
                                                    <td class="admin-actions">
                                                        <div class="dropdown">
                                                            <button class="btn btn-info btn-xsm dropdown-toggle" type="button" id="admin-actions_#{populatedRepository.repository.id}" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                                                Actions
                                                            </button>
                                                            <div class="dropdown-menu" aria-labelledby="admin-actions_#{populatedRepository.repository.id}">

                                                                <#if image.hidden>
                                                                    <button type="button" class="image--show dropdown-item btn-clickable">Show in list</button>
                                                                <#else>
                                                                    <button type="button" class="image--hide dropdown-item btn-clickable">Hide from list</button>
                                                                </#if>
                                                                <#if image.unstable>
                                                                    <button type="button" class="image--mark-stable dropdown-item btn-clickable">Mark as stable</button>
                                                                <#else>
                                                                    <button type="button" class="image--mark-unstable dropdown-item btn-clickable">Mark as unstable</button>
                                                                </#if>
                                                                <button type="button" class="dropdown-item btn-clickable">Apply version mask</button>
                                                            </div>
                                                        </div>
                                                    </td>
                                                </#if>
                                            </tr>
                                        </#if>
                                    </#list>

                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

        </#list>
    <#else>

        <div class="container">
            <div class="row">
                <div class="col-12 text-center p-3">
                    No images!
                </div>
            </div>
        </div>

    </#if>

</@base.base>