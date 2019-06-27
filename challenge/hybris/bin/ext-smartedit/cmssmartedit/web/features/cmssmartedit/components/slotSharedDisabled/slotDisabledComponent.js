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
angular.module('slotDisabledDecoratorModule', ['yPopoverModule', 'l10nModule', 'cmsSmarteditServicesModule'])
    .controller('slotDisabledComponentController', function($translate, componentHandlerService, catalogService, l10nFilter, cMSModesService) {

        var DEFAULT_DECORATOR_MSG = "se.cms.sharedslot.decorator.label";
        var EXTERNAL_SLOT_DECORATOR_MSG = "se.cms.externalsharedslot.decorator.label";

        var DEFAULT_DECORATOR_MSG_VERSIONING_MODE = "se.cms.versioning.shared.slot.from.label";
        var EXTERNAL_SLOT_DECORATOR_MSG_VERSIONING_MODE = "se.cms.versioning.global.shared.slot.from.label";

        var ICONS_CLASSES = {
            GLOBE: 'hyicon-globe',
            LOCK: 'hyicon-lock',
            HOVERED: 'hyicon-linked'
        };

        this.$onInit = function() {

            this.isReady = false;
            this._checkIfSlotIsInherited();
            this._getSourceCatalogName();
            this._isVersioningPerspectiveActive();
        };

        this._checkIfSlotIsInherited = function() {
            var componentCatalogVersion = this.componentAttributes.smarteditCatalogVersionUuid;
            componentHandlerService.getCatalogVersionUUIDFromPage().then(function(uuid) {
                this.isExternalSlot = componentCatalogVersion !== uuid;
            }.bind(this));
        };

        this._getSourceCatalogName = function() {
            var catalogVersionUUID = this.componentAttributes.smarteditCatalogVersionUuid;
            catalogService.getCatalogVersionByUuid(catalogVersionUUID).then(function(catalogVersion) {
                this.catalogName = catalogVersion.catalogName;
            }.bind(this));
        };

        this._isVersioningPerspectiveActive = function() {
            cMSModesService.isVersioningPerspectiveActive().then(function(isActive) {
                this.isVersioningPerspective = isActive;
                this.isReady = true;
            }.bind(this));
        };

        this.getPopoverMessage = function() {

            var msgToLocalize;
            if (this.isVersioningPerspective) {
                msgToLocalize = (this.isExternalSlot) ? EXTERNAL_SLOT_DECORATOR_MSG_VERSIONING_MODE : DEFAULT_DECORATOR_MSG_VERSIONING_MODE;
            } else {
                msgToLocalize = (this.isExternalSlot) ? EXTERNAL_SLOT_DECORATOR_MSG : DEFAULT_DECORATOR_MSG;
            }

            var msgParams = {
                catalogName: l10nFilter(this.catalogName),
                slotId: this.componentAttributes.smarteditComponentId
            };

            return $translate.instant(msgToLocalize, msgParams);
        };

        this.getSlotIconClass = function() {
            var iconClass = "";
            if (this.active) {
                iconClass = (this.isExternalSlot) ? ICONS_CLASSES.GLOBE : ICONS_CLASSES.HOVERED;
            } else if (this.isExternalSlot !== undefined) {
                iconClass = (this.isExternalSlot) ? ICONS_CLASSES.GLOBE : ICONS_CLASSES.HOVERED;
            }

            return iconClass;
        };

        this.getOuterSlotClass = function() {
            return this.getSlotIconClass() === ICONS_CLASSES.GLOBE ? 'disabled-shared-slot__icon--outer-globe' : '';
        }.bind(this);
    })
    .component('slotDisabledComponent', {
        templateUrl: 'slotDisabledTemplate.html',
        controller: 'slotDisabledComponentController',
        controllerAs: 'ctrl',
        bindings: {
            active: '=',
            componentAttributes: '<'
        }
    });
