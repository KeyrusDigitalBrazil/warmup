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
angular.module('setApparelStagedUKExperience', ['smarteditServicesModule'])
    .run(function(sharedDataService) {

        sharedDataService.set('experience', {
            siteDescriptor: {
                uid: 'apparel-uk'
            },
            catalogDescriptor: {
                catalogId: 'apparel-ukContentCatalog',
                catalogVersion: 'Staged'
            }
        });
    });
angular.module('smarteditcontainer').requires.push('setApparelStagedUKExperience');
