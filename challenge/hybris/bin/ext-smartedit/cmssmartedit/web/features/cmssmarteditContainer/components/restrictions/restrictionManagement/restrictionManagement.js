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
 * @name restrictionPickerModule
 * @requires itemManagementModule
 * @requires recompileDomModule
 * @requires restrictionManagementEditModule
 * @requires restrictionManagementSelectModule
 * @requires yLoDashModule
 * @description
 * This module defines the {@link restrictionPickerModule.directive:restrictionManagement restrictionManagement} component
 **/
angular.module('restrictionPickerModule', [
        'itemManagementModule',
        'recompileDomModule',
        'restrictionManagementEditModule',
        'restrictionManagementSelectModule',
        'yLoDashModule'
    ])

    /**
     * @ngdoc service
     * @name restrictionPickerModule.service:restrictionPickerConfig
     * @requires lodash
     * @description
     * The Generic Editor Modal Service is used to open an editor modal window that contains a tabset.
     */
    .service('restrictionPickerConfig', function(lodash) {

        this.MODE_EDITING = 'editing';
        this.MODE_SELECT = 'select';

        /**
         * @ngdoc method
         * @name restrictionPickerModule.service:restrictionPickerConfig#getConfigForEditing
         * @methodOf restrictionPickerModule.service:restrictionPickerConfig
         * @param {Object} existingRestriction The restriction to be edited.
         * @param {String} existingRestriction.uuid The UUID of the restriction to be edited.
         * @param {Number} existingRestriction.$restrictionIndex The index of the restriction in restrictions list.
         * @param {Function} getSupportedRestrictionTypesFn A function that returns list of restriction types that are supported for editing for a given item.
         * @returns {Object} A config object to used with the {@link restrictionPickerModule.directive:restrictionManagement restrictionManagement}
         * component in edit mode.
         */
        this.getConfigForEditing = function getConfigForEditing(existingRestriction, getSupportedRestrictionTypesFn) {
            return {
                mode: this.MODE_EDITING,
                restriction: existingRestriction,
                getSupportedRestrictionTypesFn: getSupportedRestrictionTypesFn
            };
        }.bind(this);

        /**
         * @ngdoc method
         * @name restrictionPickerModule.service:restrictionPickerConfig#getConfigForSelecting
         * @methodOf restrictionPickerModule.service:restrictionPickerConfig
         * @param {Array=} existingRestrictions An array of existing restrictions, that will be not be selectable.
         * @param {Function} getRestrictionTypesFn A function that returns list of restriction types for a given item.
         * @param {Function} getSupportedRestrictionTypesFn A function that returns list of restriction types that are supported for editing for a given item.
         * @returns {Object} A config object to used with the {@link restrictionPickerModule.directive:restrictionManagement restrictionManagement}
         * component in select/create mode.
         */
        this.getConfigForSelecting = function getConfigForSelecting(existingRestrictions, getRestrictionTypesFn, getSupportedRestrictionTypesFn) {
            return {
                mode: this.MODE_SELECT,
                getRestrictionTypesFn: getRestrictionTypesFn,
                getSupportedRestrictionTypesFn: getSupportedRestrictionTypesFn,
                existingRestrictions: existingRestrictions
            };
        }.bind(this);

        /**
         * @ngdoc method
         * @name restrictionPickerModule.service:restrictionPickerConfig#isEditingMode
         * @methodOf restrictionPickerModule.service:restrictionPickerConfig
         * @param {Object} config A config to check.
         * @returns {Boolean} True if the config param is a config object created with
         * {@link restrictionPickerModule.service:restrictionPickerConfig#methods_getConfigForEditing getConfigForEditing()}.
         */
        this.isEditingMode = function isEditingMode(config) {
            return config.mode === this.MODE_EDITING;
        }.bind(this);

        /**
         * @ngdoc method
         * @name restrictionPickerModule.service:restrictionPickerConfig#isSelectMode
         * @methodOf restrictionPickerModule.service:restrictionPickerConfig
         * @param {Object} config A config to check.
         * @returns {Boolean} True if the config param is a config object created with
         * {@link restrictionPickerModule.service:restrictionPickerConfig#methods_getConfigForSelecting getConfigForSelecting()}.
         */
        this.isSelectMode = function isSelectMode(config) {
            return config.mode === this.MODE_SELECT;
        }.bind(this);

        /**
         * @ngdoc method
         * @name restrictionPickerModule.service:restrictionPickerConfig#isValidConfig
         * @methodOf restrictionPickerModule.service:restrictionPickerConfig
         * @param {Object} config A config to check.
         * @returns {Boolean} True if the config object was created with proper params.
         */
        this.isValidConfig = function isValidConfig(config) {
            switch (config.mode) {
                case this.MODE_EDITING:
                    return lodash.isObject(config.restriction);

                case this.MODE_SELECT:
                    if (config.getSupportedRestrictionTypesFn) {
                        return lodash.isFunction(config.getRestrictionTypesFn) && lodash.isFunction(config.getSupportedRestrictionTypesFn);
                    } else {
                        return lodash.isFunction(config.getRestrictionTypesFn);
                    }
            }
        }.bind(this);

    })

    .controller('RestrictionManagementController', function($q, restrictionPickerConfig) {

        this.$onInit = function() {
            this.submitFn = function() {
                return this.submitInternal().then(function(value) {
                    return value;
                });
            }.bind(this);
        };

        this.$onChanges = function $onChanges() {
            if (restrictionPickerConfig.isValidConfig(this.config)) {
                this.editMode = restrictionPickerConfig.isEditingMode(this.config);
                this.getSupportedRestrictionTypesFn = this.config.getSupportedRestrictionTypesFn;
                if (this.editMode) {
                    this.restriction = this.config.restriction;
                } else {
                    this.getRestrictionTypesFn = this.config.getRestrictionTypesFn;
                    this.existingRestrictions = this.config.existingRestrictions;
                }
            } else {
                throw "restrictionManagementController - invalid restrictionPickerConfig";
            }
        };
    })

    /**
     * @ngdoc directive
     * @name restrictionPickerModule.directive:restrictionManagement
     * @restrict E
     * @param {< Object} Config Object created by {@link restrictionPickerModule.service:restrictionPickerConfig restrictionPickerConfig}.
     * @param {Array=} Config.existingRestrictions Array of existing restrictions, that will be not be selectable.<br /><i>(only on select mode)</i>.
     * @param {String} Config.mode Constant indicating whether the restriction picker is displayed in edit or select mode.
     * @param {String} Config.restrictionId Unique identifier for the processed restriction.
     * @param {< Object} uriContext The {@link resourceLocationsModule.object:UriContext uriContext}, as defined on the resourceLocationModule.
     * @param {= Function=} submitFn A function defined internally. After binding is complete, the caller may execute this.
     * function to trigger the POST/PUT depending on the config. Returns a promise resolving to a restriction object.
     * @param {= Function=} isDirtyFn A function defined internally. After binding is complete, the caller may execute this.
     * function, which return a boolean True if the generic edit use tor represent the restriction is in a dirty state.
     * @description
     * The restrictionManagement Angular component is designed to be able to create new restrictions, editing existing
     * restrictions, or search for restrictions, depending on the config provided.
     */
    .component('restrictionManagement', {
        controller: 'RestrictionManagementController',
        templateUrl: 'restrictionManagementTemplate.html',
        bindings: {
            config: '<',
            uriContext: '<',
            submitFn: '=?',
            isDirtyFn: '=?'
        }
    });
