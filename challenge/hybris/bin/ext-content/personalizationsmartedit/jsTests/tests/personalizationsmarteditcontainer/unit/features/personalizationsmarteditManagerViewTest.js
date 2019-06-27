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
describe('personalizationsmarteditManagerViewModule', function() {
    var mockModules = {};
    setupMockModules(mockModules); // jshint ignore:line

    var personalizationsmarteditManagerView, personalizationsmarteditManagerViewController, personalizationsmarteditContextService, scope;

    beforeEach(module('personalizationsmarteditServicesModule', function($provide) {
        personalizationsmarteditContextService = jasmine.createSpyObj('personalizationsmarteditContextService', ['getSeData']);
        $provide.value("personalizationsmarteditContextService", personalizationsmarteditContextService);
    }));

    beforeEach(module('personalizationsmarteditManagerViewModule'));
    beforeEach(inject(function(_$rootScope_, _$q_, _$controller_, _personalizationsmarteditManagerView_, _personalizationsmarteditContextService_) {
        scope = _$rootScope_.$new();
        personalizationsmarteditManagerView = _personalizationsmarteditManagerView_;
        personalizationsmarteditContextService = _personalizationsmarteditContextService_;

        mockModules.modalService.open.and.callFake(function() {
            return _$q_.defer().promise;
        });

        mockModules.confirmationModalService.confirm.and.callFake(function() {
            return _$q_.defer().promise;
        });

        personalizationsmarteditContextService.getSeData.and.callFake(function() {
            return {
                seExperienceData: {
                    catalogDescriptor: {
                        name: {
                            en: "testName"
                        },
                        catalogVersion: "testOnline"
                    },
                    languageDescriptor: {
                        isocode: "en",
                    }
                }
            };
        });

        personalizationsmarteditManagerViewController = _$controller_('personalizationsmarteditManagerViewController', {
            '$scope': scope
        });

    }));

    describe('openManagerAction', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditManagerView.openManagerAction).toBeDefined();
        });

        it('after called it is calling proper services', function() {
            personalizationsmarteditManagerView.openManagerAction();
            expect(mockModules.modalService.open).toHaveBeenCalled();
        });

    });

    describe('personalizationsmarteditManagerViewController', function() {

        it('after instantiation scope should be initialized properly', function() {
            expect(scope.catalogName).toBeDefined();
            expect(scope.customizations).toBeDefined();
            expect(scope.allCustomizationsCount).not.toBeDefined();
            expect(scope.filteredCustomizationsCount).toBeDefined();
            expect(scope.search.name).toBeDefined();
            expect(scope.pagination).toBeDefined();
            expect(scope.searchInputKeypress).toBeDefined();
            expect(scope.editCustomizationAction).toBeDefined();
            expect(scope.deleteCustomizationAction).toBeDefined();
            expect(scope.editVariationAction).toBeDefined();
            expect(scope.deleteVariationAction).toBeDefined();
            expect(scope.openNewModal).toBeDefined();
            expect(scope.isUndefined).toBeDefined();
            expect(scope.resetSearchInput).toBeDefined();
            expect(scope.scrollZoneElement).not.toBeDefined();
            expect(scope.isReturnToTopButtonVisible).toBeDefined();
            expect(scope.scrollZoneReturnToTop).toBeDefined();
            expect(scope.treeOptions).toBeDefined();
            expect(scope.isScrollZoneVisible).toBeDefined();
            expect(scope.setScrollZoneElement).toBeDefined();
            expect(scope.getElementToScroll).toBeDefined();
        });

    });

    describe('personalizationsmarteditManagerViewController.deleteCustomizationAction', function() {

        it('after called it is calling proper services', function() {
            scope.deleteCustomizationAction();
            expect(mockModules.confirmationModalService.confirm).toHaveBeenCalled();
        });

    });

    describe('personalizationsmarteditManagerViewController.deleteVariationAction', function() {

        it('after called it is calling proper services', function() {
            // given
            var variation1 = {
                code: "var1",
                status: "a"
            };
            var variation2 = {
                code: "var2",
                status: "a"
            };
            var customization = {
                code: "test",
                variations: [variation1, variation2]
            };
            var f = function() {};
            var event = {
                stopPropagation: f
            };
            // when
            scope.deleteVariationAction(customization, variation1, event);
            // then
            expect(mockModules.confirmationModalService.confirm).toHaveBeenCalled();
        });

    });

});
