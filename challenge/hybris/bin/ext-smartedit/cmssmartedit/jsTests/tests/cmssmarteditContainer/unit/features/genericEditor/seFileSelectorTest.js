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
describe('seFileSelector', function() {
    var parentScope, scope, element, ctrl;

    beforeEach(angular.mock.module('cmssmarteditContainerTemplates'));

    beforeEach(angular.mock.module('pascalprecht.translate', function($translateProvider) {
        $translateProvider.translations('en', {
            'some.i18n.label': 'Some Label'
        });
        $translateProvider.preferredLanguage('en');
    }));

    beforeEach(angular.mock.module('seFileSelectorModule'));

    beforeEach(inject(function($compile, $rootScope) {
        parentScope = $rootScope.$new();
        window.smarteditJQuery.extend(parentScope, {
            labelI18nKey: 'some.i18n.label',
            acceptedFileTypes: ['a', 'b', 'c'],
            onFileSelect: jasmine.createSpy('onFileSelect')
        });

        element = $compile('<se-file-selector ' +
            'data-label-i18n-key="labelI18nKey" ' +
            'data-disabled="false"' +
            'data-accepted-file-types="acceptedFileTypes" ' +
            'data-on-file-select="onFileSelect(files)">' +
            '</se-file-selector>')(parentScope);
        parentScope.$digest();

        scope = element.isolateScope();
        ctrl = scope.ctrl;
    }));

    describe('controller', function() {
        it('should be initialized', function() {
            expect(ctrl.labelI18nKey).toEqual('some.i18n.label');
            expect(ctrl.acceptedFileTypes).toEqual(['a', 'b', 'c']);
            expect(ctrl.onFileSelect).toEqual(jasmine.any(Function));
        });

        describe('buildAcceptedFileTypesList', function() {
            it('should build a comma separated list of file extensions with the period prefix', function() {
                expect(ctrl.buildAcceptedFileTypesList()).toEqual('.a,.b,.c');
            });
        });
    });

    describe('template', function() {
        it('should have a translated label', function() {
            expect(element.find('label .label__fileUpload-link').text().trim()).toEqual('Some Label');
        });

        it('should have a hidden file input which accepts a select set of filetypes', function() {
            expect(element.find('input').attr('class')).toContain('hide');
            expect(element.find('input').attr('accept')).toEqual(ctrl.buildAcceptedFileTypesList());
        });
    });

    describe('link', function() {
        it('should trigger on file change callback when input changes', function() {
            element.find('input').change();
            expect(parentScope.onFileSelect).toHaveBeenCalled();
        });
    });
    describe('disable', function() {
        beforeEach(inject(function($compile) {
            element = $compile('<se-file-selector ' +
                'data-label-i18n-key="labelI18nKey" ' +
                'data-disabled="true"' +
                'data-accepted-file-types="acceptedFileTypes" ' +
                'data-on-file-select="onFileSelect(files)">' +
                '</se-file-selector>')(parentScope);
            parentScope.$digest();

            scope = element.isolateScope();
            ctrl = scope.ctrl;
        }));

        it('should not have file input field', function() {
            expect(element.find('label:not(.ng-hide) input').length).toEqual(0);
        });

        it('should not have link class', function() {
            expect(element.find('label:not(.ng-hide) .label__fileUpload').attr('class')).not.toContain('label__fileUpload-link');
        });
    });
});
