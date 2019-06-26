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
describe('seMediaFormat', function() {
    var parentScope, scope, element, ctrl;
    var seMediaFormatConstants, seFileValidationServiceConstants;
    var mediaService;
    var isFieldDisabledMock = jasmine.createSpy('isFieldDisabled');

    beforeEach(angular.mock.module('cmssmarteditContainerTemplates'));

    beforeEach(angular.mock.module('pascalprecht.translate', function($translateProvider) {
        $translateProvider.translations('en', {
            'se.media.format.upload': 'Upload',
            'se.media.format.replaceimage': 'Replace Image',
            'se.media.format.under.edit': 'Editing...'
        });
        $translateProvider.preferredLanguage('en');
    }));

    beforeEach(angular.mock.module('seFileValidationServiceModule'));

    beforeEach(angular.mock.module('mediaServiceModule', function($provide, $compileProvider) {
        mediaService = jasmine.createSpyObj('mediaService', ['getMedia']);
        $provide.value('mediaService', mediaService);

        $compileProvider.directive('genericEditorField', function() {
            return {
                template: '<se-media-format ' +
                    'data-media-uuid="mediaUuid" ' +
                    'data-media-format="mediaFormat" ' +
                    'data-field="field" ' +
                    'data-is-under-edit="isUnderEdit" ' +
                    'data-on-file-select="onFileSelect"' +
                    '</se-media-format>',
                controller: function($scope) {
                    $scope.mediaUuid = 'someUuid';
                    $scope.mediaFormat = 'someFormat';
                    $scope.field = {};
                    $scope.isUnderEdit = false;
                    $scope.onFileSelect = jasmine.createSpy('onFileSelect');
                    this.isFieldDisabled = isFieldDisabledMock;
                }
            };
        });
    }));


    beforeEach(angular.mock.module('seMediaFormatModule'));


    beforeEach(inject(function($compile, $rootScope, $q, _seMediaFormatConstants_, _seFileValidationServiceConstants_) {
        seMediaFormatConstants = _seMediaFormatConstants_;
        seFileValidationServiceConstants = _seFileValidationServiceConstants_;

        mediaService.getMedia.and.returnValue($q.when({
            code: 'someCode',
            url: '/web/webroot/images/edit_icon.png'
        }));

        isFieldDisabledMock.and.returnValue(false);

        parentScope = $rootScope.$new();
        var parentElement = $compile('<generic-editor-field></generic-editor-field')(parentScope);
        parentScope.$digest();

        element = parentElement.children(':first');
        scope = element.isolateScope();
        ctrl = scope.ctrl;
    }));

    describe('controller', function() {
        it('should be initialized', function() {
            expect(ctrl.i18nKeys).toBe(seMediaFormatConstants.I18N_KEYS);
            expect(ctrl.acceptedFileTypes).toBe(seFileValidationServiceConstants.ACCEPTED_FILE_TYPES);
        });

        it('should get media if it a uuid is provided', function() {
            expect(ctrl.mediaUuid).toBe('someUuid');
            expect(ctrl.media).toEqual({
                code: 'someCode',
                url: '/web/webroot/images/edit_icon.png'
            });
        });

        it('should clear the media if the uuid is not provided', function() {

            scope.$apply(function() {
                ctrl.mediaUuid = null;
            });
            expect(ctrl.media).toEqual({});
        });
    });

    describe('template', function() {
        it('should display the format', function() {
            expect(element.text()).toContain('someFormat');
        });

        describe('when media uuid present', function() {
            it('should show the media present view', function() {
                expect(element.find('.media-present')).toExist();
                expect(element.find('.media-absent')).not.toExist();
                expect(element.find('.media-is-under-edit')).not.toExist();
            });

            it('should show the image', function() {
                expect(element.find('.thumbnail--image-preview').attr('data-ng-src')).toBe('/web/webroot/images/edit_icon.png');
            });

            it('should show a replace button', function() {
                expect(element.find('.media-selector--preview__left--p').text().trim()).toContain('se.media.format.remove');
            });
        });

        describe('when media uuid absent', function() {
            beforeEach(function() {
                ctrl.mediaUuid = null;
                scope.$digest();
            });

            it('should show the media absent view', function() {
                expect(element.find('.media-present')).not.toExist();
                expect(element.find('.media-absent')).toExist();
                expect(element.find('.media-is-under-edit')).not.toExist();
            });

            it('should show an upload button', function() {
                expect(element.find('.media-absent se-file-selector .label__fileUpload-link').text().trim()).toBe('Upload');
            });

            describe('when field.editable is false', function() {
                beforeEach(function() {
                    ctrl.field.editable = false;
                    scope.$digest();
                });
                it('should se-file-selector have file-selector-disabled class', function() {
                    expect(element.find('.media-absent se-file-selector').isolateScope().ctrl.customClass).toContain('file-selector-disabled');
                });
            });
        });

        describe('when under edit', function() {
            beforeEach(function() {
                ctrl.isUnderEdit = true;
                scope.$digest();
            });

            it('should show the media uploading view', function() {
                expect(element.find('.media-present')).not.toExist();
                expect(element.find('.media-absent')).not.toExist();
                expect(element.find('.media-is-under-edit')).toExist();
            });

            it('should show the editing text', function() {
                expect(element.find('.media-is-under-edit').text().trim()).toContain('Upload');
            });
        });

        describe('when media uuid is present and field is disabled', function() {
            beforeEach(function() {
                isFieldDisabledMock.and.returnValue(true);
                scope.$digest();
            });

            it('should se-file-selector have file-selector-disabled class', function() {
                expect(element.find('.media-present se-file-selector').isolateScope().ctrl.customClass).toContain('file-selector-disabled');
            });

            it('remove button should be disabled', function() {
                // GIVEN

                // WHEN/THEN
                expect(element.find('.media-present button.remove-image').prop('disabled')).toBe(true);
            });
        });

    });
});
