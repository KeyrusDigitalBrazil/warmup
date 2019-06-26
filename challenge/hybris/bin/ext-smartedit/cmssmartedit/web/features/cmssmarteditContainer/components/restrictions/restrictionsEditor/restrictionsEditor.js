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
 * @name restrictionsEditorModule
 * @description
 * This module contains the {@link restrictionsEditorModule.restrictionsEditor restrictionsEditor} component.
 */
angular.module('restrictionsEditorModule', [
        'cmssmarteditContainerTemplates',
        'restrictionsTableModule',
        "restrictionPickerModule",
        "sliderPanelModule",
        "yMessageModule",
        "yLoDashModule",
        'cmsitemsRestServiceModule',
        'restrictionsCriteriaServiceModule',
        'itemManagementModule',
        'functionsModule'
    ])

    .controller('restrictionsEditorController', function(
        $q, $log, lodash,
        GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT,
        GENERIC_EDITOR_LOADED_EVENT,
        ITEM_MANAGEMENT_EDITOR_ID,
        systemEventService,
        restrictionPickerConfig,
        restrictionsCriteriaService, isBlank) {

        this.setSliderConfigForAddOrCreate = function() {
            this.sliderPanelConfiguration.modal.title = "se.cms.restriction.management.panel.title.add";
            this.sliderPanelConfiguration.modal.save.label = "se.cms.restriction.management.panel.button.add";
            this.sliderPanelConfiguration.modal.save.isDisabledFn = function() {
                if (this.restrictionManagement.isDirtyFn) {
                    return !this.restrictionManagement.isDirtyFn();
                }
                return true; // disable save until save FN is bound byt restriction management component
            }.bind(this);
            this.sliderPanelConfiguration.modal.save.onClick = function() {
                this.restrictionManagement.submitFn().then(function(restriction) {
                    this.restrictions.push(restriction);
                    this.sliderPanelHide();
                }.bind(this)).catch(function() {
                    $log.warn('restrictionsEditorController.setSliderConfigForAddOrCreate - Failed to create restriction');
                });
            }.bind(this);
        }.bind(this);

        this.setSliderConfigForEditing = function() {
            this.sliderPanelConfiguration.modal.title = "se.cms.restriction.management.panel.title.edit";
            this.sliderPanelConfiguration.modal.save.label = "se.cms.restriction.management.panel.button.save";
            this.sliderPanelConfiguration.modal.save.isDisabledFn = function() {
                if (this.restrictionManagement.isDirtyFn) {
                    return !this.restrictionManagement.isDirtyFn();
                }
                return true; // disable save until save FN is bound byt restriction management component
            }.bind(this);
            this.sliderPanelConfiguration.modal.save.onClick = function() {
                this.restrictionManagement.submitFn().then(function(restrictionEdited) {
                    if (this.restrictionManagement.operation) {
                        var payloadRestriction = this.restrictionManagement.operation.restriction;
                        //Copy index back because of the backend returns response without one.
                        restrictionEdited.$restrictionIndex = payloadRestriction.$restrictionIndex;
                    }

                    var restrictionIndex = restrictionEdited.$restrictionIndex;
                    if (restrictionIndex !== -1) {
                        this.restrictions[restrictionIndex] = restrictionEdited;
                    } else {
                        throw "restrictionsEditorController - edited restriction not found in list: " + restrictionEdited;
                    }
                    this.sliderPanelHide();
                }.bind(this));
            }.bind(this);
        }.bind(this);

        this.sliderPanelConfiguration = {
            modal: {
                showDismissButton: true,
                cancel: {
                    label: "se.cms.restriction.management.panel.button.cancel",
                    onClick: function() {
                        this.sliderPanelHide();
                    }.bind(this)
                },
                save: {}
            },
            cssSelector: "#y-modal-dialog"
        };

        this.onClickOnAdd = function() {
            this.setSliderConfigForAddOrCreate();
            this.restrictionManagement.operation = restrictionPickerConfig.getConfigForSelecting(lodash.clone(this.restrictions), this.getRestrictionTypes, this.getSupportedRestrictionTypes);
            this.sliderPanelShow();
        }.bind(this);

        this.onClickOnEdit = function(restriction) {
            this.setSliderConfigForEditing();
            this.restrictionManagement.operation = restrictionPickerConfig.getConfigForEditing(lodash.clone(restriction), this.getSupportedRestrictionTypes);
            this.sliderPanelShow();
        }.bind(this);

        this.matchCriteriaChanged = function(criteriaSelected) {
            this.criteria = criteriaSelected;
            this.matchCriteriaIsDirty = this.criteria !== this.orrigCriteria;
            this.updateRestrictionsData();
        }.bind(this);

        this.setupResults = function(results) {
            this.restrictions = results;
            this.restrictions = this._indexRestrictions(this.restrictions);
            this.oldRestrictions = this._cloneRestrictions(this.restrictions);
            this.originalRestrictions = this._cloneRestrictions(this.restrictions);
            this.updateRestrictionsData();
            this.isRestrictionsReady = true;
        };

        this.updateRestrictionsData = function() {
            if (this.onRestrictionsChanged) {
                this.onRestrictionsChanged({
                    $onlyOneRestrictionMustApply: this.criteria.value,
                    $restrictions: this.restrictions
                });
            }
        };

        this._prepareRestrictionsCriteria = function() {
            this.criteriaOptions = restrictionsCriteriaService.getRestrictionCriteriaOptions();

            if (!!this.item.onlyOneRestrictionMustApply) {
                this.criteria = this.criteriaOptions[1];
                this.orrigCriteria = this.criteriaOptions[1];
            } else {
                this.criteria = this.criteriaOptions[0];
                this.orrigCriteria = this.criteriaOptions[0];
            }
        };

        this._isRestrictionRelatedError = function(validationError) {
            return lodash.includes(validationError.subject, 'restrictions');
        };

        this._formatRestrictionRelatedError = function(validationError) {
            var cloned = lodash.clone(validationError);
            if (!isBlank(cloned.position)) {
                cloned.position = parseInt(cloned.position);
            }
            if (!isBlank(cloned.subject)) {
                cloned.subject = cloned.subject.split('.').pop();
            }
            return cloned;
        };

        // Restriction Editor can be a part of a generic editor.
        // Whenever generic editor propagates unrelated errors, restriction editor
        // can extract errors related to itself.
        this._handleUnrelatedValidationErrors = function(key, validationData) {
            this.errors = validationData.messages.filter(function(error) {
                return this._isRestrictionRelatedError(error);
            }.bind(this)).map(function(error) {
                return this._formatRestrictionRelatedError(error);
            }.bind(this));
        };

        // Whenever restriction editor opens a form to edit restriction,
        // errors related to this particular restriction are propagated to it.
        this._propagateErrors = function(eventId, genericEditorId) {
            var restrictionInEditMode = this.restrictionManagement.operation &&
                this.restrictionManagement.operation.mode === restrictionPickerConfig.MODE_EDITING &&
                genericEditorId === ITEM_MANAGEMENT_EDITOR_ID;
            if (restrictionInEditMode) {
                var restrictionIndex = this.restrictionManagement.operation.restriction.$restrictionIndex;

                var errorsToPropagate = this.errors.filter(function(error) {
                    return error.position === restrictionIndex;
                });

                // Clear and reinitialize events so they do not interfere with GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT
                this._clearEvents();
                systemEventService.publishAsync(GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, {
                    messages: errorsToPropagate,
                    targetGenericEditorId: genericEditorId
                }).then(function() {
                    this._initEvents();
                }.bind(this));
            }
        };

        // Index is provided for each restriction so any restriction without
        // uuid can be easily identified for error propagation
        this._indexRestrictions = function(restrictions) {
            (restrictions || []).forEach(function(element, index) {
                element.$restrictionIndex = index;
            });
            return restrictions;
        };

        this._cloneRestrictions = function(restrictions) {
            return lodash.cloneDeep(restrictions);
        };

        this._initEvents = function() {
            this.unregisterErrorListener = systemEventService.subscribe(GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, this._handleUnrelatedValidationErrors.bind(this));
            this.unregisterErrorPropagationEvent = systemEventService.subscribe(GENERIC_EDITOR_LOADED_EVENT, this._propagateErrors.bind(this));
        };

        this._clearEvents = function() {
            this.unregisterErrorListener();
            this.unregisterErrorPropagationEvent();
        };

        this.$onInit = function() {

            this.showRestrictionPicker = false;
            this.restrictions = this.restrictions || [];
            this.originalRestrictions = [];
            this.restrictionsArrayIsDirty = false;
            this.matchCriteriaIsDirty = false;
            this.oldRestrictions = [];
            this.isRestrictionsReady = false;
            this.errors = [];

            //setting restrictions criteria
            this._prepareRestrictionsCriteria();

            //setting restrictions
            this.setupResults(this.restrictions);

            //prepare restriction management module
            this.restrictionManagement = {
                uriContext: this.item.uriContext
            };

            // It is necessary to put this function inside $onInit. Otherwise, the editor is never marked as dirty.
            this.isDirtyFn = function() {
                return this.restrictionsArrayIsDirty || (this.matchCriteriaIsDirty && this.restrictions.length >= 2);
            }.bind(this);

            this.resetFn = function() {
                return true;
            };

            this.cancelFn = function() {
                return $q.when(true);
            };

            this._initEvents();
        };

        this.$onDestroy = function() {
            this._clearEvents();
        };

        this.$doCheck = function() {
            this.restrictionsArrayIsDirty = angular.toJson(this.originalRestrictions) !== angular.toJson(this.restrictions);

            if (angular.toJson(this.oldRestrictions) !== angular.toJson(this.restrictions)) {
                this.restrictions = this._indexRestrictions(this.restrictions);
                this.oldRestrictions = this._cloneRestrictions(this.restrictions);
                this.updateRestrictionsData();
            }
        };
    })

    /**
     * @ngdoc directive
     * @name restrictionsEditorModule.restrictionsEditor
     * @restrict E
     * @scope
     * @description
     * The purpose of this directive is to allow the user to manage the restrictions for a given item. The restrictionsEditor has an editable and non-editable mode.
     * It uses the restrictionsTable to display the list of restrictions and it uses the restrictionsPicker to add or remove the restrictions.
     * 
     * @param {= Object} item The object for the item you want to manage restrictions.
     * @param {Boolean} item.onlyOneRestrictionMustApply The restriction criteria for the item.
     * @param {String} item.uuid The uuid of the item. Required if not passing initialRestrictions. Used to fetch and update restrictions for the item.
     * @param {Object} item.uriContext the {@link resourceLocationsModule.object:UriContext uriContext}
     * @param {= Boolean} editable Boolean to determine whether the editor is enabled.
     * @param {< Array=} restrictions An array of initial restriction objects to be loaded in the restrictions editor. If restrictions is not provided, it is used else it is assumed that there are no restrictions.
     * @param {= Function=} resetFn Function that returns true. This function is defined in the restrictionsEditor controller and exists only to provide an external callback.
     * @param {= Function=} cancelFn Function that returns a promise. This function is defined in the restrictionsEditor controller and exists only to provide an external callback.
     * @param {= Function=} isDirtyFn Function that returns a boolean. This function is defined in the restrictionsEditor controller and exists only to provide an external callback.
     * @param {& Expression=} onRestrictionsChanged Function that passes '$onlyOneRestrictionMustApply' boolean and an array of '$restrictions' as arguments. The invoker can bind this to a custom function to fetch these values and perform other operations.
     * @param {& Expression} getRestrictionTypes Function that return list of restriction types. The invoker can bind this to a custom function to fetch a list of restriction types.
     * @param {& Expression=} getSupportedRestrictionTypes Function that returns an arry of supported restriction types. The invoker can bind this to a custom function to fetch these values and perform other operations. If not provide, all types are assumed to be supported.
     */
    .component('restrictionsEditor', {
        templateUrl: 'restrictionsEditorTemplate.html',
        controller: 'restrictionsEditorController',
        scope: {},
        bindings: {
            item: '<',
            editable: '=',
            restrictions: '<?',
            resetFn: '=?',
            cancelFn: '=?',
            isDirtyFn: '=?',
            onRestrictionsChanged: '&?',
            getRestrictionTypes: '&',
            getSupportedRestrictionTypes: '&?'
        }
    });
