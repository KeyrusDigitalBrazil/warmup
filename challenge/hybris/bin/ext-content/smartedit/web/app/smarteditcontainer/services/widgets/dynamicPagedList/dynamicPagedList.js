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
     * @name dynamicPagedListModule
     * @description
     * # The dynamicPagedListModule
     *
     * The dynamicPagedListModule provides a directive to display a paginated list of items with custom renderers where the data is paged and fetched on demand from the provided API.
     * This directive also allows the user to search and sort the list.
     *
     */
    angular.module('dynamicPagedListModule', ['pascalprecht.translate', 'ui.bootstrap', 'smarteditCommonsModule', 'seConstantsModule', 'ngSanitize', 'paginationFilterModule',
            'yDropDownMenuModule', 'yPaginationModule', 'smarteditServicesModule', 'yLoDashModule', 'yDataTableModule', 'hasOperationPermissionModule', 'filterByFieldFilterModule'
        ])
        .controller('dynamicPagedListController', function($log, $filter, SORT_DIRECTIONS, lodash, restServiceFactory) {

            this.orderByColumn = function(columnKey, columnSortMode) {
                this.internalSortBy = columnKey.property;
                this.columnSortMode = columnSortMode;

                if (columnKey.sortable) {
                    this.currentPage = 1;
                    this.loadItems();
                }
            }.bind(this);

            this.loadItems = function() {

                this.ready = false;
                var params = lodash.merge(lodash.cloneDeep(this.config.queryParams || {}), {
                    currentPage: this.currentPage - 1,
                    mask: this.mask,
                    pageSize: this.config.itemsPerPage,
                    sort: this.internalSortBy + ':' + this.columnSortMode
                });

                return restServiceFactory.get(this.config.uri).get(params).then(function(result) {
                    this.items = result.response;

                    if (this.items.length === 0) {
                        $log.warn('PagedList: No items returned to display');
                    }

                    this.totalItems = result.pagination.totalCount;
                    this.currentPage = parseInt(result.pagination.page) + 1;

                    this.ready = true;

                    return result;
                }.bind(this), function() {
                    $log.error('PagedList: Failed to load items');
                });

            };

            this.onCurrentPageChange = function(newCurrentPage) {

                this.currentPage = newCurrentPage;
                this.loadItems();

            };

            this._validateInput = function() {

                if (!this.config) {
                    throw "config object is required";
                }

                if (!(this.config.keys instanceof Array)) {
                    throw "keys must be an array";
                }

                if (this.config.keys.length < 1) {
                    throw "dynamic paged list requires at least one key";
                }

                if (!this.config.uri) {
                    throw "dynamic paged list requires a uri to fetch the list of items";
                }

            };

            this._buildColumnData = function() {

                this.columns = this.config.keys.map(function(key) {
                    var column = lodash.cloneDeep(key);
                    column.renderer = this.config.renderers[key.property];
                    return column;
                }.bind(this));

            };

            this.$onInit = function() {

                this._validateInput();

                this.ready = false;

                this.totalItems = 0;
                this.currentPage = 1;

                this.columnSortMode = this.config.reversed ? SORT_DIRECTIONS.DESC : SORT_DIRECTIONS.ASC;
                this.internalSortBy = lodash.cloneDeep(this.config.sortBy);
                this.oldMask = lodash.cloneDeep(this.mask);

                this.columns = [];
                this._buildColumnData();
                this.loadItems();

                if (typeof this.getApi === 'function') {
                    this.getApi({
                        $api: this.api
                    });
                }
            };

            this.$doCheck = function() {

                if (this.oldMask !== this.mask) {
                    this.oldMask = this.mask;

                    //set page to 1 and reload data
                    this.currentPage = 1;
                    this.loadItems();
                }

            };

            /**
             * @ngdoc object
             * @name dynamicPagedListModule.object:api
             * @description
             * The dynamic paged list api object exposing public functionality
             */
            this.api = {

                /**
                 * @ngdoc method
                 * @name reloadItems
                 * @methodOf dynamicPagedListModule.object:api
                 * @description
                 * Function that reloads the items of the paged list.
                 */
                reloadItems: this.loadItems.bind(this)
            };


        })
        /**
         * @ngdoc directive
         * @name dynamicPagedListModule.directive:dynamicPagedList
         * @scope
         * @restrict E
         * @element dynamic-paged-list
         *
         * @description
         * Component responsible for displaying a server-side paginated list of items with custom renderers. It allows the user to search and sort the list.
         *
         * @param {<Object} config The configuration object containing the paged list properties.
         * @param {<Number} config.itemsPerPage The number of items to display per page.
         * @param {<Array} config.keys An array of object(s) that detemine the properties of each column.
         * It requires a property - unique id of the column, i18n - displayable column name and sortable - optional flag that enables column sorting.
         * The properties must match one at least one of the descriptors' keys and will be used as the columns of the table. The related i18n keys are used for the column headers' title.
         * In order for sorting to work, the REST endpoint must support for sorting of that field.
         * @param {<Object} config.renderers An object that contains HTML renderers for specific keys property. A renderer is a function that returns a HTML string. This function has access to the current item and the injected context(as $ctrl.config.injectedContext).
         * @param {<String} config.uri The uri to fetch the list from. The REST end point represented by uri must support paging and accept parameters such as currentPage, pageSize, mask and sort for fetching and fitering paged data.
         * 
         * for example: If the uri = '/someuri', then the dynamic-paged-list component will fetch paged information by making call to the API such as:
         * ```
         * /someuri?currentPage=0&pageSize=10
         * /someuri?currentPage=0&pageSize=10&mask=home
         * /someuri?currentPage=0&pageSize=10&sort=name:asc
         * ```
         * 
         * @param {<Object=} config.queryParams An object containing list of query params that needed to be passe to the uri.
         * @param {<Object} config.injectedContext An object that exposes values or functions to the directive. It can be used by the custom HTML renderers to bind a function to a click event for example.
         * @param {<String} config.sortBy The column name to sort the results.
         * @param {<Boolean?} config.reversed If set to true, the list will be sorted descending.
         * @param {<Boolean?} config.displayCount If set to true the size of the filtered collection will be displayed.
         * @param {<String?} mask The string value used to filter the result.
         * @param {<Object?} getApi Exposes the dynamic paged list module's api object
         *
         * @example
         * <em>Example of a <strong>renderers</strong> object</em>
         *
         * <pre>
         *          renderers = {
         *              name: function(item, key) {
         *                  return "<a data-ng-click=\"$ctrl.config.injectedContext.onLink( item.path )\">{{ item[key.property] }}</a>";
         *              }
         *          };
         * </pre>
         *
         * <em>Example of an <strong>injectedContext</strong> object</em>
         * <pre>
         *          injectedContext = {
         *              onLink: function(link) {
         *                  if (link) {
         *                      var experiencePath = this._buildExperiencePath();
         *                      iframeManagerService.setCurrentLocation(link);
         *                      $location.path(experiencePath);
         *                  }
         *              }.bind(this),
         *              dropdownItems: []
         *          };
         * </pre>
         *
         * */
        .component('dynamicPagedList', {
            templateUrl: 'dynamicPagedListTemplate.html',
            controller: 'dynamicPagedListController',
            bindings: {
                config: '<',
                mask: '<?',
                getApi: '&?'
            }
        });

})();
