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
describe('seMediaField', function() {
    var scope, ctrl;
    var seFileValidationService;
    var seMediaFieldConstants, seFileValidationServiceConstants, $q;

    beforeEach(angular.mock.module('cmssmarteditContainerTemplates'));
    beforeEach(angular.mock.module('pascalprecht.translate'));

    beforeEach(angular.mock.module('seFileValidationServiceModule', function($provide) {
        seFileValidationService = jasmine.createSpyObj('seFileValidationService', ['validate']);
        $provide.value('seFileValidationService', seFileValidationService);
    }));

    beforeEach(angular.mock.module('seMediaFieldModule'));

    beforeEach(inject(function(_seMediaFieldConstants_, _seFileValidationServiceConstants_, _$q_, $controller, $rootScope) {
        seMediaFieldConstants = _seMediaFieldConstants_;
        seFileValidationServiceConstants = _seFileValidationServiceConstants_;
        $q = _$q_;

        scope = $rootScope.$new();
        ctrl = $controller('seMediaFieldController', {
            $scope: scope
        }, {
            field: {},
            model: {
                someQualifier: {
                    someFormat: 'someCode'
                }
            },
            qualifier: 'someQualifier'
        });
    }));

    describe('initialization', function() {
        it('should be initialized', function() {
            ctrl.$onInit();
            scope.$digest();

            expect(ctrl.i18nKeys).toBe(seMediaFieldConstants.I18N_KEYS);
            expect(ctrl.acceptedFileTypes).toBe(seFileValidationServiceConstants.ACCEPTED_FILE_TYPES);
            expect(ctrl.image).toEqual({});
            expect(ctrl.fileErrors).toEqual([]);
        });
    });

    describe('fileSelected', function() {
        it('should set the image on the scope if only one file is selected and the file is valid', function() {
            var MOCK_FILES = [{
                name: 'someName'
            }];
            var MOCK_FORMAT = 'someFormat';
            seFileValidationService.validate.and.returnValue($q.when());
            ctrl.fileSelected(MOCK_FILES, MOCK_FORMAT);
            scope.$digest();

            expect(ctrl.fileErrors).toEqual([]);
            expect(ctrl.image).toEqual({
                file: {
                    name: 'someName'
                },
                format: 'someFormat'
            });
        });

        it('should clear image if there are validation errors', function() {
            var MOCK_FILES = [{
                name: 'someName'
            }];
            var MOCK_FORMAT = 'someFormat';
            seFileValidationService.validate.and.callFake(function(file, errorsContext) {
                errorsContext.push({
                    subject: 'code'
                });
                return $q.reject(errorsContext);
            });
            ctrl.fileSelected(MOCK_FILES, MOCK_FORMAT);
            scope.$digest();

            expect(ctrl.fileErrors).toEqual([{
                subject: 'code'
            }]);
            expect(ctrl.image).toEqual({});
        });
    });

    describe('resetImage', function() {
        it('should reset the image and file errors', function() {
            ctrl.resetImage();
            expect(ctrl.image).toEqual({});
            expect(ctrl.fileErrors).toEqual([]);
        });
    });

    describe('imageUploaded', function() {
        it('should update the model and reset the image and file errors', function() {
            ctrl.imageUploaded('someNewCode');
            expect(ctrl.model[ctrl.qualifier]).toEqual('someNewCode');
            expect(ctrl.image).toEqual({});
            expect(ctrl.fileErrors).toEqual([]);
        });

    });
});
