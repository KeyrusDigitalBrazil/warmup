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
describe('seMediaUploadField', function() {
    var element, scope, ctrl, $q;

    beforeEach(angular.mock.module('cmssmarteditContainerTemplates'));

    beforeEach(angular.mock.module('seMediaUploadFieldModule'));

    beforeEach(inject(function($rootScope, $compile, _$q_) {
        $q = _$q_;

        scope = $rootScope.$new();
        window.smarteditJQuery.extend(scope, {
            field: 'someField',
            model: {
                someField: 'someValue'
            },
            error: false
        });

        element = $compile('<se-media-upload-field ' +
            'data-error="error" ' +
            'data-field="field" ' +
            'data-model="model">' +
            '</se-media-upload-field>')(scope);
        scope.$digest();

        scope = element.isolateScope();
        ctrl = scope.ctrl;
    }));

    describe('initialization', function() {
        it('should have an input pre-populated with the model value and no icons visible', function() {
            expect(element.find('input').val()).toBe('someValue');
            expect(element.find('input').hasClass('se-mu--fileinfo--field__error')).toBe(false);
            expect(element.find('img.se-mu--fileinfo--field--icon__error')).not.toExist();
            expect(element.find('.se-mu--fileinfo--field--icon')).not.toExist();
        });
    });

    describe('on error', function() {
        beforeEach(function() {
            ctrl.error = true;
            scope.$digest();
        });

        it('should add the error class to the input', function() {
            expect(element.find('input').hasClass('se-mu--fileinfo--field__error')).toBe(true);
        });

        it('should display an error icon', function() {
            expect(element.find('img.se-mu--fileinfo--field--icon__error')).toExist();
        });
    });

    describe('on hover', function() {
        beforeEach(function() {
            element.trigger('mouseover');
        });

        it('should display an image icon', function() {
            expect(element.find('.se-mu--fileinfo--field--icon')).toExist();
        });
    });

});
