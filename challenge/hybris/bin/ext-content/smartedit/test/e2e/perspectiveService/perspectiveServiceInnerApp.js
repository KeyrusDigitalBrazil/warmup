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
angular.module('perspectiveServiceInnerApp', [
        'textDisplayDecoratorModule',
        'buttonDisplayDecoratorModule',
        'smarteditServicesModule',
        'permissionDisplayComponent',
        'permissionRegisteredInnerDecoratorModule',
        'smarteditServicesModule'
    ])
    .run(function($q, decoratorService, perspectiveService, featureService, permissionService) {

        permissionService.registerRule({
            names: ['se.some.rule.inner.app'],
            verify: function() {
                try {
                    return $q.when(window.sessionStorage.getItem("PERSPECTIVE_SERVICE_RESULT") === 'true');
                } catch (e) {
                    return $q.when(false);
                }
            }
        });

        permissionService.registerPermission({
            aliases: ['se.some.permission.inner.app'],
            rules: ['se.some.rule.inner.app']
        });

        decoratorService.addMappings({
            'componentType1': ['textDisplayDecorator'],
            'componentType2': ['buttonDisplayDecorator'],
            'SimpleResponsiveBannerComponent': ['textDisplayDecorator', 'buttonDisplayDecorator', 'permissionDisplayDecorator'],
            'componentType4': ['permissionRegisteredInnerDecorator']
        });

        featureService.addDecorator({
            key: 'textDisplayDecorator',
            nameI18nKey: 'somenameI18nKey',
            descriptionI18nKey: 'somedescriptionI18nKey'
        });

        featureService.addDecorator({
            key: 'buttonDisplayDecorator',
            nameI18nKey: 'somenameI18nKey',
            descriptionI18nKey: 'somedescriptionI18nKey'
        });

        featureService.addDecorator({
            key: 'permissionDisplayDecorator',
            nameI18nKey: 'permissionDisplayI18nKey',
            descriptionI18nKey: 'permissionDisplayDescriptionI18nKey',
            permissions: ['se.some.permission']
        });

        featureService.addDecorator({
            key: 'permissionRegisteredInnerDecorator',
            nameI18nKey: 'permissionRegisteredInnerI18nKey',
            descriptionI18nKey: 'permissionRegisteredInnerDescriptionI18nKey',
            permissions: ['se.some.permission.inner.app']
        });

        perspectiveService.register({
            key: 'somekey',
            nameI18nKey: 'somenameI18nKey',
            descriptionI18nKey: 'somedescriptionI18nKey',
            features: ['textDisplayDecorator', 'buttonDisplayDecorator'],
            perspectives: []
        });

        perspectiveService.register({
            key: 'permissionsKey',
            nameI18nKey: 'permissionsI18nKey',
            descriptionI18nKey: 'permissionsDescriptionI18nKey',
            features: ['permissionDisplayDecorator', 'permissionRegisteredInnerDecorator'],
            perspectives: []
        });

        // User restricted perspective
        permissionService.registerRule({
            names: ['se.user.restricted.perspective.rule.inner.app'],
            verify: function() {
                try {
                    return $q.when(window.sessionStorage.getItem("HAS_RESTRICTED_PERSPECTIVE") === 'true');
                } catch (e) {
                    return $q.when(false);
                }

            }
        });

        permissionService.registerPermission({
            aliases: ['se.user.restricted.perspective.permission.inner.app'],
            rules: ['se.user.restricted.perspective.rule.inner.app']
        });

        perspectiveService.register({
            key: 'userRestrictedPerspectiveKey',
            nameI18nKey: 'userRestrictedPerspectiveI18nKey',
            descriptionI18nKey: 'userRestrictedPerspectiveDescriptionI18nKey',
            features: ['textDisplayDecorator'],
            perspectives: [],
            permissions: ['se.user.restricted.perspective.permission.inner.app']
        });

    });

angular.module('textDisplayDecoratorModule', ['decoratortemplates', 'translationServiceModule'])
    .directive('textDisplayDecorator', function() {
        return {
            templateUrl: 'textDisplayDecoratorTemplate.html',
            restrict: 'C',
            transclude: true,
            replace: false,
            scope: {
                smarteditComponentId: '@',
                smarteditComponentType: '@',
                active: '='
            },

            link: function($scope) {
                $scope.textDisplayContent = "Text_is_been_displayed_TextDisplayDecorator";
            }
        };
    });

angular.module('permissionDisplayComponent', [])
    .directive('permissionDisplayDecorator', function() {
        return {
            template: '<div>Test permission component</div>',
            restrict: 'C'
        };
    });

angular.module('permissionRegisteredInnerDecoratorModule', [])
    .directive('permissionRegisteredInnerDecorator', function() {
        return {
            template: '<div>Test permission decorator registered inner</div>',
            restrict: 'C'
        };
    });

angular.module('buttonDisplayDecoratorModule', ['decoratortemplates', 'translationServiceModule'])
    .directive('buttonDisplayDecorator', function() {
        return {
            templateUrl: 'buttonDisplayDecoratorTemplate.html',
            restrict: 'C',
            transclude: true,
            replace: false,
            scope: {
                smarteditComponentId: '@',
                smarteditComponentType: '@',
                active: '='
            },

            link: function($scope) {
                $scope.buttonDisplayContent = "Button_is_been_Displayed";
            }
        };
    });

angular.module('decoratortemplates', []).run(function($templateCache) {
    'use strict';

    $templateCache.put('textDisplayDecoratorTemplate.html',
        "<div >\n" +
        "<div class=\"row\" data-ng-if=\"!active\">\n" +
        "</div>\n" +
        "{{textDisplayContent}}\n" +
        "<div data-ng-transclude></div>\n" +
        "</div>"
    );

    $templateCache.put('buttonDisplayDecoratorTemplate.html',
        "<div>\n" +
        "<div class=\"row\" data-ng-if=\"!active\">\n" +
        "</div>\n" +
        "<button>{{buttonDisplayContent}}</button>\n" +
        "<div data-ng-transclude></div>\n" +
        "</div>"
    );
});
