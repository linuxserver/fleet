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

var ajaxManager = (function($) {

    var handleError = function(jqXHR, textStatus, handleError) {

        if (jqXHR.status === 403) {

            var notification = $(
                '<div class="fleet-alert fleet-alert--warning">' +
                     '<i class="fas fa-exclamation-triangle text-warning"></i> Permission denied. Has your session expired?' +
                 '</div>'
            );

        } else {

            var message = JSON.parse(jqXHR.responseText).data;

            var notification = $(
                '<div class="fleet-alert fleet-alert--warning">' +
                     '<i class="fas fa-exclamation-triangle text-warning"></i> ' + message +
                 '</div>'
            );
        }

        $('.notifications').append(notification);
        notification.delay(3000).fadeOut(2000, function() {
            $(this).remove();
        });
    };

    var call = function(param, onDone) {
        return $.ajax(param).done(onDone).fail(handleError);
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

var bulmaManager = (function($) {

    var initMenu = function() {

        $(".navbar-burger").on('click', function() {

            $(".navbar-burger").toggleClass("is-active");
            $(".navbar-menu").toggleClass("is-active");
        });
    };

    var init = function() {

        initMenu();
    };

    return {
        init: init
    }

}(jQuery));