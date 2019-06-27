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
angular.module('httpErrorInterceptorMocksModule', ['ngMockE2E'])
    .run(function($httpBackend) {
        $httpBackend.whenGET(/error404_json/).respond(function() {
            return [404, {
                errors: []
            }, {
                'Content-type': 'text/json'
            }];
        });
        $httpBackend.whenGET(/error400_json/).respond(function() {
            return [400, {
                errors: [{
                    type: 'ValidationError',
                    message: 'validation error'
                }, {
                    message: 'error: bad request'
                }]
            }, {
                'Content-type': 'text/json'
            }];
        });
        $httpBackend.whenGET(/error404_html/).respond(function() {
            return [404, null, {
                'Content-type': 'text/html'
            }];
        });
        $httpBackend.whenGET(/error501_json/).respond(function() {
            return [501, {
                errors: [{
                    message: 'error: 501 bad request'
                }]
            }, {
                'Content-type': 'text/json'
            }];
        });
        $httpBackend.whenGET(/error503\/.*\/v1\/.*/).respond(function() {
            return [503];
        });

        var getError502Attempt = 0;
        $httpBackend.whenGET(/error502\/retry/).respond(function() {
            getError502Attempt++;
            return [getError502Attempt === 2 ? 200 : 502];
        });
    });

try {
    angular.module('smarteditloader').requires.push('httpErrorInterceptorMocksModule');
    angular.module('smarteditcontainer').requires.push('httpErrorInterceptorMocksModule');
} catch (exception) {
    console.error('httpErrorInterceptorMocksModule - Failed to add httpErrorInterceptorMocksModule as a dependency', exception);
}
