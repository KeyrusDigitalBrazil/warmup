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
angular.module('customReloadButtonModule', ['smarteditServicesModule'])
    .controller('SECustomReloadButtonController', function(systemEventService) {
        this.load = function() {
            systemEventService.publishAsync('load-structure', {
                structure: {
                    attributes: [{
                        cmsStructureType: 'ShortString',
                        qualifier: 'name',
                        i18nKey: 'type.anyComponentType.name.name'
                    }, {
                        cmsStructureType: 'RichText',
                        qualifier: 'richtext',
                        i18nKey: 'type.anyComponentType.richtext.name'
                    }, {
                        cmsStructureType: 'ShortString',
                        qualifier: 'componentCustomField',
                        i18nKey: 'type.anyComponentType.componentcustomfield.name'
                    }],
                    category: 'TEST'
                },
                content: this.model
            });
        };
    })
    .component('seCustomReloadButton', {
        transclude: true,
        template: '<input type="button" id="load-button" data-ng-click="ctrl.load()" value="Reload" /><pre>model:{{model | json}}</pre>',
        controller: 'SECustomReloadButtonController',
        controllerAs: 'ctrl',
        bindings: {
            model: '<'
        }
    });
