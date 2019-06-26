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
angular.module('homePageLinkModule', [])
    /**
     * @ngdoc directive
     * @name pageListLinkModule.directive:homePageLink
     * @scope
     * @restrict E
     * @element <home-page-link></home-page-link>
     *
     * @description
     * Directive that displays a link to the main storefront page.
     * 
     * @param {< Object} catalog Object representing the provided catalog. 
     * @param {< Boolean} catalogVersion Object representing the provided catalog version. 
     * @param {< String} siteId The ID of the site the provided catalog is associated with.  
     */
    .directive('homePageLink', function() {
        return {
            templateUrl: 'homePageLinkTemplate.html',
            restrict: 'E',
            controller: function(experienceService) {
                this.onClick = function() {
                    experienceService.loadExperience({
                        siteId: this.siteId,
                        catalogId: this.catalog.catalogId,
                        catalogVersion: this.catalogVersion.version
                    });
                };
            },
            controllerAs: 'ctrl',
            bindToController: {
                catalog: '<',
                catalogVersion: '<',
                siteId: '<'
            }
        };
    });
