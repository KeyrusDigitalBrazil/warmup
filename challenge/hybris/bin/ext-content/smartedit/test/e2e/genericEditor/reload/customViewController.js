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
angular.module('customViewModule', ['yjqueryModule', 'backendMocksModule', 'smarteditServicesModule', 'genericEditorModule', 'customReloadButtonModule'])
    .constant('PATH_TO_CUSTOM_VIEW', 'customView.html')
    .run(function(editorFieldMappingService) {
        editorFieldMappingService.addFieldMapping('CustomReloadButton', null, null, {
            template: 'customReloadButton/customReloadButtonWrapperTemplate.html'
        });
    })
    .controller('customViewController', function(yjQuery, systemEventService) {
        this.componentType = 'AnyComponent';
        this.componentId = 'anyComponentId';

        this.content = null;
        this.contentApi = '/cmswebservices/v1/catalogs/CURRENT_CONTEXT_CATALOG/versions/CURRENT_CONTEXT_CATALOG_VERSION/items';

        this.structure = null;
        this.structureApi = '/cmswebservices/v1/types/defaultComponent';

        var NEW_STRUCTURE = {
            attributes: [{
                cmsStructureType: 'ShortString',
                qualifier: 'name',
                i18nKey: 'type.anyComponentType.name.name'
            }, {
                cmsStructureType: 'RichText',
                qualifier: 'richtext',
                i18nKey: 'type.anyComponentType.richtext.name'
            }],
            category: 'TEST'
        };
        var NEW_CONTENT = {
            'type': 'anyComponentData',
            'name': 'Any new name',
            'pk': '1234567890',
            'typeCode': 'AnyComponent',
            'uid': 'ApparelDEAnyComponent',
            'visible': true,
            'richtext': '<strong>Any rich text here...</strong>'
        };
        var NEW_COMPONENT_STRUCTURE = {
            attributes: [{
                cmsStructureType: 'ShortString',
                qualifier: 'name',
                i18nKey: 'type.anyComponentType.name.name'
            }, {
                cmsStructureType: 'CustomReloadButton',
                qualifier: 'customReloadButton',
                i18nKey: 'type.anyComponentType.customreloadbutton.name'
            }],
            category: 'TEST'
        };

        /**
         * e2e test: call to this.setNewStructure and modify the name value
         */
        this.onUpdate = function(data) {
            var expectedData;
            if (data.hasOwnProperty('componentCustomField')) {
                expectedData = {
                    'type': 'anyComponentData',
                    'name': 'new component name',
                    'pk': '1234567890',
                    'typeCode': 'AnyComponent',
                    'uid': 'ApparelDEAnyComponent',
                    'visible': true,
                    'richtext': '',
                    'componentCustomField': 'custom value'
                };
            } else {
                expectedData = angular.copy(NEW_CONTENT);
                expectedData.name = 'some new name';
            }
            yjQuery('.generic-editor-status').html(angular.equals(expectedData, data) ? 'PASSED' : 'FAILED');
        };

        /**
         * SCENARIO: New structure with:
         * - qualifier:name, cmsStructureType:'ShortString'
         * - qualifier:richtext, cmsStructureType:'RichText'
         */
        this.setNewStructure = function() {
            this.content = angular.copy(this.content ? this.content : NEW_CONTENT);

            this.structureApi = null; // The Generic Editor can't have both structure and structureApi
            this.structure = angular.copy(NEW_STRUCTURE);
        };

        /**
         * SCENARIO: New structure api with:
         * - qualifier:headline, cmsStructureType:'ShortString'
         * - qualifier:active, cmsStructureType:'Boolean'
         * - qualifier:comments, cmsStructureType:'LongString'
         */
        this.setNewStructureApi = function() {
            this.content = {
                'type': 'anyComponentData',
                'headline': 'Any headline',
                'active': true,
                'comments': 'Any comments...',
                'pk': '1234567890',
                'typeCode': 'AnyComponent',
                'uid': 'ApparelDEAnyComponent',
                'visible': true
            };

            this.structure = null;
            this.structureApi = '/cmswebservices/v1/types/anotherComponent';
        };

        /**
         * SCENARIO: A new structure is set for a new component
         */
        this.setPOSTMode = function() {
            this.componentId = null;
            this.structureApi = null;
            this.structure = angular.copy(NEW_COMPONENT_STRUCTURE);

            //listen on custom load button event
            this.changeStructureEventListener = systemEventService.subscribe('load-structure', this.onChangeStructureEvent.bind(this));
        };

        /**
         * Upon reception of custom event, use the new payload.structure and payload.content to load the generic editor through attribute binding
         */
        this.onChangeStructureEvent = function(eventId, payload) {
            this.structure = angular.copy(payload.structure);
            this.content = payload.content;
        };
    });
