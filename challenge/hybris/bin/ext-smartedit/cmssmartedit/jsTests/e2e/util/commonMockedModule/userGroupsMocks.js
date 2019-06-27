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
angular.module('userGroupsMocks', ['ngMockE2E', 'yLoDashModule'])
    .run(function($httpBackend, lodash) {

        var userGroupsList = [{
            "name": {},
            "uid": "employeegroup"
        }, {
            "name": {},
            "uid": "hac_monitoring_jmx"
        }, {
            "name": {},
            "uid": "hac_monitoring_jmx_limited"
        }, {
            "name": {},
            "uid": "hac_monitoring_memory"
        }, {
            "name": {},
            "uid": "hac_monitoring_memory_limited"
        }, {
            "name": {},
            "uid": "hac_monitoring_threaddump"
        }, {
            "name": {},
            "uid": "hac_monitoring_performance"
        }, {
            "name": {
                "en": "CMS Manager Group",
                "de": "CMS Manager-Gruppe"
            },
            "uid": "cmsmanagergroup"
        }, {
            "name": {},
            "uid": "hac_console_scriptinglanguages"
        }, {
            "name": {},
            "uid": "hac_console_flexiblesearch"
        }];

        $httpBackend.whenGET(/cmswebservices\/v1\/usergroups\?.*/).respond(function() {
            var result = {
                "pagination": {
                    "count": userGroupsList.count,
                    "page": 1,
                    "totalCount": userGroupsList.count,
                    "totalPages": 1
                },
                "userGroups": userGroupsList
            };

            return [200, result];
        });

        $httpBackend.whenGET(/cmswebservices\/v1\/usergroups\/(\w+)/, undefined, ['userGroupUID']).respond(function(method, url, data, header, params) {
            var result = userGroupsList.filter(function(userGroup) {
                return userGroup.uid === params.userGroupUID;
            })[0];

            return [200, result];
        });

    });
angular.module('smarteditloader').requires.push('userGroupsMocks');
angular.module('smarteditcontainer').requires.push('userGroupsMocks');
