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
angular.module('RenderToolbarItemModule', ['toolbarModule', 'renderServiceModule'])
    .run(function(toolbarServiceFactory, renderService) {
        var toolbarService = toolbarServiceFactory.getToolbarService('smartEditPerspectiveToolbar');
        toolbarService.addItems([{
            key: 'toolbar.action.render.component',
            type: 'ACTION',
            nameI18nKey: 'toolbar.action.render.component',
            priority: 1,
            callback: function() {
                renderService.renderComponent("component1", "componentType1");
            },
            icons: ['render.png']
        }, {
            key: 'toolbar.action.render.slot',
            type: 'ACTION',
            nameI18nKey: 'toolbar.action.render.slot',
            priority: 2,
            callback: function() {
                renderService.renderSlots(["topHeaderSlot"]);
            },
            icons: ['render.png']
        }]);
    });

angular.module('smarteditcontainer').requires.push('RenderToolbarItemModule');
