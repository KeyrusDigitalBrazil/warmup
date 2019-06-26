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
angular.module('genericTabModule', ['genericEditorModule', 'resourceLocationsModule', 'componentEditorModule', 'smarteditServicesModule', 'cmsLinkToSelectModule'])
    .controller('genericTabCtrl', function(TYPES_RESOURCE_URI, CMS_LINK_TO_RELOAD_STRUCTURE_EVENT_ID, systemEventService) {
        var STRUCTURE_API_BASE_URL = TYPES_RESOURCE_URI + '?code=:smarteditComponentType&mode=:structureApiMode';

        this.$onInit = function() {
            this.structureApi = this.getStructureApiByMode('DEFAULT');
            this.changeStructureEventListener = systemEventService.subscribe(CMS_LINK_TO_RELOAD_STRUCTURE_EVENT_ID, this.onChangeStructureEvent.bind(this));
        };

        this.onChangeStructureEvent = function(eventId, payload) {
            if (payload.structureApiMode) {
                this.tabStructure = null;
                this.structureApi = this.getStructureApiByMode(payload.structureApiMode);
            } else if (payload.structure) {
                this.structureApi = null;
                this.tabStructure = payload.structure.attributes;
            }
            this.content = payload.content;
        };

        this.$onDestroy = function() {
            this.changeStructureEventListener();
        };

        this.getStructureApiByMode = function(structureApiMode) {
            return STRUCTURE_API_BASE_URL.replace(/:structureApiMode/gi, structureApiMode);
        };
    })
    .component('genericTab', {
        transclude: false,
        templateUrl: 'componentEditorWrapperTemplate.html',
        controller: 'genericTabCtrl',
        bindings: {
            saveTab: '=',
            resetTab: '=',
            cancelTab: '=',
            isDirtyTab: '=',
            componentId: '<',
            componentType: '<',
            tabId: '<',
            componentInfo: '<',
            content: '<'
        }
    });
