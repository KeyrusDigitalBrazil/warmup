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
describe('personalizationsmarteditManager', function() {
    var mockModules = {};
    setupMockModules(mockModules); // jshint ignore:line

    var personalizationsmarteditManager, scope;

    beforeEach(module('personalizationsmarteditManageCustomizationViewModule'));
    beforeEach(inject(function(_$rootScope_, _$q_, _$controller_, _personalizationsmarteditManager_) {
        scope = _$rootScope_.$new();
        personalizationsmarteditManager = _personalizationsmarteditManager_;
    }));

    describe('openCreateCustomizationModal', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditManager.openCreateCustomizationModal).toBeDefined();
        });

    });

    describe('openEditCustomizationModal', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditManager.openEditCustomizationModal).toBeDefined();
        });

    });

});
