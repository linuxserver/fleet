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

            url: '/internalApi/schedule',
            method: 'put',
            data: {
                'scheduleKey': scheduleKey
            }
        };

        toggleButtonLoadingState(trigger);
        ajaxManager.call(request, function() {

            notificationManager.makeNotification('Schedule run submitted successfully. The "Last Run" value will be updated once the task has completed.', 'success');
            toggleButtonLoadingState(trigger);

        }, function() {
            toggleButtonLoadingState(trigger);
        });
    };

    var addRepository = function(repositoryName) {

        var trimmedName = $.trim(repositoryName);
        if (trimmedName.length > 0) {

            var request = {

                url: '/internalApi/repository',
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
    };

    return {
        init: init
    }

}(jQuery));
