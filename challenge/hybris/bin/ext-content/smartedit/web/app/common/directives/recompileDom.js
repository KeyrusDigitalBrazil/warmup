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
/**
 * @ngdoc overview
 * @name recompileDomModule
 * @description
 * This module defines the {@link recompileDomModule.directive:recompileDom recompileDom} component.
 **/
angular.module('recompileDomModule', [])

    /**
     * @ngdoc directive
     * @name recompileDomModule.directive:recompileDom
     * @restrict A
     * @requires $timeout
     * @param {= Function} recompileDom Function invoked from the outer scope to trigger the recompiling of the transcluded content.
     * @description
     * The recompile dom directive accepts a function param, and can be applied to any part of the dom.
     * Upon execution of the function, the inner contents of this dom is recompiled by Angular.
     */
    .directive("recompileDom", function($timeout) {
        return {
            restrict: "A",
            replace: false,
            transclude: true,
            template: "<div data-ng-if='showContent' data-ng-transclude></div>",
            scope: {
                trigger: '=recompileDom'
            },
            link: function(scope) {
                scope.showContent = true;
                scope.trigger = function() {
                    scope.showContent = false;
                    $timeout(function() {
                        scope.showContent = true;
                    }, 0);
                };
            }
        };
    });
