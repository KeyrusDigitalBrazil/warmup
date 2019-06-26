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
angular.module('cmsItemDropdownDropdownPopulatorModule', ['dropdownPopulatorModule', 'genericEditorModule'])
    .factory('CMSItemDropdownDropdownPopulator', function(extend, lodash, DropdownPopulatorInterface, genericEditorStackService, uriDropdownPopulator) {

        // -------------------------------------------------------------------------------------
        // Constants
        // -------------------------------------------------------------------------------------
        var CMS_ITEMS_URI = '/cmswebservices/v1/sites/CURRENT_CONTEXT_SITE_ID/cmsitems';

        // -------------------------------------------------------------------------------------
        // API
        // -------------------------------------------------------------------------------------
        var CmsItemDropdownDropdownPopulator = function() {

            DropdownPopulatorInterface.call(this, lodash);
        };

        CmsItemDropdownDropdownPopulator = extend(DropdownPopulatorInterface, CmsItemDropdownDropdownPopulator);

        CmsItemDropdownDropdownPopulator.prototype.fetchAll = function(payload) {
            preparePayload(payload);
            return uriDropdownPopulator.fetchAll(payload).then(function(items) {
                return getNonNestedComponents(payload.field.editorStackId, items);
            });
        };

        CmsItemDropdownDropdownPopulator.prototype.fetchPage = function(payload) {
            preparePayload(payload);
            return uriDropdownPopulator.fetchPage(payload).then(function(result) {
                result.response = getNonNestedComponents(payload.field.editorStackId, result.response);

                // TODO: Check if this is the right count or if other count needs to be updated. 
                result.pagination.count = result.response.length;

                return result;
            });
        };

        CmsItemDropdownDropdownPopulator.prototype.getItem = function(payload) {
            preparePayload(payload);
            return uriDropdownPopulator.getItem(payload);
        };

        // -------------------------------------------------------------------------------------
        // Helper Methods
        // -------------------------------------------------------------------------------------
        var preparePayload = function(payload) {
            payload.field.uri = CMS_ITEMS_URI;
        };

        var getNonNestedComponents = function(editorStackId, components) {
            // Get the IDs of the components that are already opened in the editor's stack.
            var componentsInStack = genericEditorStackService.getEditorsStack(editorStackId).filter(function(componentInStack) {
                return componentInStack.component.uuid;
            }).map(function(componentInStack) {
                return componentInStack.component.uuid;
            });

            components = components.filter(function(componentInList) {
                return !componentInList.uuid || componentsInStack.indexOf(componentInList.uuid) === -1;
            });

            return components;
        };

        return new CmsItemDropdownDropdownPopulator();
    });
