/*
 * Copyright (c) 2019 Wallett
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

var notificationManager = (function($) {

    var makeNotification = function(message, level='info', delay) {

        var notification = $(
            '<div class="notification is-' + level + '">' +
                '<button class="delete"></button>' +
                message +
            '</div>'
        );

        $('#Notifications').append(notification);
        notification.delay((typeof delay === 'undefined' ? 3000 : delay)).fadeOut(2000, function() {
            $(this).remove();
        });
    };

    return {
        makeNotification: makeNotification
    }

}(jQuery));

var ajaxManager = (function($) {

    var handleError = function(jqXHR, onError) {

        if (jqXHR.status === 403) {
            notificationManager.makeNotification('Permission denied', 'danger');
        } else {
            notificationManager.makeNotification(jqXHR.responseText, 'danger');
        }

        if (onError) {
            onError();
        }
    };

    var call = function(param, onDone, onError) {
        return $.ajax(param).done(onDone).fail(function(jqXHR) { handleError(jqXHR, onError); });
    };

    return {
        call: call
    };

}(jQuery));

var formValidationManager = (function($) {

    var setValid = function(element) {
        element.classList.remove('is-danger');
    };

    var setInvalid = function(element) {
        element.classList.add('is-danger');
    };

    var comparePasswords = function(password, verifyPassword) {

        if (password.value !== verifyPassword.value) {

            verifyPassword.setCustomValidity('Mismatch');
            setInvalid(verifyPassword);

        } else {

            verifyPassword.setCustomValidity('');
            setValid(verifyPassword);
        }
    };

    var initPasswordVerification = function() {

        var verifyPassword = document.getElementById('password-confirm');
        if (typeof verifyPassword !== 'undefined' && null !== verifyPassword) {

            var password = document.getElementById('password');

            password.addEventListener('input', function(event) {
                comparePasswords(password, verifyPassword);
            }, false);

            verifyPassword.addEventListener('input', function(event) {
                comparePasswords(password, verifyPassword);
            }, false);
        }
    };

    var initFormValidation = function() {

        var forms = document.getElementsByClassName('needs-validation');
        var validation = Array.prototype.filter.call(forms, function(form) {

            form.addEventListener('submit', function(event) {

                if (form.checkValidity() === false) {

                    var formElements = form.getElementsByClassName('input');
                    Array.prototype.filter.call(formElements, function(element) {

                        if (!element.validity.valid) {
                            setInvalid(element);
                        } else {
                            setValid(element)
                        }
                    });

                    event.preventDefault();
                    event.stopPropagation();
                }

                form.classList.add('was-validated');

            }, false);
        });
    };

    var init = function() {

        initFormValidation();
        initPasswordVerification();
    };

    return {
        init: init
    }

}(jQuery));

var appManager = (function($) {

    var initMenu = function() {

        $(".navbar-burger").on('click', function() {

            $(".navbar-burger").toggleClass("is-active");
            $(".navbar-menu").toggleClass("is-active");
        });
    };

    var initRepositorySwitcher = function() {

        $('#RepositorySelection').on('change', function() {

            var selectedRepoKey = $(this).find('option:selected').val();
            window.location.href = '/?key=' + selectedRepoKey;
        });
    };

    var initSwitchables = function() {

        var $body = $('body');

        $body.on('click', '.has-switchable .switchable.plaintext', function() {
            $(this).parents('.has-switchable').addClass('is-active');
        });

        $body.on('click', '.has-switchable .switchable.field .cancel-switchable', function() {

            var $parent = $(this).parents('.has-switchable');
            var $editableField = $parent.find('.switchable.field').find('.input');
            $editableField.val($editableField.data('original-value'));
            $parent.removeClass('is-active');
        });

        $body.on('click', '.has-switchable .switchable.field .is-accept-switchable', function() {
            $(this).parents('.has-switchable').removeClass('is-active');
        });
    };

    var initDropdowns = function() {

        $('.dropdown-trigger').on('click', function() {
            $(this).parents('.dropdown').toggleClass('is-active');
        });
    };

    var initNotifications = function() {

        $('#Notifications').on('click', '.delete', function() {
            $(this).parents('.notification').remove();
        });
    };

    var init = function() {

        initRepositorySwitcher();
        initSwitchables();
        initDropdowns();
        initNotifications();
        initMenu();
    };

    return {
        init: init
    }

}(jQuery));

var imageSearchManager = (function($) {

    var performSearch = function() {

        var $searchBox      = $(this);
        var currentSearch   = $.trim($searchBox.val()).toLowerCase();
        var rows            = $('#ImageTable').find('tbody tr');

        rows.each(function(i, row) {

            var $row = $(row);
            var imageName = $row.data('image-name').toLowerCase();

            if (imageName.startsWith(currentSearch) || currentSearch.length === 0) {
                $row.show();
            } else {
                $row.hide();
            }
        });

    };

    var init = function() {
        $('#SearchImages').on('keyup', performSearch);
    };

    return {
        init: init
    }

}(jQuery));
