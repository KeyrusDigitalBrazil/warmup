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
 * @name componentCloneInfoFormModule
 * @description
 * #componentCloneInfoFormModule
 *
 * The componentCloneInfoFormModule module contains the clone page generic editor form fields
 *
 */
angular.module('componentCloneInfoFormModule', ['yLoDashModule', 'pageFacadeModule', 'pageServiceModule', 'genericEditorModule', 'resourceLocationsModule'])

    .controller('componentCloneInfoFormController', function($translate, lodash, catalogService, languageService, pageFacade, pageService, systemEventService, PAGE_CONTEXT_SITE_ID, PAGE_CONTEXT_CATALOG, PAGE_CONTEXT_CATALOG_VERSION, GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT) {

        this.$onInit = function() {
            this.pageLabel = null;
            this.catalogVersionContainsPageWithSameTypeCode = false;

            if (this.pageTypeCode !== 'ContentPage' && this.targetCatalogVersion &&
                !this._isUriContextEqualToCatalogVersion(this.uriContext, this.targetCatalogVersion)) {
                var uriContextForSelectedCatalogVersion = {};

                uriContextForSelectedCatalogVersion[PAGE_CONTEXT_SITE_ID] = this.targetCatalogVersion.siteId;
                uriContextForSelectedCatalogVersion[PAGE_CONTEXT_CATALOG] = this.targetCatalogVersion.catalogId;
                uriContextForSelectedCatalogVersion[PAGE_CONTEXT_CATALOG_VERSION] = this.targetCatalogVersion.version;

                pageService.getPrimaryPagesForPageType(this.pageTypeCode, uriContextForSelectedCatalogVersion).then(function(result) {
                    this.catalogVersionContainsPageWithSameTypeCode = !lodash.isEmpty(result);
                }.bind(this));
            }

            this.setGenericEditorApi = function(api) {
                this.pageInfoEditorApi = api;

                if (this.targetCatalogVersion && !this._isUriContextEqualToCatalogVersion(this.uriContext, this.targetCatalogVersion)) {
                    this.pageInfoEditorApi.getLanguages = function() {
                        return languageService.getLanguagesForSite(this.targetCatalogVersion.siteId);
                    }.bind(this);
                }
            }.bind(this);
        };

        this.$doCheck = function() {
            if (this.pageTypeCode === 'ContentPage' && this.targetCatalogVersion && this.pageInfoEditorApi &&
                !this._isUriContextEqualToCatalogVersion(this.uriContext, this.targetCatalogVersion)) {

                var content = this.pageInfoEditorApi.getContent();

                if (content && content.label !== this.pageLabel) {
                    this.pageLabel = content.label;

                    pageFacade.contentPageWithLabelExists(this.pageLabel, this.targetCatalogVersion.catalogId, this.targetCatalogVersion.version).then(function(pageExists) {
                        if (pageExists) {
                            systemEventService.publishAsync(GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, {
                                messages: [{
                                    subject: 'label',
                                    message: $translate.instant('se.cms.clonepagewizard.pageinfo.targetcatalogversion.label.exists.message'),
                                    type: 'Warning'
                                }]
                            });
                        } else {
                            this.pageInfoEditorApi.clearMessages();
                        }
                    }.bind(this));
                }
            }
        };

        this._isUriContextEqualToCatalogVersion = function(uriContext, catalogVersion) {
            return uriContext && catalogVersion && catalogVersion.siteId === uriContext.CURRENT_CONTEXT_SITE_ID &&
                catalogVersion.catalogId === uriContext.CURRENT_CONTEXT_CATALOG &&
                catalogVersion.version === uriContext.CURRENT_CONTEXT_CATALOG_VERSION;
        };
    })

    /**
     * @ngdoc directive
     * @name componentCloneInfoFormModule.directive:componentCloneInfoForm
     * @scope
     * @restrict E
     * @element component-clone-info-form
     *
     * @description
     * Component for the clone page info form
     *
     * @param {<Object} structure The structure that is passed on to the generic editor
     * @param {<Object} content The model that is passed on to the generic editor
     * @param {Function} submit Function defined in outer scope for saving the form fields
     * @param {Function} reset Function defined in outer scope for reseting all fields in the form
     * @param {Function} isDirty Function defined in outer scope returning if the form is in pristine state or not (e.g., have been modified).
     * @param {Function} isValid Function defined in outer scope returning if all fields in the form are valid
     * @param {<String} pageTemplate The pageTemplate property of the cloned page
     * @param {<String} pageTypeCode The typeCode property of the cloned page
     * @param {<Object} uriContext The uri context containing site/catalog information. This is necessary for the
     * component to determine if the selected target catalog version is different then the current page context
     * @param {String} uriContext.siteUID The site ID of the current page
     * @param {String} uriContext.catalogId The catalog ID of the current page
     * @param {String} uriContext.catalogVersion The catalog version of the current page
     * @param {<Object} targetCatalogVersion The selected catalogVersion containing the catalogId, version, and siteId
     * @param {String} targetCatalogVersion.siteId The selected site ID
     * @param {String} targetCatalogVersion.catalogId The selected catalog ID
     * @param {String} targetCatalogVersion.version The selected catalog version
     */
    .component('componentCloneInfoForm', {
        controller: 'componentCloneInfoFormController',
        templateUrl: 'componentCloneInfoTemplate.html',
        bindings: {
            structure: '<',
            content: '<',
            submit: '=',
            reset: '=',
            isDirty: '=',
            isValid: '=',
            pageTemplate: '<',
            pageTypeCode: '<',
            uriContext: '<',
            targetCatalogVersion: '<'
        }
    });
