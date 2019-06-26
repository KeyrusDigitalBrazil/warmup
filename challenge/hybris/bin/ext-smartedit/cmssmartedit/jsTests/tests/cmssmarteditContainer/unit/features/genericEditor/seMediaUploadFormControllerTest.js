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
describe('seMediaUploadFormController', function() {

    var scope, ctrl, $q;
    var seMediaService, seObjectValidatorFactory, seBackendValidationHandler, validator;
    var seMediaUploadFormConstants, seFileValidationServiceConstants;
    var onSelectCallback, onUploadCallback, onCancelCallback;

    beforeEach(angular.mock.module('seFileValidationServiceModule'));

    beforeEach(angular.mock.module('seMediaServiceModule', function($provide) {
        seMediaService = jasmine.createSpyObj('seMediaService', ['uploadMedia']);
        $provide.value('seMediaService', seMediaService);
    }));

    beforeEach(angular.mock.module('seBackendValidationHandlerModule', function($provide) {
        seBackendValidationHandler = jasmine.createSpyObj('seBackendValidationHandler', ['handleResponse']);
        $provide.value('seBackendValidationHandler', seBackendValidationHandler);
    }));

    beforeEach(angular.mock.module('seObjectValidatorFactoryModule', function($provide) {
        validator = jasmine.createSpyObj('validator', ['validate']);
        seObjectValidatorFactory = jasmine.createSpyObj('seObjectValidatorFactory', ['build']);
        seObjectValidatorFactory.build.and.returnValue(validator);
        $provide.value('seObjectValidatorFactory', seObjectValidatorFactory);
    }));

    beforeEach(angular.mock.module('seMediaUploadFormModule'));

    beforeEach(inject(function(_seMediaUploadFormConstants_, _seFileValidationServiceConstants_) {
        seMediaUploadFormConstants = _seMediaUploadFormConstants_;
        seFileValidationServiceConstants = _seFileValidationServiceConstants_;
    }));

    beforeEach(inject(function($rootScope, $compile, _$q_, _$controller_) {
        $q = _$q_;

        scope = $rootScope.$new();

        ctrl = _$controller_('seMediaUploadFormController', {
            $scope: scope
        }, {
            field: {
                qualifier: 'someQualifier'
            },
            image: {
                file: {
                    name: 'someName'
                }
            },
            onSelectCallback: onSelectCallback = jasmine.createSpy('onSelectCallback'),
            onUploadCallback: onUploadCallback = jasmine.createSpy('onUploadCallback'),
            onCancelCallback: onCancelCallback = jasmine.createSpy('onCancelCallback')
        });
    }));

    describe('initialization', function() {
        it('should set-up the default state on the ctrl', function() {
            expect(ctrl.i18nKeys).toEqual(seMediaUploadFormConstants.I18N_KEYS);
            expect(ctrl.acceptedFileTypes).toEqual(seFileValidationServiceConstants.ACCEPTED_FILE_TYPES);

            expect(ctrl.imageParameters).toEqual({});
            expect(ctrl.isUploading).toBe(false);
            expect(ctrl.fieldErrors).toEqual([]);
        });
    });

    describe('on first digest', function() {
        it('should setup the image parameters to take the selected file name as the defaults', function() {
            scope.$digest();
            expect(ctrl.imageParameters).toEqual({
                code: 'someName',
                description: 'someName',
                altText: 'someName'
            });
        });
    });


    describe('onCancel', function() {
        beforeEach(function() {
            ctrl.onCancel();
        });

        it('should reset parameters', function() {
            expect(ctrl.imageParameters).toEqual({});
            expect(ctrl.fieldErrors).toEqual([]);
            expect(ctrl.isUploading).toBe(false);
            expect(onCancelCallback).toHaveBeenCalled();
        });
    });

    describe('onImageUploadSuccess', function() {
        beforeEach(function() {
            ctrl.onImageUploadSuccess({
                uuid: "someCode"
            });
        });

        it('should reset parameters', function() {
            expect(ctrl.imageParameters).toEqual({});
            expect(ctrl.fieldErrors).toEqual([]);
            expect(ctrl.isUploading).toBe(false);
            expect(onUploadCallback).toHaveBeenCalledWith({
                uuid: "someCode"
            });
        });
    });

    describe('onImageUploadFail', function() {
        beforeEach(function() {
            ctrl.onImageUploadFail({});
        });

        it('should clear upload state', function() {
            expect(ctrl.isUploading).toBe(false);
        });

        it('should call backend validation handler', function() {
            expect(seBackendValidationHandler.handleResponse).toHaveBeenCalled();
        });
    });

    describe('onMediaUploadSubmit', function() {
        it('should upload image if there are not validation errors', function() {
            validator.validate.and.returnValue(true);
            seMediaService.uploadMedia.and.returnValue($q.defer().promise);
            scope.$digest();
            ctrl.onMediaUploadSubmit();

            expect(ctrl.isUploading).toBe(true);
            expect(seMediaService.uploadMedia).toHaveBeenCalledWith({
                file: {
                    name: 'someName'
                },
                code: 'someName',
                description: 'someName',
                altText: 'someName'
            });
            expect(ctrl.fieldErrors).toEqual([]);
        });

        it('should not upload image if there are validation errors', function() {
            validator.validate.and.returnValue(false);
            ctrl.onMediaUploadSubmit();
            expect(seMediaService.uploadMedia).not.toHaveBeenCalled();
        });
    });

    describe('getErrorsForField', function() {
        it('should filter errors on subject and get messages', function() {
            ctrl.fieldErrors = [{
                subject: 'code',
                message: 'some code message'
            }];
            expect(ctrl.getErrorsForField('code')).toEqual(['some code message']);
        });

        it('should not populate messages for unmatched subjects in errors', function() {
            ctrl.fieldErrors = [{
                subject: 'code',
                message: 'some code message'
            }];
            expect(ctrl.getErrorsForField('invalid')).toEqual([]);
        });
    });

    describe('hasErrors', function() {
        it('should return true if there is a matching error', function() {
            ctrl.fieldErrors = [{
                subject: 'code',
                message: 'some code message'
            }];
            expect(ctrl.hasError('code')).toBe(true);
        });

        it('should return false if there is no matching error', function() {
            ctrl.fieldErrors = [{
                subject: 'code',
                message: 'some code message'
            }];
            expect(ctrl.hasError('invalid')).toBe(false);
        });
    });
});
