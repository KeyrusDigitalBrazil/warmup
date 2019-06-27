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
        'smarteditServicesModule',
        'dummyModule'
    ])
    .run(function(contextualMenuService, decoratorService, smarteditroot) {

        decoratorService.addMappings({
            '^((?!Slot).)*$': ['contextualMenu']
        });

        decoratorService.enable('contextualMenu');

        contextualMenuService.addItems({
            'componentType1': [{
                key: 'templateString2',
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
            }]
        });

    });
angular.module('dummyModule', ['l10nModule'])
    .controller('dummyController', function() {

        this.$onInit = function() {
            this.dymmyText = {
                'en': 'dymmyText in english',
                'fr': 'dymmyText in french'
            };
        };

    })
    .component('dummy', {
        template: '<div>{{ctrl.dymmyText | l10n}}</div>',
        controller: 'dummyController',
        controllerAs: 'ctrl',
        bindings: {}
    });
