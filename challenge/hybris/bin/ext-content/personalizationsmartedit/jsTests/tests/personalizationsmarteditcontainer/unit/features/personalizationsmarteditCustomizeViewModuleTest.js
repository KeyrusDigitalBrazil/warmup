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
describe('personalizationsmarteditCustomizeViewModule', function() {
    var mockModules = {};
    setupMockModules(mockModules); // jshint ignore:line

    var mockVariation = {
        code: "testVariation"
    };
    var mockCustomization = {
        code: "testCustomization",
        variations: [mockVariation],
        status: "stat1"
    };

    var $componentController, $timeout;

    beforeEach(module('personalizationsmarteditServicesModule', function($provide) {
        mockModules.personalizationsmarteditRestService = jasmine.createSpyObj('personalizationsmarteditRestService', ['getCustomizations']);
        $provide.value('personalizationsmarteditRestService', mockModules.personalizationsmarteditRestService);
    }));

    beforeEach(module('personalizationsmarteditCommons', function($provide) {
        mockModules.personalizationsmarteditUtils = jasmine.createSpyObj('personalizationsmarteditUtils', ['getStatusesMapping']);
        $provide.value('personalizationsmarteditUtils', mockModules.personalizationsmarteditUtils);
    }));

    beforeEach(module('personalizationsmarteditContextMenu', function($provide) {
        mockModules.personalizationsmarteditContextService = jasmine.createSpyObj('personalizationsmarteditContextService', ['getCustomizeFiltersState', 'setCustomizeFiltersState', 'refreshExperienceData']);
        $provide.value('personalizationsmarteditContextService', mockModules.personalizationsmarteditContextService);
    }));

    beforeEach(module('personalizationsmarteditCustomizeViewModule'));
    beforeEach(inject(function(_$q_, _$timeout_, _$componentController_) {
        $componentController = _$componentController_;
        $timeout = _$timeout_;

        mockModules.sharedDataService.get.and.callFake(function() {
            var deferred = _$q_.defer();
            deferred.resolve({});
            return deferred.promise;
        });

        mockModules.personalizationsmarteditRestService.getCustomizations.and.callFake(function() {
            var deferred = _$q_.defer();
            var retCustomizatons = [mockCustomization, mockCustomization];

            deferred.resolve({
                customizations: retCustomizatons,
                pagination: {
                    count: 5,
                    page: 0,
                    totalCount: 5,
                    totalPages: 1
                }
            });
            return deferred.promise;
        });

        mockModules.personalizationsmarteditUtils.getStatusesMapping.and.callFake(function() {
            return [{
                modelStatuses: {},
                code: "all"
            }];
        });

        mockModules.personalizationsmarteditContextService.getCustomizeFiltersState.and.callFake(function() {
            return {
                catalogFilter: "catalogMock",
                pageFilter: "pageMock",
                statusFilter: "statusMock",
                nameFilter: "nameMock"
            };
        });

    }));

    describe('Component API', function() {

        it('should have proper api when initialized without parameters', function() {
            var ctrl = $componentController('personalizationsmarteditCustomizeView', null);

            expect(ctrl.catalogFilerChange).toBeDefined();
            expect(ctrl.pageFilerChange).toBeDefined();
            expect(ctrl.statusFilerChange).toBeDefined();
            expect(ctrl.nameInputKeypress).toBeDefined();
            expect(ctrl.addMoreCustomizationItems).toBeDefined();
            expect(ctrl.$onInit).toBeDefined();
            expect(ctrl.$onDestroy).toBeDefined();
        });
    });

    describe('customizationsList', function() {

        it('should be instantianed and empty', function() {
            var ctrl = $componentController('personalizationsmarteditCustomizeView', null);
            ctrl.$onInit();
            expect(ctrl.customizationsList).toBeDefined();
            expect(ctrl.customizationsList.length).toBe(0);
        });

    });

    describe('addMoreCustomizationItems', function() {

        it('after called array ctrl.customizationsOnPage should contain objects return by REST service', function() {
            //given
            var ctrl = $componentController('personalizationsmarteditCustomizeView', null);

            // when
            ctrl.$onInit();
            ctrl.statusFilter = "all";
            ctrl.addMoreCustomizationItems();

            $timeout.flush();
            // then
            expect(ctrl.customizationsList).toBeDefined();
            expect(ctrl.customizationsList.length).toBe(2);
            expect(ctrl.customizationsList).toContain(mockCustomization);
        });

    });

    describe('onInit', function() {

        it('should call proper service on init', function() {
            var ctrl = $componentController('personalizationsmarteditCustomizeView', null);

            ctrl.$onInit();

            expect(ctrl.catalogFilter).toBe("catalogMock");
            expect(ctrl.pageFilter).toBe("pageMock");
            expect(ctrl.statusFilter).toBe("statusMock");
            expect(ctrl.nameFilter).toBe("nameMock");
        });

    });

    describe('onDestroy', function() {

        it('should call proper service on destroy', function() {
            var ctrl = $componentController('personalizationsmarteditCustomizeView', null);
            var mockFilter = {
                catalogFilter: "catalog",
                pageFilter: "page",
                statusFilter: "status",
                nameFilter: "name"
            };
            ctrl.catalogFilter = mockFilter.catalogFilter;
            ctrl.pageFilter = mockFilter.pageFilter;
            ctrl.statusFilter = mockFilter.statusFilter;
            ctrl.nameFilter = mockFilter.nameFilter;

            ctrl.$onDestroy();

            expect(mockModules.personalizationsmarteditContextService.setCustomizeFiltersState).toHaveBeenCalledWith(mockFilter);
        });

    });

});
