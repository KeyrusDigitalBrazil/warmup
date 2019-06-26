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
angular.module('componentItemModule', ['cmsSmarteditServicesModule', 'translationServiceModule'])
    .controller('componentItemController', function(assetsService, $translate) {

        this.$onInit = function() {
            this.imageRoot = assetsService.getAssetsRoot();
        };

        this.getTemplateInfoForNonCloneableComponent = function() {
            var message = $translate.instant('se.cms.component.non.cloneable.tooltip', {
                componentName: this.componentInfo.name
            });
            return "<div class='se-ypopover--inner-content'>" + message + "</div>";
        };

        this.$onChanges = function(changesObj) {
            if (changesObj.cloneOnDrop) {
                this.componentDisabled = this.cloneOnDrop && !this.componentInfo.cloneable;
            }
        };

    })
    .component('componentItem', {
        templateUrl: 'componentItemTemplate.html',
        controller: 'componentItemController',
        bindings: {
            componentInfo: '<',
            cloneOnDrop: '<'
        }
    });
