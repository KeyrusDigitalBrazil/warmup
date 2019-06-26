/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
describe('Test Personalizationsmartedit Combined View Module', function() {
    var mockModules = {};
    setupMockModules(mockModules); // jshint ignore:line

    var personalizationsmarteditCombinedView, personalizationsmarteditCombinedViewCommons, scope, modalService, controller;
    var mockCombinedView = {
        enabled: false,
        selectedItems: []
    };
    var mockExperienceData = {
        catalogDescriptor: {
            catalogId: "myId"
        }
    };
    var mockVariation = {
        code: "testVariation"
    };
    var mockCustomization = {
        code: "testCustomization",
        variations: [mockVariation]
    };

    beforeEach(module('personalizationsmarteditCombinedViewModule', function($provide) {
        mockModules.personalizationsmarteditContextService = jasmine.createSpyObj('personalizationsmarteditContextService', ['getCombinedView', 'getSeExperienceData']);
        $provide.value('personalizationsmarteditContextService', mockModules.personalizationsmarteditContextService);
    }));

    beforeEach(module('personalizationsmarteditServicesModule', function($provide) {
        mockModules.personalizationsmarteditRestService = jasmine.createSpyObj('personalizationsmarteditRestService', ['getCustomizations']);
        $provide.value('personalizationsmarteditRestService', mockModules.personalizationsmarteditRestService);
    }));

    beforeEach(inject(function(_$rootScope_, _$controller_, _$q_, _personalizationsmarteditCombinedView_, _personalizationsmarteditCombinedViewCommons_, _modalService_) {
        scope = _$rootScope_.$new();
        controller = _$controller_;
        personalizationsmarteditCombinedView = _personalizationsmarteditCombinedView_;
        personalizationsmarteditCombinedViewCommons = _personalizationsmarteditCombinedViewCommons_;
        modalService = _modalService_;

        mockModules.modalService.open.and.callFake(function() {
            return _$q_.defer().promise;
        });

        mockModules.personalizationsmarteditContextService.getCombinedView.and.callFake(function() {
            return mockCombinedView;
        });

        mockModules.personalizationsmarteditContextService.getSeExperienceData.and.callFake(function() {
            return mockExperienceData;
        });

        mockModules.personalizationsmarteditRestService.getCustomizations.and.callFake(function() {
            var deferred = _$q_.defer();
            deferred.resolve({
                customizations: [mockCustomization, mockCustomization],
                pagination: {
                    count: 5,
                    page: 0,
                    totalCount: 5,
                    totalPages: 1
                }
            });
            return deferred.promise;
        });

    }));

    it('GIVEN that personalizationsmarteditCombinedView is instantiated it contains proper functions', function() {
        expect(personalizationsmarteditCombinedView.openManagerAction).toBeDefined();
    });

    it('GIVEN that personalizationsmarteditCombinedViewCommons is instantiated it contains proper functions', function() {
        expect(personalizationsmarteditCombinedViewCommons.updatePreview).toBeDefined();
        expect(personalizationsmarteditCombinedViewCommons.getVariationsForPreviewTicket).toBeDefined();
        expect(personalizationsmarteditCombinedViewCommons.combinedViewEnabledEvent).toBeDefined();
    });

    it('GIVEN that modal for configure combined view is open, proper functions should be called', function() {
        personalizationsmarteditCombinedView.openManagerAction();
        expect(modalService.open).toHaveBeenCalled();
    });

    it('GIVEN that personalizationsmarteditCombinedViewMenuController properties in scope are instantiated properly', function() {
        controller('personalizationsmarteditCombinedViewMenuController', {
            $scope: scope
        });
        expect(scope.combinedView).toBeDefined();
        expect(scope.selectedItems).toBeDefined();
        expect(scope.getClassForElement).toBeDefined();
        expect(scope.combinedView).toBe(mockCombinedView);
    });

    it('GIVEN that personalizationsmarteditCombinedViewController properties in scope are instantiated properly', function() {
        scope.modalManager = {};
        scope.modalManager.setButtonHandler = function() {};
        scope.modalManager.disableButton = function() {};
        controller('personalizationsmarteditCombinedViewController', {
            $scope: scope
        });
        personalizationsmarteditCombinedView.openManagerAction();

        expect(scope.combinedView).toBeDefined();
        expect(scope.selectedItems).toBeDefined();
        expect(scope.selectedElement).toBeDefined();
        expect(scope.selectionArray).toBeDefined();
        expect(mockModules.personalizationsmarteditContextService.getCombinedView).toHaveBeenCalled();
        expect(scope.combinedView).toBe(mockCombinedView);
    });


});
