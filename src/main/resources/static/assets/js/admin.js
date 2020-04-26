/*
 * Copyright (c)  2019 LinuxServer.io
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

var Admin = (function($) {

    'use strict';

    // Repositories
    var $SubmitNewRepository;
    var $NewRepositoryName;
    var $RepositoryPendingDeletion;
    var $DeleteRepository;

    // Image Edit
    var $ImageExternalUrls;
    var $AddNewExternalUrl;
    var $TrackNewBranch;
    var $NewTrackedBranch;
    var $ImageKey;

    // Image Template
    var $ImageTemplateTabContent;
    var $ImageTemplatePorts;
    var $AddNewPort;
    var $ImageTemplateVolumes;
    var $AddNewVolume;
    var $ImageTemplateEnv;
    var $AddNewEnv;
    var $ImageTemplateDevices;
    var $AddNewDevice;

    // Users
    var $UserNamePendingDeletion;
    var $UserPendingDeletion;
    var $UserPendingPasswordChange;
    var $UserNamePendingPasswordChange;

    var reload = function() {
        window.location.reload();
    };

    var cleanEmpty = function(val) {
        return (typeof val === 'undefined' || $.trim(val).length === 0) ? null : val;
    };

    var runSchedule = function(trigger) {

        Ajax.put('/internalapi/schedule', { 'scheduleKey': trigger.data('schedule-key') }, function() {
            Notifications.makeNotification('Schedule run submitted successfully.', 'success');
        }, trigger);
    };

    var deleteRepository = function(trigger) {
        Ajax.del('/internalapi/repository?repositoryKey=' + trigger.data('repository-key'), reload, trigger);
    };

    var syncRepository = function(trigger) {

        Ajax.put('/internalapi/repository/sync', { 'repositoryKey': trigger.data('repository-key') }, function() {
            Notifications.makeNotification('Sync request submitted.', 'success');
        }, trigger);
    };

    var addRepository = function(repositoryName) {

        var trimmedName = $.trim(repositoryName);
        if (trimmedName.length > 0) {

            Notifications.makeNotification('Verifying repository "' + trimmedName + '" and fetching images. Please wait...', 'info', '10000');

            Ajax.postJson('/internalapi/repository', { 'repositoryName': trimmedName }, reload, $SubmitNewRepository, function() {
                $('#NewRepositoryName').val('');
            });
        }
    };

    var updateRepositorySpec = function($row) {

        var data = {
            'repositoryKey': $row.data('repository-key'),
            'syncEnabled':   $row.find('.editable-repository-enabled').find('input[type="checkbox"]').is(':checked'),
            'versionMask':   cleanEmpty($row.find('.editable-repository-version-mask').find('.switchable.field input[type="text"]').val())
        };

        Ajax.putJson('/internalapi/repository', data, function(data) {
            Notifications.makeNotification(data.name + ' updated', 'success');
        });
    };

    var syncImage = function(trigger) {

        Ajax.put('/internalapi/image/sync', { 'imageKey': trigger.data('image-key') }, function() {
            Notifications.makeNotification('Sync request submitted.', 'success');
        }, trigger);
    };

    var updateImageSpec = function($row) {

        var data = {
            'imageKey':    $row.data('image-key'),
            'syncEnabled': $row.find('.editable-image-sync-enabled').find('input[type="checkbox"]').is(':checked'),
            'versionMask': cleanEmpty($row.find('.editable-image-version-mask').find('.switchable.field input[type="text"]').val()),
            'stable':      $row.find('.editable-image-stable').find('input[type="checkbox"]').is(':checked'),
            'hidden':      $row.find('.editable-image-hidden').find('input[type="checkbox"]').is(':checked'),
            'deprecated':  $row.find('.editable-image-deprecated').find('input[type="checkbox"]').is(':checked')
        };

        Ajax.putJson('/internalapi/image', data, function() {});
    };

    var trackNewBranch = function(branchName, imageKey) {
        Ajax.put('/internalapi/image/track', { 'imageKey': imageKey, 'branchName': branchName }, reload, $TrackNewBranch);
    };

    var removeTrackedBranch = function(branchName, imageKey) {
        Ajax.del('/internalapi/image/track?imageKey=' + imageKey +'&branchName=' + branchName, reload);
    };

    var makeInput = function(name, type='text', required=false) {
        return '<input type="' + type + '" class="input is-small" name="' + name + '"' + (required ? 'required' : '') + ' />'
    };

    var makeSelect = function(name, values=[]) {

        var options = '';
        values.forEach(function(value) {
           options += '<option value="' + value + '">' + value + '</option>';
        });

        return (
            '<div class="select is-small"><select name="' + name + '">' +
                options +
            '</select></div>'
        );
    };

    var makeButton = function(value, classes, colour) {
        return '<button class="button is-small is-' + colour + ' ' + classes + '">' + value + '</button>';
    };

    var addPortRow = function() {

        $ImageTemplatePorts.find('tbody').append($(
           '<tr>' +
                '<td>' +
                    makeInput('imageTemplatePort', 'number', true) +
                '</td>' +
                '<td>' +
                    makeSelect('imageTemplatePortProtocol', ['tcp', 'udp']) +
                '</td>' +
                '<td>' +
                    makeInput('imageTemplatePortDescription') +
                '</td>' +
                '<td>' +
                    '<div class="buttons is-right">' +
                        makeButton('<i class="fas fa-trash is-marginless"></i>', 'remove-image-template-item', 'danger') +
                    '</div>' +
                '</td>' +
           '</tr>'
        ));
    };

    var addExternalUrlRow = function() {

        $ImageExternalUrls.find('tbody').append($(
            '<tr>' +
                '<td>' +
                    makeSelect('imageExternalUrlType', externalUrlTypes) +
                '</td>' +
                '<td>' +
                    '<input type="hidden" value="-1" name="imageExternalUrlKey" />' +
                    makeInput('imageExternalUrlName', 'text', true) +
                '</td>' +
                '<td>' +
                    makeInput('imageExternalUrlPath', 'text', true) +
                '</td>' +
                '<td>' +
                    '<div class="buttons is-right">' +
                        makeButton('<i class="fas fa-trash is-marginless"></i>', 'remove-image-external-url', 'danger') +
                    '</div>' +
                '</td>' +
            '</tr>'
        ));
    };

    var addVolumeRow = function() {

        $ImageTemplateVolumes.find('tbody').append($(
            '<tr>' +
                '<td>' +
                    makeInput('imageTemplateVolume', 'text', true) +
                '</td>' +
                '<td>' +
                    makeSelect('imageTemplateVolumeReadonly', ['read-write', 'readonly']) +
                '</td>' +
                '<td>' +
                    makeInput('imageTemplateVolumeDescription') +
                '</td>' +
                '<td>' +
                    '<div class="buttons is-right">' +
                        makeButton('<i class="fas fa-trash is-marginless"></i>', 'remove-image-template-item', 'danger') +
                    '</div>' +
                '</td>' +
            '</tr>'
        ));
    };

    var addEnvRow = function() {

        $ImageTemplateEnv.find('tbody').append($(
            '<tr>' +
                '<td>' +
                    makeInput('imageTemplateEnv', 'text', true) +
                '</td>' +
                '<td>' +
                    makeInput('imageTemplateEnvDescription') +
                '</td>' +
                '<td>' +
                    '<div class="buttons is-right">' +
                        makeButton('<i class="fas fa-trash is-marginless"></i>', 'remove-image-template-item', 'danger') +
                    '</div>' +
                '</td>' +
            '</tr>'
        ));
    };

    var addDeviceRow = function() {

        $ImageTemplateDevices.find('tbody').append($(
            '<tr>' +
                '<td>' +
                    makeInput('imageTemplateDevice', 'text', true) +
                '</td>' +
                '<td>' +
                    makeInput('imageTemplateDeviceDescription') +
                '</td>' +
                '<td>' +
                    '<div class="buttons is-right">' +
                        makeButton('<i class="fas fa-trash is-marginless"></i>', 'remove-image-template-item', 'danger') +
                    '</div>' +
                '</td>' +
            '</tr>'
        ));
    };

    var init = function() {

        $SubmitNewRepository           = $('#SubmitNewRepository');
        $NewRepositoryName             = $('#NewRepositoryName');
        $TrackNewBranch                = $('#TrackNewBranch');
        $NewTrackedBranch              = $('#NewTrackedBranch');
        $RepositoryPendingDeletion     = $('#RepositoryPendingDeletion');
        $DeleteRepository              = $('#DeleteRepository');
        $ImageKey                      = $('#ImageKey');
        $ImageTemplateTabContent       = $('#ImageTemplateTabContent');
        $ImageTemplatePorts            = $('#ImageTemplatePorts');
        $AddNewPort                    = $('#AddNewPort');
        $ImageTemplateVolumes          = $('#ImageTemplateVolumes');
        $AddNewVolume                  = $('#AddNewVolume');
        $ImageTemplateEnv              = $('#ImageTemplateEnv');
        $AddNewEnv                     = $('#AddNewEnv');
        $ImageTemplateDevices          = $('#ImageTemplateDevices');
        $AddNewDevice                  = $('#AddNewDevice');
        $ImageExternalUrls             = $('#ImageExternalUrls');
        $AddNewExternalUrl             = $('#AddNewExternalUrl');
        $UserPendingDeletion           = $('#UserPendingDeletion');
        $UserNamePendingDeletion       = $('#UserNamePendingDeletion');
        $UserNamePendingPasswordChange = $('#UserNamePendingPasswordChange');
        $UserPendingPasswordChange     = $('#UserPendingPasswordChange');

        $SubmitNewRepository.on('click', function() {
            addRepository($NewRepositoryName.val());
        });

        $('.force-schedule-run').on('click', function() {
            runSchedule($(this));
        });

        $('.sync-repository').on('click', function() {
           syncRepository($(this));
        });

        $('.update-repository-trigger').on('click', function() {
            updateRepositorySpec($(this).parents('.repository-row'));
        });

        $DeleteRepository.on('click', function() {
            deleteRepository($(this));
        });

        $('.delete-repository').on('click', function() {

            var $trigger = $(this);

            $RepositoryPendingDeletion.text($trigger.data('repository-name'));
            $DeleteRepository.data('repository-key', $trigger.data('repository-key'))
        });

        $('.delete-user').on('click', function() {

            var $triggerParent = $(this).parents('tr');

            $UserPendingDeletion.val($triggerParent.data('user-key'));
            $UserNamePendingDeletion.text($triggerParent.data('user-name'));
        });

        $('.edit-password').on('click', function() {

            var $triggerParent = $(this).parents('tr');

            $UserPendingPasswordChange.val($triggerParent.data('user-key'));
            $UserNamePendingPasswordChange.text($triggerParent.data('user-name'));
        });

        $('.update-image-trigger').on('click', function() {
            updateImageSpec($(this).parents('.image-row'));
        });

        $('.sync-image').on('click', function() {
            syncImage($(this));
        });

        $TrackNewBranch.on('click', function() {

            var branchName = $.trim($NewTrackedBranch.val());
            if (branchName.length > 0) {
                trackNewBranch(branchName, $ImageKey.val());
            }
        });

        $('.remove-tag-branch').on('click', function() {

            var branchName = $(this).parents('.tracked-branch').data('branch-name');
            removeTrackedBranch(branchName, $ImageKey.val());
        });

        $AddNewExternalUrl.on('click', addExternalUrlRow);

        $ImageExternalUrls.on('click', '.remove-image-external-url', function() {
            $(this).parents('tr').remove();
        });

        $AddNewPort.on('click',   addPortRow);
        $AddNewVolume.on('click', addVolumeRow);
        $AddNewEnv.on('click',    addEnvRow);
        $AddNewDevice.on('click', addDeviceRow);

        $ImageTemplateTabContent.on('click', '.remove-image-template-item', function() {
            $(this).parents('tr').remove();
        });
    };

    return {
        init: init
    }

}(jQuery));
