/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
window.setupMockModules = function(container) {

    beforeEach(module("ngMock"));

    angular.module('toolbarModule', []);
    angular.module('alertServiceModule', []);
    angular.module('sharedDataServiceModule', []);
    angular.module('loadConfigModule', []);
    angular.module('configModule', []);
    angular.module('iframeClickDetectionServiceModule', []);
    angular.module('smarteditServicesModule', []);
    angular.module('functionsModule', []);
    angular.module('contextualMenuServiceModule', []);
    angular.module('decoratorServiceModule', []);
    angular.module('modalServiceModule', []);
    angular.module('genericEditorModule', []);
    angular.module('editorModalServiceModule', []);
    angular.module('confirmationModalServiceModule', []);
    angular.module('coretemplates', []);
    angular.module('perspectiveServiceModule', []);
    angular.module('featureServiceModule', []);
    angular.module('smarteditCommonsModule', []);
    angular.module('smarteditRootModule', []);
    angular.module('renderServiceModule', []);
    angular.module('sliderPanelModule', []);
    angular.module('contextualMenuServiceModule', []);
    angular.module('slotSharedServiceModule', []);
    angular.module('storageServiceModule', []);
    angular.module('seConstantsModule', []);
    angular.module('functionsModule', []);
    angular.module('waitDialogServiceModule', []);
    angular.module('slotRestrictionsServiceModule', []);
    angular.module('externalComponentDecoratorModule', []);
    angular.module('externalComponentButtonModule', []);
    angular.module('componentHandlerServiceModule', []);
    angular.module('yCollapsibleContainerModule', []);
    angular.module('ySelectModule', []);
    angular.module('componentMenuServiceModule', []);
    angular.module('l10nModule', []);
    angular.module('slotVisibilityServiceModule', []);
    angular.module('permissionServiceModule', []);
    angular.module('experienceServiceModule', []);
    angular.module('PersonalizationsmarteditCommonsModule', []);

    angular.module('yjqueryModule').constant('domain', 'http://localhost:9230');

    beforeEach(function() {
        module(['$provide', function($provide) {
            $provide.value('translateFilter', [function(value) {
                return value;
            }][0]);
            $provide.constant('CONTAINER_TYPE_ATTRIBUTE', 'data-smartedit-container-type');
            $provide.constant('COMPONENT_CONTAINER_TYPE', 'CxCmsComponentContainer');
            $provide.constant('CONTAINER_ID_ATTRIBUTE', 'data-smartedit-container-id');
            $provide.constant('TYPE_ATTRIBUTE', 'data-smartedit-component-type');
            $provide.constant('CONTENT_SLOT_TYPE', 'ContentSlot');
        }]);
    });

    beforeEach(module('toolbarModule', function($provide) {
        container.toolbarServiceFactory = jasmine.createSpyObj('toolbarServiceFactory', ['getToolbarService']);
        container.experienceSelectorToolbarService = jasmine.createSpyObj('experienceSelectorToolbarService', ['getAliases', 'addItems']);
        container.experienceSelectorToolbarService.getAliases.and.returnValue([]);
        container.toolbarServiceFactory.getToolbarService.and.returnValue(container.experienceSelectorToolbarService);
        $provide.value('toolbarServiceFactory', container.toolbarServiceFactory);
    }));

    beforeEach(module('alertServiceModule', function($provide) {
        container.alertService = jasmine.createSpyObj('alertService', ['pushAlerts', 'showInfo', 'showDanger', 'showWarning', 'showSuccess']);
        $provide.value('alertService', container.alertService);
    }));

    beforeEach(module('sharedDataServiceModule', function($provide) {
        container.sharedDataService = jasmine.createSpyObj('sharedDataService', ['put', 'get']);
        $provide.value('sharedDataService', container.sharedDataService);
    }));

    beforeEach(module('loadConfigModule', function($provide) {
        container.loadConfigManagerService = jasmine.createSpyObj('loadConfigManagerService', ['loadAsObject']);
        $provide.value('loadConfigManagerService', container.loadConfigManagerService);
    }));

    beforeEach(module('iframeClickDetectionServiceModule', function($provide) {
        container.iframeClickDetectionService = jasmine.createSpyObj('iframeClickDetectionService', ['click']);
        $provide.value('iframeClickDetectionService', container.iframeClickDetectionService);
    }));

    beforeEach(module('smarteditServicesModule', function($provide) {
        container.restServiceFactory = jasmine.createSpyObj('restServiceFactory', ['get']);
        $provide.value('restServiceFactory', container.restServiceFactory);
        container.restService = jasmine.createSpyObj('restService', ['get']);
        $provide.value('restService', container.restService);
        container.restServiceFactory.get.and.returnValue(container.restService);
    }));

    beforeEach(module('contextualMenuServiceModule', function($provide) {
        container.contextualMenuService = jasmine.createSpyObj('contextualMenuService', ['addItems']);
        $provide.value('contextualMenuService', container.contextualMenuService);
    }));

    beforeEach(module('decoratorServiceModule', function($provide) {
        container.decoratorService = jasmine.createSpyObj('decoratorService', ['addMappings']);
        $provide.value('decoratorService', container.decoratorService);
    }));

    beforeEach(module('modalServiceModule', function($provide) {
        container.modalService = jasmine.createSpyObj('modalService', ['addMappings', 'open']);
        $provide.value('modalService', container.modalService);
        $provide.constant('MODAL_BUTTON_ACTIONS', {
            NONE: "none",
            CLOSE: "close",
            DISMISS: "dismiss"
        });
        $provide.constant('MODAL_BUTTON_STYLES', {
            DEFAULT: "default",
            PRIMARY: "primary",
            SECONDARY: "default"
        });
    }));

    beforeEach(module('genericEditorModule', function($provide) {
        container.genericEditor = jasmine.createSpyObj('GenericEditor', ['addMappings']);
        $provide.value('GenericEditor', container.genericEditor);
    }));

    beforeEach(module('editorModalServiceModule', function($provide) {
        container.editorModalService = jasmine.createSpyObj('editorModalService', ['open']);
        $provide.value('editorModalService', container.editorModalService);
    }));

    beforeEach(module('confirmationModalServiceModule', function($provide) {
        container.confirmationModalService = jasmine.createSpyObj('confirmationModalService', ['confirm']);
        $provide.value('confirmationModalService', container.confirmationModalService);
    }));

    beforeEach(module('perspectiveServiceModule', function($provide) {
        container.perspectiveService = jasmine.createSpyObj('perspectiveService', ['register']);
        $provide.value('perspectiveService', container.perspectiveService);
    }));

    beforeEach(module('featureServiceModule', function($provide) {
        container.featureService = jasmine.createSpyObj('featureService', ['register', 'addToolbarItem', 'addDecorator', 'addContextualMenuButton']);
        $provide.value('featureService', container.featureService);
    }));

    beforeEach(module('smarteditCommonsModule', function($provide) {
        container.systemEventService = jasmine.createSpyObj('systemEventService', ['sendAsynchEvent', 'registerEventHandler']);
        $provide.value('systemEventService', container.systemEventService);
        container.crossFrameEventService = jasmine.createSpyObj('crossFrameEventService', ['subscribe']);
        $provide.value('crossFrameEventService', container.crossFrameEventService);
        container.languageService = jasmine.createSpyObj('languageService', ['getBrowserLocale']);
        container.languageService.getBrowserLocale.and.returnValue("en-US");
        $provide.value('languageService', container.languageService);
        container.catalogService = jasmine.createSpyObj('catalogService', ['']);
        $provide.value('catalogService', container.catalogService);
        container.gatewayFactory = jasmine.createSpyObj('gatewayFactory', ['initListener']);
        $provide.value('gatewayFactory', container.gatewayFactory);
        container.gatewayProxy = jasmine.createSpyObj('gatewayProxy', ['initForService']);
        $provide.value('gatewayProxy', container.gatewayProxy);
    }));

    beforeEach(module('smarteditRootModule', function($provide) {
        container.gatewayFactory = jasmine.createSpyObj('gatewayFactory', ['initListener']);
        $provide.value('gatewayFactory', container.gatewayFactory);
        container.gatewayProxy = jasmine.createSpyObj('gatewayProxy', ['initForService']);
        $provide.value('gatewayProxy', container.gatewayProxy);
    }));

    beforeEach(module('renderServiceModule', function($provide) {
        container.renderService = jasmine.createSpyObj('renderService', ['renderSlots']);
        $provide.value('renderService', container.renderService);
    }));

    beforeEach(module('contextualMenuServiceModule', function($provide) {
        container.contextualMenuService = jasmine.createSpyObj('contextualMenuService', ['refreshMenuItems']);
        $provide.value('contextualMenuService', container.contextualMenuService);
    }));

    beforeEach(module('slotSharedServiceModule', function($provide) {
        container.slotSharedService = jasmine.createSpyObj('slotSharedService', ['isSlotShared']);
        $provide.value('slotSharedService', container.slotSharedService);
    }));

    beforeEach(module('storageServiceModule', function($provide) {
        container.storageService = jasmine.createSpyObj('storageService', ['getValueFromCookie', 'putValueInCookie']);
        $provide.value('storageService', container.storageService);
    }));

    beforeEach(module('seConstantsModule', function($provide) {
        $provide.constant('DATE_CONSTANTS', {
            MOMENT_FORMAT: 'M/D/YY h:mm A'
        });

        $provide.constant('EVENT_PERSPECTIVE_UNLOADING', {
            EVENT_PERSPECTIVE_UNLOADING: 'EVENT_PERSPECTIVE_UNLOADING'
        });
    }));

    beforeEach(module('functionsModule', function($provide) {
        container.isBlank = jasmine.createSpyObj('isBlank', ['']);
        $provide.value('isBlank', container.isBlank);
    }));

    beforeEach(module('waitDialogServiceModule', function($provide) {
        container.waitDialogService = jasmine.createSpyObj('waitDialogService', ['showWaitModal', 'hideWaitModal']);
        $provide.value('waitDialogService', container.waitDialogService);
    }));

    beforeEach(module('slotRestrictionsServiceModule', function($provide) {
        container.slotRestrictionsService = jasmine.createSpyObj('slotRestrictionsService', ['getSlotRestrictions']);
        $provide.value('slotRestrictionsService', container.slotRestrictionsService);
    }));

    beforeEach(module('componentHandlerServiceModule', function($provide) {
        container.componentHandlerService = jasmine.createSpyObj('componentHandlerService', ['getParentSlotForComponent', 'isExternalComponent', 'getAllSlotsSelector', 'getFromSelector']);
        $provide.value('componentHandlerService', container.componentHandlerService);
        $provide.constant('OVERLAY_COMPONENT_CLASS', 'smartEditComponentX');
        $provide.constant('CONTAINER_ID_ATTRIBUTE', 'data-smartedit-container-id');
        $provide.constant('TYPE_ATTRIBUTE', 'data-smartedit-component-type');
        $provide.constant('ID_ATTRIBUTE', 'data-smartedit-component-id');
        $provide.constant('CATALOG_VERSION_UUID_ATTRIBUTE', 'data-smartedit-catalog-version-uuid');
    }));

    beforeEach(module('componentMenuServiceModule', function($provide) {
        container.componentMenuService = jasmine.createSpyObj('componentMenuService', ['getValidContentCatalogVersions', 'getInitialCatalogVersion']);
        $provide.value('componentMenuService', container.componentMenuService);
    }));

    beforeEach(module('l10nModule', function($provide) {
        container.l10nFilter = jasmine.createSpyObj('l10nFilter', ['']);
        $provide.value('l10nFilter', container.l10nFilter);
    }));

    beforeEach(module('slotVisibilityServiceModule', function($provide) {
        container.slotVisibilityService = jasmine.createSpyObj('slotVisibilityService', ['reloadSlotsInfo']);
        $provide.value('slotVisibilityService', container.slotVisibilityService);
    }));

    beforeEach(module('permissionServiceModule', function($provide) {
        container.permissionService = jasmine.createSpyObj('permissionService', ['isPermitted']);
        $provide.value('permissionService', container.permissionService);
    }));

    beforeEach(module('experienceServiceModule', function($provide) {
        container.experienceService = jasmine.createSpyObj('experienceService', ['setCurrentExperience', 'getCurrentExperience', 'updateExperience']);
        $provide.value("experienceService", container.experienceService);
    }));

};
