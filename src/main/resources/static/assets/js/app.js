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

var Notifications = (function($) {

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

var Ajax = (function($) {

    var toggleLoadingState = function(button) {

        if (typeof button !== 'undefined' && null !== button) {
            button.prop('disabled', !button.prop('disabled')).toggleClass('is-loading');
        }
    };

    var handleError = function(jqXHR, onError) {

        $('.modal').removeClass('is-active');

        if (jqXHR.status === 403) {
            Notifications.makeNotification('Permission denied', 'danger');
        } else {
            Notifications.makeNotification(jqXHR.responseText, 'danger');
        }

        if (onError) {
            onError();
        }
    };

    var call = function(param, onDone, triggerButton, onError) {

        toggleLoadingState(triggerButton);

        return $.ajax(param)
            .done(function(data) { if (onDone) onDone(data); })
            .fail(function(jqXHR) { handleError(jqXHR, onError); })
            .always(function() { toggleLoadingState(triggerButton); });
    };

    var get = function(url, params, onDone, triggerButton, onError) {
        call({ method: 'get', url: url, data: params}, onDone, triggerButton, onError);
    };

    var put = function(url, params, onDone, triggerButton, onError) {
        call({ method: 'put', url: url, data: params}, onDone, triggerButton, onError);
    };

    var putJson = function(url, params, onDone, triggerButton, onError) {
        call({ method: 'put', url: url, contentType: 'application/json', dataType: 'json', data: JSON.stringify(params) }, onDone, triggerButton, onError);
    };

    var post = function(url, params, onDone, triggerButton, onError) {
        call({ method: 'post', url: url, data: params}, onDone, triggerButton, onError);
    };

    var postJson = function(url, params, onDone, triggerButton, onError) {
        call({ method: 'post', url: url, contentType: 'application/json', dataType: 'json', data: JSON.stringify(params) }, onDone, triggerButton, onError);
    };

    var del = function(url, onDone, triggerButton, onError) {
        call({ method: 'delete', url: url }, onDone, triggerButton, onError);
    };

    return {
        call:     call,
        get:      get,
        put:      put,
        putJson:  putJson,
        post:     post,
        postJson: postJson,
        del:      del
    };

}(jQuery));

var FormValidation = (function($) {

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

var App = (function($) {

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

            var $parent = $(this).parents('.has-switchable');
            var $editableField = $parent.find('.switchable.field').find('.input');
            var $plainTextField = $parent.find('.switchable.plaintext');
            $plainTextField.text($editableField.val());
            $parent.removeClass('is-active');
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

    var initModals = function() {

        $('.is-modal-trigger').on('click', function() {
            $($(this).data('modal')).addClass('is-active');
        });

        $('.is-modal-cancel').on('click', function() {
            $(this).parents('.modal').removeClass('is-active');
        });
    };

    var initTabs = function() {

        $('.tabs').each(function (i, tab) {

            var $tab     = $(tab);
            var $items   = $tab.find('ul li');
            var $content = $($tab.data('tabs-for'));

            $items.each(function(j, item) {

                var $item = $(item);
                $item.on('click', function() {

                    $items.removeClass('is-active');
                    $content.find('.tab-content').removeClass('is-active');

                    $item.addClass('is-active');
                    $($item.data('tab-for')).addClass('is-active');
                });
            });
        });
    };

    var init = function() {

        initRepositorySwitcher();
        initSwitchables();
        initDropdowns();
        initNotifications();
        initMenu();
        initModals();
        initTabs();
    };

    return {
        init: init
    }

}(jQuery));

var Search = (function($) {

    var $ImageTable;
    var $SearchImages;

    var performSearch = function() {

        var $searchBox      = $(this);
        var currentSearch   = $.trim($searchBox.val()).toLowerCase();
        var rows            = $ImageTable.find('tbody tr');

        rows.each(function(i, row) {

            var $row = $(row);
            var imageName = $row.data('image-name').toLowerCase();

            if (imageName.includes(currentSearch) || currentSearch.length === 0) {
                $row.show();
            } else {
                $row.hide();
            }
        });

    };

    var init = function() {

        $SearchImages = $('#SearchImages');
        $ImageTable   = $('#ImageTable');

        $SearchImages.on('keyup', performSearch);
    };

    return {
        init: init
    }

}(jQuery));

var PullChart = (function($) {

    var DATE_MONTHS = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

    var formatNumber = function(num) {

        var numericalNum = parseInt(num);
        if (numericalNum > 1000000) {
            return (num / 1000000) + 'm';
        } else if (numericalNum > 1000) {
            return (num / 1000) + 'k';
        } else {
            return num;
        }
    };

    var formatDate = function(group) {

        if (group.length === 8) {

            var year  = parseInt(group.substr(0, 4));
            var month = parseInt(group.substr(4, 2));
            var day   = parseInt(group.substr(6, 2));

            var date = new Date(year, month - 1, day);
            return date.getDate() + ' ' + DATE_MONTHS[date.getMonth()];
        }

        return group;
    };

    var populateChart = function(imageKey, groupMode) {

        Ajax.get('/internalapi/image/stats', { 'imageKey': imageKey, 'groupMode': groupMode }, function(history) {

            var ctx = document.getElementById('ImagePullHistory').getContext('2d');

            new Chart(ctx, {
                type: 'line',
                data: {
                    labels: history.pullDifferential.labels,
                    datasets: [
                        {
                            lineTension: 0,
                            data: history.pullDifferential.pulls,
                            pointRadius: 0,
                            pointHitRadius: 6,
                            borderWidth: 1.5,
                            borderColor: 'rgba(33, 96, 196, 0.7)',
                            backgroundColor : 'rgba(33, 96, 196, 0.1)'
                        }
                    ]
                },
                options:  {
                    responsive: true,
                    maintainAspectRatio: false,
                    legend: {
                        display: false
                    },
                    scales: {
                        fill: false,
                        xAxes: [
                            {
                                gridLines: { display: false },
                                display: true,
                                ticks: {
                                    callback: function(label) {
                                        return formatDate(label);
                                    }
                                }
                            }
                        ],
                        yAxes: [
                            {
                                gridLines: { display: false },
                                ticks: {
                                    callback: function(label) {
                                        return formatNumber(label);
                                    }
                                }
                            }
                        ]
                    }
                }
            });
        });
    };

    return {
        populateChart: populateChart
    }

}(jQuery));
