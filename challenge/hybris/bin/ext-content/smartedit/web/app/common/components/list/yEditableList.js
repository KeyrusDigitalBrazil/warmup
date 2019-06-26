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
 * @name yEditableListModule
 * @description
 *
 * The yEditableList module contains a component which allows displaying a list of elements. The items in 
 * that list can be added, removed, and re-ordered.
 * 
 */
angular.module("yEditableListModule", ['treeModule', 'yLoDashModule'])
    .controller('YEditableListController', function($q) {

        this.$onInit = function() {
            this._enableDragAndDrop = function() {
                this.dragOptions.allowDropCallback = function(event) {
                    // Just allow dropping elements of the same list.
                    return (event.sourceNode.parentUid === this.rootId);
                }.bind(this);

                this.dragOptions.onDropCallback = function() {
                    this.actions.performUpdate();
                }.bind(this);

            };

            var dropdownItems = [{
                key: 'se.ydropdownmenu.remove',
                callback: function(handle) {
                    this.actions.removeItem(handle);
                }.bind(this)
            }, {
                key: 'se.ydropdownmenu.move.up',
                condition: function(handle) {
                    return this.actions.isMoveUpAllowed(handle);
                }.bind(this),
                callback: function(handle) {
                    this.actions.moveUp(handle);
                }.bind(this)
            }, {
                key: 'se.ydropdownmenu.move.down',
                condition: function(handle) {
                    return this.actions.isMoveDownAllowed(handle);
                }.bind(this),
                callback: function(handle) {
                    this.actions.moveDown(handle);
                }.bind(this)
            }];

            this.actions = {

                fetchData: function(treeService, nodeData) {
                    this.items.map(function(item) {
                        if (item.id && !item.uid) {
                            item.uid = item.id;
                        }

                        item.parentUid = this.rootId;
                    }.bind(this));
                    nodeData.nodes = this.items;

                    return $q.when(nodeData);
                }.bind(this),
                getDropdownItems: function() {
                    return dropdownItems;
                },
                removeItem: function(treeService, handle) {
                    var nodeData = handle.$modelValue;
                    var pos = this.root.nodes.indexOf(nodeData);
                    this.root.nodes.splice(pos, 1);

                    this.performUpdate(parent, handle);
                },
                moveUp: function(treeService, handle) {
                    var nodeData = handle.$modelValue;
                    var pos = this.root.nodes.indexOf(nodeData);
                    var upperEntry = this.root.nodes[pos - 1];
                    this.root.nodes.splice(pos - 1, 2, nodeData, upperEntry);

                    this.performUpdate(parent, handle);
                },
                moveDown: function(treeService, handle) {
                    var nodeData = handle.$modelValue;
                    var pos = this.root.nodes.indexOf(nodeData);
                    var lowerEntry = this.root.nodes[pos + 1];
                    this.root.nodes.splice(pos, 2, lowerEntry, nodeData);

                    this.performUpdate(parent, handle);
                },

                isMoveUpAllowed: function(treeService, handle) {
                    var nodeData = handle.$modelValue;
                    return this.root.nodes.indexOf(nodeData) > 0;
                },

                isMoveDownAllowed: function(treeService, handle) {
                    var nodeData = handle.$modelValue;
                    var entriesArrayLength = this.root.nodes.length;

                    return this.root.nodes.indexOf(nodeData) !== (entriesArrayLength - 1);
                },

                performUpdate: function() {
                    if (this.onChange) {
                        this.onChange();
                    }
                }.bind(this),
                refreshList: function() {
                    this.fetchData(this.root);
                }
            };

            this.dragOptions = {}; // set in $onInit based on editable input binding

            this.refresh = function() {
                this.actions.refreshList();
            }.bind(this);

            if (!this.itemTemplateUrl) {
                this.itemTemplateUrl = 'yEditableListDefaultItemTemplate.html';
            }

            this.rootId = 'root' + this.id;
            if (this.editable === undefined) {
                this.editable = true;
            }

            if (this.editable === true) {
                this._enableDragAndDrop();
            }
        };
    })
    /**
     * @ngdoc directive
     * @name yEditableListModule.directive:yEditableList
     * @scope
     * @restrict E
     * @element y-editable-list
     * @description
     * The yEditableList component allows displaying a list of items. The list can be managed dynamically, by 
     * adding, removing, and re-ordering it. 
     * @param {@String} id A string used to track and identify the component. 
     * @param {<Array} items The collection of items to display in the component.
     * @param {=Function=} refresh A function that can be called to update the content of the list. 
     * @param {<Function} onChange A function that will be called whenever the content of the list changes. 
     * @param {<String=} itemTemplateUrl The path to the template to display each of the items in the list. 
     * @param {<boolean=} editable The property specifies whether the content of the list can be modified. 
     */
    .component('yEditableList', {
        templateUrl: 'yEditableListTemplate.html',
        controller: 'YEditableListController',
        controllerAs: 'ctrl',
        bindings: {
            id: '@',
            items: '<',
            refresh: '=?',
            onChange: '<',
            itemTemplateUrl: '<?',
            editable: '<?'
        }
    });
