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
angular.module('sanitizeHtmlInputModule', [])
    .directive('sanitizeHtmlInput', function() {
        return {
            restrict: "A",
            replace: false,
            transclude: false,
            priority: 1000,
            link: function($scope, element) {
                element.change(
                    function() {
                        var target = element.val();
                        target = target.replace(new RegExp('{', 'g'), '%7B');
                        target = target.replace(new RegExp('}', 'g'), '%7D');
                        element.val(target);
                    });
            }
        };
    });
