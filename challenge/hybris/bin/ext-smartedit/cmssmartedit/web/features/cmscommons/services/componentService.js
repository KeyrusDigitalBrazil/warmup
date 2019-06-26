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
angular.module('componentServiceModule', [
        'smarteditServicesModule',
        'cmsSmarteditServicesModule',
        'resourceLocationsModule',
        'cmsitemsRestServiceModule',
        'slotRestrictionsServiceModule'
    ])

    /**
     * @ngdoc service
     * @name componentMenuModule.ComponentService
     *
     * @description
     * Service which manages component types and items
     */
    .service('ComponentService', function(
        $q,
        restServiceFactory,
        TYPES_RESOURCE_URI,
        PAGES_CONTENT_SLOT_COMPONENT_RESOURCE_URI,
        CONTEXT_CATALOG,
        CONTEXT_CATALOG_VERSION,
        cmsitemsRestService,
        catalogService,
        slotRestrictionsService,
        pageInfoService,
        pageContentSlotsComponentsRestService,
        typePermissionsRestService) {

        var restServiceForTypes = restServiceFactory.get(TYPES_RESOURCE_URI);
        var restServiceForItems = cmsitemsRestService;
        var restServiceForAddExistingComponent = restServiceFactory.get(PAGES_CONTENT_SLOT_COMPONENT_RESOURCE_URI);

        /**
         * @ngdoc method
         * @name componentMenuModule.ComponentService#createNewComponent
         * @methodOf componentMenuModule.ComponentService
         *
         * @description given a component info and the component payload, a new componentItem is created and added to a slot
         *
         * @param {Object} componentInfo The basic information of the ComponentType to be created and added to the slot.
         * @param {String} componentInfo.componenCode componenCode of the ComponentType to be created and added to the slot.
         * @param {String} componentInfo.name name of the new component to be created.
         * @param {String} componentInfo.pageId pageId used to identify the current page template.
         * @param {String} componentInfo.slotId slotId used to identify the slot in the current template.
         * @param {String} componentInfo.position position used to identify the position in the slot in the current template.
         * @param {String} componentInfo.type type of the component being created.
         * @param {Object} componentPayload payload of the new component to be created.
         */
        this.createNewComponent = function(componentInfo, componentPayload) {

            //FIXME fix naming of slotId
            var _payload = {};
            _payload.name = componentInfo.name;
            _payload.slotId = componentInfo.targetSlotId;
            _payload.pageId = componentInfo.pageId;
            _payload.position = componentInfo.position;
            _payload.typeCode = componentInfo.componentType;
            _payload.itemtype = componentInfo.componentType;
            _payload.catalogVersion = componentInfo.catalogVersionUuid;

            if (typeof componentPayload === "object") {
                for (var property in componentPayload) {
                    if (componentPayload.hasOwnProperty(property)) {
                        _payload[property] = componentPayload[property];
                    }
                }
            } else if (componentPayload) {
                throw "ComponentService.createNewComponent() - Illegal componentPayload - [" + componentPayload + "]";
            }

            return restServiceForItems.create(_payload);

        };

        /**
         * @ngdoc method
         * @name componentMenuModule.ComponentService#updateComponent
         * @methodOf componentMenuModule.ComponentService
         *
         * @description Given a component info and the payload related to an existing component, the latter will be updated with the new supplied values.
         *
         * @param {Object} componentPayload of the new component to be created, including the info.
         * @param {String} componentPayload.componenCode of the ComponentType to be created and added to the slot.
         * @param {String} componentPayload.name of the new component to be created.
         * @param {String} componentPayload.pageId used to identify the current page template.
         * @param {String} componentPayload.slotId used to identify the slot in the current template.
         * @param {String} componentPayload.position used to identify the position in the slot in the current template.
         * @param {String} componentPayload.type of the component being created.
         */
        this.updateComponent = function(componentPayload) {
            return restServiceForItems.update(componentPayload);
        };

        /**
         * @ngdoc method
         * @name componentMenuModule.ComponentService#addExistingComponent
         * @methodOf componentMenuModule.ComponentService
         *
         * @description add an existing component item to a slot
         *
         * @param {String} pageId used to identify the page containing the slot in the current template.
         * @param {String} componentId used to identify the existing component which will be added to the slot.
         * @param {String} slotId used to identify the slot in the current template.
         * @param {String} position used to identify the position in the slot in the current template.
         */
        this.addExistingComponent = function(pageId, componentId, slotId, position) {

            var _payload = {};
            _payload.pageId = pageId;
            _payload.slotId = slotId;
            _payload.componentId = componentId;
            _payload.position = position;

            return restServiceForAddExistingComponent.save(_payload);
        };

        /**
         * @ngdoc method
         * @name componentMenuModule.ComponentService#loadComponentTypes
         * @methodOf componentMenuModule.ComponentService
         *
         * @description all component types are retrieved
         */
        this.loadComponentTypes = function() {
            return restServiceForTypes.get({
                category: 'COMPONENT'
            });
        };

        /**
         * @ngdoc method
         * @name componentMenuModule.ComponentService#getSupportedComponentTypesForCurrentPage
         * @methodOf componentMenuModule.ComponentService
         *
         * @description Fetches all component types supported by the system, then filters this list
         * using the restricted component types for all the slots on the current page.
         *
         * @returns {Array} A promise resolving to the component types that can be added to the current page
         */
        this.getSupportedComponentTypesForCurrentPage = function() {

            return $q.all([
                this.loadComponentTypes(),
                slotRestrictionsService.getAllComponentTypesSupportedOnPage()
            ]).then(function(values) {
                var supportedComponentTypes = values[1];
                var componentTypes = values[0].componentTypes.filter(function(componentType) {
                    return supportedComponentTypes.indexOf(componentType.code) > -1;
                });

                var componentTypeCodes = componentTypes.map(function(componentType) {
                    return componentType.code;
                });

                return typePermissionsRestService.hasCreatePermissionForTypes(componentTypeCodes).then(function(permissionResult) {
                    return componentTypes.filter(function(componentType) {
                        return permissionResult[componentType.code];
                    });
                });
            }.bind(this));
        }.bind(this);

        /**
         * @ngdoc method
         * @name componentMenuModule.ComponentService#loadComponentItem
         * @methodOf componentMenuModule.ComponentService
         *
         * @description load a component identified by its id
         */
        this.loadComponentItem = function(id) {
            return restServiceForItems.getById(id);
        };

        /**
         * @ngdoc method
         * @name componentMenuModule.ComponentService#loadPagedComponentItems
         * @methodOf componentMenuModule.ComponentService
         *
         * @description all existing component items for the current catalog are retrieved in the form of pages
         * used for pagination especially when the result set is very large.
         * 
         * @param {String} mask the search string to filter the results.
         * @param {String} pageSize the number of elements that a page can contain.
         * @param {String} page the current page number.
         */
        this.loadPagedComponentItems = function(mask, pageSize, page) {

            return catalogService.retrieveUriContext().then(function(uriContext) {
                var requestParams = {
                    pageSize: pageSize,
                    currentPage: page,
                    mask: mask,
                    sort: 'name',
                    typeCode: 'AbstractCMSComponent',
                    catalogId: uriContext[CONTEXT_CATALOG],
                    catalogVersion: uriContext[CONTEXT_CATALOG_VERSION]
                };

                return restServiceForItems.get(requestParams);
            });
        };

        /**
         * @ngdoc method
         * @name componentMenuModule.ComponentService#loadPagedComponentItemsByCatalogVersion
         * @methodOf componentMenuModule.ComponentService
         *
         * @description all existing component items for the provided content catalog are retrieved in the form of pages
         * used for pagination especially when the result set is very large.
         * 
         * @param {Object} payload The payload that contains the information of the page of components to load
         * @param {String} payload.catalogId the id of the catalog for which to retrieve the component items. 
         * @param {String} payload.catalogVersion the id of the catalog version for which to retrieve the component items. 
         * @param {String} payload.mask the search string to filter the results.
         * @param {String} payload.pageSize the number of elements that a page can contain.
         * @param {String} payload.page the current page number.
         * 
         * @returns {Promise} A promise resolving to a page of component items retrieved from the provided catalog version. 
         */
        this.loadPagedComponentItemsByCatalogVersion = function(payload) {
            var requestParams = {
                pageSize: payload.pageSize,
                currentPage: payload.page,
                mask: payload.mask,
                sort: 'name',
                typeCode: 'AbstractCMSComponent',
                catalogId: payload.catalogId,
                catalogVersion: payload.catalogVersion
            };

            return restServiceForItems.get(requestParams);
        };

        this.getSlotsForComponent = function(componentUuid) {
            var slotIds = [];
            return this._getContentSlotsForComponents().then(function(allSlotsToComponents) {
                Object.keys(allSlotsToComponents).forEach(function(slotId) {
                    if (allSlotsToComponents[slotId].find(function(component) {
                            return component.uuid === componentUuid;
                        })) {
                        slotIds.push(slotId);
                    }
                });
                return slotIds;
            }.bind(this));
        };

        this._getContentSlotsForComponents = function() {
            return pageInfoService.getPageUID().then(function(pageId) {
                return pageContentSlotsComponentsRestService.getSlotsToComponentsMapForPageUid(pageId);
            });
        };
    });
