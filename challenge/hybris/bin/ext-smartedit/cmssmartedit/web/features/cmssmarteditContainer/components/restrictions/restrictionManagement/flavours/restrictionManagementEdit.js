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
 * @name restrictionManagementEditModule
 * @requires alertServiceModule
 * @requires restrictionsServiceModule
 * @description
 * This module defines the {@link restrictionManagementEditModule.directive:restrictionManagementEdit restrictionManagementEdit} component.
 **/
angular.module('restrictionManagementEditModule', [
        'alertServiceModule',
        'restrictionsServiceModule',
        'functionsModule'
    ])

    .controller('RestrictionManagementEditController', function(
        $q,
        alertService,
        restrictionsService,
        URIBuilder,
        cmsitemsUri
    ) {
        this._internalInit = function(isRestrictionTypeSupported) {
            this.isTypeSupported = isRestrictionTypeSupported;
            if (isRestrictionTypeSupported) {
                this.submitFn = function() {
                    return this.submitInternal().then(function(itemResponse) {
                        return $q.when(itemResponse);
                    }.bind(this));
                }.bind(this);
            } else {
                // type not supported, disable the save button always
                this.submitFn = function() {};
                this.isDirtyFn = function() {
                    return false;
                };
            }
            this.ready = true;
        }.bind(this);

        this.$onInit = function $onInit() {
            this.ready = false;
            this.restriction = this.restriction || {};
            this.itemManagementMode = 'edit';
            var dryRunCmsItemsUri = cmsitemsUri + '/:identifier?dryRun=true';
            this.contentApi = new URIBuilder(dryRunCmsItemsUri).replaceParams(this.uriContext).build();
            this.structureApi = restrictionsService.getStructureApiUri(this.itemManagementMode);

            if (typeof this.getSupportedRestrictionTypesFn !== 'undefined') {
                return this.getSupportedRestrictionTypesFn().then(function(supportedTypes) {
                    this._internalInit(supportedTypes.indexOf(this.restriction.itemtype) >= 0);
                }.bind(this));
            } else {
                return this._internalInit(true);
            }
        };

    })

    /**
     * @ngdoc directive
     * @name restrictionManagementEditModule.directive:restrictionManagementEdit
     * @restrict E
     * @scope
     * @param {? Function=} isDirtyFn Function returning the dirtiness status of the component.
     * @param {< String} restriction Restriction object.
     * @param {? Function=} submitFn Function defined in outer scope to validate restriction edit.
     * @param {< Object} uriContext The {@link resourceLocationsModule.object:UriContext uriContext}, as defined on the resourceLocationModule.
     * @param {< Function=} getSupportedRestrictionTypesFn A function that returns list of restriction types that are supported for editing for a given item.
     * @description
     * The restrictionManagementEdit Angular component is designed to be able to edit restrictions.
     */
    .component('restrictionManagementEdit', {
        controller: 'RestrictionManagementEditController',
        templateUrl: 'restrictionManagementEditTemplate.html',
        bindings: {
            isDirtyFn: '=?',
            restriction: '<',
            getSupportedRestrictionTypesFn: '<?',
            submitFn: '=?',
            uriContext: '<'
        }
    });
