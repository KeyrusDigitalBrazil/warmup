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
angular.module('FakeModule', ['decoratorServiceModule'])
    .run(function(decoratorService, contextualMenuService, smarteditroot, $log, $templateCache) {

        $templateCache.put('path/to/dummyTemplate.html',
            "<div> ..... Dummy Template <img src='/static-resources/images/slot_contextualmenu_placeholder_off.png'/>   ..... </div>"
        );

        decoratorService.addMappings({
            '^.*Slot$': ['se.slotContextualMenu']
        });

        decoratorService.enable('se.slotContextualMenu');

        contextualMenuService.addItems({
            '^.*Slot$': [{
                key: 'slot.context.menu.title.dummy1',
                displayClass: 'editbutton',
                i18nKey: 'slot.context.menu.title.dummy1',
                iconIdle: smarteditroot + '/static-resources/images/slot_contextualmenu_placeholder_off.png',
                iconNonIdle: smarteditroot + '/static-resources/images/slot_contextualmenu_placeholder_on.png',
                smallIcon: smarteditroot + '/static-resources/images/slot_contextualmenu_placeholder_small.png',
                callback: function() {
                    addContextualMenuItem('dummy3');
                }
            }, {
                key: 'slot.context.menu.title.dummy2',
                displayClass: 'editbutton',
                i18nKey: 'slot.context.menu.title.dummy2',
                iconIdle: smarteditroot + '/static-resources/images/slot_contextualmenu_placeholder_off.png',
                iconNonIdle: smarteditroot + '/static-resources/images/slot_contextualmenu_placeholder_on.png',
                smallIcon: smarteditroot + '/static-resources/images/slot_contextualmenu_placeholder_small.png',
                callback: function() {
                    addContextualMenuItem('dummy4');
                }
            }, {
                key: 'slot.context.menu.title.dummy6',
                templateUrl: 'path/to/dummyTemplate.html'
            }]
        });

        var addContextualMenuItem = function(key) {
            contextualMenuService.addItems({
                '^.*Slot$': [{
                    key: 'slot.context.menu.title' + key,
                    displayClass: 'editbutton',
                    i18nKey: 'slot.context.menu.title.' + key,
                    iconIdle: smarteditroot + '/static-resources/images/slot_contextualmenu_placeholder_off.png',
                    iconNonIdle: smarteditroot + '/static-resources/images/slot_contextualmenu_placeholder_on.png',
                    smallIcon: smarteditroot + '/static-resources/images/slot_contextualmenu_placeholder_small.png',
                    callback: function() {
                        addContextualMenuItem('dummy5');
                    }
                }]
            });
        };
    });
