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
describe('seMediaPreview', function() {
    var parentScope, scope, element, ctrl;
    var seMediaPreviewConstants;

    beforeEach(angular.mock.module('cmssmarteditContainerTemplates'));

    beforeEach(angular.mock.module('pascalprecht.translate', function($translateProvider) {
        $translateProvider.translations('en', {
            'media.preview': 'Preview'
        });
        $translateProvider.preferredLanguage('en');
    }));

    beforeEach(angular.mock.module('seMediaPreviewModule'));

    beforeEach(inject(function($compile, $rootScope, _seMediaPreviewConstants_) {
        seMediaPreviewConstants = _seMediaPreviewConstants_;

        parentScope = $rootScope.$new();
        window.smarteditJQuery.extend(parentScope, {
            code: 'someCode',
            description: 'someDescription',
            altText: 'someAltText'
        });

        element = $compile('<se-media-preview ' +
            'data-image-url="someUrl" ' +
            '</se-media-preview>')(parentScope);
        parentScope.$digest();

        scope = element.isolateScope();
        ctrl = scope.ctrl;
    }));

    describe('controller', function() {
        it('should be initialized', function() {
            expect(ctrl.i18nKeys).toBe(seMediaPreviewConstants.I18N_KEYS);
            expect(ctrl.contentUrl).toBe(seMediaPreviewConstants.CONTENT_URL);
        });
    });

    describe('template', function() {
        it('should trigger bootstrap popover', function() {
            expect(window.smarteditJQuery(element.find('a')[0]).attr('data-uib-popover-template')).toBeTruthy();
        });
    });
});
