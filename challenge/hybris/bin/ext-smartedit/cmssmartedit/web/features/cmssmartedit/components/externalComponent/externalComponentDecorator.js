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
angular.module('externalComponentDecoratorModule', ['functionsModule', 'cmsSmarteditServicesModule', 'yPopoverModule'])
    .controller('externalComponentDecoratorController', function($element, $translate, $log, l10nFilter, CONTENT_SLOT_TYPE, componentHandlerService, catalogService, cMSModesService) {

        this.$onInit = function() {

            var parentSlotIdForComponent = componentHandlerService.getParentSlotForComponent($element);
            this.isExtenalSlot = componentHandlerService.isExternalComponent(parentSlotIdForComponent, CONTENT_SLOT_TYPE);
            this.isReady = false;

            cMSModesService.isVersioningPerspectiveActive().then(function(isActive) {
                this.isVersioningPerspective = isActive;
                catalogService.getCatalogVersionByUuid(this.componentAttributes.smarteditCatalogVersionUuid).then(function(catalogVersion) {
                    this.catalogVersionText = l10nFilter(catalogVersion.catalogName) + ' (' + catalogVersion.version + ')';
                    this.isReady = true;
                }.bind(this), function() {
                    $log.error('externalComponentDecorator - cannot find catalog version for uuid', this.componentAttributes.smarteditCatalogVersionUuid);
                }.bind(this));
            }.bind(this));

        };

        this.getTooltipTemplate = function() {
            return "<div class='external-component-decorator__tooltip'>" + $translate.instant('se.cms.from.external.catalog.version', {
                catalogVersion: this.catalogVersionText
            }) + "</div>";
        };
    })
    .directive('externalComponentDecorator', function() {
        return {
            templateUrl: 'externalComponentDecoratorTemplate.html',
            restrict: 'C',
            transclude: true,
            replace: false,
            controller: 'externalComponentDecoratorController',
            controllerAs: 'ctrl',
            scope: {},
            bindToController: {
                active: '=',
                componentAttributes: '<'
            }
        };
    });
