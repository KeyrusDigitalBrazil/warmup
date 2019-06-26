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
angular
    .module('cmsGenericEditorConfigurationServiceModule', ['genericEditorModule', 'functionsModule'])
    .service('cmsGenericEditorConfigurationService', function(editorFieldMappingService, genericEditorTabService, sanitize) {

        // --------------------------------------------------------------------------------------
        // Constants
        // --------------------------------------------------------------------------------------
        var DEFAULT_PAGE_TAB_ID = 'information';
        var CATEGORIES = {
            PAGE: 'PAGE',
            COMPONENT: 'COMPONENT'
        };

        // --------------------------------------------------------------------------------------
        // Public Methods
        // --------------------------------------------------------------------------------------
        this.setDefaultEditorFieldMappings = function() {
            editorFieldMappingService.addFieldMapping('Media', null, null, {
                template: 'mediaTemplate.html'
            });

            editorFieldMappingService.addFieldMapping('MediaContainer', null, null, {
                template: 'mediaContainerTemplate.html'
            });

            editorFieldMappingService.addFieldMapping('NavigationNodeSelector', null, null, {
                template: 'navigationNodeSelectorWrapperTemplate.html'
            });

            editorFieldMappingService.addFieldMapping('MultiProductSelector', null, null, {
                template: 'multiProductSelectorTemplate.html'
            });

            editorFieldMappingService.addFieldMapping('MultiCategorySelector', null, null, {
                template: 'multiCategorySelectorTemplate.html'
            });

            editorFieldMappingService.addFieldMapping('CMSLinkToSelect', null, null, {
                template: 'cmsLinkToSelectWrapperTemplate.html'
            });

            editorFieldMappingService.addFieldMapping('SingleOnlineProductSelector', null, null, {
                template: 'singleActiveCatalogAwareItemSelectorWrapperTemplate.html'
            });

            editorFieldMappingService.addFieldMapping('SingleOnlineCategorySelector', null, null, {
                template: 'singleActiveCatalogAwareItemSelectorWrapperTemplate.html'
            });

            editorFieldMappingService.addFieldMapping('CMSItemDropdown', null, null, {
                template: 'cmsItemDropdownWrapperTemplate.html'
            });

            editorFieldMappingService.addFieldMapping('CMSComponentRestrictionsEditor', null, null, {
                template: 'componentRestrictionsEditorWrapperTemplate.html'
            });

            editorFieldMappingService.addFieldMapping('PageRestrictionsEditor', null, "restrictions", {
                template: 'pageRestrictionsEditorWrapperTemplate.html'
            });

            // for editing modal only, not used for create/clone
            editorFieldMappingService.addFieldMapping('DisplayConditionEditor', null, 'displayCondition', {
                template: 'pageDisplayConditionWrapperTemplate.html',
                hidePrefixLabel: true
            });

            editorFieldMappingService.addFieldMapping('ShortString', this._isPagePredicate, 'typeCode', {
                template: 'pageTypeEditorTemplate.html',
                hidePrefixLabel: true
            });

            editorFieldMappingService.addFieldMapping('Boolean', null, 'visible', {
                template: 'booleanWrapperTemplate.html',
                i18nKey: 'type.component.abstractcmscomponent.visible.name'
            });

            editorFieldMappingService.addFieldMapping('LinkToggle', null, null, {
                template: 'linkToggleWrapperTemplate.html',
                customSanitize: function(payload, sanitizeFn) {
                    if (sanitizeFn === undefined) {
                        sanitizeFn = sanitize;
                    }
                    payload.urlLink = sanitizeFn(payload.urlLink);
                }
            });

            editorFieldMappingService.addFieldMapping('RestrictionsList', null, null, {
                template: 'restrictionsListWrapperTemplate.html',
                hidePrefixLabel: true
            });

            // Page restore widgets. 
            editorFieldMappingService.addFieldMapping('DuplicatePrimaryNonContentPageMessage', null, null, {
                template: 'duplicatePrimaryNonContentPageWrapperTemplate.html',
                hidePrefixLabel: true
            });

            editorFieldMappingService.addFieldMapping('DuplicatePrimaryContentPage', null, null, {
                template: 'duplicatePrimaryContentPageWrapperTemplate.html',
                hidePrefixLabel: false
            });

            editorFieldMappingService.addFieldMapping('MissingPrimaryContentPage', null, null, {
                template: 'missingPrimaryContentPageWrapperTemplate.html',
                hidePrefixLabel: false
            });
        };

        this.setDefaultTabsConfiguration = function() {
            genericEditorTabService.configureTab('default', {
                priority: 5
            });
            genericEditorTabService.configureTab('information', {
                priority: 5
            });
            genericEditorTabService.configureTab('administration', {
                priority: 4
            });
        };

        this.setDefaultTabFieldMappings = function() {
            // Set default tab. 
            genericEditorTabService.addComponentTypeDefaultTabPredicate(this._defaultTabPredicate);

            // Set tabs
            editorFieldMappingService.addFieldTabMapping(
                null, this._isComponentPredicate, "visible", "visibility");
            editorFieldMappingService.addFieldTabMapping(
                null, this._isComponentPredicate, "restrictions", "visibility");
            editorFieldMappingService.addFieldTabMapping(
                null, this._isComponentPredicate, "onlyOneRestrictionMustApply", "visibility");
            editorFieldMappingService.addFieldTabMapping(
                null, this._isComponentPredicate, "uid", "basicinfo");
            editorFieldMappingService.addFieldTabMapping(
                null, this._isComponentPredicate, "id", "basicinfo");
            editorFieldMappingService.addFieldTabMapping(
                null, this._isComponentPredicate, "modifiedtime", "basicinfo");
            editorFieldMappingService.addFieldTabMapping(
                "DateTime", this._isComponentPredicate, "creationtime", "basicinfo");

            // Page Tabs
            editorFieldMappingService.addFieldTabMapping(
                "DisplayConditionEditor", this._isPagePredicate, "displayCondition", "displaycondition");
            editorFieldMappingService.addFieldTabMapping(
                null, this._isPagePredicate, "restrictions", "restrictions");
        };

        // --------------------------------------------------------------------------------------
        // Predicates
        // --------------------------------------------------------------------------------------
        this._defaultTabPredicate = function(componentTypeStructure) {
            return (componentTypeStructure.category === CATEGORIES.PAGE) ? DEFAULT_PAGE_TAB_ID : null;
        };

        this._isPagePredicate = function(componentType, field, componentTypeStructure) {
            return componentTypeStructure.category === CATEGORIES.PAGE;
        };

        this._isComponentPredicate = function(componentType, field, componentTypeStructure) {
            return componentTypeStructure.category === CATEGORIES.COMPONENT;
        };
    });
