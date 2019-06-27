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
 * @name itemManagementModule
 * @description
 * This module contains the itemManager component.
 */
angular.module('itemManagementModule', ['functionsModule'])
    .constant('ITEM_MANAGEMENT_EDITOR_ID', 'se-item-management-editor')
    .controller('ItemManagementController', function($q, ITEM_MANAGEMENT_EDITOR_ID) {

        var supportedModes = ['add', 'edit', 'create'];

        this.editorId = ITEM_MANAGEMENT_EDITOR_ID;

        this._submitInternal = function() {
            switch (this.mode.toLowerCase()) {
                case "add":
                    return $q.when(this.item);
                case "edit":
                    return this.submit();
                case "create":
                    return this.submit().then(function(itemResponse) {
                        return itemResponse;
                    }.bind(this));

                default:
                    throw "ItemManagementController - The given mode [" + this.mode + "] has not been implemented for this component";
            }
        }.bind(this);

        this._isDirtyLocal = function _isDirtyLocal() {
            if (this.isDirtyInternal) {
                return this.isDirtyInternal();
            }
            return false;
        }.bind(this);

        // all overridden by the embedded generic editor
        this.isDirty = function isDirty() {};
        this.submit = function submit() {};
        this.reset = function reset() {};

        this.$onInit = function $onInit() {
            if (supportedModes.indexOf(this.mode) === -1) {
                throw "ItemManagementController.$onInit() - Mode not supported: " + this.mode;
            }
            this.submitFunction = this._submitInternal;
            this.isDirty = this._isDirtyLocal;

            if (!this.componentType && this.item) {
                this.componentType = this.item.typeCode;
            }
            if (!this.item) {
                this.itemId = null;
            }

            if (this.item && this.item.uuid) {
                this.itemId = this.item.uuid;
            } else if (this.item && this.item.uid) {
                this.itemId = this.item.uid;
            }

        };

    })


    /**
     * @ngdoc directive
     * @name itemManagementModule.itemManager
     * @description
     * The purpose of this component is handle the logic of displaying the fields and PUT/POST logic
     * to add, edit or create CMS Items
     *
     * @param {Object} item An item to use as preset fields in the editor
     * @param {Object} uriContext A {@link resourceLocationsModule.object:UriContext UriContext}
     * @param {String} mode Either 'edit', 'add', or 'create'
     * @param {String} contentApi A URI for GET/PUT/POST of Item content
     * @param {String} structureApi A URI for fetching the structure of the editor
     * @param {String} componentType Component type code for the underlying generic editor
     * @param {Function} isDirty A function defined within itemManager. Returns true when editing an Item, creating a new
     * non-empty Item, or adding an existing Item.
     * @param {Function} submitFunction A function defined within itemManager. Call this function to invoke submit,
     * triggering any PUT/POST operations and returning the item created/edited, wrapped in a promise.
     */
    .component('itemManager', {
        controller: "ItemManagementController",
        templateUrl: 'itemManagementTemplate.html',
        bindings: {
            // in
            item: '<',
            uriContext: '<',
            mode: '<', // add, edit, or create
            contentApi: '<',
            structureApi: '<',
            componentType: '<?',
            // out
            isDirty: '=',
            submitFunction: '='
        }
    });
