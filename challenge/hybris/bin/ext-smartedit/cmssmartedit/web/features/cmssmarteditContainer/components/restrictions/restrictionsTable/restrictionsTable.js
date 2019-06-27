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
 * @name restrictionsTableModule
 * @requires l10nModule
 * @requires restrictionsCriteriaServiceModule
 * @description
 * This module defines the {@link restrictionsTableModule.directive:restrictionsTable restrictionsTable} component
 */
angular.module('restrictionsTableModule', [
        'cmssmarteditContainerTemplates',
        'l10nModule',
        'restrictionsCriteriaServiceModule',
        'yLoDashModule'
    ])

    .controller('restrictionsTableController', function(restrictionsCriteriaService, lodash) {
        this.resetRestrictionCriteria = function() {

            if (!this.restrictions || this.restrictions.length < 2) { //default if none is provided or restrictions less than 2
                this.restrictionCriteria = this.criteriaOptions[0];
            }
        }.bind(this);

        this.removeRestriction = function(restriction) {
            var restrictionIndex = this.restrictions.indexOf(restriction);
            this.restrictions.splice(restrictionIndex, 1);
            this._removeUnnecessaryError(restrictionIndex);
            this._modifyErrorPositions(restrictionIndex);
        }.bind(this);

        this._removeUnnecessaryError = function(removedRestrictionIndex) {
            var errorIndex = this.errors.findIndex(function(error) {
                return error.position === removedRestrictionIndex;
            });

            if (errorIndex > -1) {
                this.errors.splice(errorIndex, 1);
            }
        }.bind(this);

        this._modifyErrorPositions = function(removedRestrictionIndex) {
            this.errors.forEach(function(error) {
                if (error.position >= removedRestrictionIndex) {
                    error.position = error.position - 1;
                }
            });
        }.bind(this);

        this.editRestriction = function(restriction) {
            this.onClickOnEdit(restriction);
        }.bind(this);


        this.criteriaClicked = function() {
            this.onCriteriaSelected(this.restrictionCriteria);
        };

        this.removeAllRestrictions = function() {
            this.restrictions = [];
        };

        this.showRemoveAllButton = function() {
            return this.restrictions && this.restrictions.length > 0 && this.editable;
        };

        this.isInError = function(index) {
            return !!this.errors && lodash.some(this.errors, function(error) {
                return error.position === index;
            });
        };

        this.$onInit = function() {

            this.criteriaOptions = restrictionsCriteriaService.getRestrictionCriteriaOptions();
            this.resetRestrictionCriteria();

            this.actions = [{
                key: 'se.cms.restrictions.item.edit',
                callback: this.editRestriction
            }, {
                key: 'se.cms.restrictions.item.remove',
                callback: this.removeRestriction
            }];
        };

        this.$doCheck = function() {
            this.resetRestrictionCriteria();
        };

    })

    /**
     * @ngdoc directive
     * @name restrictionsTableModule.directive:restrictionsTable
     * @restrict E
     * @scope
     * @param {= String} customClass The name of the CSS class.
     * @param {= Boolean} editable States whether the restrictions table could be modified.
     * @param {< Function=} onClickOnEdit Triggers the custom on edit event.
     * @param {= Function} onCriteriaSelected Function that accepts the selected value of criteria.
     * @param {= Function} onSelect Triggers the custom on select event.
     * @param {= Object} restrictions The object of restrictions.
     * @param {= Object =} restrictionCriteria The object that contains information about criteria.
     * @param {< Array =} errors The list of errors.
     * @description
     * Directive that can render a list of restrictions and provides callback functions such as onSelect and onCriteriaSelected. *
     */
    .component('restrictionsTable', {
        templateUrl: 'restrictionsTableTemplate.html',
        controller: 'restrictionsTableController',
        controllerAs: '$ctrl',
        bindings: {
            customClass: '=',
            editable: '=',
            onClickOnEdit: '<?',
            onCriteriaSelected: '=',
            onSelect: '=',
            restrictions: '=',
            restrictionCriteria: '=?',
            errors: '<?'
        }
    });
