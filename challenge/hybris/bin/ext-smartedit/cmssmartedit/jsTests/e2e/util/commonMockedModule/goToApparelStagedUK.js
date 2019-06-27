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
angular.module('goToApparelStagedUK', ['resourceLocationsModule'])
    .run(function(experienceService) {

        experienceService.loadExperience({
            siteId: "apparel-uk",
            catalogId: "apparel-ukContentCatalog",
            catalogVersion: "Staged"
        });
    });
try {
    angular.module('smarteditloader').requires.push('goToApparelStagedUK');
} catch (e) {}
try {
    angular.module('smarteditcontainer').requires.push('goToApparelStagedUK');
} catch (e) {}
