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
    angular.module('catalogDetailsModule', ['catalogVersionDetailsModule', 'catalogVersionsThumbnailCarouselModule', 'smarteditServicesModule'])

        .controller('catalogDetailsController', function(catalogService) {

            this.$onInit = function() {
                this.activeCatalogVersion = this.catalog.versions.find(function(catalogVersion) {
                    return catalogVersion.active;
                });

                catalogService.getDefaultSiteForContentCatalog(this.catalog.catalogId).then(function(site) {
                    this.siteIdForCatalog = site.uid;
                }.bind(this));

                this.cataloDeviderImage = 'static-resources/images/icon_catalog_arrow.png';
                this.sortedCatalogVersions = this._sortCatalogVersions();
                this.collapsibleConfiguration = {
                    expandedByDefault: this.isCatalogForCurrentSite
                };
            };

            this._sortCatalogVersions = function() {
                var sortedCatalogVersions = [];
                sortedCatalogVersions.push(this.activeCatalogVersion);

                return sortedCatalogVersions.concat(this.catalog.versions.filter(function(catalogVersion) {
                    return !catalogVersion.active;
                }));
            };

        })
        /**
         * @ngdoc directive
         * @name catalogDetailsModule.component:catalogDetails
         * @scope
         * @restrict E
         * @element catalog-details
         *
         * @description
         * Component responsible for displaying a catalog details. It contains a thumbnail representing the whole 
         * catalog and the list of catalog versions available to the current user. 
         *
         * This component is currently used in the landing page. 
         *
         * @param {< String} catalog The catalog that needs to be displayed
         * @param {< Boolean} isCatalogForCurrentSite A flag that specifies if the provided catalog is associated with the selected site in the landing page
         */
        .component('catalogDetails', {
            templateUrl: 'catalogDetailsTemplate.html',
            controller: 'catalogDetailsController',
            bindings: {
                catalog: '<',
                isCatalogForCurrentSite: '<'
            }
        });
})();
