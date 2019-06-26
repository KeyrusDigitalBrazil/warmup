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
describe('seFileMimeTypeService', function() {
    var seFileMimeTypeService;
    var seFileReader, seFileMimeTypeServiceConstants;

    beforeEach(angular.mock.module('seFileMimeTypeServiceModule', function($provide) {
        seFileReader = jasmine.createSpyObj('seFileReader', ['read']);
        $provide.value('seFileReader', seFileReader);

        seFileMimeTypeServiceConstants = {
            VALID_IMAGE_MIME_TYPE_CODES: ['89504E47']
        };
        $provide.constant('seFileMimeTypeServiceConstants', seFileMimeTypeServiceConstants);
    }));

    beforeEach(inject(function(_seFileMimeTypeService_) {
        seFileMimeTypeService = _seFileMimeTypeService_;
    }));

    describe('isFileMimeTypeValid', function() {
        it('will return a resolved promise when the file mime type is valid', function() {
            var MOCK_FILE = [0x89, 0x50, 0x4E, 0x47];
            seFileReader.read.and.callFake(function(file, config) {
                config.onLoadEnd({
                    target: {
                        result: file
                    }
                });
            });
            var promise = seFileMimeTypeService.isFileMimeTypeValid(MOCK_FILE);

            expect(promise).toBeResolved();
        });

        it('will return a rejected promise when the file mime type is invalid', function() {
            var MOCK_FILE = [0x84, 0x83, 0x35, 0x53];
            seFileReader.read.and.callFake(function(file, config) {
                config.onLoadEnd({
                    target: {
                        result: file
                    }
                });
            });
            var promise = seFileMimeTypeService.isFileMimeTypeValid(MOCK_FILE);

            expect(promise).toBeRejected();
        });

        it('will return a rejected promise when the file fails to load', function() {
            var MOCK_FILE = [0x89, 0x50, 0x4E, 0x47];
            seFileReader.read.and.callFake(function(file, config) {
                config.onError();
            });
            var promise = seFileMimeTypeService.isFileMimeTypeValid(MOCK_FILE);

            expect(promise).toBeRejected();
        });
    });
});
