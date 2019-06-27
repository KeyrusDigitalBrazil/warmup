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
angular.module('httpMethodPredicatesModule', ['yLoDashModule'])
    .constant('HTTP_METHODS_UPDATE', ['PUT', 'POST', 'DELETE', 'PATCH'])
    .constant('HTTP_METHODS_READ', ['GET', 'OPTIONS', 'HEAD'])
    .service('updatePredicate', function(lodash, HTTP_METHODS_UPDATE) {
        return function(response) {
            return lodash.includes(HTTP_METHODS_UPDATE, response.config.method);
        };
    })
    .service('readPredicate', function(lodash, HTTP_METHODS_READ) {
        return function(response) {
            return lodash.includes(HTTP_METHODS_READ, response.config.method);
        };
    });
