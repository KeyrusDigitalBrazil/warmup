/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
/**
 * @ngdoc overview
 * @name modalServiceModule
 * @description
 * # The modalServiceModule
 *
 * The modal service module is a module devoted to providing an easy way to create, use, manage, and style modal windows,
 * and it contains all the components related to achieving this goal.
 *
 * Use the {@link modalServiceModule.modalService modalService} to open modal windows.
 *
 * Once a modal window is opened, you can use it's {@link modalServiceModule.service:ModalManager ModalManager} to manage
 * the modal, such as closing the modal, adding buttons, or chaning the title.
 *
 * Additionally you may use {@link modalServiceModule.object:MODAL_BUTTON_ACTIONS button actions} or
 * {@link modalServiceModule.object:MODAL_BUTTON_STYLES button styles} to affect how buttons look and behave on the modal
 * window.
 *
 */
angular.module('modalServiceModule', ['ui.bootstrap', 'translationServiceModule', 'functionsModule', 'coretemplates'])

    /**
     * @ngdoc object
     * @name modalServiceModule.object:MODAL_BUTTON_ACTIONS
     *
     * @description
     * Injectable angular constant<br/>
     * Defines the action to be taken after executing a button on a modal window. To be used when adding a button to the modal,
     * either when opening a modal (see {@link modalServiceModule.service:ModalManager#methods_getButtons ModalManager.getButtons()}) or
     * when adding a button to an existing modal (see {@link modalServiceModule.modalService#methods_open modalService.open()})
     *
     * Example:
     * <pre>
     *      myModalManager.addButton({
     *          id: 'button id',
     *          label: 'close_modal',
     *          action: MODAL_BUTTON_ACTIONS.CLOSE
     *      });
     * </pre>
     */
    .constant('MODAL_BUTTON_ACTIONS', {
        /**
         * @ngdoc property
         * @name NONE
         * @propertyOf modalServiceModule.object:MODAL_BUTTON_ACTIONS
         *
         * @description
         * Indicates to the {@link modalServiceModule.service:ModalManager ModalManager} that after executing the modal button
         * no action should be performed.
         **/
        NONE: "none",

        /**
         * @ngdoc property
         * @name CLOSE
         * @propertyOf modalServiceModule.object:MODAL_BUTTON_ACTIONS
         *
         * @description
         * Indicates to the {@link modalServiceModule.service:ModalManager ModalManager} that after executing the modal button,
         * the modal window should close, and the {@link https://docs.angularjs.org/api/ng/service/$q promise} returned by the modal should be resolved.
         **/
        CLOSE: "close",

        /**
         * @ngdoc property
         * @name DISMISS
         * @propertyOf modalServiceModule.object:MODAL_BUTTON_ACTIONS
         *
         * @description
         * Indicates to the {@link modalServiceModule.service:ModalManager ModalManager} that after executing the modal button,
         * the modal window should close, and the {@link https://docs.angularjs.org/api/ng/service/$q promise} returned by the modal should be rejected.
         **/
        DISMISS: "dismiss"
    })

    /**
     * @ngdoc object
     * @name modalServiceModule.object:MODAL_BUTTON_STYLES
     *
     * @description
     * Injectable angular constant<br/>
     * Defines the look and feel of a button on a modal window. To be used when adding a button to the modal,
     * either when opening a modal (see {@link modalServiceModule.service:ModalManager#methods_getButtons ModalManager.getButtons()}) or
     * when adding a button to an existing modal (see {@link modalServiceModule.modalService#methods_open modalService.open()})
     *
     * Example:
     * <pre>
     *      myModalManager.addButton({
     *          id: 'button id',
     *          label: 'cancel_button',
     *          style: MODAL_BUTTON_STYLES.SECONDARY
     *      });
     * </pre>
     */
    .constant('MODAL_BUTTON_STYLES', {

        /**
         * @ngdoc property
         * @name DEFAULT
         * @propertyOf modalServiceModule.object:MODAL_BUTTON_STYLES
         *
         * @description
         * Equivalent to SECONDARY
         **/
        DEFAULT: "default",

        /**
         * @ngdoc property
         * @name PRIMARY
         * @propertyOf modalServiceModule.object:MODAL_BUTTON_STYLES
         *
         * @description
         * Indicates to the modal window that this button is the primary button of the modal, such as save or submit,
         * and should be styled accordingly.
         **/
        PRIMARY: "primary",

        /**
         * @ngdoc property
         * @name SECONDARY
         * @propertyOf modalServiceModule.object:MODAL_BUTTON_STYLES
         *
         * @description
         * Indicates to the modal window that this button is a secondary button of the modal, such as cancel,
         * and should be styled accordingly.
         **/
        SECONDARY: "default"
    })

    /**
     * @ngdoc service
     * @name modalServiceModule.modalService
     *
     * @description
     * Convenience service to open and style a promise-based templated modal window.
     *
     * Simple Example:
     * <pre>
        angular.module('app', ['modalServiceModule'])
            .factory('someService', function($log, modalService, MODAL_BUTTON_ACTIONS) {

                modalService.open({
                    title: "My Title",
                    template: '<div>some content</div>',
                    buttons: [{
                        label: "Close",
                        action: MODAL_BUTTON_ACTIONS.CLOSE
                    }]
                }).then(function (result) {
                    $log.debug("modal closed!");
                    }, function (failure) {
                }
            );

        });
     * </pre>
     *
     * More complex example:
     * <pre>
     *
        angular.module('app', ['modalServiceModule'])

         .factory('someService',
            function($q, modalService, MODAL_BUTTON_ACTIONS, MODAL_BUTTON_STYLES) {

                modalService.open({
                    title: "modal.title",
                    template: '<div>some content</div>',
                    controller: 'modalController',
                    buttons: [
                        {
                            id: 'submit',
                            label: "Submit",
                            action: MODAL_BUTTON_ACTIONS.CLOSE
                        },
                        {
                            label: "Cancel",
                            action: MODAL_BUTTON_ACTIONS.DISMISS
                        },

                    ]
                }).then(function (result) {
                    $log.log("Modal closed with data:", result);
                }, function (failure) {
                });
            }
         )

         .controller('modalController', function($scope, $q) {

                function validateSomething() {
                    return true;
                };

                var buttonHandlerFn = function (buttonId) {
                    if (buttonId === 'submit') {
                        var deferred = $q.defer();
                        if (validateSomething()) {
                            deferred.resolve("someResult");
                        } else {
                            deferred.reject();  // cancel the submit button's close action
                        }
                        return deferred.promise;
                    }
                };

                $scope.modalManager.setButtonHandler(buttonHandlerFn);

            });
     * </pre>
     */
    .factory('modalService', function($uibModal, $injector, $controller, $rootScope, $templateCache, $translate, $log, MODAL_BUTTON_ACTIONS, MODAL_BUTTON_STYLES, merge, copy, generateIdentifier) {


        /**
         * @ngdoc service
         * @name modalServiceModule.service:ModalManager
         *
         * @description
         * The ModalManager is a service designed to provide easy runtime modification to various aspects of a modal window,
         * such as the modifying the title, adding a buttons, setting callbacks, etc...
         *
         * The ModalManager constructor is not exposed publicly, but an instance of ModalManager is added to the scope of
         * the modal content implicitly through the scope chain/prototyping. As long as you don't create an
         * {@link https://docs.angularjs.org/guide/scope isolated scope} for the modal, you can access it through $scope.modalManager
         *
         * <pre>
         *  .controller('modalTestController', function($scope, $log) {
         *    var buttonHandlerFn = function (buttonId) {
         *        $log.debug("button with id", buttonId, "was pressed!");
         *    };
         *    $scope.modalManager.setButtonHandler(buttonHandlerFn);
         *    ...
         * </pre>
         *
         */
        function ModalManager(conf) {

            var buttonEventCallback;
            var showDismissX = true;
            var dismissCallback = null;
            var buttons = [];

            if (!conf.modalInstance) {
                throw 'no.modalInstance.injected';
            }
            this.closeFunction = conf.modalInstance.close;
            this.dismissFunction = conf.modalInstance.dismiss;

            this._defaultButtonOptions = {
                id: 'button.id',
                label: 'button.label',
                action: MODAL_BUTTON_ACTIONS.NONE,
                style: MODAL_BUTTON_STYLES.PRIMARY,
                disabled: false,
                callback: null
            };

            this._createButton = function(buttonConfig) {
                var defaultButtonConfig = copy(this._defaultButtonOptions);

                merge(defaultButtonConfig, buttonConfig || {});
                $translate(defaultButtonConfig.label).then(
                    function(translatedValue) {
                        defaultButtonConfig.label = translatedValue;
                    }
                );

                var styleValidated = false;
                for (var style in MODAL_BUTTON_STYLES) {
                    if (MODAL_BUTTON_STYLES[style] === defaultButtonConfig.style) {
                        styleValidated = true;
                        break;
                    }
                }
                if (!styleValidated) {
                    throw 'modalService.ModalManager._createButton.illegal.button.style';
                }

                var actionValidated = false;
                for (var action in MODAL_BUTTON_ACTIONS) {
                    if (MODAL_BUTTON_ACTIONS[action] === defaultButtonConfig.action) {
                        actionValidated = true;
                        break;
                    }
                }
                if (!actionValidated) {
                    throw 'modalService.ModalManager._createButton.illegal.button.action';
                }

                return defaultButtonConfig;
            };

            this.title = "";

            if (typeof conf.title === 'string') {
                this.title = conf.title;
            }

            if (typeof conf.titleSuffix === 'string') {
                this.titleSuffix = conf.titleSuffix;
            }

            if (conf.buttons) {
                for (var index = 0; index < conf.buttons.length; index++) {
                    buttons.push(this._createButton(conf.buttons[index]));
                }
            }

            this._buttonPressed = function(button) {
                var callbackReturnedPromise = null;
                if (button.callback) {
                    callbackReturnedPromise = button.callback();
                } else if (buttonEventCallback) {
                    callbackReturnedPromise = buttonEventCallback(button.id);
                }
                if (button.action !== MODAL_BUTTON_ACTIONS.NONE) {
                    // by contract, callbackReturnedPromise must be a promise if it exists by this point
                    var exitFn = button.action === MODAL_BUTTON_ACTIONS.CLOSE ? this.close : this.dismiss;
                    if (callbackReturnedPromise) {
                        callbackReturnedPromise.then(function(data) {
                            exitFn.call(this, data);
                        }.bind(this));
                        // if promise rejected - do nothing
                    } else {
                        exitFn.call(this);
                    }
                }
            };

            this._handleDismissButton = function() {
                if (dismissCallback) {
                    var promise = dismissCallback();
                    promise.then(function(result) {
                        this.dismiss(result);
                    }.bind(this));
                } else {
                    this.dismiss();
                }
            };


            this._showDismissButton = function() {
                return showDismissX;
            };

            this._hasButtons = function() {
                return buttons.length > 0;
            };

            // -------------------------- Public API -----------------------------

            /**
             * @ngdoc method
             * @name modalServiceModule.service:ModalManager#addButton
             * @methodOf modalServiceModule.service:ModalManager
             *
             * @param {Object} conf (OPTIONAL) Button configuration
             * @param {String} [conf.id='button.id'] An ID for the button. It does not need to be unique, but it is suggested.
             * Can be used with the modal manager to enable/disable buttons, see which button is fired in the button handler, etc...
             * @param {String} [conf.label='button.label'] An i18n key that will be translated, and applied as the label of the button
             * @param {Boolean} [conf.disabled=false] Flag to enable/disable the button on the modal
             * @param {MODAL_BUTTON_STYLES} [conf.style=MODAL_BUTTON_STYLES.DEFAULT] One of {@link modalServiceModule.object:MODAL_BUTTON_STYLES MODAL_BUTTON_STYLES}
             * @param {MODAL_BUTTON_ACTIONS} [conf.action=MODAL_BUTTON_ACTIONS.NONE] One of {@link modalServiceModule.object:MODAL_BUTTON_ACTIONS MODAL_BUTTON_ACTIONS}
             * @param {function} [conf.callback=null] A function that will be called with no parameters when the button is pressed.
             * This (optional) function may return null, undefined, or a {@link https://docs.angularjs.org/api/ng/service/$q promise}.
             * Resolving the {@link https://docs.angularjs.org/api/ng/service/$q promise} will trigger the
             * {@link modalServiceModule.object:MODAL_BUTTON_ACTIONS button action} (if any), and rejecting the
             * {@link https://docs.angularjs.org/api/ng/service/$q promise} will prevent the action from being executed.
             *
             * Note: If a button has a callback and the ModalManager has registered a
             * {@link modalServiceModule.service:ModalManager#methods_setButtonHandler button handler}, only the button callback
             * will be executed on button press. This is to avoid the unnecessary complexity of having multiple handlers for a single button.
             *
             * @returns {Object} An object representing the newly added button
             */
            this.addButton = function(newButtonConf) {
                var newButton = this._createButton(newButtonConf);
                buttons.push(newButton);
                return newButton;
            };


            /**
             * @ngdoc method
             * @name modalServiceModule.service:ModalManager#getButtons
             * @methodOf modalServiceModule.service:ModalManager
             *
             * @description
             * Caution!
             *
             * This is a reference to the buttons being used by the modal manager, not a clone. This should
             * only be used to read or update properties provided in the Button configuration. See
             * {@link modalServiceModule.service:ModalManager#methods_addButton addButton()} for more details.
             *
             * @returns {Array} An array of all the buttons on the modal window, empty array if there are no buttons.
             */
            this.getButtons = function() {
                return buttons;
            };


            /**
             * @ngdoc method
             * @name modalServiceModule.service:ModalManager#removeAllButtons
             * @methodOf modalServiceModule.service:ModalManager
             *
             * @description
             * Remove all buttons from the modal window
             *
             */
            this.removeAllButtons = function() {
                buttons.splice(0, buttons.length);
            };


            /**
             * @ngdoc method
             * @name modalServiceModule.service:ModalManager#removeButton
             * @methodOf modalServiceModule.service:ModalManager
             *
             * @param {String} id The id of the button to be removed.
             *
             * @description
             * Remove a buttons from the modal window
             *
             */
            this.removeButton = function(buttonId) {
                for (var buttonIndex = buttons.length - 1; buttonIndex >= 0; buttonIndex--) {
                    if (buttons[buttonIndex].id === buttonId) {
                        buttons.splice(buttonIndex, 1);
                    }
                }
            };


            /**
             * @ngdoc method
             * @name modalServiceModule.service:ModalManager#enableButton
             * @methodOf modalServiceModule.service:ModalManager
             *
             * @param {String} id The id of the button to be enabled.
             *
             * @description
             * Enables a button on the modal window, allowing it to be pressed.
             *
             */
            this.enableButton = function(buttonId) {
                for (var buttonIndex = 0; buttonIndex < buttons.length; buttonIndex++) {
                    if (buttons[buttonIndex].id === buttonId) {
                        buttons[buttonIndex].disabled = false;
                    }
                }
            };


            /**
             * @ngdoc method
             * @name modalServiceModule.service:ModalManager#disableButton
             * @methodOf modalServiceModule.service:ModalManager
             *
             * @param {String} id The id of the button to be disabled.
             *
             * @description
             * Disabled a button on the modal window, preventing it from be pressed.
             *
             */
            this.disableButton = function(buttonId) {
                for (var buttonIndex = 0; buttonIndex < buttons.length; buttonIndex++) {
                    if (buttons[buttonIndex].id === buttonId) {
                        buttons[buttonIndex].disabled = true;
                    }
                }
            };


            /**
             * @ngdoc method
             * @name modalServiceModule.service:ModalManager#getButton
             * @methodOf modalServiceModule.service:ModalManager
             *
             * @param {String} id The id of the button to be fetched
             *
             * @returns {Object} The first button found with a matching id, or null
             */
            this.getButton = function(buttonId) {
                for (var buttonIndex = 0; buttonIndex < buttons.length; buttonIndex++) {
                    if (buttons[buttonIndex].id === buttonId) {
                        return buttons[buttonIndex];
                    }
                }
                return null;
            };


            /**
             * @ngdoc method
             * @name modalServiceModule.service:ModalManager#setShowHeaderDismiss
             * @methodOf modalServiceModule.service:ModalManager
             *
             * @param {boolean} showX Flag to show/hide the X dismiss button at the top right corner of the modal window,
             * when the modal header is displayed
             *
             */
            this.setShowHeaderDismiss = function(showButton) {
                if (typeof showButton === 'boolean') {
                    showDismissX = showButton;
                } else {
                    throw 'modalService.ModalManager.showDismissX.illegal.param';
                }
            };


            /**
             * @ngdoc method
             * @name modalServiceModule.service:ModalManager#setDismissCallback
             * @methodOf modalServiceModule.service:ModalManager
             *
             * @param {function} dismissCallback A function to be called when the X dismiss button at the top right corner of the modal window
             * is pressed. This function must either return null or a {@link https://docs.angularjs.org/api/ng/service/$q promise}.
             *
             * If the {@link https://docs.angularjs.org/api/ng/service/$q promise} is resolved, or if the function returns null or undefined, then the modal is closed and the returned
             * modal {@link https://docs.angularjs.org/api/ng/service/$q promise} is rejected.
             *
             * If the callback {@link https://docs.angularjs.org/api/ng/service/$q promise} is rejected, the modal is not closed, allowing you to provide some kind of validation
             * before closing.
             *
             */
            this.setDismissCallback = function(dismissCallbackFunction) {
                dismissCallback = dismissCallbackFunction;
            };


            /**
             * @ngdoc method
             * @name modalServiceModule.service:ModalManager#setButtonHandler
             * @methodOf modalServiceModule.service:ModalManager
             *
             * @description
             *
             * @param {Function} buttonPressedCallback The buttonPressedCallback is a function that is called when any button on the
             * modal, that has no {@link modalServiceModule.service:ModalManager#methods_addButton button callback}, is pressed. If a button has a
             * {@link modalServiceModule.service:ModalManager#methods_addButton button callback} function, then that function will be
             * called instead of the buttonPressedCallback.
             *
             * This buttonPressedCallback receives a single parameter, which is the string ID of the button that was pressed.
             * Additionally, this function must either return null, undefined or a {@link https://docs.angularjs.org/api/ng/service/$q promise}.
             *
             * If null/undefined is return, the modal will continue to process the {@link modalServiceModule.object:MODAL_BUTTON_ACTIONS button action}
             * In this case, no data will be returned to the modal {@link https://docs.angularjs.org/api/ng/service/$q promise} if the modal is closed.
             *
             * If a promise is returned by this function, then the {@link modalServiceModule.object:MODAL_BUTTON_ACTIONS button action}
             * may be cancelled/ignored by rejecting the promise. If the promise is resolved, the {@link modalServiceModule.service:ModalManager ModalManager}
             * will continue to process the {@link modalServiceModule.object:MODAL_BUTTON_ACTIONS button action}.
             *
             * If by resolving the promise returned by the buttonHandlerFunction with data passed to the resolve, and the {@link modalServiceModule.object:MODAL_BUTTON_ACTIONS button action}
             * is such that it results in the modal closing, then the modal promise is resolved/rejected with that same data. This allows you to pass data from the  buttonHandlerFunction
             * the the modalService.open(...) caller.
             *
             * See {@link modalServiceModule.service:ModalManager#methods_addButton addButton() for more details on the button callback }
             *
             *
             * A few scenarios for example:
             * #1 A button with a button callback is pressed.
             * <br/>Result: buttonPressedCallback is never called.
             *
             * #2 A button is pressed, buttonPressedCallback return null
             * <br/>Result: The modal manager will execute any action on the button
             *
             * #3 A button is pressed, buttonPressedCallback returns a promise, that promise is rejected
             * <br/>Result: Modal Manager will ignore the button action and nothing else will happen
             *
             * #4 A button with a dismiss action is pressed, buttonPressedCallback returns a promise, and that promise is resolved with data "Hello"
             * <br/>Result: ModalManager will execute the dismiss action, closing the modal, and errorCallback of the modal promise, passing "Hello" as data
             *
             *
             * Code sample of validating some data before closing the modal
             * <pre>
             function validateSomething() {
        return true;
    };

             var buttonHandlerFn = function (buttonId) {
        if (buttonId === 'submit') {
            var deferred = $q.defer();
            if (validateSomething()) {
                deferred.resolve("someResult");
            } else {
                deferred.reject();  // cancel the submit button's close action
            }
            return deferred.promise;
        }
    };

             $scope.modalManager.setButtonHandler(buttonHandlerFn);
             * </pre>
             */
            this.setButtonHandler = function(buttonHandlerFunction) {
                buttonEventCallback = buttonHandlerFunction;
            };

        }

        /**
         * @ngdoc method
         * @name modalServiceModule.service:ModalManager#close
         * @methodOf modalServiceModule.service:ModalManager
         *
         * @description
         * The close function will close the modal window, passing the provided data (if any) to the successCallback
         * of the modal {@link https://docs.angularjs.org/api/ng/service/$q promise} by resolving the {@link https://docs.angularjs.org/api/ng/service/$q promise}.
         *
         * @param {Object} data Any data to be returned to the resolved modal {@link https://docs.angularjs.org/api/ng/service/$q promise} when the modal is closed.
         *
         */
        ModalManager.prototype.close = function(dataToReturn) {
            if (this.closeFunction) {
                this.closeFunction(dataToReturn);
            }
        };


        /**
         * @ngdoc method
         * @name modalServiceModule.service:ModalManager#dismiss
         * @methodOf modalServiceModule.service:ModalManager
         *
         * @description
         * The dismiss function will close the modal window, passing the provided data (if any) to the {@link https://docs.angularjs.org/api/ng/service/$q errorCallback}
         * of the modal {@link https://docs.angularjs.org/api/ng/service/$q promise} by rejecting the {@link https://docs.angularjs.org/api/ng/service/$q promise}.
         *
         * @param {Object} data Any data to be returned to the rejected modal {@link https://docs.angularjs.org/api/ng/service/$q promise} when the modal is closed.
         *
         */
        ModalManager.prototype.dismiss = function(dataToReturn) {
            if (this.dismissFunction) {
                this.dismissFunction(dataToReturn);
            }
        };


        function getControllerClass(conf) {
            var that = this;
            return ['$scope', '$uibModalInstance', function ModalController($scope, $uibModalInstance) {
                conf.modalInstance = $uibModalInstance;

                this._modalManager = new ModalManager(conf);
                that._modalManager = this._modalManager;
                $scope.modalController = this;

                if (conf.controller) {
                    angular.extend(this, $controller(conf.controller, {
                        $scope: $scope,
                        modalManager: this._modalManager
                    }));

                    if (this.init) {
                        this.init();
                    }
                }
                if (conf.controllerAs) {
                    $scope[conf.controllerAs] = this;
                }

                if (conf.templateInline) {
                    this.templateUrl = "modalTemplateKey" + btoa(generateIdentifier());
                    $templateCache.put(this.templateUrl, conf.templateInline);
                } else {
                    this.templateUrl = conf.templateUrl;
                }

                this.close = function(data) {
                    this._modalManager.close(data);
                    $templateCache.remove(this.templateUrl);
                }.bind(this);

                this.dismiss = function(data) {
                    this._modalManager.dismiss(data);
                    $templateCache.remove(this.templateUrl);
                }.bind(this);
            }];
        }


        function ModalOpener() {}


        /**
         * @ngdoc method
         * @name modalServiceModule.modalService#open
         * @methodOf modalServiceModule.modalService
         *
         * @description
         * Open provides a simple way to open modal windows with custom content, that share a common look and feel.
         *
         * The modal window can be closed multiple ways, through {@link modalServiceModule.object:MODAL_BUTTON_ACTIONS button actions},
         * by explicitly calling the {@link modalServiceModule.service:ModalManager#methods_close close} or
         * {@link modalServiceModule.service:ModalManager#methods_close dismiss} functions, etc... Depending on how you
         * choose to close a modal, either the modal {@link https://docs.angularjs.org/api/ng/service/$q promise's}
         * {@link https://docs.angularjs.org/api/ng/service/$q successCallback} or {@link https://docs.angularjs.org/api/ng/service/$q errorCallback}
         * will be called. You can use the callbacks to return data from the modal content to the caller of this function.
         *
         * @param {Object} conf configuration
         * @param {String} conf.title (OPTIONAL) key for your modal title to be translated
         * @param {Array} conf.buttons (OPTIONAL) Array of button configurations. See {@link modalServiceModule.service:ModalManager#methods_addButton ModalManager.addButton()} for config details.
         * @param {String} conf.templateUrl path to an HTML fragment you mean to display in the modal window
         * @param {String} conf.templateInline inline HTML fragment you mean to display in the the modal window
         * @param {function} conf.controller the piece of logic that acts as controller of your template. It can be declared through its ID in AngularJS dependency injection or through explicit function
         * @param {String} conf.cssClasses space separated list of additional css classed to be added to the overall modal
         * @param {=boolean=} [conf.animation=true] determines whether CSS animations will be used for the modal
         *
         * @returns {function} {@link https://docs.angularjs.org/api/ng/service/$q promise} that will either be resolved or
         * rejected when the modal window is closed.
         */
        ModalOpener.prototype.open = function(conf) {
            var configuration = conf || {};

            if (!configuration.templateUrl && !configuration.templateInline) {
                throw "modalService.configuration.errors.no.template.provided";
            }
            if (configuration.templateUrl && configuration.templateInline) {
                throw "modalService.configuration.errors.2.templates.provided";
            }

            return $uibModal.open({
                templateUrl: 'modalTemplate.html',
                size: configuration.size || 'lg',
                backdrop: 'static',
                keyboard: false,
                controller: getControllerClass.call(this, configuration),
                controllerAs: 'modalController',
                windowClass: configuration.cssClasses || null,
                animation: (!conf.animation || conf.animation !== false) && !($injector.has('$animate') && !$injector.get('$animate').enabled())
            }).result;
        };

        return new ModalOpener();
    });
