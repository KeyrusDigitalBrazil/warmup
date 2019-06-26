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
describe('seMediaContainerField', function() {
    var scope, ctrl;
    var seFileValidationService;
    var typePermissionsRestService;
    var $q;
    var $log;

    beforeEach(angular.mock.module('cmssmarteditContainerTemplates'));
    beforeEach(angular.mock.module('pascalprecht.translate'));

    beforeEach(angular.mock.module('seFileValidationServiceModule', function($provide) {
        seFileValidationService = jasmine.createSpyObj('seFileValidationService', ['validate']);
        $provide.value('seFileValidationService', seFileValidationService);
    }));

    beforeEach(angular.mock.module('cmsSmarteditServicesModule', function($provide) {
        typePermissionsRestService = jasmine.createSpyObj('typePermissionsRestService', ['hasAllPermissionsForTypes']);
        $provide.value('typePermissionsRestService', typePermissionsRestService);
        $log = jasmine.createSpyObj('$log', ['error', 'warn']);
        $provide.value('$log', $log);
    }));

    beforeEach(angular.mock.module('seMediaContainerFieldModule'));

    beforeEach(inject(function(yjQuery, $compile, $rootScope, _$q_) {
        $q = _$q_;

        parentScope = $rootScope.$new();

        yjQuery.extend(parentScope, {
            field: {
                containedTypes: [
                    'MediaContainer',
                    'MediaFormat'
                ]
            },
            editor: {},
            model: {
                someQualifier: {
                    someFormat: 'someCode'
                }
            },
            qualifier: 'someQualifier'
        });

        element = $compile('<se-media-container-field data-field="field"' +
            'data-model="model"' +
            'data-editor="editor"' +
            'data-qualifier="qualifier"></se-media-container-field>')(parentScope);

        parentScope.$digest();
        scope = element.isolateScope();
        ctrl = scope.ctrl;

        typePermissionsRestService.hasAllPermissionsForTypes.and.returnValue($q.when({
            MediaContainer: {
                read: true,
                create: true,
                change: true,
                remove: true
            },
            MediaFormat: {
                read: true,
                create: true,
                change: true,
                remove: true
            }
        }));
    }));

    describe('initialization', function() {
        it('should be initialized', function() {
            ctrl.$onInit();
            scope.$digest();

            expect(ctrl.image).toEqual({});
            expect(ctrl.fileErrors).toEqual([]);
            expect(ctrl.hasReadPermissionOnMediaRelatedTypes).toBe(true);
        });
    });

    describe('type permissions on media related types', function() {
        it('GIVEN user no read permission on any of the media types WHEN initialized THEN hasReadPermissionOnMediaRelatedTypes will be set to false', function() {
            typePermissionsRestService.hasAllPermissionsForTypes.and.returnValue($q.when({
                MediaContainer: {
                    read: false,
                    create: true,
                    change: true,
                    remove: true
                },
                MediaFormat: {
                    read: true,
                    create: false,
                    change: true,
                    remove: true
                }
            }));
            ctrl.$onInit();
            scope.$digest();
            expect(ctrl.hasReadPermissionOnMediaRelatedTypes).toBe(false);
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
            expect(ctrl.model[ctrl.qualifier][ctrl.image.format]).toEqual('someNewCode');
            expect(ctrl.image).toEqual({});
            expect(ctrl.fileErrors).toEqual([]);
        });
    });

    describe('isFormatUnderEdit', function() {
        it('should return true if the format is under edit', function() {
            ctrl.image = {
                format: 'someFormat'
            };
            expect(ctrl.isFormatUnderEdit('someFormat')).toBe(true);
        });
    });
});
