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
describe('personalizationsmarteditPreviewService', function() {
    var mockModules = {};
    setupMockModules(mockModules); // jshint ignore:line

    var personalizationsmarteditPreviewService;

    beforeEach(module('personalizationsmarteditPreviewServiceModule'));
    beforeEach(inject(function(_$q_, _personalizationsmarteditPreviewService_) {
        personalizationsmarteditPreviewService = _personalizationsmarteditPreviewService_;
        spyOn(personalizationsmarteditPreviewService, 'updatePreviewTicketWithVariations').and.callThrough();

        mockModules.experienceService.getCurrentExperience.and.returnValue(_$q_.defer().promise);
        mockModules.experienceService.setCurrentExperience.and.returnValue(_$q_.defer().promise);

    }));

    describe('updatePreviewTicketWithVariations', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditPreviewService.updatePreviewTicketWithVariations).toBeDefined();
        });

        it('should pass proper object to experienceService', function() {
            // when
            personalizationsmarteditPreviewService.updatePreviewTicketWithVariations(['variation1', 'variation2']);
            // then
            expect(mockModules.experienceService.getCurrentExperience).toHaveBeenCalled();
        });

    });

    describe('removePersonalizationDataFromPreview', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditPreviewService.removePersonalizationDataFromPreview).toBeDefined();
        });

        it('should call proper function with proper arguments', function() {
            // when
            personalizationsmarteditPreviewService.removePersonalizationDataFromPreview();
            // then
            expect(personalizationsmarteditPreviewService.updatePreviewTicketWithVariations).toHaveBeenCalledWith([]);
            expect(mockModules.experienceService.getCurrentExperience).toHaveBeenCalled();
        });

    });

});
