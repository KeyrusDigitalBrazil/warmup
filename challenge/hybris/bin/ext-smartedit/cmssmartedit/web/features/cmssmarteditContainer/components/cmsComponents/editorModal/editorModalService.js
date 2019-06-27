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
angular.module('editorModalServiceModule', ['genericEditorModalServiceModule', 'smarteditServicesModule', 'typeStructureRestServiceModule', 'componentServiceModule', 'renderServiceModule', 'cmsitemsRestServiceModule', 'yLoDashModule'])
    .factory('editorModalService', function($q, isBlank, genericEditorModalService, gatewayProxy, ComponentService, renderService) {

        function EditorModalService() {
            this.gatewayId = 'EditorModal';
            gatewayProxy.initForService(this, ["open", "openAndRerenderSlot"]);
        }

        function prepareContentForCreate(content, componentType, catalogVersionUuid, slotId, position) {
            content = content || {};
            content.position = !isBlank(content.position) ? content.position : position;
            content.slotId = content.slotId || slotId;
            content.typeCode = content.typeCode || componentType;
            content.itemtype = content.itemtype || componentType;
            content.catalogVersion = content.catalogVersion || catalogVersionUuid;
            content.visible = !isBlank(content.visible) ? content.visible : true;
            return content;
        }

        function createComponentData(componentAttributes, params) {
            var type;
            try {
                type = componentAttributes.smarteditComponentType.toLowerCase();
            } catch (e) {
                throw "editorModalService._createComponentData - invalid component type in componentAttributes." + e;
            }

            var isCreateOperation = componentAttributes.smarteditComponentUuid === undefined;
            var content = null;
            if (isCreateOperation) {
                content = prepareContentForCreate(
                    componentAttributes.content,
                    componentAttributes.smarteditComponentType,
                    componentAttributes.catalogVersionUuid,
                    params.slotId,
                    params.position
                );
            } else {
                //EditOperation
                content = undefined;
            }

            return {
                componentUuid: componentAttributes.smarteditComponentUuid,
                componentType: componentAttributes.smarteditComponentType,
                title: 'type.' + type + '.name',
                targetedQualifier: params.targetedQualifier,
                initialDirty: componentAttributes.initialDirty,
                content: content
            };
        }

        EditorModalService.prototype.openAndRerenderSlot = function(componentType, componentUuid, targetedQualifier) {
            var componentAttributes = {
                smarteditComponentType: componentType,
                smarteditComponentUuid: componentUuid
            };

            var componentData = createComponentData(componentAttributes, {
                targetedQualifier: targetedQualifier
            });
            return genericEditorModalService.open(componentData, null, function() {
                ComponentService.getSlotsForComponent(componentData.componentUuid).then(function(slotIds) {
                    renderService.renderSlots(slotIds);
                });
            });
        };

        EditorModalService.prototype.open = function(componentAttributes, targetSlotId, position, targetedQualifier) {
            var componentData = createComponentData(componentAttributes, {
                slotId: targetSlotId,
                position: position,
                targetedQualifier: targetedQualifier
            });
            return genericEditorModalService.open(componentData, null, function() {
                if (componentData.componentUuid) {
                    return ComponentService.getSlotsForComponent(componentData.componentUuid).then(function(slotIds) {
                        renderService.renderSlots(slotIds);
                    });
                }
            });
        };

        return new EditorModalService();
    });
