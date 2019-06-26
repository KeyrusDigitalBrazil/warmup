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
describe('seMediaUploadForm', function() {
    var element, scope, ctrl, $q;
    var seMediaService;
    var seMediaUploadFormConstants, validator, seObjectValidatorFactory, seBackendValidationHandler;
    var onCancel, onUploadSuccess;

    beforeEach(angular.mock.module('cmssmarteditContainerTemplates'));

    beforeEach(angular.mock.module('pascalprecht.translate', function($translateProvider) {
        $translateProvider.translations('en', {
            'se.upload.image.cancel': 'Cancel',
            'se.upload.image.submit': 'Upload',
            'se.upload.image.replace': 'Replace Image',
            'se.uploaded.image.description': 'Description',
            'se.uploaded.image.code': 'Code',
            'se.uploaded.image.alt.text': 'Alt Text',
            'se.uploaded.is.uploading': 'Uploading...'
        });
        $translateProvider.preferredLanguage('en');
    }));

    beforeEach(angular.mock.module('seFileValidationServiceModule', function($provide) {
        $provide.constant('seFileValidationServiceConstants', {
            ACCEPTED_FILE_TYPES: ['a', 'b', 'c']
        });
    }));

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

    beforeEach(inject(function(_seMediaUploadFormConstants_) {
        seMediaUploadFormConstants = _seMediaUploadFormConstants_;
    }));

    beforeEach(inject(function($rootScope, $compile, _$q_) {
        $q = _$q_;

        scope = $rootScope.$new();
        window.smarteditJQuery.extend(scope, {
            image: {
                file: {
                    name: 'someFile.png'
                }
            },
            field: {
                qualifier: 'someQualifier'
            },
            onUploadSuccess: onUploadSuccess = jasmine.createSpy('onUploadSuccess'),
            onCancel: onCancel = jasmine.createSpy('onCancel')
        });
        element = $compile('<se-media-upload-form ' +
            'data-image="image" ' +
            'data-field="field" ' +
            'data-on-upload-callback="onUploadSuccess(code)" ' +
            'data-on-cancel-callback="onCancel()">' +
            '</se-media-upload-form>')(scope);
        scope.$digest();

        scope = element.isolateScope();
        ctrl = scope.ctrl;
    }));

    describe('initialization', function() {
        it('should show file name', function() {
            expect(element.find('.se-media-upload--fn--name').text()).toBe('someFile.png');
        });

        it('should show the truncated file name', function() {
            ctrl.image.file.name = 'aVeryVeryVeryLongName.png';
            scope.$digest();
            expect(element.find('.se-media-upload--fn--name').text()).toBe('aVeryVeryVeryLo...');
        });

        it('should show translated labels for each of the fields', function() {
            expect(element.find('label[name="label-description"]').parent().text()).toContain('Description');
            expect(element.find('label[name="label-code"]').parent().text()).toContain('Code');
            expect(element.find('label[name="label-alt-text"]').parent().text()).toContain('Alt Text');
        });

        it('should set all parameters to filename by default', function() {
            expect(element.find('input[name="description"]').val()).toBe(ctrl.image.file.name);
            expect(element.find('input[name="code"]').val()).toBe(ctrl.image.file.name);
            expect(element.find('input[name="altText"]').val()).toBe(ctrl.image.file.name);
        });
    });

    describe('on cancel', function() {
        beforeEach(function() {
            element.find('.se-media-upload-btn__cancel').click();
        });

        it('should clear the field errors', function() {
            expect(element.find('.upload-field-error-description').text()).toBe('');
            expect(element.find('.upload-field-error-code').text()).toBe('');
            expect(element.find('.upload-field-error-alt-text').text()).toBe('');
        });

        it('should hide the upload in progress section', function() {
            expect(element.find('.upload-image-in-progress')).not.toExist();
        });
    });

    describe('on upload with invalid parameters', function() {
        beforeEach(function() {
            validator.validate.and.callFake(function(objectToValidate, errorsContext) {
                errorsContext.push({
                    subject: 'code',
                    message: 'some code message'
                });
                errorsContext.push({
                    subject: 'description',
                    message: 'some description message'
                });
                errorsContext.push({
                    subject: 'altText',
                    message: 'some alt text message'
                });
                return false;
            });
            element.find('.se-media-upload-btn__submit').click();
        });

        it('should show validation errors for each field', function() {
            expect(element.find('span.upload-field-error-description').text()).toBe('some description message');
            expect(element.find('span.upload-field-error-code').text()).toBe('some code message');
            expect(element.find('span.upload-field-error-alt-text').text()).toBe('some alt text message');
        });
    });

    describe('on upload', function() {
        var uploadMediaDeferred;

        beforeEach(function() {
            validator.validate.and.returnValue(true);
            uploadMediaDeferred = $q.defer();
            seMediaService.uploadMedia.and.returnValue(uploadMediaDeferred.promise);
            element.find('.se-media-upload-btn__submit').click();
        });

        it('should show in progress status', function() {
            expect(element.find('.upload-image-in-progress')).toExist();
        });

        describe('on success,', function() {
            beforeEach(function() {
                uploadMediaDeferred.resolve({
                    code: 'someCode'
                });
                scope.$digest();
            });

            it('should hide progress status', function() {
                expect(element.find('.upload-image-in-progress')).not.toExist();
            });

            it('should should clear the image parameter inputs', function() {
                expect(element.find('input[name="description"]').val()).toBe('');
                expect(element.find('input[name="code"]').val()).toBe('');
                expect(element.find('input[name="altText"]').val()).toBe('');
            });

            it('should clear the field errors', function() {
                expect(element.find('.upload-field-error-description').text()).toBe('');
                expect(element.find('.upload-field-error-code').text()).toBe('');
                expect(element.find('.upload-field-error-alt-text').text()).toBe('');
            });

            it('should hide the upload in progress section', function() {
                expect(element.find('.upload-image-in-progress')).not.toExist();
            });
        });

        describe('on failure', function() {
            beforeEach(function() {
                seBackendValidationHandler.handleResponse.and.callFake(function(response, errorsContext) {
                    errorsContext.push({
                        subject: 'code',
                        message: 'some backend code message'
                    });
                    errorsContext.push({
                        subject: 'description',
                        message: 'some backend description message'
                    });
                    errorsContext.push({
                        subject: 'altText',
                        message: 'some backend alt text message'
                    });
                });
                uploadMediaDeferred.reject({});
                scope.$digest();
            });

            it('should hide progress status', function() {
                expect(element.find('.upload-image-in-progress')).not.toExist();
            });

            it('should display validation errors from the backend', function() {
                expect(element.find('.upload-field-error-description').text()).toBe('some backend description message');
                expect(element.find('.upload-field-error-code').text()).toBe('some backend code message');
                expect(element.find('.upload-field-error-alt-text').text()).toBe('some backend alt text message');
            });
        });
    });
});
