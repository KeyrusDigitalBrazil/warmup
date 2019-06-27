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
describe('personalizationsmarteditContextModal', function() {
    var mockModules = {};
    setupMockModules(mockModules); // jshint ignore:line

    var personalizationsmarteditContextModal;

    beforeEach(module('personalizationsmarteditContextMenu'));
    beforeEach(inject(function(_personalizationsmarteditContextModal_) {
        personalizationsmarteditContextModal = _personalizationsmarteditContextModal_;
    }));

    describe('openDeleteAction', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditContextModal.openDeleteAction).toBeDefined();
        });

    });

    describe('openAddAction', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditContextModal.openAddAction).toBeDefined();
        });

    });

    describe('openEditAction', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditContextModal.openEditAction).toBeDefined();
        });

    });

    describe('openEditComponentAction', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditContextModal.openEditComponentAction).toBeDefined();
        });

    });

});
