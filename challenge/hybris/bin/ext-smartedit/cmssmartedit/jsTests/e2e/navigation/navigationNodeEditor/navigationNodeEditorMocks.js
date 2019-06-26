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
angular.module('navigationNodeEditorMocks', ['ngMockE2E'])
    .run(function(backendMocksUtils) {
        backendMocksUtils.getBackendMock('componentTypesGenericGET').respond(function() {
            var componentTypes = {
                "componentTypes": [{
                    "attributes": [{
                        "cmsStructureType": "ShortString",
                        "collection": false,
                        "editable": true,
                        "i18nKey": "type.cmsitem.name.name",
                        "localized": false,
                        "paged": false,
                        "qualifier": "name",
                        "required": true
                    }, {
                        "cmsStructureType": "ShortString",
                        "collection": false,
                        "editable": true,
                        "i18nKey": "type.cmsnavigationnode.title.name",
                        "localized": true,
                        "paged": false,
                        "qualifier": "title",
                        "required": false
                    }, {
                        "cmsStructureType": "CMSItemDropdown",
                        "collection": true,
                        "editable": true,
                        "i18nKey": "type.cmsnavigationnode.entries.name",
                        "idAttribute": "uuid",
                        "labelAttributes": ["name", "uid"],
                        "localized": false,
                        "paged": true,
                        "params": {
                            "typeCode": "CMSNavigationEntry"
                        },
                        "qualifier": "entries",
                        "required": false,
                        "subTypes": {
                            "CMSNavigationEntry": "type.cmsnavigationentry.name"
                        }
                    }, {
                        "cmsStructureType": "CMSItemDropdown",
                        "collection": true,
                        "editable": true,
                        "i18nKey": "type.cmsnavigationnode.pages.name",
                        "idAttribute": "uuid",
                        "labelAttributes": ["name", "uid"],
                        "localized": false,
                        "paged": true,
                        "params": {
                            "itemSearchParams": "pageStatus:active",
                            "typeCode": "ContentPage"
                        },
                        "qualifier": "pages",
                        "required": false,
                        "subTypes": {
                            "ContentPage": "type.contentpage.name"
                        }
                    }, {
                        "cmsStructureType": "ShortString",
                        "collection": false,
                        "editable": false,
                        "i18nKey": "type.cmsitem.uid.name",
                        "localized": false,
                        "paged": false,
                        "qualifier": "uid",
                        "required": false
                    }],
                    "category": "COMPONENT",
                    "code": "CMSNavigationNode",
                    "i18nKey": "type.cmsnavigationnode.name",
                    "name": "Navigation Node"
                }]
            };

            return [200, componentTypes];
        });

    });

angular.module('smarteditloader').requires.push('navigationNodeEditorMocks');
angular.module('smarteditcontainer').requires.push('navigationNodeEditorMocks');
