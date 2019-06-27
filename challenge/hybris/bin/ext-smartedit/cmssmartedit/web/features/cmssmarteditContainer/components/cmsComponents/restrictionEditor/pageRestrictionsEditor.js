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
angular.module('pageRestrictionsEditorModule', ['pageRestrictionsModule', 'restrictionTypesServiceModule', 'restrictionsServiceModule'])
    .controller('pageRestrictionsEditorController', function(restrictionTypesService, pageRestrictionsFacade, restrictionsService) {

        this.restrictionsResult = function(onlyOneRestrictionMustApply, restrictions) {
            this.model.onlyOneRestrictionMustApply = onlyOneRestrictionMustApply;
            this.model.restrictions = restrictions;
        }.bind(this);

        this.getRestrictionTypes = function() {
            return pageRestrictionsFacade.getRestrictionTypesByPageType(this.model.typeCode);
        }.bind(this);

        this.getSupportedRestrictionTypes = function() {
            return restrictionsService.getSupportedRestrictionTypeCodes();
        };

        this.$onChanges = function(changesObj) {

            if (changesObj.model) {
                this.model.restrictions = changesObj.model.currentValue.restrictions;
                this.model.onlyOneRestrictionMustApply = changesObj.model.currentValue.onlyOneRestrictionMustApply;
            }
        };

    })
    /**
     * @name pageRestrictionsEditorModule.directive:pageRestrictionsEditor
     * @scope
     * @restrict E
     * @element page-restrictions-editor
     * 
     * @description
     * page wrapper for Restrictions on top of {@link restrictionsEditorModule.restrictionsEditor restrictionsEditor} page.
     * 
     * @param {<Object} model The page model
     */
    .component('pageRestrictionsEditor', {
        templateUrl: 'pageRestrictionsEditorTemplate.html',
        controller: 'pageRestrictionsEditorController',
        controllerAs: 'pageRestrictionsEditorCtrl',
        bindings: {
            model: '<page'
        }
    });
