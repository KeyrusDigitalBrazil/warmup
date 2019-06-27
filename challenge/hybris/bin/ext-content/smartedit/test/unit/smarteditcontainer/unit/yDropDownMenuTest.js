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
describe('YDropDownMenuController', function() {

    // service
    var YDropDownMenuController;

    // injected
    var $q,
        scope;

    // mocks
    var spy_$templateCache,
        spy_getEncodedString;

    // data
    var mocked_dropdownItems = [],
        mocked_selectedItem = [],
        mockedDropdownItems = {
            callbackAndTemplate: [{
                callback: null,
                template: null
            }],
            callbackAndTemplateUrl: [{
                callback: null,
                templateUrl: null
            }],
            templateAndTemplateUrl: [{
                template: null,
                templateUrl: null
            }],
            callbackString: [{
                callback: "string"
            }],
            templateInteger: [{
                template: 0
            }],
            templateUrlInteger: [{
                templateUrl: 0
            }],
            callback: [{
                callback: function() {}
            }],
            template: [{
                template: "MOCKED_TEMPLATE"
            }],
            templateUrl: [{
                templateUrl: "MOCKED_TEMPLATE_URL"
            }]

        },
        mocked_encoded_string = "MOCKED_ENCODED_STRING",
        mocked_encoded_template_url = "yDropdownItem_" + mocked_encoded_string + "_Template.html";

    beforeEach(function() {

        angular.mock.module('yDropDownMenuModule', function($provide) {
            spy_$templateCache = jasmine.createSpyObj('spy_$templateCache', ['get', 'put']);
            $provide.value('$templateCache', spy_$templateCache);
            spy_getEncodedString = jasmine.createSpy('getEncodedString');
            $provide.value('getEncodedString', spy_getEncodedString);
        });

        inject(function($rootScope, $componentController, _$q_) {
            $q = _$q_;
            scope = $rootScope.$new();
            YDropDownMenuController = $componentController(
                'yDropDownMenu', {
                    $scope: scope
                }, {
                    dropdownItems: mocked_dropdownItems,
                    selectedItem: mocked_selectedItem
                }
            );
        });

    });

    describe('$onChanges', function() {

        it('throws an exception if more than one parameter is sent.', function() {

            // Given
            YDropDownMenuController.dropdownItems = mockedDropdownItems.callbackAndTemplate;

            // When
            expect(function() {
                YDropDownMenuController.$onChanges();
            }).toThrow(
                new Error("Please provide only one of callback, template or templateUrl.")
            );

            // Given
            YDropDownMenuController.dropdownItems = mockedDropdownItems.callbackAndTemplateUrl;

            // When
            expect(function() {
                YDropDownMenuController.$onChanges();
            }).toThrow(
                new Error("Please provide only one of callback, template or templateUrl.")
            );

            // Given
            YDropDownMenuController.dropdownItems = mockedDropdownItems.templateAndTemplateUrl;

            // When
            expect(function() {
                YDropDownMenuController.$onChanges();
            }).toThrow(
                new Error("Please provide only one of callback, template or templateUrl.")
            );

        });

        it('throws an exception if the sent parameter is not of the expected type.', function() {

            // Given
            YDropDownMenuController.dropdownItems = mockedDropdownItems.callbackString;

            // When
            expect(function() {
                YDropDownMenuController.$onChanges();
            }).toThrow(
                new Error("Please provide a parameter of a proper type: callback(Function), template(String) or templateUrl(String).")
            );

            // Given
            YDropDownMenuController.dropdownItems = mockedDropdownItems.templateInteger;

            // When
            expect(function() {
                YDropDownMenuController.$onChanges();
            }).toThrow(
                new Error("Please provide a parameter of a proper type: callback(Function), template(String) or templateUrl(String).")
            );

            // Given
            YDropDownMenuController.dropdownItems = mockedDropdownItems.templateUrlInteger;

            // When
            expect(function() {
                YDropDownMenuController.$onChanges();
            }).toThrow(
                new Error("Please provide a parameter of a proper type: callback(Function), template(String) or templateUrl(String).")
            );

        });

        it('sets the proper templateUrl according to the sent parameter.', function() {

            // Given
            YDropDownMenuController.dropdownItems = mockedDropdownItems.callback;

            // When
            YDropDownMenuController.$onChanges();
            scope.$digest();

            // Assert
            expect(YDropDownMenuController.clonedDropdownItems[0].templateUrl).toBe("yDropdownDefaultItemTemplate.html");

            // Given
            YDropDownMenuController.dropdownItems = mockedDropdownItems.template;
            spy_getEncodedString.and.returnValue(mocked_encoded_string);

            // When
            YDropDownMenuController.$onChanges();
            scope.$digest();

            // Assert
            expect(YDropDownMenuController.clonedDropdownItems[0].templateUrl).toBe(mocked_encoded_template_url);

            // Given
            YDropDownMenuController.dropdownItems = mockedDropdownItems.templateUrl;

            // When
            YDropDownMenuController.$onChanges();
            scope.$digest();

            // Assert
            expect(YDropDownMenuController.clonedDropdownItems[0].templateUrl).toBe("MOCKED_TEMPLATE_URL");

        });

    });

});
