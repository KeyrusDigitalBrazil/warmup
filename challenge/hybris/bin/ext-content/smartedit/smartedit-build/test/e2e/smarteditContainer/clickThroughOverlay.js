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
    angular.module('clickThroughOverlayTriggerModule', [
            'toolbarModule',
            'smarteditServicesModule'
        ])
        .run(function(toolbarServiceFactory, crossFrameEventService) {
            var tbs = toolbarServiceFactory.getToolbarService('smartEditPerspectiveToolbar');
            tbs.addItems([{
                key: 'se.CLICK_THROUGH_OVERLAY',
                type: 'ACTION',
                nameI18nKey: 'CLICK_THROUGH_OVERLAY',
                descriptionI18nKey: 'CLICK_THROUGH_OVERLAY',
                section: 'left',
                iconClassName: 'hyicon hyicon-dragdrop se-toolbar-menu-ddlb--button__icon',
                callback: function() {
                    crossFrameEventService.publish('CLICK_THROUGH_OVERLAY');
                }
            }]);
            tbs.addItems([{
                key: 'se.PREVENT_OVERLAY_CLICKTHROUGH',
                type: 'ACTION',
                nameI18nKey: 'PREVENT_OVERLAY_CLICKTHROUGH',
                descriptionI18nKey: 'PREVENT_OVERLAY_CLICKTHROUGH',
                section: 'left',
                iconClassName: 'hyicon hyicon-list se-toolbar-menu-ddlb--button__icon',
                callback: function() {
                    crossFrameEventService.publish('PREVENT_OVERLAY_CLICKTHROUGH');
                }
            }]);
        });

    angular.module('smarteditcontainer').requires.push('clickThroughOverlayTriggerModule');

}(angular));
