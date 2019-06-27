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
angular.module('dummyContainer', ['ngRoute', 'smarteditServicesModule'])
    .config(function($routeProvider) {
        $routeProvider.when('/customView', {
            templateUrl: 'web/customView.html'
        });
    })
    .run(
        ['$templateCache', function($templateCache) {
            $templateCache.put('web/customView.html',
                "<div class= \"customView\"> \n" +
                "customView" +
                "</div>" +
                "<iframe id='ySmartEditFrame' src=\"/test/e2e/apiAuthentication/customiframe.html\" style=\"width:100%;height:800px\"></iframe>"
            );
        }]
    );
