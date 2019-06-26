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
/**
 * @ngdoc overview
 * @name editorEnablerServiceModule
 * @description
 * # The editorEnablerServiceModule
 *
 * The editor enabler service module provides a service that allows enabling the Edit Component contextual menu item,
 * providing a SmartEdit CMS admin the ability to edit various properties of the given component.
 */
angular.module("editorEnablerServiceModule", [
        "componentVisibilityAlertServiceModule",
        "editorModalServiceModule",
        "slotRestrictionsServiceModule",
        "cmsSmarteditServicesModule"
    ])

    /**
     * @ngdoc service
     * @name editorEnablerServiceModule.service:editorEnablerService
     *
     * @description
     * Convenience service to attach the open editor modal action to the contextual menu of a given component type, or
     * given regex corresponding to a selection of component types.
     *
     * Example: The Edit button is added to the contextual menu of the CMSParagraphComponent, and all types postfixed
     * with BannerComponent.
     *
     * <pre>
     angular.module('app', ['editorEnablerServiceModule'])
     .run(function(editorEnablerService) {
                editorEnablerService.enableForComponents(['CMSParagraphComponent', '*BannerComponent']);
            });
     * </pre>
     */
    .factory("editorEnablerService", function(
        componentVisibilityAlertService,
        componentHandlerService,
        editorModalService,
        featureService,
        slotRestrictionsService,
        typePermissionsRestService
    ) {

        // Class Definition
        function EditorEnablerService() {}

        // Private


        EditorEnablerService.prototype._key = "se.cms.edit";

        EditorEnablerService.prototype._nameI18nKey = "se.cms.contextmenu.nameI18nKey.edit";

        EditorEnablerService.prototype._i18nKey = "se.cms.contextmenu.title.edit";

        EditorEnablerService.prototype._descriptionI18nKey = "se.cms.contextmenu.descriptionI18n.edit";

        EditorEnablerService.prototype._editDisplayClass = "editbutton";

        EditorEnablerService.prototype._editDisplayIconClass = "hyicon hyicon-edit";

        EditorEnablerService.prototype._editDisplaySmallIconClass = "hyicon hyicon-edit";

        EditorEnablerService.prototype._editButtonCallback = function(contextualMenuParams) {
            var slotUuid = contextualMenuParams.slotUuid;
            editorModalService.open(contextualMenuParams.componentAttributes).then(function(item) {
                componentVisibilityAlertService.checkAndAlertOnComponentVisibility({
                    itemId: item.uuid,
                    itemType: item.itemtype,
                    catalogVersion: item.catalogVersion,
                    restricted: item.restricted,
                    slotId: slotUuid,
                    visible: item.visible
                });
            });
        };

        EditorEnablerService.prototype._condition = function(contextualMenuParams) {
            var slotId = componentHandlerService.getParentSlotForComponent(contextualMenuParams.element);

            return slotRestrictionsService.isSlotEditable(slotId).then(function(isSlotEditable) {
                return typePermissionsRestService.hasUpdatePermissionForTypes([contextualMenuParams.componentType]).then(function(hasEditPermission) {
                    return hasEditPermission[contextualMenuParams.componentType] && isSlotEditable && !componentHandlerService.isExternalComponent(contextualMenuParams.componentId, contextualMenuParams.componentType);
                }.bind(this));
            });
        };

        // Public
        /**
         * @ngdoc method
         * @name editorEnablerServiceModule.service:editorEnablerService#enableForComponents
         * @methodOf editorEnablerServiceModule.service:editorEnablerService
         *
         * @description
         * Enables the Edit contextual menu item for the given component types.
         *
         * @param {Array} componentTypes The list of component types, as defined in the platform, for which to enable the
         * Edit contextual menu.
         */
        EditorEnablerService.prototype.enableForComponents = function(componentTypes) {
            var contextualMenuConfig = {
                key: this._key, // It's the same key as the i18n, however here we're not doing any i18n work.
                nameI18nKey: this._nameI18nKey,
                descriptionI18nKey: this._descriptionI18nKey,
                regexpKeys: componentTypes,
                displayClass: this._editDisplayClass,
                displayIconClass: this._editDisplayIconClass,
                displaySmallIconClass: this._editDisplaySmallIconClass,
                i18nKey: this._i18nKey,
                callback: this._editButtonCallback,
                condition: this._condition,
                permissions: ['se.context.menu.edit.component']
            };

            featureService.addContextualMenuButton(contextualMenuConfig);
        };

        // Factory Definition
        return new EditorEnablerService();
    });
