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
 * @name confirmationModalServiceModule
 * @description
 * # The confirmationModalServiceModule
 *
 * The confirmation modal service module provides a service that allows opening a confirmation (an OK/Cancel prompt with
 * a title and content) within a modal.
 *
 * This module is dependent on the {@link modalServiceModule modalServiceModule}.
 */
angular.module('confirmationModalServiceModule', ['modalServiceModule', 'smarteditServicesModule'])

    /**
     * @ngdoc service
     * @name confirmationModalServiceModule.service:confirmationModalService
     *
     * @description
     * Service used to open a confirmation modal in which an end-user can confirm or cancel an action. A confirmation modal
     * consists of a title, content, and an OK and cancel button. This modal may be used in any context in which a
     * confirmation is required.
     */
    .factory('confirmationModalService', function($q, modalService, gatewayProxy, MODAL_BUTTON_STYLES, MODAL_BUTTON_ACTIONS) {

        function ConfirmationModalService() {
            this.gatewayId = 'confirmationModal';
            gatewayProxy.initForService(this, ["confirm"]);
        }

        var _validateConfirmationParameters = function(description, template, templateUrl) {

            var checkMoreThanOnePropertySet = [description, template, templateUrl];
            var numOfProperties = 0;
            for (var i = 0; i < checkMoreThanOnePropertySet.length; i++) {
                if (checkMoreThanOnePropertySet[i] !== undefined) {
                    numOfProperties++;
                }
            }

            if (numOfProperties === 0) {
                return "You must have one of the following configuration properties configured: description, template, or templateUrl";
            } else if (numOfProperties > 1) {
                return "You have more than one of the following configuration properties configured: description, template, or templateUrl";
            }

            return undefined;
        };

        var _initializeControllerObjectWithScope = function(configuration) {
            var controller;
            if (configuration.scope) {
                var scope = configuration.scope;
                controller = [function() {
                    for (var key in scope) {
                        if (scope.hasOwnProperty(key)) {
                            this[key] = scope[key];
                        }
                    }
                }];
            }
            return controller;
        };

        /**
         * @ngdoc method
         * @name confirmationModalServiceModule.service:confirmationModalService#open
         * @methodOf confirmationModalServiceModule.service:confirmationModalService
         *
         * @description
         * Uses the {@link modalServiceModule.modalService modalService} to open a confirmation modal.
         *
         * The confirmation modal is initialized by a default i18N key as a title or by an override title passed through the
         * input configuration object. The configuration object must have one and only one of the following parameters set: description, template, or templateUrl
         *
         * @param {Object} configuration Configuration for the confirmation modal
         * @param {String} configuration.title The override title for the confirmation modal. If a title is provided, it
         * overrides the default title, which is an i18n key. This property is optional.
         * @param {String} configuration.description The description to be used as the content of the confirmation modal.
         * This is the text that is displayed to the end user.
         * @param {String} configuration.template The template in string format.
         * @param {String} configuration.templateUrl The link to the template to be used as the content of the confirmation modal.
         * This is the rendered template that is displayed to the end user.
         * @param {String} configuration.scope additional scope parameters to be added to the controller constructed for your template.
         * example: {scopeParam: "my param"}
         * In the template bind to the parameter using modalController for the controllerAs reference
         * example: <div>{{modalController.scopeParam}}</div>
         *
         * @returns {Promise} A promise that is resolved when the OK button is actioned or is rejected when the Cancel
         * button is actioned.
         */
        ConfirmationModalService.prototype.confirm = function(configuration) {

            var validationMessage = _validateConfirmationParameters(configuration.description, configuration.template, configuration.templateUrl);
            if (validationMessage) {
                return $q.reject(validationMessage);
            }

            var controller = _initializeControllerObjectWithScope(configuration);
            var templateInline;
            var templateUrl;
            if (configuration.templateUrl) {
                templateUrl = configuration.templateUrl;
            } else if (configuration.description) {
                if (configuration.descriptionPlaceholders) {
                    templateInline = '<div id="confirmationModalDescription">{{ "' + configuration.description + '" | translate: modalController.descriptionPlaceholders }}</div>';
                    controller = [function() {
                        this.descriptionPlaceholders = configuration.descriptionPlaceholders;
                    }];
                } else {
                    templateInline = '<div id="confirmationModalDescription">{{ "' + configuration.description + '" | translate }}</div>';
                }

            } else if (configuration.template) {
                templateInline = configuration.template;
            }

            var buttons = [];
            if (!configuration.showOkButtonOnly) {
                buttons.push({
                    id: 'confirmCancel',
                    label: 'se.confirmation.modal.cancel',
                    style: MODAL_BUTTON_STYLES.SECONDARY,
                    action: MODAL_BUTTON_ACTIONS.DISMISS
                });
            }

            buttons.push({
                id: 'confirmOk',
                label: 'se.confirmation.modal.ok',
                action: MODAL_BUTTON_ACTIONS.CLOSE
            });

            return modalService.open({
                size: 'md',
                title: configuration.title || 'se.confirmation.modal.title',
                templateInline: templateInline,
                templateUrl: templateUrl,
                controller: controller,
                cssClasses: 'yFrontModal',
                buttons: buttons
            });
        };

        return new ConfirmationModalService();
    });
