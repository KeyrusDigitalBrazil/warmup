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
(function(angular) {
    angular.module('clickThroughOverlayModule', [
            'yjqueryModule',
            'smarteditServicesModule'
        ])

        .run(function(crossFrameEventService, $timeout, yjQuery) {

            crossFrameEventService.subscribe('PREVENT_OVERLAY_CLICKTHROUGH', function() {
                yjQuery('#smarteditoverlay').css('pointer-events', 'auto');
                yjQuery('body').removeClass('clickthroughflash');
            });

            crossFrameEventService.subscribe('CLICK_THROUGH_OVERLAY', function() {
                yjQuery('#smarteditoverlay').css('pointer-events', 'none');
                yjQuery('body').removeClass('clickthroughflash');
                $timeout(function() {
                    yjQuery('body').addClass('clickthroughflash');
                });
            });

        });

}(angular));
