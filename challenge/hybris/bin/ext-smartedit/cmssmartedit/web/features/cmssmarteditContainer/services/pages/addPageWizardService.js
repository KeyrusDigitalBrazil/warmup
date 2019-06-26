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
(function() {
    /**
     * @ngdoc overview
     * @name addPageServiceModule
     * @description
     * # The addPageServiceModule
     *
     * The add page service module provides the functionality necessary to enable the creation of pages through a modal wizard.
     *
     * Use the {@link addPageServiceModule.service:addPageWizardService addPageWizardService} to open the add page wizard modal.
     *
     */
    angular.module('addPageServiceModule', [
            'genericEditorModule',
            'wizardServiceModule',
            'functionsModule',
            'pageTypeServiceModule',
            'cmsitemsRestServiceModule',
            'pageRestrictionsModule',
            'restrictionsServiceModule',
            'newPageDisplayConditionModule',
            'yLoDashModule',
            'smarteditServicesModule',
            'selectPageTypeModule',
            'selectPageTemplateModule',
            'contextAwarePageStructureServiceModule',
            'confirmationModalServiceModule',
            'resourceLocationsModule',
            'restrictionsStepHandlerFactoryModule',
            'pageRestrictionsInfoMessageModule',
            'pageFacadeModule'
        ])


        /**
         * @ngdoc service
         * @name addPageServiceModule.service:addPageWizardService
         *
         * @description
         * The add page wizard service allows opening a modal wizard to create a page.
         */
        .service('addPageWizardService', function(modalWizard, catalogService) {

            /**
             * @ngdoc method
             * @name addPageServiceModule.service:addPageWizardService#openAddPageWizard
             * @methodOf addPageServiceModule.service:addPageWizardService
             *
             * @description
             * When called, this method opens a modal window containing a wizard to create new pages.
             *
             * @returns {Promise} A promise that will resolve when the modal wizard is closed or reject if it's canceled.
             *
             */
            this.openAddPageWizard = function openAddPageWizard() {
                return catalogService.retrieveUriContext().then(function(uriContext) {
                    return modalWizard.open({
                        controller: 'addPageWizardController',
                        controllerAs: 'addPageWizardCtl',
                        properties: {
                            uriContext: uriContext
                        }
                    });
                });
            };
        })

        .factory('pageBuilderFactory', function(contextAwarePageStructureService, catalogService) {

            function PageBuilder(restrictionsStepHandler, uriContext) {

                var model = {};
                var page = {};

                catalogService.getCatalogVersionUUid(uriContext).then(function(catalogVersionUuid) {
                    page.catalogVersion = catalogVersionUuid;
                });

                function updatePageInfoFields() {

                    if (page.defaultPage !== undefined) {
                        if (model.pageType) {
                            contextAwarePageStructureService.getPageStructureForNewPage(model.pageType.code, page.defaultPage).then(
                                function(pageInfoFields) {
                                    model.pageInfoFields = pageInfoFields;
                                }
                            );
                        } else {
                            model.pageInfoFields = [];
                        }
                    }
                }

                this.pageTypeSelected = function(pageTypeObject) {
                    model.pageType = pageTypeObject;
                    model.pageTemplate = null;
                    updatePageInfoFields();
                };

                this.pageTemplateSelected = function(pageTemplateObject) {
                    model.pageTemplate = pageTemplateObject;
                };

                this.getPageTypeCode = function() {
                    return model.pageType ? model.pageType.code : null;
                };

                this.getTemplateUuid = function() {
                    return model.pageTemplate ? model.pageTemplate.uuid : "";
                };

                this.getPage = function() {
                    page.typeCode = model.pageType ? model.pageType.code : null;
                    page.itemtype = page.typeCode;
                    page.type = model.pageType ? model.pageType.type : null;
                    page.masterTemplate = model.pageTemplate ? model.pageTemplate.uuid : null;
                    page.template = model.pageTemplate.uid;
                    return page;
                };

                this.setPageUid = function(uid) {
                    page.uid = uid;
                };

                this.setRestrictions = function(onlyOneRestrictionMustApply, restrictions) {
                    page.onlyOneRestrictionMustApply = onlyOneRestrictionMustApply;
                    page.restrictions = restrictions;
                };

                this.getPageInfoStructure = function() {
                    return model.pageInfoFields;
                };

                this.displayConditionSelected = function(displayConditionResult) {
                    var isPrimaryPage = displayConditionResult.isPrimary;
                    page.defaultPage = isPrimaryPage;
                    page.homepage = displayConditionResult.homepage;
                    if (isPrimaryPage) {
                        page.label = null;
                        restrictionsStepHandler.hideStep();
                    } else {
                        page.label = displayConditionResult.primaryPage ? displayConditionResult.primaryPage.label : "";
                        restrictionsStepHandler.showStep();
                    }
                    updatePageInfoFields();
                };

            }

            return {
                createPageBuilder: function(restrictionsStepHandler, uriContext) {
                    return new PageBuilder(restrictionsStepHandler, uriContext);
                }
            };
        })


        /**
         * @ngdoc controller
         * @name addPageServiceModule.controller:addPageWizardController
         *
         * @description
         * The add page wizard controller manages the operation of the wizard used to create new pages.
         */
        .controller('addPageWizardController', function($q, wizardManager, pageBuilderFactory, lodash, restrictionsStepHandlerFactory, experienceService, confirmationModalService, systemEventService, pageRestrictionsFacade, restrictionsService, GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, pageFacade) {

            this.uriContext = wizardManager.properties.uriContext;
            this.callbacks = {};

            this.restrictionStepProperties = {
                id: 'restrictionsStepId',
                name: 'se.cms.restrictions.editor.tab',
                title: 'se.cms.addpagewizard.pagetype.title',
                templateUrl: 'pageRestrictionsStepTemplate.html'
            };

            var self = this;

            var restrictionsEditorFunctionBindingsClosure = {}; // bound in the view for restrictions step
            var restrictionsStepHandler = restrictionsStepHandlerFactory.createRestrictionsStepHandler(wizardManager, restrictionsEditorFunctionBindingsClosure, this.restrictionStepProperties);

            // Constants
            var ADD_PAGE_WIZARD_STEPS = {
                PAGE_TYPE: 'pageType',
                PAGE_TEMPLATE: 'pageTemplate',
                PAGE_DISPLAY_CONDITION: 'pageDisplayCondition',
                PAGE_INFO: 'pageInfo',
                PAGE_RESTRICTIONS: restrictionsStepHandler.getStepId()
            };

            this.pageBuilder = pageBuilderFactory.createPageBuilder(restrictionsStepHandler, this.uriContext);

            this.restrictionsEditorFunctionBindings = restrictionsEditorFunctionBindingsClosure;
            this.typeChanged = true;
            this.infoChanged = true;
            this.model = {
                page: {},
                sharedPage: {}
            };

            // Wizard Configuration
            this.getWizardConfig = function() {
                var wizardConfig = {
                    isFormValid: this.isFormValid.bind(this),
                    onNext: this.onNext.bind(this),
                    onDone: this.onDone.bind(this),
                    onCancel: this.onCancel,
                    steps: [{
                        id: ADD_PAGE_WIZARD_STEPS.PAGE_TYPE,
                        name: 'se.cms.addpagewizard.pagetype.tabname',
                        title: 'se.cms.addpagewizard.pagetype.title',
                        templateUrl: 'pageTypeStepTemplate.html'
                    }, {
                        id: ADD_PAGE_WIZARD_STEPS.PAGE_TEMPLATE,
                        name: 'se.cms.addpagewizard.pagetemplate.tabname',
                        title: 'se.cms.addpagewizard.pagetype.title',
                        templateUrl: 'pageTemplateStepTemplate.html'
                    }, {
                        id: ADD_PAGE_WIZARD_STEPS.PAGE_DISPLAY_CONDITION,
                        name: 'se.cms.addpagewizard.pageconditions.tabname',
                        title: 'se.cms.addpagewizard.pagetype.title',
                        templateUrl: 'pageDisplayConditionStepTemplate.html'
                    }, {
                        id: ADD_PAGE_WIZARD_STEPS.PAGE_INFO,
                        name: 'se.cms.addpagewizard.pageinfo.tabname',
                        title: 'se.cms.addpagewizard.pagetype.title',
                        templateUrl: 'pageInfoStepTemplate.html'
                    }]
                };

                return wizardConfig;
            }.bind(this);

            this.onCancel = function onCancel() {
                return confirmationModalService.confirm({
                    description: 'se.editor.cancel.confirm'
                });
            };

            // Wizard Navigation
            this.isFormValid = function(stepId) {
                switch (stepId) {
                    case ADD_PAGE_WIZARD_STEPS.PAGE_TYPE:
                        return !!self.pageBuilder.getPageTypeCode();

                    case ADD_PAGE_WIZARD_STEPS.PAGE_TEMPLATE:
                        return !!self.pageBuilder.getTemplateUuid();

                    case ADD_PAGE_WIZARD_STEPS.PAGE_DISPLAY_CONDITION:
                        return true;

                    case ADD_PAGE_WIZARD_STEPS.PAGE_INFO:
                        return (self.callbacks.isDirtyPageInfo && self.callbacks.isDirtyPageInfo() && self.callbacks.isValidPageInfo && self.callbacks.isValidPageInfo());

                    case ADD_PAGE_WIZARD_STEPS.PAGE_RESTRICTIONS:
                        return restrictionsStepHandler.isStepValid();

                }

                return false;
            };

            this.onNext = function() {
                return $q.when(true);
            };

            this.restrictionsResult = function(onlyOneRestrictionMustApply, restrictions) {
                this.pageBuilder.setRestrictions(onlyOneRestrictionMustApply, restrictions);
            }.bind(this);

            this.onDone = function() {

                return self.callbacks.savePageInfo().then(function(page) {
                    lodash.defaultsDeep(page, self.pageBuilder.getPage());
                    return pageFacade.createPage(page).then(function(pageCreated) {
                        self.pageBuilder.setPageUid(pageCreated.uid);
                        return experienceService.loadExperience({
                            siteId: self.uriContext.CURRENT_CONTEXT_SITE_ID,
                            catalogId: self.uriContext.CURRENT_CONTEXT_CATALOG,
                            catalogVersion: self.uriContext.CURRENT_CONTEXT_CATALOG_VERSION,
                            pageId: self.pageBuilder.getPage().uid
                        });
                    }, function(failure) {
                        systemEventService.publishAsync(GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, {
                            messages: failure.data.errors
                        });

                        if (!failure.data.errors.find(function(error) {
                                return error.subject.indexOf("restrictions") === 0;
                            })) {
                            wizardManager.goToStepWithId(ADD_PAGE_WIZARD_STEPS.PAGE_INFO);
                        }

                        return $q.reject();
                    });
                });
            };

            this.typeSelected = function typeSelected(pageType) {
                self.infoChanged = true;
                self.typeChanged = true;
                self.pageBuilder.pageTypeSelected(pageType);
            };

            this.templateSelected = function templateSelected(pageTemplate) {
                self.pageBuilder.pageTemplateSelected(pageTemplate);
            };

            this.getPageTypeCode = function getPageTypeCode() {
                return self.pageBuilder.getPageTypeCode();
            };

            this.variationResult = function(displayConditionResult) {
                self.infoChanged = true;
                self.pageBuilder.displayConditionSelected(displayConditionResult);
            };

            this.getPageInfo = function getPageInfo() {
                var page = self.pageBuilder.getPage();
                page.uriContext = self.uriContext;
                return page;
            }.bind(this);

            this.getPageInfoStructure = function getPageInfoStructure() {
                return self.pageBuilder.getPageInfoStructure();
            };

            this.isRestrictionsActive = function isRestrictionsActive() {
                if (!self.typeChanged || wizardManager.getCurrentStepId() === ADD_PAGE_WIZARD_STEPS.PAGE_RESTRICTIONS) {
                    self.typeChanged = false;
                    return true;
                }
                return false;
            };

            this.isPageInfoActive = function isPageInfoActive() {
                if (!self.infoChanged || wizardManager.getCurrentStepId() === ADD_PAGE_WIZARD_STEPS.PAGE_INFO) {
                    self.infoChanged = false;
                    return true;
                }
                return false;
            };

            this.resetQueryFilter = function() {
                this.query.value = '';
            };

            this.getRestrictionTypes = function() {
                return pageRestrictionsFacade.getRestrictionTypesByPageType(this.getPageTypeCode());
            }.bind(this);

            this.getSupportedRestrictionTypes = function() {
                return restrictionsService.getSupportedRestrictionTypeCodes();
            };


        });
})();
