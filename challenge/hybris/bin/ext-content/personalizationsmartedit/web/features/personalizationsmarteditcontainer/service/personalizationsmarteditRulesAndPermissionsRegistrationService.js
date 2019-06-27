/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
angular.module('personalizationsmarteditRulesAndPermissionsRegistrationModule', [
    'permissionServiceModule',
    'personalizationsmarteditServicesModule'
]).run(function($q, permissionService, personalizationsmarteditRestService, personalizationsmarteditContextService) {

    var getCustomizationFilter = function() {
        return {
            currentPage: 0,
            currentSize: 1
        };
    };

    // Rules
    permissionService.registerRule({
        names: ['se.access.personalization'],
        verify: function() {
            return personalizationsmarteditContextService.refreshExperienceData().then(function() {
                return personalizationsmarteditRestService.getCustomizations(getCustomizationFilter()).then(function() {
                    return $q.when(true);
                }, function(errorResp) {
                    if (errorResp.status === 403) {
                        //Forbidden status on GET /customizations - user doesn't have permission to personalization perspective
                        return $q.when(false);
                    } else {
                        //other errors will be handled with personalization perspective turned on
                        return $q.when(true);
                    }
                });
            });
        }
    });

    // Permissions
    permissionService.registerPermission({
        aliases: ['se.personalization.open'],
        rules: ['se.read.page', 'se.access.personalization']
    });
});
