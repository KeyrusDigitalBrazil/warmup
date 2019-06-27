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
describe('personalizationsmarteditCommerceCustomizationViewController', function() {
    var mockModules = {};
    setupMockModules(mockModules); // jshint ignore:line


    var personalizationsmarteditCommerceCustomizationViewController, personalizationsmarteditRestService, personalizationsmarteditContextService, scope, $q, PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES;

    var mockVariation = {
        code: "testVariation"
    };
    var mockCustomization = {
        code: "testCustomization",
        variations: [mockVariation]
    };

    var mockAction = {
        type: "mock",
        code: "mock"
    };

    var mockActionWrapper;

    var mockPromotionAction = {
        type: "cxPromotionActionData",
        code: "promotion code",
        promotionId: "promotion id"
    };

    var mockPromotionActionWrapper = {
        action: mockPromotionAction
    };

    var mockComparer = function(a1, a2) {
        return a1.type === a2.type && a1.code === a2.code;
    };

    var promotionComparer = function(a1, a2) { // jshint ignore:line
        return a1.type === a2.type && a1.promotionId === a2.promotionId;
    };

    beforeEach(module('personalizationsmarteditServicesModule', function($provide) {
        personalizationsmarteditRestService = jasmine.createSpyObj('personalizationsmarteditRestService', ['getActions']);
        $provide.value("personalizationsmarteditRestService", personalizationsmarteditRestService);
    }));

    beforeEach(module('personalizationsmarteditServicesModule', function($provide) {
        personalizationsmarteditContextService = jasmine.createSpyObj('personalizationsmarteditContextService', ['getSeData']);
        $provide.value("personalizationsmarteditContextService", personalizationsmarteditContextService);
    }));

    beforeEach(module('personalizationsmarteditCommerceCustomizationModule'));
    beforeEach(inject(function($controller, _$rootScope_, _$q_, _PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES_) {
        $q = _$q_;
        PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES = _PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES_;
        scope = _$rootScope_.$new();
        scope.modalManager = {
            setButtonHandler: function() {},
            setDismissCallback: function() {}
        };
        scope.customization = mockCustomization;
        scope.variation = mockVariation;

        personalizationsmarteditRestService.getActions.and.returnValue(
            _$q_.defer().promise
        );
        personalizationsmarteditContextService.getSeData.and.callFake(function() {
            return {
                seConfigurationData: {}
            };
        });

        personalizationsmarteditCommerceCustomizationViewController = $controller('personalizationsmarteditCommerceCustomizationViewController', {
            $scope: scope
        });
        scope.availableTypes = [{
            type: 'cxPromotionActionData',
            getName: function(action) {
                return "P - " + action.promotionId;
            }
        }];

        mockActionWrapper = {
            action: mockAction,
            status: PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.OLD
        };

    }));

    describe('getActionsToDisplay', function() {
        it('should be empty', function() {
            //then
            expect(scope.getActionsToDisplay).toBeDefined();
            expect(scope.getActionsToDisplay.length).toBe(0);
        });

        it('should contain action', function() {
            //when
            scope.addAction(mockAction, mockComparer);

            //then
            expect(scope.getActionsToDisplay).toBeDefined();
            expect(scope.getActionsToDisplay().length).toBe(1);
        });

    });

    describe('displayAction', function() {
        it('should use default display', function() {
            expect(scope.displayAction(mockActionWrapper)).toBe('mock');
        });

        it('should use promotion display', function() {
            expect(scope.displayAction(mockPromotionActionWrapper)).toBe('P - promotion id');
        });
    });

    describe('addAction', function() {
        it('should add new item', function() {
            //given
            expect(scope.getActionsToDisplay().length).toBe(0);

            //when
            scope.addAction(mockAction, mockComparer);

            //then
            expect(scope.getActionsToDisplay().length).toBe(1);
        });

        it('should ignore existing item', function() {
            //given
            expect(scope.getActionsToDisplay().length).toBe(0);

            //when
            scope.addAction(mockAction, mockComparer);
            scope.addAction(mockAction, mockComparer);

            //then
            expect(scope.getActionsToDisplay().length).toBe(1);

        });

        it('should restore item from delete queue', function() {
            //given
            expect(scope.getActionsToDisplay().length).toBe(0);
            scope.addAction(mockAction, mockComparer);
            expect(scope.getActionsToDisplay().length).toBe(1);
            scope.removeSelectedAction(scope.actions[0]);
            expect(scope.getActionsToDisplay().length).toBe(0);

            //when
            scope.addAction(mockAction, mockComparer);

            //then
            expect(scope.getActionsToDisplay().length).toBe(1);
            expect(scope.removedActions.length).toBe(0);
        });

    });

    describe('removeSelectedAction', function() {
        it('should delete item', function() {
            //given
            scope.addAction(mockAction, mockComparer);
            expect(scope.getActionsToDisplay().length).toBe(1);

            //when
            scope.removeSelectedAction(scope.actions[0]);

            //then
            expect(scope.getActionsToDisplay().length).toBe(0);
        });
    });

    describe('isDirty', function() {
        it('should not be dirty after adding exiting item', function() {
            //given
            scope.actions.push(mockActionWrapper);

            //when
            scope.addAction(mockAction, mockComparer);

            //then
            expect(scope.isDirty()).toBe(false);
        });

        it('should not be dirty after removing new item', function() {
            //given
            scope.addAction(mockAction, mockComparer);

            //when
            scope.removeSelectedAction(scope.actions[0]);

            //then
            expect(scope.isDirty()).toBe(false);
        });

        it('should be dirty after adding new item', function() {
            //when
            scope.addAction(mockAction, mockComparer);

            //then
            expect(scope.isDirty()).toBe(true);
        });

        it('should be dirty after removing exiting item', function() {
            //given
            scope.actions.push(mockActionWrapper);

            //when
            scope.removeSelectedAction(scope.actions[0]);

            //then
            expect(scope.isDirty()).toBe(true);
        });
    });
});
