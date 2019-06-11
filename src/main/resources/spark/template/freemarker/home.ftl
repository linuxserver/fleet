<#import "./base.ftl" as base>

<@base.base title="Image Status" context="home">

    <#if populatedRepositories?size &gt; 0>

        <div class="container">
            <div class="row">
                <div class="col-12">
                    <ul class="nav nav--repositories" id="all-repositories-tablist" role="tablist">
                        <#list populatedRepositories as populatedRepository>
                            <li class="nav-item">
                                <a class="nav-link <#if populatedRepository?index == 0>active</#if>" data-toggle="tab" role="tab" aria-controls="repository_#{populatedRepository.repository.id}" id="repository-tab_#{populatedRepository.repository.id}" href="#repository_#{populatedRepository.repository.id}">${populatedRepository.repository.name}</a>
                            </li>
                        </#list>
                    </ul>
                </div>
            </div>
        </div>

        <div class="tab-content" id="repository-tab-content">
            <#list populatedRepositories as populatedRepository>

                <div class="tab-pane<#if populatedRepository?index == 0> show active</#if>" id="repository_#{populatedRepository.repository.id}" role="tabpanel" aria-labelledby="repository-tab_#{populatedRepository.repository.id}">
                    <div class="container">

                        <#if populatedRepository.everyImageStable>
                            <div class="row my-3">
                                <div class="col-12">
                                    <div class="fleet-alert fleet-alert--success">
                                        <i class="fas fa-check text-success"></i> No issues reported
                                    </div>
                                </div>
                            </div>
                        <#else>
                            <div class="row my-3">
                                <div class="col-12">
                                    <div class="fleet-alert fleet-alert--warning">
                                        <i class="fas fa-exclamation-triangle text-warning"></i> Some instability reported
                                    </div>
                                </div>
                            </div>
                        </#if>

                        <#if populatedRepository.images?size &gt; 0>
                            <div class="row">
                                <div class="col-md-2"></div>
                                <div class="col-md-8">
                                    <div class="input-group input-group-sm mb-3 mt-3">
                                        <div class="input-group-prepend">
                                            <span class="input-group-text" id="searchLabel_#{populatedRepository.repository.id}"><i class="fas fa-search"></i> Search ${populatedRepository.repository.name}</span>
                                        </div>
                                        <input type="text" class="form-control image-search" id="search_#{populatedRepository.repository.id}" data-repository-id="#{populatedRepository.repository.id}" aria-describedby="searchLabel_#{populatedRepository.repository.id}">
                                    </div>
                                </div>
                                <div class="col-md-2"></div>
                                <div class="col-12">

                                    <div class="table-responsive" id="#{populatedRepository.repository.id}_images">
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
                                                        <tr class="<#if image.hidden>hidden-image</#if><#if image.deprecated>deprecated-image</#if>" data-image-id="#{image.id}" data-image-name="${image.name}">
                                                            <td class="image-name">
                                                                <span class="image-name--repository">${populatedRepository.repository.name} /</span> <a target="_blank" href="https://hub.docker.com/r/${populatedRepository.repository.name}/${image.name}"><span class="image-name__image">${image.name}</span></a>
                                                                <#if image.deprecated>
                                                                <span class="deprecation-message" title="This image has been deprecated: ${image.deprecationReason!""}"><i class="fas fa-exclamation-circle"></i></span>
                                                                </#if>
                                                            </td>
                                                            <td>
                                                                <#if image.maskedVersion?has_content>
                                                                    <code title="${image.rawVersion}">${image.maskedVersion}</code>
                                                                </#if>
                                                            </td>
                                                            <td>
                                                                <span class="number">${image.pullCount}</span>
                                                            </td>
                                                            <td class="text-center image-status">

                                                                <#if image.unstable>
                                                                    <i class="fas fa-exclamation-triangle text-warning" title="Potentially unstable"></i>
                                                                <#else>
                                                                    <i class="fas fa-check text-success" title="No issues reported"></i>
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
                                                                                <button type="button" class="image--show dropdown-item btn-clickable"><i class="fas fa-eye"></i> Show in list</button>
                                                                            <#else>
                                                                                <button type="button" class="image--hide dropdown-item btn-clickable"><i class="fas fa-eye-slash"></i> Hide from list</button>
                                                                            </#if>
                                                                            <#if image.unstable>
                                                                                <button type="button" class="image--mark-stable dropdown-item btn-clickable"><i class="fas fa-check"></i> Mark as stable</button>
                                                                            <#else>
                                                                                <button type="button" class="image--mark-unstable dropdown-item btn-clickable"><i class="fas fa-exclamation-triangle"></i> Mark as unstable</button>
                                                                            </#if>
                                                                            <button type="button" class="dropdown-item btn-clickable" data-toggle="modal" data-target="#update-image-version-mask"><i class="fas fa-mask"></i> Apply version mask</button>
                                                                            <#if image.deprecated>
                                                                                <button type="button" class="image--remove-deprecation-notice dropdown-item btn-clickable"><i class="fas fa-thumbs-up"></i> Remove deprecation notice</button>
                                                                            <#else>
                                                                                <button id="deprecate-image_#{image.id}" type="button" class="dropdown-item btn-clickable" data-toggle="modal" data-target="#update-image-deprecation"><i class="fas fa-exclamation-circle"></i> Mark as deprecated</button>
                                                                            </#if>
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
                        <#else>
                            <div class="container">
                                <div class="row">
                                    <div class="col-12 text-center p-3">
                                        No images!
                                    </div>
                                </div>
                            </div>
                        </#if>
                    </div>
                </div>
            </#list>
        </div>

        <#if __AUTHENTICATED_USER?has_content>

            <div class="modal" id="update-image-version-mask" tabindex="-1" role="dialog">
                <div class="modal-dialog modal-dialog-centered" role="document">
                    <div class="modal-content">
                        <div class="modal-header" id="selected-mask-image-name"></div>
                        <div class="modal-body">
                            <div class="input-group">
                                <input type="text" class="form-control version-mask" id="image-version-mask" />
                                <div class="input-group-append">
                                    <button class="btn btn-secondary" type="button" id="submit-version-mask-change">Update</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="modal" id="update-image-deprecation" tabindex="-1" role="dialog">
                <div class="modal-dialog modal-dialog-centered" role="document">
                    <div class="modal-content">
                        <div class="modal-header" id="selected-deprecation-image-name"></div>
                        <div class="modal-body">
                            <p>
                                Provide a reason for the deprecation of this image.
                            </p>
                            <div class="input-group">
                                <textarea class="form-control" id="image-deprecation-reason" maxlength="255" rows="4"></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button class="btn btn-secondary" type="button" id="submit-deprecation-change">Save</button>
                        </div>
                    </div>
                </div>
            </div>

        </#if>

    </#if>

</@base.base>