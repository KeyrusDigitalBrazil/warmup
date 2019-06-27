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
     * @name catalogVersionDetailsModule
     * @description
     * This module contains the {@link catalogVersionDetailsModule.component:catalogVersionDetails} component.
     */
    angular.module('catalogVersionDetailsModule', ['homePageLinkModule'])
        /**
         * @ngdoc object
         * @name catalogVersionDetailsModule.object:CATALOG_DETAILS_COLUMNS
         *
         * @description
         * Injectable angular constant<br/>
         * This object provides an enumeration with values for each of the possible places to add items to 
         * extend the {@link catalogVersionDetailsModule.component:catalogVersionDetails} component. Currently, 
         * the available options are CATALOG_DETAILS_COLUMNS.LEFT and CATALOG_DETAILS_COLUMNS.RIGHT. 
         *
         */
        .constant('CATALOG_DETAILS_COLUMNS', {
            LEFT: 'left',
            RIGHT: 'right'
        })

        /**
         * @ngdoc service
         * @name catalogVersionDetailsModule.service:catalogDetailsService
         * @description
         *
         * The catalog details Service makes it possible to add items in form of directive
         * to the catalog details directive 
         * 
         */
        .service('catalogDetailsService', function(CATALOG_DETAILS_COLUMNS) {
            this._customItems = {
                left: [],
                right: []
            };

            /**
             * @ngdoc method
             * @name catalogVersionDetailsModule.service:catalogDetailsService#addItems
             * @methodOf catalogVersionDetailsModule.service:catalogDetailsService
             *
             * @description
             * This method allows to add a new item/items to the template array.
             *
             * @param {Array} items An array that hold a list of items.
             * @param {String=} column The place where the template will be added to. If this value is empty 
             * the template will be added to the left side by default. The available places are defined in the 
             * constant {@link catalogVersionDetailsModule.object:CATALOG_DETAILS_COLUMNS}
             */
            this.addItems = function(items, column) {
                if (column === CATALOG_DETAILS_COLUMNS.RIGHT) {
                    this._customItems.right = this._customItems.right.concat(items);
                } else {
                    this._customItems.left = this._customItems.left.concat(items);
                }
            };

            /**
             * @ngdoc method
             * @name catalogVersionDetailsModule.service:catalogDetailsService#getItems
             * @methodOf catalogVersionDetailsModule.service:catalogDetailsService
             *
             * @description
             * This retrieves the list of items currently extending catalog version details components. 
             *
             */
            this.getItems = function() {
                return this._customItems;
            };

            // Add default items
            this.addItems([{
                include: 'homePageLinkWrapperTemplate.html'
            }], CATALOG_DETAILS_COLUMNS.LEFT);
        })
        .controller('catalogVersionDetailsController', function(catalogDetailsService) {
            this.$onInit = function() {
                var customItems = catalogDetailsService.getItems();
                this.leftItems = customItems.left;
                this.rightItems = customItems.right;
            };
        })
        /**
         * @ngdoc directive
         * @name catalogVersionDetailsModule.component:catalogVersionDetails
         * @scope
         * @restrict E
         * @element catalog-version-details
         *
         * @description
         * Component responsible for displaying a catalog version details. Contains a link, called homepage, that 
         * redirects to the default page with the right experience (site, catalog, and catalog version). 
         * 
         * Can be extended with custom items to provide new links and functionality. 
         *
         * @param {<Object} catalog Object representing the parent catalog of the catalog version to display. 
         * @param {<Object} catalogVersion Object representing the catalog version to display. 
         * @param {<Object} activeCatalogVersion Object representing the active catalog version of the parent catalog.  
         * @param {<String} siteId The site associated with the provided catalog. 
         * */
        .component('catalogVersionDetails', {
            templateUrl: 'catalogVersionDetailsTemplate.html',
            controller: 'catalogVersionDetailsController',
            bindings: {
                catalog: '<',
                catalogVersion: '<',
                activeCatalogVersion: '<',
                siteId: '<'
            }
        });


})();
