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
/* jshint unused:false, undef:false */
var setupComponentServiceMock, setupController;

var componentMenuSetup = function() {
    setupComponentServiceMock = function() {
        componentService = jasmine.createSpyObj('ComponentService', ['loadComponentTypes', 'addExistingComponent', 'addNewComponent']);
        componentService.loadComponentTypes.andCallFake(function(component) {
            return {
                then: function() {
                    return component;
                }
            };
        });
        componentService.addExistingComponent.and.returnValue();
        componentService.addNewComponent.and.returnValue();

        return componentService;
    };

    setupComponentMenuController = function($controller, $scope, componentService) {
        return $controller('ComponentMenuController as menuCtrl', {
            $scope: directiveScope,
            ComponentService: componentService,
        });

    };

};
