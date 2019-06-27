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
describe('personalizationsmarteditCommerceCustomizationService', function() {
    var mockModules = {};
    setupMockModules(mockModules); // jshint ignore:line

    var type1 = {
        type: 'type1',
        confProperty: 'type1.enabled'
    };

    var confWithType1Enabled = {
        'type1.enabled': true
    };

    var confWithType1Disabled = {
        'type1.enabled': false
    };

    var personalizationsmarteditCommerceCustomizationService;

    beforeEach(module('personalizationsmarteditCommons'));
    beforeEach(inject(function(_personalizationsmarteditCommerceCustomizationService_) {
        personalizationsmarteditCommerceCustomizationService = _personalizationsmarteditCommerceCustomizationService_;
    }));

    describe('getAvailableTypes', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditCommerceCustomizationService.getAvailableTypes).toBeDefined();
        });

        it('should be empty when configuration is null', function() {
            expect(personalizationsmarteditCommerceCustomizationService.getAvailableTypes(null)).toEqual([]);
        });

        it('should be empty when configuration is undefined', function() {
            expect(personalizationsmarteditCommerceCustomizationService.getAvailableTypes(undefined)).toEqual([]);
        });

        it('should be empty when types are empty', function() {
            expect(personalizationsmarteditCommerceCustomizationService.getAvailableTypes(confWithType1Enabled)).toEqual([]);
        });

        it('should return type when it is enabled in configuration', function() {
            //given
            personalizationsmarteditCommerceCustomizationService.registerType(type1);

            //when
            var availabelTypes = personalizationsmarteditCommerceCustomizationService.getAvailableTypes(confWithType1Enabled);

            //then
            expect(availabelTypes).toContain(type1);
        });

        it('should not return type when it is disabled in configuration', function() {
            //given
            personalizationsmarteditCommerceCustomizationService.registerType(type1);

            //when
            var availabelTypes = personalizationsmarteditCommerceCustomizationService.getAvailableTypes(confWithType1Disabled);

            //then
            expect(availabelTypes).toEqual([]);
        });

        it('should return only enabled types', function() {
            //given
            var configuration = {
                'type1.enabled': false,
                'type2.enabled': true,
                'type3.enabled': true,
                'type4.enabled': false
            };
            var type2 = {
                type: 'type2',
                confProperty: 'type2.enabled'
            };
            var type3 = {
                type: 'type3',
                confProperty: 'type3.enabled'
            };
            var type4 = {
                type: 'type4',
                confProperty: 'type4.enabled'
            };
            personalizationsmarteditCommerceCustomizationService.registerType(type1);
            personalizationsmarteditCommerceCustomizationService.registerType(type2);
            personalizationsmarteditCommerceCustomizationService.registerType(type3);
            personalizationsmarteditCommerceCustomizationService.registerType(type4);

            //when
            var availabelTypes = personalizationsmarteditCommerceCustomizationService.getAvailableTypes(configuration);

            //then
            expect(availabelTypes.length).toBe(2);
            expect(availabelTypes).toContain(type2);
            expect(availabelTypes).toContain(type3);
        });
    });

    describe('isCommerceCustomizationEnabled', function() {
        it('should be defined', function() {
            expect(personalizationsmarteditCommerceCustomizationService.isCommerceCustomizationEnabled).toBeDefined();
        });

        it('should return false when configuration is null', function() {
            expect(personalizationsmarteditCommerceCustomizationService.isCommerceCustomizationEnabled(null)).toBe(false);
        });

        it('should return false when configuration is undefined', function() {
            expect(personalizationsmarteditCommerceCustomizationService.isCommerceCustomizationEnabled(undefined)).toBe(false);
        });

        it('should return false when types are empty', function() {
            expect(personalizationsmarteditCommerceCustomizationService.isCommerceCustomizationEnabled(confWithType1Enabled)).toBe(false);
        });

        it('should return true when at least one type is enabled', function() {
            //given
            personalizationsmarteditCommerceCustomizationService.registerType(type1);

            //when
            var enabled = personalizationsmarteditCommerceCustomizationService.isCommerceCustomizationEnabled(confWithType1Enabled);

            //then
            expect(enabled).toBe(true);
        });

        it('should return false when there is no enabled type', function() {
            //given
            personalizationsmarteditCommerceCustomizationService.registerType(type1);

            //when
            var enabled = personalizationsmarteditCommerceCustomizationService.isCommerceCustomizationEnabled(confWithType1Disabled);

            //then
            expect(enabled).toBe(false);
        });

    });
});
