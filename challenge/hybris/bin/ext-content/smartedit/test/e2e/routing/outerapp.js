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
(function() {
    angular.module('outerapp', ['ui.bootstrap', 'ngRoute', 'smarteditServicesModule'])
        .config(function($routeProvider) {
            $routeProvider.when('/test', {
                templateUrl: 'web/test.html'
            });

            $routeProvider.when('/customView', {
                templateUrl: 'web/customView.html'
            });
        })
        .run(
            ['$templateCache', 'urlService', function($templateCache) {
                $templateCache.put('web/test.html',
                    "<div class= \"customView\"> \n" +
                    "Test View" +
                    "</div>" +
                    "<iframe src=\"/test/e2e/routing/customiframe.html\" style=\"width:100%;height:800px\"></iframe>"
                );

                $templateCache.put('web/customView.html',
                    "<div >\n" +
                    "<div class= \"content\"> \n" +
                    "custom view {{ 1+1 }} " +
                    "</div>" +
                    "</div>"
                );
            }]
        )
        .controller("DefaultCtrl", function(urlService) {
            this.navigate = function() {
                urlService.path('/customView');
            };
        });
    angular.module('smarteditloader').controller("DefaultCtrl", function() {});
    angular.module('smarteditcontainer').requires.push('outerapp');
})();
