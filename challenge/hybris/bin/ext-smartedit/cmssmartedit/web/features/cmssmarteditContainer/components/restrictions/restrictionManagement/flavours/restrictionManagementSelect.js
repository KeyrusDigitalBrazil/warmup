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
 * @name restrictionManagementSelectModule
 * @requires restrictionsServiceModule
 * @requires yActionableSearchItemModule
 * @requires ySelectModule
 * @description
 * This module defines the {@link restrictionManagementSelectModule.directive:restrictionManagementSelect restrictionManagementSelect} component
 **/
angular.module('restrictionManagementSelectModule', [
        'restrictionsServiceModule',
        'yActionableSearchItemModule',
        'ySelectModule',
        'yMessageModule',
        'cmsitemsRestServiceModule',
        'functionsModule',
        'yLoDashModule'
    ])

    .controller('RestrictionManagementSelectController', function(
        $q,
        restrictionManagementSelectModel,
        restrictionsService,
        systemEventService,
        cmsitemsUri,
        URIBuilder
    ) {

        var RESTRICTION_CREATE_BUTTON_PRESSED_EVENT_ID = "RESTRICTION_CREATE_BUTTON_PRESSED_EVENT_ID";
        var createButtonUnSubscribeFn;

        this.resultsHeaderTemplate = "<y-actionable-search-item data-event-id='" + RESTRICTION_CREATE_BUTTON_PRESSED_EVENT_ID + "'></y-actionable-search-item>";
        this.resultsHeaderLabel = "se.cms.restrictionmanagement.restrictionresults.header";
        this.itemTemplateUrl = "restrictionManagementItemNameTemplate.html";
        this.editorHeader = "";

        this.getResultsHeaderTemplate = function getResultsHeaderTemplate() {
            return this.selectModel.isTypeSupported() ? this.resultsHeaderTemplate : "";
        };

        this.selectRestrictionType = function selectRestrictionType() {
            if (this.selectModel.restrictionTypeSelected()) {
                if (this.controllerModel.showRestrictionSelector) {
                    this.resetSelector();
                } else {
                    this.controllerModel.showRestrictionSelector = true;
                }
                this.controllerModel.showRestrictionEditor = false;
            }
        }.bind(this);

        this.selectRestriction = function selectRestriction() {
            if (this.selectModel.restrictionSelected()) {
                this.editorHeader = 'se.cms.restriction.management.select.editor.header.add';
                this.controllerModel.mode = 'add';
                this.controllerModel.structureApi = restrictionsService.getStructureApiUri(this.controllerModel.mode);

                if (this.controllerModel.showRestrictionEditor) {
                    this.resetEditor();
                } else {
                    this.controllerModel.showRestrictionEditor = true;
                }
            }
        }.bind(this);

        this.createButtonEventHandler = function(eventId, name) {
            this.createRestriction(name);
        }.bind(this);

        this.createRestriction = function createRestriction(name) {

            this.selectModel.createRestrictionSelected(name, this.uriContext);
            this.editorHeader = 'se.cms.restriction.management.select.editor.header.create';
            this.controllerModel.mode = 'create';
            this.controllerModel.structureApi = restrictionsService.getStructureApiUri(this.controllerModel.mode);
            if (this.controllerModel.showRestrictionEditor) {
                this.resetEditor();
            } else {
                this.controllerModel.showRestrictionEditor = true;
            }
        }.bind(this);

        this.disableRestrictionChoice = function(restriction) {
            var existingIndex = this.existingRestrictions.findIndex(function(existingRestriction) {
                return restriction.uid === existingRestriction.uid;
            });
            return existingIndex !== -1;
        }.bind(this);

        this.$onDestroy = function() {
            if (createButtonUnSubscribeFn) {
                createButtonUnSubscribeFn();
            }
        }.bind(this);

        this.$onInit = function $onInit() {

            this.selectModel = restrictionManagementSelectModel.createRestrictionManagementSelectModel(this.getRestrictionTypesFn, this.getSupportedRestrictionTypesFn);

            // bound by the recompile dom directive
            this.resetEditor = function resetEditor() {};
            this.resetSelector = function resetSelector() {};
            var dryRunCmsItemsUri = cmsitemsUri + '/:identifier?dryRun=true';

            this.controllerModel = {
                showRestrictionSelector: false,
                showRestrictionEditor: false,
                mode: 'add',
                contentApi: new URIBuilder(dryRunCmsItemsUri).replaceParams(this.uriContext).build()
            };

            this.isDirtyFn = function() {
                if (this.controllerModel.mode === 'add') {
                    // if we're in adding mode and an editor is displayed then a restriction has been picked
                    return this.controllerModel.showRestrictionEditor;
                } else if (this.isDirtyInternal) {
                    // if we're creating a new restriction the use isDirty from GE
                    return this.isDirtyInternal();
                }
                return false;
            }.bind(this);


            this.fetchOptions = {
                fetchPage: this.selectModel.getRestrictionsPaged,
                fetchEntity: this.selectModel.getRestrictionFromBackend
            };

            this.submitFn = function() {
                if (this.selectModel.isTypeSupported()) {
                    return this.submitInternal().then(function(value) {
                        return value;
                    }.bind(this));
                } else {
                    return $q.when(this.selectModel.getRestriction());
                }
            }.bind(this);

            createButtonUnSubscribeFn = systemEventService.subscribe(RESTRICTION_CREATE_BUTTON_PRESSED_EVENT_ID, this.createButtonEventHandler);


        }.bind(this);
    })

    .factory('restrictionManagementSelectModel', function(
        $q, lodash,
        cmsitemsRestService,
        catalogService
    ) {

        function RestrictionManagementSelectModel(getRestrictionTypesFn, getSupportedRestrictionTypesFn) {

            var model = {};
            var restrictions;
            var selectedRestriction;
            var supportedRestrictionTypes = [];

            getRestrictionTypesFn().then(function(restrictionTypesResponse) {
                model.restrictionTypes = restrictionTypesResponse;
                var ctr = 0;
                model.restrictionTypes.forEach(function(type) {
                    type.id = ctr++;
                });

                if (typeof(getSupportedRestrictionTypesFn) !== 'undefined') {
                    getSupportedRestrictionTypesFn().then(function(result) {
                        supportedRestrictionTypes = result;
                    });
                } else {
                    supportedRestrictionTypes = lodash.map(model.restrictionTypes, 'code');
                }

            }.bind(this));

            this.selectedIds = {
                // restriction
                // restrictionType
            };

            this.getRestrictionsPaged = function(mask, pageSize, currentPage) {

                var requestParams = {
                    pageSize: pageSize,
                    currentPage: currentPage,
                    typeCode: model.selectedRestrictionType.code,
                    mask: mask
                };

                return cmsitemsRestService.get(requestParams).then(function(pagedRestrictionsResult) {
                    restrictions = pagedRestrictionsResult.response;
                    var ctr = 0;
                    pagedRestrictionsResult.response.forEach(function(restriction) {
                        restriction.id = ctr++;
                    }.bind(this));
                    pagedRestrictionsResult.results = pagedRestrictionsResult.response;
                    delete pagedRestrictionsResult.response;
                    return pagedRestrictionsResult;
                }.bind(this));

            }.bind(this);

            this.getRestrictionFromBackend = function() {
                return {};
            };

            this.getRestrictionTypes = function() {
                return $q.when(model.restrictionTypes);
            }.bind(this);

            this.restrictionSelected = function() {
                if (this.selectedIds.restriction || this.selectedIds.restriction === 0) {
                    selectedRestriction = restrictions.find(function(restriction) {
                        return restriction.id === this.selectedIds.restriction;
                    }.bind(this));
                    return true;
                }
                return false;
            }.bind(this);

            this.restrictionTypeSelected = function() {
                delete this.selectedIds.restriction;
                model.selectedRestrictionType = model.restrictionTypes ? model.restrictionTypes.find(function(restrictionType) {
                    return restrictionType.id === this.selectedIds.restrictionType;
                }.bind(this)) : null;
                if (model.selectedRestrictionType) {
                    selectedRestriction = {
                        typeCode: model.selectedRestrictionType.code
                    };
                    return true;
                }
                return false;
            }.bind(this);

            this.createRestrictionSelected = function(name, uriContext) {
                selectedRestriction = {
                    itemtype: model.selectedRestrictionType.code,
                    name: name
                };
                catalogService.getCatalogVersionUUid(uriContext).then(function(catalogVersionUuid) {
                    selectedRestriction.catalogVersion = catalogVersionUuid;
                });
            }.bind(this);

            this.getRestrictionTypeCode = function() {
                return model.selectedRestrictionType.code;
            };

            this.getRestriction = function() {
                return selectedRestriction;
            };

            this.isTypeSupported = function() {
                if (model.selectedRestrictionType && model.selectedRestrictionType.code) {
                    return supportedRestrictionTypes.indexOf(model.selectedRestrictionType.code) >= 0;
                }
                return false;
            };
        }


        return {
            createRestrictionManagementSelectModel: function(getRestrictionTypesFn, getSupportedRestrictionTypesFn) {
                return new RestrictionManagementSelectModel(getRestrictionTypesFn, getSupportedRestrictionTypesFn);
            }
        };


    })

    /**
     * @ngdoc directive
     * @name restrictionManagementSelectModule.directive:restrictionManagementSelect
     * @restrict E
     * @scope
     * @param {< Array=} existingRestrictions Array of existing restrictions, that will be not be selectable.
     * @param {? Function=} isDirtyFn Function returning the dirtiness status of the component.
     * @param {? Function=} submitFn Function defined in outer scope to validate restriction edit.
     * @param {< Object} uriContext The {@link resourceLocationsModule.object:UriContext uriContext}, as defined on the resourceLocationModule.
     * @param {& Expression} getRestrictionTypesFn A function that returns list of restriction types for a given item.
     * @param {< Function=} getSupportedRestrictionTypesFn A function that returns list of restriction types that are supported for editing for a given item. If not provided, all types are assumed to be supported.
     * @description
     * The restrictionManagementSelect Angular component is designed to be able to create or display restrictions.
     */
    .component('restrictionManagementSelect', {
        controller: 'RestrictionManagementSelectController',
        templateUrl: 'restrictionManagementSelectTemplate.html',
        bindings: {
            //in
            uriContext: '<',
            existingRestrictions: '<?',
            getRestrictionTypesFn: '&',
            getSupportedRestrictionTypesFn: '<?',
            //out
            submitFn: '=?',
            isDirtyFn: '=?'
        }
    });
