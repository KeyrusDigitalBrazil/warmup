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
angular.module('cmsPerspectivesMocks', ['smarteditServicesModule'])
    .run(function(perspectiveService) {
        perspectiveService.register({
            key: 'someperspective',
            nameI18nKey: 'someperspective',
            descriptionI18nKey: 'someperspective',
            features: ['se.contextualMenu'],
            perspectives: []
        });
    });
try {
    angular.module('smarteditcontainer').requires.push('cmsPerspectivesMocks');
} catch (e) {}
