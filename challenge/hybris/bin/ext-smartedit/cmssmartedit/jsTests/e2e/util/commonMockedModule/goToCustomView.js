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
angular.module('customViewModule', []) // this module will be overriden by custom views
    .constant('PATH_TO_CUSTOM_VIEW', null); //set constant to null by default. custom view will override it.

angular.module('customViewHTML', []);
angular.module('goToCustomView', ['ngRoute', 'customViewModule', 'customViewHTML'])
    .config(function($routeProvider, PATH_TO_CUSTOM_VIEW) {
        $routeProvider.when('/customView', {
            templateUrl: PATH_TO_CUSTOM_VIEW,
            controller: 'customViewController',
            controllerAs: 'controller'
        });
    })
    .run(function($location, PATH_TO_CUSTOM_VIEW) {
        if (PATH_TO_CUSTOM_VIEW !== null) {
            $location.path("/customView");
        }
    });

angular.module('smarteditcontainer').requires.push('customViewHTML');
angular.module('smarteditcontainer').requires.push('goToCustomView');
