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
describe('ConfirmationModalService', function() {

    // Service Under Test
    var confirmationModalService;

    //Angular injections
    var scope;

    // Mocks
    var modalService;
    var MODAL_BUTTON_ACTIONS;
    var MODAL_BUTTON_STYLES;

    // Set-up Mocks
    beforeEach(function() {
        angular.mock.module("modalServiceModule", function($provide) {
            modalService = jasmine.createSpyObj("modalService", ["open"]);

            MODAL_BUTTON_ACTIONS = {
                NONE: "none",
                CLOSE: "close",
                DISMISS: "dismiss"
            };

            MODAL_BUTTON_STYLES = {
                DEFAULT: "default",
                PRIMARY: "primary",
                SECONDARY: "default"
            };

            $provide.value("modalService", modalService);
            $provide.value("MODAL_BUTTON_ACTIONS", MODAL_BUTTON_ACTIONS);
            $provide.value("MODAL_BUTTON_STYLES", MODAL_BUTTON_STYLES);
        });
    });

    // Set-up Service Under Test
    beforeEach(function() {
        angular.mock.module("confirmationModalServiceModule");
        inject(function($rootScope, _confirmationModalService_) {
            confirmationModalService = _confirmationModalService_;
            scope = $rootScope.$new();
        });
    });

    it('GIVEN showOkButtonOnly WHEN modalService is called THEN it will display the modal with only the OK button', function() {
        // Arrange

        // Act 
        confirmationModalService.confirm({
            title: 'my.confirmation.title',
            description: 'my.confirmation.message',
            showOkButtonOnly: true
        });

        // Assert 
        expect(modalService.open).toHaveBeenCalledWith({
            size: 'md',
            title: 'my.confirmation.title',
            templateInline: '<div id="confirmationModalDescription">{{ "my.confirmation.message" | translate }}</div>',
            templateUrl: undefined,
            controller: undefined,
            cssClasses: 'yFrontModal',
            buttons: [{
                id: 'confirmOk',
                label: 'se.confirmation.modal.ok',
                action: MODAL_BUTTON_ACTIONS.CLOSE
            }]
        });
    });

    describe('with description', function() {

        it('confirm will call open on the modalService with the given description and title when provided with a description and title', function() {
            // Arrange

            // Act
            confirmationModalService.confirm({
                title: 'my.confirmation.title',
                description: 'my.confirmation.message'
            });

            // Assert
            expect(modalService.open).toHaveBeenCalledWith({
                size: 'md',
                title: 'my.confirmation.title',
                templateInline: '<div id="confirmationModalDescription">{{ "my.confirmation.message" | translate }}</div>',
                templateUrl: undefined,
                controller: undefined,
                cssClasses: 'yFrontModal',
                buttons: [{
                    id: 'confirmCancel',
                    label: 'se.confirmation.modal.cancel',
                    style: MODAL_BUTTON_STYLES.SECONDARY,
                    action: MODAL_BUTTON_ACTIONS.DISMISS
                }, {
                    id: 'confirmOk',
                    label: 'se.confirmation.modal.ok',
                    action: MODAL_BUTTON_ACTIONS.CLOSE
                }]
            });
        });
    });

    describe('with template', function() {

        it('confirm will call open on the modalService with the given template and title when provided with a template and title', function() {
            // Arrange

            // Act
            confirmationModalService.confirm({
                title: 'my.confirmation.title',
                template: '<div>my template</div>'
            });

            // Assert
            expect(modalService.open).toHaveBeenCalledWith({
                size: 'md',
                title: 'my.confirmation.title',
                templateInline: '<div>my template</div>',
                templateUrl: undefined,
                controller: undefined,
                cssClasses: 'yFrontModal',
                buttons: [{
                    id: 'confirmCancel',
                    label: 'se.confirmation.modal.cancel',
                    style: MODAL_BUTTON_STYLES.SECONDARY,
                    action: MODAL_BUTTON_ACTIONS.DISMISS
                }, {
                    id: 'confirmOk',
                    label: 'se.confirmation.modal.ok',
                    action: MODAL_BUTTON_ACTIONS.CLOSE
                }]
            });
        });
    });

    describe('with templateUrl', function() {

        it('confirm will call open on the modalService with the given templateUrl and title when provided with a templateUrl and title', function() {
            // Arrange

            // Act
            confirmationModalService.confirm({
                title: 'my.confirmation.title',
                templateUrl: 'myTemplate.html'
            });

            // Assert
            expect(modalService.open).toHaveBeenCalledWith({
                size: 'md',
                title: 'my.confirmation.title',
                templateInline: undefined,
                templateUrl: 'myTemplate.html',
                controller: undefined,
                cssClasses: 'yFrontModal',
                buttons: [{
                    id: 'confirmCancel',
                    label: 'se.confirmation.modal.cancel',
                    style: MODAL_BUTTON_STYLES.SECONDARY,
                    action: MODAL_BUTTON_ACTIONS.DISMISS
                }, {
                    id: 'confirmOk',
                    label: 'se.confirmation.modal.ok',
                    action: MODAL_BUTTON_ACTIONS.CLOSE
                }]
            });
        });
    });

    describe('confirm validation', function() {

        it('Confirm will return rejected promise when all configuration properties description, template, templateUrl are undefined', function() {
            // Arrange
            var promise = confirmationModalService.confirm({
                title: 'my.confirmation.title'
            });

            //Act
            promise.then(function() {
                // Promise is resolved
                fail("ConfirmationModalService.confirm return Promise should have been rejected not resolved");
            }, function(reason) {
                // Promise is rejected
                expect(reason).toBe("You must have one of the following configuration properties configured: description, template, or templateUrl");
            });
            scope.$apply();
        });

        it('Confirm will return rejected promise when more than on configuration property description, template, templateUrl is set', function() {
            // Arrange
            var promise = confirmationModalService.confirm({
                title: 'my.confirmation.title',
                templateUrl: 'myTemplate.html',
                description: 'myTemplate.html'
            });

            //Act
            promise.then(function() {
                // Promise is resolved
                fail("ConfirmationModalService.confirm return Promise should have been rejected not resolved");
            }, function(reason) {
                // Promise is rejected
                expect(reason).toBe("You have more than one of the following configuration properties configured: description, template, or templateUrl");
            });
            scope.$apply();
        });
    });
});
