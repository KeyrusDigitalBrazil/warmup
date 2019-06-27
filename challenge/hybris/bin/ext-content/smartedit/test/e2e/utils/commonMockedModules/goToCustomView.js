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
angular.module('goToCustomView', ['ngRoute', 'customViewModule'])
    .config(function($routeProvider, PATH_TO_CUSTOM_VIEW) {
        $routeProvider.when('/customView', {
            templateUrl: PATH_TO_CUSTOM_VIEW,
            controller: 'customViewController',
            controllerAs: 'controller'
        });
    })
    .run(function($timeout, $location) {
        /*
         * FIXME :without a timeout the redirection of experienceService.loadExperience of e2eOnLoadingSetup.js
         * will trigger after the redirection hereunder
         * to be fixed durign test alignment.
         */
        $timeout(function() {
            $location.path("/customView");
        }, 1000);

    });
angular.module('smarteditcontainer').requires.push('customViewModule');
angular.module('smarteditcontainer').requires.push('goToCustomView');
