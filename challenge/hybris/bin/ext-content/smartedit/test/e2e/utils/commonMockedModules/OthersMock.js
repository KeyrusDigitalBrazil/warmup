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
angular.module('OthersMockModule', ['ngMockE2E'])
    .run(function($httpBackend) {
        // Pass through all other requests
        $httpBackend.whenGET(/^\w+.*/).passThrough();
    });

try {
    angular.module('smarteditloader').requires.push('OthersMockModule');
    angular.module('smarteditcontainer').requires.push('OthersMockModule');
} catch (ex) {}
