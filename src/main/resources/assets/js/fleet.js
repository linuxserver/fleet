/*
 * Copyright (c) 2019 LinuxServer.io
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

var ajaxManager = (function($) {

    var handleError = function(jqXHR, textStatus, handleError) {

        var error = JSON.parse(jqXHR.responseText);
        console.log(error);
    };

    var call = function(param, onDone) {
        return $.ajax(param).done(onDone).fail(handleError);
    };

    return {
        call: call
    };

}(jQuery));

var repositoryManager = (function($) {

    var updateSyncSettings = function() {

        var syncSwitch = $(this);
        var row = syncSwitch.parents('tr');
        var repositoryId = row.data('repository-id');
        var syncEnabled = syncSwitch.is(':checked');

        var request = {
            url: '/admin/manageRepository',
            method: 'POST',
            data: {
                repositoryId: repositoryId,
                action: syncEnabled ? 'ENABLE_SYNC' : 'DISABLE_SYNC'
            }
        };

        ajaxManager.call(request, function(data) { console.log("Updated") });
    };

    var updateVersionMask = function() {

        var versionMask = $(this);
        var row = versionMask.parents('tr');
        var repositoryId = row.data('repository-id');
        var maskValue = versionMask.val();

        var request = {
            url: '/admin/manageRepository',
            method: 'POST',
            data: {
                repositoryId: repositoryId,
                action: 'MASK',
                versionMask: maskValue
            }
        };

        ajaxManager.call(request, function(data) { console.log("Updated") });
    };

    var init = function() {

        $('.sync-repository').on('change', updateSyncSettings);
        $('.version-mask').on('blur', updateVersionMask);
    };

    return {
        init: init
    };

}(jQuery));

var imageListManager = (function($) {

    var showImage = function() {

        var option  = $(this);
        var row     = getImageRow(option);
        var imageId = getImageId(row);
        var request = buildRequest('SHOW', imageId)

        ajaxManager.call(request, function(data) {

            row.removeClass('hidden-image');
            option.replaceWith(createHideButton());
        });
    };

    var hideImage = function() {

        var option  = $(this);
        var row     = getImageRow(option);
        var imageId = getImageId(row);
        var request = buildRequest('HIDE', imageId)

        var call = ajaxManager.call(request, function(data) {

            row.addClass('hidden-image');
            option.replaceWith(createShowButton());
        });
    };

    var markImageUnstable = function() {

        var option  = $(this);
        var row     = getImageRow(option);
        var imageId = getImageId(row);
        var request = buildRequest('UNSTABLE', imageId)

        var call = ajaxManager.call(request, function(data) {

            row.find('.image-status').find('i.fas').replaceWith(createUnstableIcon());
            option.replaceWith(createStableButton());
        });

    };

    var markImageStable = function() {

        var option  = $(this);
        var row     = getImageRow(option);
        var imageId = getImageId(row);
        var request = buildRequest('STABLE', imageId)

        var call = ajaxManager.call(request, function(data) {

            row.find('.image-status').find('i.fas').replaceWith(createStableIcon());
            option.replaceWith(createUnstableButton());
        });
    };

    var buildRequest = function(action, imageId) {

        return  {
           url: '/admin/manageImage',
           method: 'POST',
           data: {
               action: action,
               imageId: imageId
           }
       };
    };

    var createHideButton = function() {
        return $('<button type="button" class="image--hide dropdown-item btn-clickable">Hide from list</button>');
    };

    var createShowButton = function() {
        return $('<button type="button" class="image--show dropdown-item btn-clickable">Show in list</button>');
    };

    var createStableButton = function() {
        return $('<button type="button" class="image--mark-stable dropdown-item btn-clickable">Mark as stable</button>');
    };

    var createUnstableButton = function() {
        return $('<button type="button" class="image--mark-unstable dropdown-item btn-clickable">Mark as unstable</button>');
    };

    var createUnstableIcon = function() {
        return $('<i class="fas fa-exclamation-triangle text-warning" title="Potentially unstable"></i>');
    };

    var createStableIcon = function() {
        return $('<i class="fas fa-check-circle text-success" title="No issues reported"></i>');
    };

    var getImageRow = function(item) {
        return item.parents('tr');
    };

    var getImageId = function(row) {
        return parseInt(row.data('image-id'));
    };

    var init = function() {

        $('.admin-actions').on('click', '.image--show', showImage);
        $('.admin-actions').on('click', '.image--hide', hideImage);
        $('.admin-actions').on('click', '.image--mark-stable', markImageStable);
        $('.admin-actions').on('click', '.image--mark-unstable', markImageUnstable);
    };

    return {
        init: init
    }

}(jQuery));

imageListManager.init();
repositoryManager.init();