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
describe('catalogVersionFilterDropdownModule', function() {
    var mockModules = {};
    setupMockModules(mockModules); // jshint ignore:line

    var $componentController, $rootScope;

    var mockCatalog1 = {
        catalogId: "electronicsContentCatalog",
        catalogName: {
            en: "Electronics Content Catalog",
            de: "Elektronikkatalog"
        },
        catalogVersionId: "Online",
        id: "electronicsContentCatalog/Online",
        isCurrentCatalog: false
    };
    var mockCatalog2 = {
        catalogId: "electronics-euContentCatalog",
        catalogName: {
            en: "Electronics Content Catalog EU",
            de: "Elektronikkatalog EU"
        },
        catalogVersionId: "Online",
        id: "electronics-euContentCatalog/Online",
        isCurrentCatalog: false
    };
    var mockCatalog3 = {
        catalogId: "electronics-ukContentCatalog",
        catalogName: {
            en: "Electronics Content Catalog UK",
            de: "Elektronikkatalog UK"
        },
        catalogVersionId: "Staged",
        id: "electronics-ukContentCatalog/Staged",
        isCurrentCatalog: true
    };

    var mockExperienceData = {
        seExperienceData: {
            catalogDescriptor: {
                catalogVersionUuid: "mockUuid"
            }
        }
    };

    beforeEach(module('personalizationsmarteditServicesModule', function($provide) {
        mockModules.personalizationsmarteditContextService = jasmine.createSpyObj('personalizationsmarteditContextService', ['getSeData']);
        $provide.value('personalizationsmarteditContextService', mockModules.personalizationsmarteditContextService);
    }));

    beforeEach(module('catalogVersionFilterDropdownModule'));
    beforeEach(inject(function(_$componentController_, _$q_, _$rootScope_) {
        $componentController = _$componentController_;
        $rootScope = _$rootScope_;

        mockModules.componentMenuService.getValidContentCatalogVersions.and.callFake(function() {
            var deferred = _$q_.defer();
            deferred.resolve(
                [mockCatalog1, mockCatalog2, mockCatalog3]
            );
            return deferred.promise;
        });
        mockModules.componentMenuService.getInitialCatalogVersion.and.callFake(function() {
            var deferred = _$q_.defer();
            deferred.resolve(
                mockCatalog3
            );
            return deferred.promise;
        });

        mockModules.personalizationsmarteditContextService.getSeData.and.callFake(function() {
            return mockExperienceData;
        });

    }));

    describe('Component API', function() {

        it('should have proper api before initialized', function() {
            var ctrl = $componentController('catalogVersionFilterDropdown', null);

            expect(ctrl.items).not.toBeDefined();
            expect(ctrl.selectedId).not.toBeDefined();
            expect(ctrl.onSelectCallback).not.toBeDefined();
            expect(ctrl.$onInit).toBeDefined();
            expect(ctrl.onChange).toBeDefined();
            expect(ctrl.fetchStrategy).toBeDefined();
        });

        it('should have proper api after initialized', function() {
            var ctrl = $componentController('catalogVersionFilterDropdown', null);
            ctrl.onSelectCallback = function() {};

            ctrl.$onInit();
            $rootScope.$digest();

            expect(ctrl.items.length).toBe(3);
            expect(ctrl.selectedId).toBe(ctrl.items[2].id);
            expect(ctrl.$onInit).toBeDefined();
            expect(ctrl.onChange).toBeDefined();
            expect(ctrl.fetchStrategy).toBeDefined();
        });

    });

});
