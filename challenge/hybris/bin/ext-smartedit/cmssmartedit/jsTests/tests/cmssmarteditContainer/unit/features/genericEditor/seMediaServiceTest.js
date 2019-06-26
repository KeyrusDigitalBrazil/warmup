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
/* jshint unused:false, undef:false */
describe('seMediaService', function() {

    var seMediaResource;
    var seMediaResourceService;
    var seMediaService;
    var $q;

    beforeEach(angular.mock.module('seMediaServiceModule', function($provide) {
        seMediaResource = jasmine.createSpyObj('seMediaResource', ['get', 'save']);
        seMediaResourceService = jasmine.createSpyObj('seMediaResourceService', ['getMediaByCode']);
        $provide.value('seMediaResource', seMediaResource);
        $provide.value('seMediaResourceService', seMediaResourceService);
    }));

    beforeEach(inject(function(_seMediaService_, _$q_) {
        $q = _$q_;
        seMediaService = _seMediaService_;
    }));

    describe('uploadMedia', function() {
        var IMAGE_MOCK = {
            file: {
                name: 'filename'
            }
        };
        var result;

        beforeEach(function() {
            seMediaResource.save.and.returnValue({
                $promise: $q.defer().promise
            });
            result = seMediaService.uploadMedia(IMAGE_MOCK);
        });

        it('should call the save method of the seMediaResource resource', function() {
            expect(seMediaResource.save).toHaveBeenCalledWith(IMAGE_MOCK);
        });

        it('should return a promise', function() {
            expect(result).toBePromise();
        });
    });

    describe('getMediaByCode', function() {
        var result;

        beforeEach(function() {
            seMediaResource.get.and.returnValue({
                $promise: $q.defer().promise
            });
            seMediaResourceService.getMediaByCode.and.returnValue(seMediaResource);
            result = seMediaService.getMediaByCode('someCode');
        });

        it('should call the get method of the seMediaResourceService', function() {
            expect(seMediaResourceService.getMediaByCode).toHaveBeenCalledWith('someCode');
            expect(seMediaResource.get).toHaveBeenCalled();
        });

        it('should return a promise', function() {
            expect(result).toBePromise();
        });
    });
});
