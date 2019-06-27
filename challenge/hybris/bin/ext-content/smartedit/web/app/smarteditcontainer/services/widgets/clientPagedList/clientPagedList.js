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
     * @name clientPagedListModule
     * @description
     * # The clientPagedListModule
     *
     * The clientPagedListModule provides a directive to display a paginated list of items with custom renderers.
     * This directive also allows the user to search and sort the list.
     *
     */
    angular.module('clientPagedListModule', ['pascalprecht.translate', 'ui.bootstrap', 'ngSanitize', 'paginationFilterModule',
            'smarteditCommonsModule', 'yDropDownMenuModule', 'filterByFieldFilterModule',
            'hasOperationPermissionModule'
        ])

        /**
         * @ngdoc directive
         * @name clientPagedListModule.directive:clientPagedList
         * @scope
         * @restrict E
         * @element client-paged-list
         *
         * @description
         * Directive responsible for displaying a client-side paginated list of items with custom renderers. It allows the user to search and sort the list.
         *
         * @param {Array} items An array of item descriptors.
         * @param {Array} keys An array of object(s) with a property and an i18n key.
         * The properties must match one at least one of the descriptors' keys and will be used as the columns of the table. The related i18n keys are used for the column headers' title.
         * @param {Object} renderers An object that contains HTML renderers for specific keys property. A renderer is a function that returns a HTML string. This function has access to the current item and key.
         * @param {Object} injectedContext An object that exposes values or functions to the directive. It can be used by the custom HTML renderers to bind a function to a click event for example.
         * @param {Boolean} reversed If set to true, the list will be sorted descending.
         * @param {Number} itemsPerPage The number of items to display per page.
         * @param {Object} query The ngModel query object used to filter the list.
         * @param {Boolean} displayCount If set to true the size of the filtered collection will be displayed.
         * @param {Array} itemFilterKeys (OPTIONAL) An array of object keys that will determine which fields the {@link filterByFieldFilterModule.filter:filterByField filterByFieldFilter}
         * will use to filter through the items.
         *
         * @example
         * <pre>
         *          <client-paged-list items="pageListCtl.pages"
         *                      keys="[{
         *                              property:'title',
         *                              i18n:'pagelist.headerpagetitle'
         *                              },{
         *                              property:'uid',
         *                              i18n:'pagelist.headerpageid'
         *                              },{
         *                              property:'typeCode',
         *                              i18n:'pagelist.headerpagetype'
         *                              },{
         *                              property:'template',
         *                              i18n:'pagelist.headerpagetemplate'
         *                              }]"
         *                      renderers="pageListCtl.renderers"
         *                      injectedContext="pageListCtl.injectedContext"
         *                      sort-by="'title'"
         *                      reversed="true"
         *                      items-per-page="10"
         *                      query="pageListCtl.query.value"
         *                      display-count="true"
         *            ></paged-list>
         * </pre>
         *
         * <em>Example of a <strong>renderers</strong> object</em>
         *
         * <pre>
         *          renderers = {
         *              name: function(item, key) {
         *                  return "<a data-ng-click=\"injectedContext.onLink( item.path )\">{{ item[key.property] }}</a>";
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
         *              }.bind(this)
         *          };
         * </pre>
         *
         * */
        .directive('clientPagedList', function($filter) {

            return {
                templateUrl: 'clientPagedList.html',
                restrict: 'E',
                transclude: false,
                scope: {
                    items: '=',
                    itemsPerPage: '=',
                    totalItems: '=?',
                    keys: '=',
                    renderers: "=",
                    injectedContext: '=',
                    identifier: '=',
                    sortBy: '=',
                    reversed: '=',
                    query: '=',
                    displayCount: '=',
                    dropdownItems: '=',
                    selectedItem: '=',
                    itemFilterKeys: '<?'
                },
                link: function(scope) {
                    scope.totalItems = 0;
                    scope.currentPage = 1;
                    scope.filteredItems = [];

                    scope.columnWidth = 100 / scope.keys.length;
                    scope.columnToggleReversed = scope.reversed;

                    scope.headersSortingState = {};
                    scope.headersSortingState[scope.sortBy] = scope.reversed;
                    scope.visibleSortingHeader = scope.sortBy;

                    var orderByUnwatch = scope.$watch('sortBy', function() {
                        scope.items = $filter('orderBy')(scope.items, scope.sortBy, scope.columnToggleReversed);
                        if (scope.sortBy) {
                            orderByUnwatch();
                        }
                    });

                    scope.$watch('items', function() {
                        scope.totalItems = scope.items.length;
                    });

                    scope.filterCallback = function(filteredList) {
                        scope.totalItems = filteredList.length;
                    };

                    scope.getFilterKeys = function() {
                        return scope.itemFilterKeys || [];
                    };

                    scope.orderByColumn = function(columnKey) {
                        scope.columnToggleReversed = !scope.columnToggleReversed;
                        scope.headersSortingState[columnKey] = scope.columnToggleReversed;
                        scope.visibleSortingHeader = columnKey;
                        scope.items = $filter('orderBy')(scope.items, columnKey, scope.columnToggleReversed);
                    };
                }
            };

        });

})();
