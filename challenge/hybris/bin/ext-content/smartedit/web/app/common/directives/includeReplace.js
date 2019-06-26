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
angular.module('includeReplaceModule', [])
    .directive('includeReplace', function() {
        return {
            require: 'ngInclude',
            restrict: 'A',
            /* optional */
            link: function(scope, el) {
                el.replaceWith(el.children());
            }
        };
    });
