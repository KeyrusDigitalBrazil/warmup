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
     * @name clonePageWizardServiceModule
     * @description
     * # The clonePageWizardServiceModule
     *
     * The clone page service module provides the functionality necessary to enable the cloning of pages through a modal wizard.
     *
     * Use the {@link clonePageWizardServiceModule.service:clonePageWizardService clonePageWizardService} to open the add page wizard modal.
     *
     */
    angular.module('clonePageWizardServiceModule', [
            'createPageServiceModule', 'newPageDisplayConditionModule', 'yLoDashModule', 'smarteditServicesModule',
            'selectPageTypeModule', 'selectPageTemplateModule', 'contextAwarePageStructureServiceModule',
            'confirmationModalServiceModule', 'resourceLocationsModule', 'typeStructureRestServiceModule', 'pageDisplayConditionsServiceModule',
            'componentCloneOptionFormModule', 'componentCloneInfoFormModule', 'restrictionsStepHandlerFactoryModule',
            'cmsitemsRestServiceModule', 'genericEditorModule', 'pageRestrictionsModule',
            'restrictionsServiceModule', 'pageRestrictionsInfoMessageModule', 'selectTargetCatalogVersionModule',
            'clonePageAlertServiceModule', 'pageFacadeModule', 'functionsModule', 'cmsSmarteditServicesModule'
        ])


        /**
         * @ngdoc service
         * @name clonePageWizardServiceModule.service:clonePageWizardService
         *
         * @description
         * The clone page wizard service allows opening a modal wizard to clone a page.
         */
        .service('clonePageWizardService', function(modalWizard, catalogService, pageFacade) {

            /**
             * @ngdoc method
             * @name clonePageWizardServiceModule.service:clonePageWizardService#openClonePageWizard
             * @methodOf clonePageWizardServiceModule.service:clonePageWizardService
             *
             * @description
             * When called, this method opens a modal window containing a wizard to clone an existing page.
             *
             * @param {Object} pageData An object containing the pageData when the clone page wizard is opened from the page list.
             * @returns {Promise} A promise that will resolve when the modal wizard is closed or reject if it's canceled.
             *
             */
            this.openClonePageWizard = function openClonePageWizard(pageData) {
                var promise = pageData ? catalogService.retrieveUriContext() : pageFacade.retrievePageUriContext();
                return promise.then(function(uriContext) {
                    return modalWizard.open({
                        controller: 'clonePageWizardController',
                        controllerAs: 'clonePageWizardCtrl',
                        properties: {
                            uriContext: uriContext,
                            basePageUUID: pageData ? pageData.uuid : undefined
                        }
                    });
                });
            };
        })

        .factory('ClonePageBuilderFactory', function($q, lodash, isBlank, contextAwarePageStructureService, pageInfoService, typeStructureRestService, cmsitemsRestService, catalogService) {

            var getPageUUID = function(pageUUID) {
                return !isBlank(pageUUID) ? $q.when(pageUUID) : pageInfoService.getPageUUID();
            };

            var ClonePageBuilder = function(restrictionsStepHandler, basePageUUID, uriContext) {
                this.restrictionsStepHandler = restrictionsStepHandler;

                this.pageInfoStructure = [];
                this.basePage = {}; // the page being cloned
                this.pageData = {}; // holds current clone page tabs data
                this.componentCloneOption = null;
                this.basePageInfoAvailable = false;

                getPageUUID(basePageUUID).then(function(pageUUID) {
                    var promises = $q.all([cmsitemsRestService.getById(pageUUID), catalogService.getCatalogVersionUUid(uriContext)]);
                    promises.then(function(values) {
                        this.basePage = values[0];
                        this.pageData = lodash.cloneDeep(this.basePage);
                        this.pageData.catalogVersion = values[1];
                        this.pageData.pageUuid = this.basePage.uuid;
                        delete this.pageData.uuid;

                        this.basePageInfoAvailable = true;

                        cmsitemsRestService.getById(this.basePage.masterTemplate).then(function(templateInfo) {                
                            this.pageData.template = templateInfo.uid;                
                        }.bind(this));

                        typeStructureRestService.getStructureByTypeAndMode(this.pageData.typeCode, 'DEFAULT', {
                            getWholeStructure: true
                        }).then(function(structure) {
                            this.pageData.type = structure.type;
                        }.bind(this));
                    }.bind(this));
                }.bind(this));
            };

            ClonePageBuilder.prototype._updatePageInfoFields = function() {

                if (typeof this.pageData.defaultPage !== 'undefined') {
                    if (this.pageData.typeCode) {
                        contextAwarePageStructureService.getPageStructureForNewPage(this.pageData.typeCode, this.pageData.defaultPage).then(function(pageInfoFields) {
                            this.pageInfoStructure = pageInfoFields;
                        }.bind(this));
                    } else {
                        this.pageInfoStructure = [];
                    }
                }
            };

            ClonePageBuilder.prototype.getPageTypeCode = function() {
                return this.pageData.typeCode;
            };

            ClonePageBuilder.prototype.getPageTemplate = function() {
                return this.pageData.template;
            };

            ClonePageBuilder.prototype.getPageLabel = function() {
                return this.pageData.label;
            };

            ClonePageBuilder.prototype.getPageInfo = function() {
                return this.pageData;
            };

            ClonePageBuilder.prototype.getBasePageInfo = function() {
                return this.basePage;
            };

            ClonePageBuilder.prototype.getPageProperties = function() {
                var pageProperties = {};
                pageProperties.type = this.pageData.type;
                pageProperties.typeCode = this.pageData.typeCode;
                pageProperties.template = this.pageData.template;
                pageProperties.onlyOneRestrictionMustApply = this.pageData.onlyOneRestrictionMustApply;
                pageProperties.catalogVersion = this.pageData.catalogVersion;

                return pageProperties;
            };

            ClonePageBuilder.prototype.getPageInfoStructure = function() {
                return this.pageInfoStructure;
            };

            ClonePageBuilder.prototype.getPageRestrictions = function() {
                return this.pageData.restrictions || [];
            };

            ClonePageBuilder.prototype.getComponentCloneOption = function() {
                return this.componentCloneOption;
            };

            ClonePageBuilder.prototype.displayConditionSelected = function(displayConditionResult) {
                var isPrimaryPage = displayConditionResult.isPrimary;
                this.pageData.defaultPage = isPrimaryPage;
                this.pageData.homepage = displayConditionResult.homepage;
                if (isPrimaryPage) {
                    this.pageData.label = this.basePage.label;

                    if (this.pageData.restrictions) {
                        delete this.pageData.restrictions;
                    }
                    this.restrictionsStepHandler.hideStep();
                } else {
                    this.pageData.label = displayConditionResult.primaryPage ? displayConditionResult.primaryPage.label : '';
                    this.restrictionsStepHandler.showStep();
                }
                this.pageData.uid = '';
                this._updatePageInfoFields();
            };

            ClonePageBuilder.prototype.onTargetCatalogVersionSelected = function(targetCatalogVersion) {
                this.targetCatalogVersion = targetCatalogVersion;
                this.pageData.catalogVersion = targetCatalogVersion.uuid;
            };

            ClonePageBuilder.prototype.componentCloneOptionSelected = function(cloneOptionResult) {
                this.componentCloneOption = cloneOptionResult;
            };

            ClonePageBuilder.prototype.restrictionsSelected = function(onlyOneRestrictionMustApply, restrictions) {
                this.pageData.onlyOneRestrictionMustApply = onlyOneRestrictionMustApply;
                this.pageData.restrictions = restrictions;
            };

            ClonePageBuilder.prototype.getTargetCatalogVersion = function() {
                return this.targetCatalogVersion;
            };

            ClonePageBuilder.prototype.isBasePageInfoAvailable = function() {
                return this.basePageInfoAvailable;
            };

            return ClonePageBuilder;
        })

        /**
         * @ngdoc controller
         * @name clonePageWizardServiceModule.controller:clonePageWizardController
         *
         * @description
         * The clone page wizard controller manages the operation of the wizard used to create new pages.
         */
        .controller('clonePageWizardController', function($q, GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, lodash, wizardManager, ClonePageBuilderFactory, restrictionsStepHandlerFactory, experienceService, confirmationModalService, cmsitemsRestService, systemEventService, pageRestrictionsFacade, restrictionsService, sharedDataService, clonePageAlertService, alertService, pageFacade, pageSynchronizationService) {

            this.uriContext = wizardManager.properties.uriContext;
            this.callbacks = {};
            this.basePageUUID = wizardManager.properties.basePageUUID;
            var cloneInprogress = false;

            var self = this;

            this.restrictionStepProperties = {
                id: 'restrictionsStepId',
                name: 'se.cms.restrictions.editor.tab',
                title: 'se.cms.clonepagewizard.pageclone.title',
                templateUrl: 'clonePageRestrictionsStepTemplate.html'
            };

            var restrictionsEditorFunctionBindingsClosure = {}; // bound in the view for restrictions step
            var restrictionsStepHandler = restrictionsStepHandlerFactory.createRestrictionsStepHandler(wizardManager, restrictionsEditorFunctionBindingsClosure, this.restrictionStepProperties);

            // Constants
            var CLONE_PAGE_WIZARD_STEPS = {
                PAGE_CLONE_OPTIONS: 'cloneOptions',
                PAGE_INFO: 'pageInfo',
                PAGE_RESTRICTIONS: this.restrictionStepProperties.id
            };

            this.pageBuilder = new ClonePageBuilderFactory(restrictionsStepHandler, this.basePageUUID, this.uriContext);

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
                        id: CLONE_PAGE_WIZARD_STEPS.PAGE_CLONE_OPTIONS,
                        name: 'se.cms.clonepagewizard.pagecloneoptions.tabname',
                        title: 'se.cms.clonepagewizard.pageclone.title',
                        templateUrl: 'clonePageOptionsStepTemplate.html'
                    }, {
                        id: CLONE_PAGE_WIZARD_STEPS.PAGE_INFO,
                        name: 'se.cms.clonepagewizard.pageinfo.tabname',
                        title: 'se.cms.clonepagewizard.pageclone.title',
                        templateUrl: 'clonePageInfoStepTemplate.html'
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
                    case CLONE_PAGE_WIZARD_STEPS.PAGE_CLONE_OPTIONS:
                        return true;

                    case CLONE_PAGE_WIZARD_STEPS.PAGE_INFO:
                        return !cloneInprogress && (self.callbacks.isValidPageInfo && self.callbacks.isValidPageInfo());

                    case CLONE_PAGE_WIZARD_STEPS.PAGE_RESTRICTIONS:
                        return !cloneInprogress && this.pageBuilder.getPageRestrictions().length > 0;
                }
                return false;
            };

            this.onNext = function() {
                return $q.when(true);
            };

            this.onDone = function() {
                cloneInprogress = true;
                return this.callbacks.savePageInfo().then(function(page) {
                    var payload = this._preparePagePayload(page);

                    if (this.pageBuilder.getTargetCatalogVersion()) {
                        payload.siteId = this.pageBuilder.getTargetCatalogVersion().siteId;
                        payload.catalogId = this.pageBuilder.getTargetCatalogVersion().catalogId;
                        payload.version = this.pageBuilder.getTargetCatalogVersion().version;
                    }
                    return sharedDataService.get('experience').then(function(experience) {
                        return pageFacade.createPageForSite(payload, payload.siteId).then(function(response) {
                            var forceGetSynchronization = true;
                            pageSynchronizationService.getSyncStatus(payload.pageUuid, this.uriContext, forceGetSynchronization);

                            if (experience.catalogDescriptor.catalogVersionUuid === response.catalogVersion) {
                                experienceService.loadExperience({
                                    siteId: payload.siteId,
                                    catalogId: payload.catalogId,
                                    catalogVersion: payload.version,
                                    pageId: response.uid
                                });
                            } else {
                                clonePageAlertService.displayClonePageAlert(response);
                            }
                            return alertService.showSuccess({
                                message: "se.cms.clonepage.alert.success"
                            });

                        }, function(failure) {
                            cloneInprogress = false; // re-enable the button
                            systemEventService.publishAsync(GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, {
                                messages: failure.data.errors
                            });

                            if (!failure.data.errors.find(function(error) {
                                    return error.subject.indexOf("restrictions") === 0;
                                })) {
                                wizardManager.goToStepWithId(CLONE_PAGE_WIZARD_STEPS.PAGE_INFO);
                            }

                            return $q.reject();
                        });
                    }.bind(this));
                }.bind(this), function() {
                    cloneInprogress = false; // re-enable the button
                });
            };

            this._preparePagePayload = function(page) {
                var newClonePage = lodash.cloneDeep(page);

                lodash.merge(newClonePage, this.pageBuilder.getPageProperties()); // set page info properties

                newClonePage.cloneComponents = this.pageBuilder.getComponentCloneOption() === 'clone'; // set clone option
                newClonePage.itemtype = page.typeCode;

                if (this.isRestrictionsActive()) { // set restrictions
                    newClonePage.restrictions = this.pageBuilder.getPageRestrictions();
                }

                return newClonePage;
            }.bind(this);

            this.getPageTypeCode = function() {
                return this.pageBuilder.getPageTypeCode();
            }.bind(this);

            this.getPageLabel = function() {
                return this.pageBuilder.getPageLabel();
            }.bind(this);

            this.getPageTemplate = function() {
                return this.pageBuilder.getPageTemplate();
            }.bind(this);

            this.getPageInfo = function getPageInfo() {
                var page = this.pageBuilder.getPageInfo();
                page.uriContext = this.uriContext;
                return page;
            }.bind(this);

            this.getBasePageInfo = function getBasePageInfo() {
                var page = this.pageBuilder.getBasePageInfo();
                page.uriContext = this.uriContext;
                return page;
            }.bind(this);

            this.getPageRestrictions = function() {
                return this.pageBuilder.getPageRestrictions();
            }.bind(this);

            this.variationResult = function(displayConditionResult) {
                this.infoChanged = true;
                this.pageBuilder.displayConditionSelected(displayConditionResult);
            }.bind(this);

            this.onTargetCatalogVersionSelected = function($catalogVersion) {
                this.pageBuilder.onTargetCatalogVersionSelected($catalogVersion);
            }.bind(this);

            this.triggerUpdateCloneOptionResult = function(cloneOptionResult) {
                this.pageBuilder.componentCloneOptionSelected(cloneOptionResult);
            }.bind(this);

            this.getPageInfoStructure = function getPageInfoStructure() {
                return this.pageBuilder.getPageInfoStructure();
            }.bind(this);

            this.restrictionsResult = function(onlyOneRestrictionMustApply, restrictions) {
                this.pageBuilder.restrictionsSelected(onlyOneRestrictionMustApply, restrictions);
            }.bind(this);

            this.isRestrictionsActive = function isRestrictionsActive() {
                if (!this.typeChanged || wizardManager.getCurrentStepId() === CLONE_PAGE_WIZARD_STEPS.PAGE_RESTRICTIONS) {
                    this.typeChanged = false;
                    return true;
                }
                return false;
            }.bind(this);

            this.isPageInfoActive = function isPageInfoActive() {
                if (!this.infoChanged || wizardManager.getCurrentStepId() === CLONE_PAGE_WIZARD_STEPS.PAGE_INFO) {
                    this.infoChanged = false;
                    return true;
                }
                return false;
            }.bind(this);

            this.resetQueryFilter = function() {
                this.query.value = '';
            }.bind(this);

            this.getRestrictionTypes = function() {
                return pageRestrictionsFacade.getRestrictionTypesByPageType(this.getPageTypeCode());
            }.bind(this);

            this.getSupportedRestrictionTypes = function() {
                return restrictionsService.getSupportedRestrictionTypeCodes();
            };

            this.getTargetCatalogVersion = function() {
                return this.pageBuilder.getTargetCatalogVersion();
            }.bind(this);

            this.isBasePageInfoAvailable = function() {
                return this.pageBuilder.isBasePageInfoAvailable();
            }.bind(this);
        });
})();
