<#--
 Copyright (c) 2020 LinuxServer.io
 
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
<#import "../../prebuilt/base.ftl"         as base />
<#import "../../prebuilt/fleet-title.ftl"  as title />

<#import "../../ui/components/modal.ftl"   as modal />
<#import "../../ui/elements/button.ftl"    as button />
<#import "../../ui/elements/table.ftl"     as table />
<#import "../../ui/form/input.ftl"         as input />
<#import "../../ui/layout/section.ftl"     as section />
<#import "../../ui/layout/container.ftl"   as container />

<@base.base title='Users | Admin' context="admin_users">

    <@section.section id="LoadedSchedules">
        <@container.container>

            <div class="columns is-multiline">
                <div class="column is-12">
                    <@title.title boldValue="Users" icon="users" subtitle="Add/remove or update system users" />
                </div>

                <div class="column is-12 has-margin-top">

                    <@table.table isFullWidth=true isHoverable=true isScrollable=true>
                        <thead>
                            <tr>
                                <th>Username</th>
                                <th>Role</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <#list users as user>
                                <tr data-user-key="${user.key}" data-user-name="${user.username}">
                                    <td class="is-vcentered">
                                        ${user.username}
                                    </td>
                                    <td class="is-vcentered" style="width: 300px">
                                        ${user.role}
                                    </td>
                                    <td class="is-vcentered has-text-right" style="width: 150px">
                                        <@button.buttons isGrouped=true isRightAligned=true>
                                            <@button.button size="small" colour="normal-colour" extraClasses="edit-password" modal="#EditUserPassword" title="Change password">
                                                <i class="fas fa-pencil-alt is-marginless"></i>
                                            </@button.button>
                                            <#if users?size &gt; 1 && user.username!=__AuthenticatedUser.name>
                                                <@button.button size="small" colour="danger" extraClasses="delete-user" modal="#ConfirmDeleteUser" title="Delete user">
                                                    <i class="fas fa-trash is-marginless"></i>
                                                </@button.button>
                                            </#if>
                                        </@button.buttons>
                                    </td>
                                </tr>
                            </#list>
                        </tbody>
                    </@table.table>

                </div>

                <div class="column is-full">

                    <h2 class="title is-5">New User</h2>

                    <form method="post" action="/admin/users?action=create" novalidate class="needs-validation">

                        <@input.text     id="NewUserName"     size="small" label="Username" icon="user" isInline=true isRequired=true />
                        <@input.password id="NewUserPassword" size="small" label="Password" icon="lock" isInline=true isRequired=true />

                        <@button.buttons isRightAligned=true>
                            <@button.submit id="CreateUser" colour="primary is-wide-mobile">
                                <i class="fas fa-plus"></i> Create new user
                            </@button.submit>
                        </@button.buttons>

                    </form>

                </div>
            </div>

        </@container.container>
    </@section.section>

    <@modal.modal id="ConfirmDeleteUser" title="Are you sure?" isDismissable=true extraClasses="has-text-centered">

        You are about to delete <strong><span id="UserNamePendingDeletion"></span></strong> from this app.

        <p class="has-margin-top">
            <i class="fas fa-exclamation-triangle has-text-danger"></i> <strong>This action will permanently delete this user.</strong>
        </p>

        <form method="post" action="/admin/users?action=delete">

            <input type="hidden" name="UserPendingDeletion" id="UserPendingDeletion" />

            <@button.buttons isRightAligned=true>

                <@button.button colour="light" extraClasses="is-modal-cancel is-wide-mobile">
                    Cancel
                </@button.button>

                <@button.submit id="DeleteUser" colour="danger is-wide-mobile">
                    <i class="fas fa-trash"></i> Delete
                </@button.submit>

            </@button.buttons>
        </form>

    </@modal.modal>

    <@modal.modal id="EditUserPassword" title="Update Password" isDismissable=true extraClasses="has-text-centered">

        Update the password for <strong><span id="UserNamePendingPasswordChange"></span></strong>.

        <form method="post" action="/admin/users?action=update">

            <@input.password id="UserPassword" />
            <input type="hidden" name="UserPendingPasswordChange" id="UserPendingPasswordChange" />

            <@button.buttons isRightAligned=true>

                <@button.button colour="light" extraClasses="is-modal-cancel is-wide-mobile">
                    Cancel
                </@button.button>

                <@button.submit id="DeleteUser" colour="primary is-wide-mobile">
                    <i class="fas fa-save"></i> Save Changes
                </@button.submit>

            </@button.buttons>
        </form>

    </@modal.modal>


</@base.base>
