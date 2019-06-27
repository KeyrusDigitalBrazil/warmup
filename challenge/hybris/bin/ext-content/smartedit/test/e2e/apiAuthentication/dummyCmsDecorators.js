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
angular.module('FakeModule', [
        'fake1Decorator',
        'fake2Decorator'
    ])
    .run(function(decoratorService) {

        decoratorService.addMappings({
            'componentType1': ['fake1'],
            'componentType2': ['fake2']
        });

        decoratorService.enable('fake1');
        decoratorService.enable('fake2');
    });
angular.module('fake1Decorator', ['decoratortemplates', 'ngResource'])
    .directive('fake1', ['$timeout', 'restServiceFactory', function($timeout, restServiceFactory) {
        return {
            templateUrl: 'fake1DecoratorTemplate.html',
            restrict: 'C',
            transclude: true,
            replace: false,
            scope: {
                smarteditComponentId: '@',
                smarteditComponentType: '@',
                active: '='
            },

            link: function($scope) {
                console.info("fake1Decorator decorator created");

                $scope.found = false;

                restServiceFactory.get("https://api1/somepath/id").get().then(function() {
                    $scope.found = true;
                    console.info("fake1Decorator get api1 success");
                }, function() {
                    console.info("fake1Decorator get api1 failed");

                });
                $scope.visible = false;
                $scope.mouseleave = function() {
                    $timeout(function() {
                        $scope.visible = false;
                    }, 1000);
                };
                $scope.mouseenter = function() {
                    $scope.visible = true;
                };

            }
        };
    }]);
angular.module('fake2Decorator', ['decoratortemplates', 'ngResource'])
    .directive('fake2', ['$timeout', 'restServiceFactory', function($timeout, restServiceFactory) {
        return {
            templateUrl: 'fake2DecoratorTemplate.html',
            restrict: 'C',
            transclude: true,
            replace: false,
            scope: {
                smarteditComponentId: '@',
                smarteditComponentType: '@',
                active: '='
            },

            link: function($scope) {
                console.info("fake2Decorator decorator created");

                $scope.found = false;
                $timeout(function() { //so that we make sure login 2 pops up after login 1
                    restServiceFactory.get("https://api2/someotherpath/id").get().then(function() {
                        $scope.found = true;
                        console.info("fake2Decorator get api2 success");

                    }, function() {
                        console.info("fake2Decorator get api2 failed");

                    });
                }, 1000);
                $scope.visible = false;
                $scope.mouseleave = function() {
                    $timeout(function() {
                        $scope.visible = false;
                    }, 1000);
                };
                $scope.mouseenter = function() {
                    $scope.visible = true;
                };

            }
        };
    }]);
angular.module('decoratortemplates', []).run(function($templateCache) {
    'use strict';

    $templateCache.put('fake1DecoratorTemplate.html',
        "<div>\n" +
        "    <div data-ng-mouseleave=\"mouseleave()\">\n" +
        "        <div data-ng-mouseenter=\"mouseenter()\">\n" +
        "            <div data-ng-transclude></div>\n" +
        "               <span id='fake1' data-ng-if='found'>fake1</span>" +
        "        </div>\n" +
        "    </div>\n" +
        "</div>"
    );


    $templateCache.put('fake2DecoratorTemplate.html',
        "<div>\n" +
        "    <div data-ng-mouseleave=\"mouseleave()\">\n" +
        "        <div data-ng-mouseenter=\"mouseenter()\">\n" +
        "            <div data-ng-transclude></div>\n" +
        "                <span id='fake2' data-ng-if='found'>fake2</span>" +
        "        </div>\n" +
        "    </div>\n" +
        "</div>"
    );

});
