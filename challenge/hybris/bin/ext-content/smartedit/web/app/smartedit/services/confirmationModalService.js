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
angular.module('confirmationModalServiceModule', ['smarteditServicesModule'])

    /**
     * @ngdoc service
     * @name confirmationModalServiceModule.service:confirmationModalService
     *
     * @description
     * Service used to open a confirmation modal in which an end-user can confirm or cancel an action. A confirmation modal
     * consists of a title, content, and an OK and cancel button. This modal may be used in any context in which a
     * confirmation is required.
     */
    .factory('confirmationModalService', function(gatewayProxy) {

        function ConfirmationModalService() {
            this.gatewayId = 'confirmationModal';
            gatewayProxy.initForService(this, ["confirm"]);
        }

        /**
         * @ngdoc method
         * @name confirmationModalServiceModule.service:confirmationModalService#open
         * @methodOf confirmationModalServiceModule.service:confirmationModalService
         *
         * @description
         * Uses the {@link modalServiceModule.modalService modalService} to open a confirmation modal.
         *
         * The confirmation modal is initialized by a default i18N key as a title or by an override title passed through the
         * input configuration object.
         *
         * @param {Object} configuration Configuration for the confirmation modal
         * @param {String} configuration.title The override title for the confirmation modal. If a title is provided, it
         * overrides the default title, which is an i18n key. This property is optional.
         * @param {String} configuration.description The description to be used as the content of the confirmation modal.
         * This is the text that is displayed to the end user.
         *
         * @returns {Promise} A promise that is resolved when the OK button is actioned or is rejected when the Cancel
         * button is actioned.
         */
        ConfirmationModalService.prototype.confirm = function() {};

        return new ConfirmationModalService();

    });
