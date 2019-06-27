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
angular.module('nestedComponentManagementServiceModule', ['genericEditorModalServiceModule', 'yLoDashModule'])
    .service('nestedComponentManagementService', function(genericEditorModalService, lodash) {

        // ------------------------------------------------------------------------
        // Public API
        // ------------------------------------------------------------------------
        this.openNestedComponentEditor = function(componentInfo, editorStackId, saveCallback) {
            var componentData = prepareComponentData(componentInfo, editorStackId);
            saveCallback = (saveCallback) ? saveCallback : this.defaultSaveCallback;

            return genericEditorModalService.open(componentData, null, saveCallback);
        };

        // ------------------------------------------------------------------------
        // Helper Methods
        // ------------------------------------------------------------------------
        var prepareComponentData = function(componentInfo, editorStackId) {
            var type = componentInfo.componentType.toLowerCase();
            return {
                componentUuid: componentInfo.componentUuid,
                componentType: componentInfo.componentType,
                title: 'type.' + type + '.name',
                content: lodash.defaultsDeep({}, componentInfo.content, {
                    typeCode: componentInfo.componentType,
                    itemtype: componentInfo.componentType,
                    visible: true
                }),
                editorStackId: editorStackId
            };
        };

        this.defaultSaveCallback = function() {

        }.bind(this);
    });
