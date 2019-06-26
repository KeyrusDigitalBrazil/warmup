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
angular.module('yDropDownMenuModule', [
        "functionsModule"
    ])

    .controller('YDropDownMenuController', function($templateCache, getEncodedString) {

        var _validatePassedAttribute = function(dropdownItem) {
            var expectedAttributesAndTypes = {
                callback: "function",
                template: "string",
                templateUrl: "string"
            };
            var expectedAttributes = Object.keys(expectedAttributesAndTypes);
            var passedAttributes = Object.keys(dropdownItem);
            var validatedAttribute = passedAttributes.filter(function(attribute) {
                return (expectedAttributes.indexOf(attribute) !== -1);
            }.bind(this));
            if (validatedAttribute.length !== 1) {
                throw new Error("Please provide only one of callback, template or templateUrl.");
            }
            if (typeof dropdownItem[validatedAttribute[0]] !== expectedAttributesAndTypes[validatedAttribute[0]]) {
                throw new Error("Please provide a parameter of a proper type: callback(Function), template(String) or templateUrl(String).");
            }
            return validatedAttribute[0];
        }.bind(this);

        var _cacheDropdownItemTemplate = function(dropdownItem) {
            var nameOfCachedTemplate = "yDropdownItem_" + getEncodedString(dropdownItem.template) + "_Template.html";
            if (!$templateCache.get(nameOfCachedTemplate)) {
                $templateCache.put(nameOfCachedTemplate, dropdownItem.template);
            }
            return nameOfCachedTemplate;
        }.bind(this);

        this.setTemplateUrl = function(dropdownItem) {
            switch (_validatePassedAttribute(dropdownItem)) {
                case 'callback':
                    dropdownItem.templateUrl = "yDropdownDefaultItemTemplate.html";
                    break;
                case 'template':
                    // replacing 'template' with cached 'templateUrl'
                    dropdownItem.templateUrl = _cacheDropdownItemTemplate(dropdownItem);
                    delete dropdownItem.template;
                    break;
                default:
                    break;
            }
        };

        this.$onChanges = function() {

            // cloning binded object
            this.clonedDropdownItems = angular.copy(this.dropdownItems);

            this.clonedDropdownItems.forEach(function(dropdownItem) {
                // setting 'condition'
                dropdownItem.condition = dropdownItem.condition || function() {
                    return true;
                };
                // setting 'templateUrl'
                this.setTemplateUrl(dropdownItem);
            }.bind(this));

        };
    })

    /**
     * @ngdoc directive
     * @name yDropDownMenuModule.directive:yDropDownMenu
     * @scope
     * @restrict E
     *
     * @description
     * yDropDownMenu builds a drop-down menu. It has two parameters
     * dropdownItems and selectedItem. The dropdownItems is an array of objects
     * which must contain either an i18n key associated to a callback function,
     * a static HTML template or a templateUrl leading to an external HTML file.
     * An optional condition can be added to define whether the item is to get
     * rendered.
     * The selectedItem is the object associated to the drop-down. It is passed
     * as argument for the callback of dropdownItems.
     * For a given item, if a condition callback is defined, the item will show
     * only if this callback returns true
     * Example:
     * <pre>
     *	this.dropdownItems = [{
     *       key: 'my.translated.key',
     *       callback: function() {
     *           doSomething();
     *       }.bind(this)
     *   }, {
     *       template: '<my-component />'
     *   }, {
     *       templateUrl: 'myComponentTemplate.html'
     *   }];
     * </pre>
     *
     * @param {<Array} dropdownItems An object containing parameters allowing
     * for the selection of a cached HTML template used to render the dropdown
     * menu item.
     * @param {Function?} dropdownItems.callback Function will be called when
     * the user click on the drop down item.
     * @param {Function?} dropdownItems.condition Function will be called to
     * check whether the item is to be rendered.
     * @param {String?} dropdownItems.icon Identifier of the icon associated to
     * the dropdown item
     * @param {String?} dropdownItems.key I18n key for the label associated to
     * the dropdown item
     * @param {String?} dropdownItems.template Static HTML template used to
     * render the dropdown item.
     * @param {String?} dropdownItems.templateUrl HTML file used to render the
     * dropdown item.
     * @param {Object?} selectedItem An object defining the context of the page
     * associated to the dropdown item.
     */
    .component(
        'yDropDownMenu', {
            templateUrl: 'yDropDownMenuTemplate.html',
            controller: 'YDropDownMenuController',
            bindings: {
                dropdownItems: '<',
                selectedItem: '<'
            }
        }
    );
