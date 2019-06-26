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
describe('seMediaAdvancedProperties', function() {
    var parentScope, scope, element, ctrl;
    var seMediaAdvancedPropertiesConstants;

    beforeEach(angular.mock.module('cmssmarteditContainerTemplates'));

    beforeEach(angular.mock.module('pascalprecht.translate', function($translateProvider) {
        $translateProvider.translations('en', {
            'se.media.advanced.information.description': 'Description',
            'se.media.advanced.information.code': 'Code',
            'se.media.advanced.information.alt.text': 'Alt Text',
            'se.media.information': 'Information'
        });
        $translateProvider.preferredLanguage('en');
    }));

    beforeEach(angular.mock.module('seMediaAdvancedPropertiesModule'));

    beforeEach(inject(function($compile, $rootScope, _seMediaAdvancedPropertiesConstants_) {
        seMediaAdvancedPropertiesConstants = _seMediaAdvancedPropertiesConstants_;

        parentScope = $rootScope.$new();
        window.smarteditJQuery.extend(parentScope, {
            code: 'someCode',
            description: 'someDescription',
            altText: 'someAltText'
        });

        element = $compile('<se-media-advanced-properties ' +
            'data-code="code" ' +
            'data-description="description" ' +
            'data-alt-text="altText">' +
            '</se-media-advanced-properties>')(parentScope);
        parentScope.$digest();

        scope = element.isolateScope();
        ctrl = scope.ctrl;
    }));

    describe('controller', function() {
        it('should be initialized', function() {
            expect(ctrl.i18nKeys).toBe(seMediaAdvancedPropertiesConstants.I18N_KEYS);
            expect(ctrl.contentUrl).toBe(seMediaAdvancedPropertiesConstants.CONTENT_URL);
        });
    });

    describe('template', function() {
        it('should have a link with translated text', function() {
            expect(window.smarteditJQuery(element.find('a')[0]).text()).toContain('Information');
        });

        it('should trigger bootstrap popover', function() {
            expect(window.smarteditJQuery(element.find('a')[0]).attr('data-uib-popover-template')).toBeTruthy();
        });
    });
});
