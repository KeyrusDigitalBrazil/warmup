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
 * @name yDataTableModule
 * @description
 * # The yDataTableModule
 *
 * The yDataTableModule is used to print the input data in the form of a table and also allowing to sort by column.
 *
 */
angular.module('yDataTableModule', ['seConstantsModule'])
    .controller('yDataTableController', function(SORT_DIRECTIONS) {


        this._validateInput = function() {
            if (!(this.columns instanceof Array)) {
                throw "Columns must be an array";
            }

            if (!(this.config instanceof Object)) {
                throw "Config must be an object";
            }
        };

        this.$onInit = function() {

            this._validateInput();

            this.columnWidth = 100 / this.columns.length;
            this.columnToggleReversed = this.config.reversed;
            this.columnSortMode = this.config.reversed ? SORT_DIRECTIONS.DESC : SORT_DIRECTIONS.ASC;

            this.headersSortingState = {};
            this.headersSortingState[this.config.sortBy] = this.config.reversed;
            this.visibleSortingHeader = this.config.sortBy;

        };

        this.sortColumn = function(columnKey) {

            if (columnKey.sortable) {

                this.columnToggleReversed = !this.columnToggleReversed;
                this.headersSortingState[columnKey.property] = this.columnToggleReversed;
                this.visibleSortingHeader = columnKey.property;

                this.currentPage = 1;
                this.internalSortBy = columnKey.property;
                this.columnSortMode = this.columnToggleReversed ? SORT_DIRECTIONS.DESC : SORT_DIRECTIONS.ASC;

                this.onSortColumn({
                    $columnKey: columnKey,
                    $columnSortMode: this.columnSortMode
                });

            }

        }.bind(this);

    })
    /**
     * @ngdoc directive
     * @name yDataTableModule.directive:yDataTable
     * @scope
     * @restrict E
     * @element y-data-table
     * 
     * @description
     * Component used to print the provided data in the form of table and also enable sorting by column.
     * 
     * @param {<Array} columns An array containiung the properties of the column.
     * @param {<String} columns.key A unique identification key for the column
     * @param {<String} columns.i18n A printable column name.
     * @param {<Function} columns.renderer A renderer function that returns a template for the column value.
     * @param {<Boolean} columns.sortable Boolean that determines if the column is sortable or not. [Default = false]
     * @param {<Object} config The config object that contains reversed, sortBy params.
     * @param {<Number} items The items to be displayed
     * @param {&Function} onSortColumn The function that is called with the column key and the sort direction every time sorting is done. The invoker 
     * can bind this to a custom function to act and fetch results based on new data.
     */
    .component('yDataTable', {
        templateUrl: 'yDataTableTemplate.html',
        controller: 'yDataTableController',
        bindings: {
            columns: '<',
            config: '<',
            items: '<',
            onSortColumn: '&'
        }
    });
