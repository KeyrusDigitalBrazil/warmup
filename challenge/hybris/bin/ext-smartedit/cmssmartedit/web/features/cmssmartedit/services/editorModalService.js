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
 * @name editorModalServiceModule
 * @description
 * # The editorModalServiceModule
 *
 * The editor modal service module provides a service that allows opening an editor modal for a given component type and
 * component ID. The editor modal is populated with a save and cancel button, and is loaded with the 
 * editorTabset of cmssmarteditContainer as its content, providing a way to edit
 * various fields of the given component.
 */
angular.module('editorModalServiceModule', [])

    /**
     * @ngdoc service
     * @name editorModalServiceModule.service:editorModalService
     *
     * @description
     * Convenience service to open an editor modal window for a given component type and component ID.
     *
     * Example: A button is bound to the function '$scope.onClick' via the ng-click directive. Clicking the button will
     * trigger the opening of an editor modal for a CMSParagraphComponent with the ID 'termsAndConditionsParagraph'
     *
     * <pre>
        angular.module('app', ['editorModalServiceModule'])
            .controller('someController', function($scope, editorModalService) {
                $scope.onClick = function() {
                    editorModalService.open(componentAttributes);
                };
            });
     * </pre>
    */
    .factory('editorModalService', function(gatewayProxy) {
        function EditorModalService() {
            this.gatewayId = 'EditorModal';
            gatewayProxy.initForService(this, ["open", "openAndRerenderSlot"]);
        }

        /**
         * @ngdoc method
         * @name editorModalServiceModule.service:editorModalService#open
         * @methodOf editorModalServiceModule.service:editorModalService
         *
         * @description
         * Proxy function which delegates opening an editor modal for a given component type and component ID to the
         * SmartEdit container.
         *
         * @param {Object} componentAttributes The details of the component to be created/edited
         * @param {Object?} componentAttributes.smarteditComponentUuid An optional universally unique id of the component if the component is being edited.
         * @param {Object} componentAttributes.smarteditComponentType The component type
         * @param {Object} componentAttributes.catalogVersionUuid The catalog version UUID to add the component to.
         * @param {Object?} componentAttributes.content An optional content for create operation. It's ignored if componentAttributes.smarteditComponentUuid is defined.
         * @param {String?} targetSlotId The ID of the slot in which the component is placed.
         * @param {String?} position The position in a given slot where the component should be placed.
         * @param {String?} targetedQualifier Causes the genericEditor to switch to the tab containing a qualifier of the given name.
         *
         * @returns {Promise} A promise that resolves to the data returned by the modal when it is closed.
         */
        EditorModalService.prototype.open = function() {};


        /**
         * @ngdoc method
         * @name editorModalServiceModule.service:editorModalService#open
         * @methodOf editorModalServiceModule.service:editorModalService
         *
         * @description
         * Proxy function which delegates opening an editor modal for a given component type and component ID to the
         * SmartEdit container.
         *
         * @param {String} componentType The type of component as defined in the platform.
         * @param {String} componentUuid The UUID of the component as defined in the database.
         * @param {String?} targetedQualifier Causes the genericEditor to switch to the tab containing a qualifier of the given name.
         *
         * @returns {Promise} A promise that resolves to the data returned by the modal when it is closed.
         */
        EditorModalService.prototype.openAndRerenderSlot = function() {};

        return new EditorModalService();
    });
