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
angular.module('mockDataOverridesModule', ['backendMocksUtilsModule'])
    .run(function(backendMocksUtils) {

        var emptyVersionList = JSON.parse(sessionStorage.getItem("emptyVersionList"));
        if (emptyVersionList) {
            backendMocksUtils.getBackendMock('pageVersionsGETMock').respond(function() {
                var pagedResults = {
                    pagination: {
                        count: 0,
                        page: 0,
                        totalCount: 0,
                        totalPages: 0
                    },
                    results: []
                };

                return [200, pagedResults];
            });
        }
    });

try {
    angular.module('smarteditloader').requires.push('mockDataOverridesModule');
} catch (e) {}
try {
    angular.module('smarteditcontainer').requires.push('mockDataOverridesModule');
} catch (e) {}
