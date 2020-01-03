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

var adminManager = (function($) {

    var toggleButtonLoadingState = function(button) {
        button.prop('disabled', !button.prop('disabled')).toggleClass('is-loading');
    };

    var runSchedule = function(trigger) {

        var scheduleKey = trigger.data('schedule-key');

        var request = {

            url: '/internalapi/schedule',
            method: 'put',
            data: {
                'scheduleKey': scheduleKey
            }
        };

        toggleButtonLoadingState(trigger);
        ajaxManager.call(request, function() {

            notificationManager.makeNotification('Schedule run submitted successfully.', 'success');
            toggleButtonLoadingState(trigger);

        }, function() {
            toggleButtonLoadingState(trigger);
        });
    };

    var deleteRepository = function(trigger) {

        var repositoryKey = trigger.data('repository-key');

        var request = {

            url: '/internalapi/repository?repositoryKey=' + repositoryKey,
            method: 'delete'
        };

        toggleButtonLoadingState(trigger);
        ajaxManager.call(request, function() { window.location.reload(); }, function() {
            toggleButtonLoadingState(trigger);
        });
    };

    var syncRepository = function(trigger) {

        var repositoryKey = trigger.data('repository-key');

        var request = {

            url: '/internalapi/repository/sync',
            method: 'put',
            data: {
                'repositoryKey': repositoryKey
            }
        };

        toggleButtonLoadingState(trigger);
        ajaxManager.call(request, function() {

            notificationManager.makeNotification('Sync request submitted.', 'success');
            toggleButtonLoadingState(trigger);

        }, function() {
            toggleButtonLoadingState(trigger);
        });
    };

    var addRepository = function(repositoryName) {

        var trimmedName = $.trim(repositoryName);
        if (trimmedName.length > 0) {

            var request = {

                url: '/internalapi/repository',
                method: 'post',
                contentType: 'application/json',
                dataType: 'json',
                data: JSON.stringify({
                    'repositoryName': trimmedName
                })
            };

            notificationManager.makeNotification('Verifying repository "' + trimmedName + '" and fetching images. Please wait...');

            ajaxManager.call(request, function() { window.location.reload(); }, function() {

                toggleButtonLoadingState($('#SubmitNewRepository'));
                $('#NewRepositoryName').val('');
            });
        }
    };

    var updateRepositorySpec = function($row) {

        var repositoryKey = $row.data('repository-key');
        var syncEnabled   = $row.find('.editable-repository-enabled').find('input[type="checkbox"]').is(':checked');
        var versionMask   = cleanEmpty($row.find('.editable-repository-version-mask').find('.switchable.field input[type="text"]').val());

        var request = {

            url: '/internalapi/repository',
            method: 'put',
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify({
                'repositoryKey': repositoryKey,
                'syncEnabled': syncEnabled,
                'versionMask': versionMask
            })
        };

        ajaxManager.call(request, function(data) {
            notificationManager.makeNotification(data.name + ' updated', 'success');
        });
    };

    var syncImage = function(trigger) {

        var imageKey = trigger.data('image-key');

        var request = {

            url: '/internalapi/image/sync',
            method: 'put',
            data: {
                'imageKey': imageKey
            }
        };

        toggleButtonLoadingState(trigger);
        ajaxManager.call(request, function() {

            notificationManager.makeNotification('Sync request submitted.', 'success');
            toggleButtonLoadingState(trigger);

        }, function() {
            toggleButtonLoadingState(trigger);
        });
    };

    var updateImageSpec = function($row) {

        var imageKey     = $row.data('image-key');
        var syncEnabled  = $row.find('.editable-image-sync-enabled').find('input[type="checkbox"]').is(':checked');
        var stable       = $row.find('.editable-image-stable').find('input[type="checkbox"]').is(':checked');
        var hidden       = $row.find('.editable-image-hidden').find('input[type="checkbox"]').is(':checked');
        var deprecated   = $row.find('.editable-image-deprecated').find('input[type="checkbox"]').is(':checked');
        var versionMask  = cleanEmpty($row.find('.editable-image-version-mask').find('.switchable.field input[type="text"]').val());

        var request = {

            url: '/internalapi/image',
            method: 'put',
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify({
                'imageKey': imageKey,
                'syncEnabled': syncEnabled,
                'versionMask': versionMask,
                'stable': stable,
                'hidden': hidden,
                'deprecated': deprecated
            })
        };

        ajaxManager.call(request, function() {});
    };

    var cleanEmpty = function(val) {
        return (typeof val === 'undefined' || $.trim(val).length === 0) ? null : val;
    };

    var init = function() {

        $('#SubmitNewRepository').on('click', function() {

            var $button        = $(this);
            var repositoryName = $('#NewRepositoryName').val();

            toggleButtonLoadingState($button);
            addRepository(repositoryName);
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

        $('#DeleteRepository').on('click', function() {
            deleteRepository($(this));
        });

        $('.delete-repository').on('click', function() {

            var $trigger = $(this);

            $('#RepositoryPendingDeletion').text($trigger.data('repository-name'));
            $('#DeleteRepository').data('repository-key', $trigger.data('repository-key'))
        });

        $('.update-image-trigger').on('click', function() {
            updateImageSpec($(this).parents('.image-row'));
        });

        $('.sync-image').on('click', function() {
            syncImage($(this));
        });
    };

    return {
        init: init
    }

}(jQuery));
