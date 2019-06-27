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
angular.module('genericEditorApp', [
        'ySelectModule',
        'genericEditorModule',
        'smarteditServicesModule',
        'localizedElementModule',
    ])
    .controller('defaultController', function(restServiceFactory, sharedDataService, editorFieldMappingService, genericEditorTabService) {

        // Get the test configuration. 
        var rawConfig = window.sessionStorage.getItem("TEST_CONFIGS");
        var testConfig = (rawConfig) ? JSON.parse(rawConfig) : {};

        restServiceFactory.setDomain('thedomain');
        sharedDataService.set('experience', {
            siteDescriptor: {
                uid: 'someSiteUid'
            },
            catalogDescriptor: {
                catalogId: 'electronics',
                catalogVersion: 'staged'
            }
        });

        this.thesmarteditComponentType = 'thesmarteditComponentType';
        this.thesmarteditComponentId = 'thesmarteditComponentId';
        this.structureApi = "cmswebservices/v1/types/:smarteditComponentType";
        this.displaySubmit = true;
        this.displayCancel = true;
        this.CONTEXT_CATALOG = "CURRENT_CONTEXT_CATALOG";
        this.CONTEXT_CATALOG_VERSION = "CURRENT_CONTEXT_CATALOG_VERSION";

        this.contentApi = '/cmswebservices/v1/catalogs/' + this.CONTEXT_CATALOG + '/versions/' + this.CONTEXT_CATALOG_VERSION + '/items';


        if (testConfig.multipleTabs) {
            editorFieldMappingService.addFieldTabMapping(null, null, "visible", "visibility");
            editorFieldMappingService.addFieldTabMapping(null, null, "id", "administration");
            editorFieldMappingService.addFieldTabMapping(null, null, "modifiedtime", "administration");
            editorFieldMappingService.addFieldTabMapping("DateTime", null, "creationtime", "administration");

            genericEditorTabService.configureTab('default', {
                priority: 5
            });
            genericEditorTabService.configureTab('administration', {
                priority: 4
            });
        }
        genericEditorTabService.configureTab('default', {
            priority: 5
        });
        genericEditorTabService.configureTab('administration', {
            priority: 4
        });
    });
