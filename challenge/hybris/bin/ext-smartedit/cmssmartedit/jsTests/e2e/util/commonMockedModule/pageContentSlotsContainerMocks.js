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
angular.module('pagesContentSlotsContainerMocks', ['ngMockE2E']);

angular.module('pagesContentSlotsContainerMocks')
    .run(function($httpBackend) {

        /**
         * Mocks the /pagescontentslots resource with the query parameter 'pageId' set to 'homepage' to return four associations, indicating that the page has four slots.
         */
        $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pagescontentslotscontainers\?pageId=.*/).respond({
            pageContentSlotContainerList: []
        });

    });
try {
    angular.module('smarteditloader').requires.push('pagesContentSlotsContainerMocks');
} catch (e) {}
try {
    angular.module('smarteditcontainer').requires.push('pagesContentSlotsContainerMocks');
} catch (e) {}
