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

                $('#SubmitNewRepository').prop('disabled', false).removeClass('is-loading');
                $('#NewRepositoryName').val('');
            });
        }
    };

    var init = function() {

        $('#SubmitNewRepository').on('click', function() {

            var $button        = $(this);
            var repositoryName = $('#NewRepositoryName').val();

            $button.prop('disabled', true).addClass('is-loading');
            addRepository(repositoryName);
        })
    };

    return {
        init: init
    }

}(jQuery));
