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
angular.module('FakeModule', [
        'decoratorServiceModule',
        'smarteditServicesModule'
    ])
    .run(function(contextualMenuService, decoratorService, smarteditroot) {

        decoratorService.addMappings({
            '^((?!Slot).)*$': ['contextualMenu']
        });

        decoratorService.enable('contextualMenu');

        contextualMenuService.addItems({
            'componentType1': [{
                key: 'infoFeature',
                i18nKey: 'INFO',
                condition: function(configuration) {
                    configuration.element.addClass('conditionClass1');
                    return true;
                },
                callback: function() {
                    alert('whatever');
                },
                displayClass: 'editbutton',
                iconIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_edit_off.png',
                iconNonIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_edit_on.png',
                smallIcon: smarteditroot + '/../../cmssmartedit/icons/info.png'
            }, {
                key: 'infoFeature1',
                i18nKey: 'INFO',
                condition: function(configuration) {
                    configuration.element.addClass('conditionClass1');
                    return true;
                },
                callback: function() {
                    alert('whatever');
                },
                displayClass: 'editbutton',
                iconIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_edit_off.png',
                iconNonIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_edit_on.png',
                smallIcon: smarteditroot + '/../../cmssmartedit/icons/info.png'
            }, {
                key: 'infoFeature2',
                i18nKey: 'INFO',
                condition: function(configuration) {
                    configuration.element.addClass('conditionClass1');
                    return true;
                },
                callback: function() {
                    alert('whatever');
                },
                displayClass: 'editbutton',
                iconIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_edit_off.png',
                iconNonIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_edit_on.png',
                smallIcon: smarteditroot + '/../../cmssmartedit/icons/info.png'
            }, {
                key: 'infoFeature3',
                i18nKey: 'INFO',
                condition: function(configuration) {
                    configuration.element.addClass('conditionClass1');
                    return true;
                },
                callback: function() {
                    alert('whatever');
                },
                displayClass: 'editbutton',
                iconIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_edit_off.png',
                iconNonIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_edit_on.png',
                smallIcon: smarteditroot + '/../../cmssmartedit/icons/info.png'
            }, {
                key: 'infoFeature4',
                i18nKey: 'INFO',
                condition: function(configuration) {
                    configuration.element.addClass('conditionClass1');
                    return true;
                },
                callback: function() {
                    alert('whatever');
                },
                displayClass: 'editbutton',
                iconIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_edit_off.png',
                iconNonIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_edit_on.png',
                smallIcon: smarteditroot + '/../../cmssmartedit/icons/info.png'
            }, {
                key: 'templateString',
                i18nKey: 'TEMPLATE',
                condition: function(configuration) {
                    configuration.element.addClass('conditionClass1');
                    return true;
                },
                action: {
                    template: '<div id="ctx-template">testing 1,2...</div>'
                },
                displayClass: 'editbutton',
                iconIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_edit_off.png',
                iconNonIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_edit_on.png',
                smallIcon: smarteditroot + '/../../cmssmartedit/icons/info.png'
            }],
            'componentType2': [{
                key: 'deleteFeature',
                i18nKey: 'DELETE',
                condition: function() {
                    return true;
                },
                callback: function() {
                    alert('delete for paragraph component');
                },
                displayClass: 'hyicon hyicon-remove',
                iconIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/trash_small.png',
                iconNonIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/trash_small.png',
                smallIcon: smarteditroot + '/../../cmssmartedit/icons/info.png'
            }, {
                key: 'templateString',
                i18nKey: 'TEMPLATEURL',
                condition: function(configuration) {
                    configuration.element.addClass('conditionClass1');
                    return true;
                },
                action: {
                    templateUrl: 'ctxTemplate.html'
                },
                displayClass: 'editbutton',
                iconIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_edit_off.png',
                iconNonIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_edit_on.png',
                smallIcon: smarteditroot + '/../../cmssmartedit/icons/info.png'
            }],
            'SimpleResponsiveBannerComponent': [{
                key: 'enableDisableContextualMenuItems',
                i18nKey: 'enable',
                callback: function() {
                    if (!areItemsEnabled) {
                        addContextualMenuItems();
                    } else {
                        removeContextualMenuItems();
                    }

                    contextualMenuService.refreshMenuItems();
                    areItemsEnabled = !areItemsEnabled;
                },
                displayClass: 'editbutton',
                iconIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/info_small.png',
                iconNonIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/info_small.png',
                smallIcon: smarteditroot + '/../../cmssmartedit/icons/info.png'
            }],
            'componentType7': [{
                key: 'se.cms.draganddrop',
                i18nKey: 'Drag-Drop',
                condition: function(configuration) {
                    configuration.element.addClass('conditionClass1');
                    return true;
                },
                callback: function() {
                    alert('DnD image icon');
                },
                displayClass: 'movebutton',
                iconIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_move_off_test.png',
                iconNonIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_move_on_test.png'
            }, {
                key: 'se.cms.remove',
                i18nKey: 'Remove',
                condition: function(configuration) {
                    configuration.element.addClass('conditionClass1');
                    return true;
                },
                callback: function() {
                    alert('Remove image icon');
                },
                displayClass: 'removebutton',
                iconIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_delete_off.png',
                iconNonIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_delete_on.png'
            }, {
                key: 'se.cms.edit',
                i18nKey: 'Edit',
                condition: function(configuration) {
                    configuration.element.addClass('conditionClass1');
                    return true;
                },
                callback: function() {
                    alert('Edit image icon');
                },
                displayClass: 'editbutton',
                iconIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_edit_off.png',
                iconNonIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_edit_on.png'
            }, {
                key: 'se.cms.draganddropsmall',
                i18nKey: 'Drag-Drop',
                condition: function(configuration) {
                    configuration.element.addClass('conditionClass1');
                    return true;
                },
                callback: function() {
                    alert('Small DnD image icon');
                },
                displayClass: 'editbutton',
                smallIconIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_move_off_s.png',
                smallIconNonIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_move_on_s.png'
            }, {
                key: 'se.cms.removesmall',
                i18nKey: 'Remove',
                condition: function(configuration) {
                    configuration.element.addClass('conditionClass1');
                    return true;
                },
                callback: function() {
                    alert('Small remove image icon');
                },
                displayClass: 'removebutton',
                smallIconIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_delete_off.png',
                smallIconNonIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_delete_on.png'
            }, {
                key: 'se.cms.editsmall',
                i18nKey: 'Edit',
                condition: function(configuration) {
                    configuration.element.addClass('conditionClass1');
                    return true;
                },
                callback: function() {
                    alert('Small edit image icon');
                },
                displayClass: 'editbutton',
                smallIconIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_edit_off.png',
                smallIconNonIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_edit_on.png'
            }],
            'componentType8': [{
                key: 'se.cms.draganddropFeature2',
                i18nKey: 'Drag-Drop',
                condition: function(configuration) {
                    configuration.element.addClass('conditionClass1');
                    return true;
                },
                callback: function() {
                    alert('DnD techne icon');
                },
                displayClass: 'movebutton',
                displayIconClass: 'hyicon hyicon-dragdroplg',
                displaySmallIconClass: 'hyicon hyicon-dragdroplg'
            }, {
                key: 'se.cms.removeFeature2',
                i18nKey: 'Remove',
                condition: function(configuration) {
                    configuration.element.addClass('conditionClass1');
                    return true;
                },
                callback: function() {
                    alert('Remove techne icon');
                },
                displayClass: 'removebutton',
                displayIconClass: 'hyicon hyicon-removelg',
                displaySmallIconClass: 'hyicon hyicon-removelg'
            }, {
                key: 'se.cms.editFeature2',
                i18nKey: 'Edit',
                condition: function(configuration) {
                    configuration.element.addClass('conditionClass1');
                    return true;
                },
                callback: function() {
                    alert('Edit techne icon');
                },
                displayClass: 'editbutton',
                displayIconClass: 'hyicon hyicon-edit',
                displaySmallIconClass: 'hyicon hyicon-edit'
            }, {
                key: 'se.cms.draganddropSmallFeature2',
                i18nKey: 'Drag-Drop',
                condition: function(configuration) {
                    configuration.element.addClass('conditionClass1');
                    return true;
                },
                callback: function() {
                    alert('DnD small techne icon');
                },
                displayClass: 'movebutton',
                displayIconClass: 'hyicon hyicon-dragdroplg',
                displaySmallIconClass: 'hyicon hyicon-dragdroplg'
            }, {
                key: 'se.cms.removeSmallFeature2',
                i18nKey: 'Remove',
                condition: function(configuration) {
                    configuration.element.addClass('conditionClass1');
                    return true;
                },
                callback: function() {
                    alert('Remove small techne icon');
                },
                displayClass: 'removebutton',
                displayIconClass: 'hyicon hyicon-removelg',
                displaySmallIconClass: 'hyicon hyicon-removelg'
            }, {
                key: 'se.cms.editSmallFeature2',
                i18nKey: 'Edit',
                condition: function(configuration) {
                    configuration.element.addClass('conditionClass1');
                    return true;
                },
                callback: function() {
                    alert('Edit small techne icon');
                },
                displayClass: 'editbutton',
                displayIconClass: 'hyicon hyicon-edit',
                displaySmallIconClass: 'hyicon hyicon-edit'
            }],
            'componentType9': [{
                key: 'se.cms.draganddropFeature3',
                i18nKey: 'Drag-Drop',
                condition: function(configuration) {
                    configuration.element.addClass('conditionClass1');
                    return true;
                },
                callback: function() {
                    alert('DnD image icon -- mixed');
                },
                displayClass: 'movebutton',
                iconIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_move_off_test.png',
                iconNonIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_move_on_test.png'
            }, {
                key: 'se.cms.removeFeature3',
                i18nKey: 'Remove',
                condition: function(configuration) {
                    configuration.element.addClass('conditionClass1');
                    return true;
                },
                callback: function() {
                    alert('Remove techne icon -- mixed');
                },
                displayClass: 'removebutton',
                displayIconClass: 'hyicon hyicon-removelg',
                displaySmallIconClass: 'hyicon hyicon-removelg'
            }, {
                key: 'se.cms.editFeature3',
                i18nKey: 'Edit',
                condition: function(configuration) {
                    configuration.element.addClass('conditionClass1');
                    return true;
                },
                callback: function() {
                    alert('Edit techne icon -- mixed');
                },
                displayClass: 'editbutton',
                displayIconClass: 'hyicon hyicon-edit',
                displaySmallIconClass: 'hyicon hyicon-edit'
            }, {
                key: 'se.cms.draganddropSmallFeature3',
                i18nKey: 'Drag-Drop',
                condition: function(configuration) {
                    configuration.element.addClass('conditionClass1');
                    return true;
                },
                callback: function() {
                    alert('DnD small image icon -- mixed');
                },
                displayClass: 'movebutton',
                smallIconIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_move_off_s.png',
                smallIconNonIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_move_on_s.png'
            }, {
                key: 'se.cms.removeSmallFeature3',
                i18nKey: 'Remove',
                condition: function(configuration) {
                    configuration.element.addClass('conditionClass1');
                    return true;
                },
                callback: function() {
                    alert('Remove small techne icon -- mixed');
                },
                displayClass: 'removebutton',
                displayIconClass: 'hyicon hyicon-removelg',
                displaySmallIconClass: 'hyicon hyicon-removelg'
            }, {
                key: 'se.cms.editSmallFeature3',
                i18nKey: 'Edit',
                condition: function(configuration) {
                    configuration.element.addClass('conditionClass1');
                    return true;
                },
                callback: function() {
                    alert('Edit small techne icon -- mixed');
                },
                displayClass: 'editbutton',
                displayIconClass: 'hyicon hyicon-edit',
                displaySmallIconClass: 'hyicon hyicon-edit'
            }],
            'componentType10': [{
                key: 'se.cms.draganddropSmallFeature4',
                i18nKey: 'Drag-Drop',
                condition: function(configuration) {
                    configuration.element.addClass('conditionClass1');
                    return true;
                },
                callback: function() {
                    alert('DnD small image icon -- more menu');
                },
                displayClass: 'movebutton',
                iconIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_move_off_test.png',
                iconNonIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_move_on_test.png',
                smallIconIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_move_off_s.png',
                smallIconNonIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_move_on_s.png'
            }, {
                key: 'se.cms.removeSmallFeature4',
                i18nKey: 'Remove',
                condition: function(configuration) {
                    configuration.element.addClass('conditionClass1');
                    return true;
                },
                callback: function() {
                    alert('Remove small techne icon -- more menu');
                },
                displayClass: 'removebutton',
                displayIconClass: 'hyicon hyicon-removelg',
                displaySmallIconClass: 'hyicon hyicon-removelg'
            }, {
                key: 'se.cms.editSmallFeature4',
                i18nKey: 'Edit',
                condition: function(configuration) {
                    configuration.element.addClass('conditionClass1');
                    return true;
                },
                callback: function() {
                    alert('Edit small techne icon -- more menu');
                },
                displayClass: 'editbutton',
                displayIconClass: 'hyicon hyicon-edit',
                displaySmallIconClass: 'hyicon hyicon-edit'
            }],
            'componentType11': [{
                key: 'se.cms.draganddropSmallFeature5',
                i18nKey: 'Drag-Drop',
                condition: function(configuration) {
                    configuration.element.addClass('conditionClass1');
                    return true;
                },
                callback: function() {
                    alert('DnD small image icon -- more menu');
                },
                displayClass: 'movebutton',
                iconIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_move_off_test.png',
                iconNonIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_move_on_test.png',
                smallIconIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_move_off_s.png',
                smallIconNonIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/contextualmenu_move_on_s.png'
            }, {
                key: 'se.cms.removeSmallFeature5',
                i18nKey: 'Remove',
                condition: function(configuration) {
                    configuration.element.addClass('conditionClass1');
                    return true;
                },
                callback: function() {
                    alert('Remove small techne icon -- more menu');
                },
                displayClass: 'removebutton',
                displayIconClass: 'hyicon hyicon-removelg',
                displaySmallIconClass: 'hyicon hyicon-removelg'
            }, {
                key: 'se.cms.editSmallFeature5',
                i18nKey: 'Edit',
                condition: function(configuration) {
                    configuration.element.addClass('conditionClass1');
                    return true;
                },
                callback: function() {
                    alert('Edit small techne icon -- more menu');
                },
                displayClass: 'editbutton',
                displayIconClass: 'hyicon hyicon-edit',
                displaySmallIconClass: 'hyicon hyicon-edit'
            }]
        });

        var areItemsEnabled = false;
        var addContextualMenuItems = function() {
            contextualMenuService.addItems({
                'componentType4': [{
                    key: 'newFeature',
                    i18nKey: 'INFO',
                    condition: function(configuration) {
                        configuration.element.addClass('conditionClass1');
                        return true;
                    },
                    callback: function() {
                        alert('new Feature called');
                    },
                    displayClass: 'editbutton',
                    iconIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/info_small.png',
                    iconNonIdle: smarteditroot + '/../../test/e2e/contextualMenu/icons/info_small.png',
                    smallIcon: smarteditroot + '/../../cmssmartedit/icons/info.png'
                }]
            });
        };

        var removeContextualMenuItems = function() {
            contextualMenuService.removeItemByKey('newFeature');
        };
    });
