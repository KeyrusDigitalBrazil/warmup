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
 * @name typeStructureRestServiceModule
 * @description
 * # The typeStructureRestServiceModule
 *
 * The typeStructureRestServiceModule provides REST services for the CMS page structure
 *
 */
angular.module('typeStructureRestServiceModule', ['resourceLocationsModule'])

    /**
     * @ngdoc service
     * @name typeStructureRestServiceModule.service:typeStructureRestService
     *
     * @description
     * The typeStructureRestService provides functionality for fetching page structures
     */
    .service('typeStructureRestService', function(TYPES_RESOURCE_URI, restServiceFactory) {

        var structureRestService = restServiceFactory.get(TYPES_RESOURCE_URI);

        /**
         * @ngdoc method
         * @name typeStructureRestServiceModule.service:typeStructureRestService#getStructureByType
         * @methodOf typeStructureRestServiceModule.service:typeStructureRestService
         *
         * @description
         * Fetches the type structure (fields) for CMS pages for a give type
         *
         * @param {String} typeCode The type code of type structure to be fetched
         * @param {Object} [options={}] an optional object to control which part of the structure is being returned
         * @returns {Array} An array of fields, representing the type structure for the generic editor
         */
        this.getStructureByType = function(typeCode, options) {
            var opts = (options) ? options : {};

            return structureRestService.getById(typeCode).then(function(structure) {
                return (opts.getWholeStructure) ? structure : structure.attributes;
            });
        };

        /**
         * @ngdoc method
         * @name typeStructureRestServiceModule.service:typeStructureRestService#getStructureByType
         * @methodOf typeStructureRestServiceModule.service:typeStructureRestService
         *
         * @description
         * Fetches the type structure (fields) for CMS pages for a give type
         *
         * @param {String} typeCode The type code of type structure to be fetched
         * @param {String} mode The mode to fetch the structure
         * @param {Object} [options={}] an optional object to control which part of the structure is being returned
         * @returns {Array} An array of fields, representing the type structure for the generic editor
         */
        this.getStructureByTypeAndMode = function(typeCode, mode, options) {

            var structureByModeRestService = restServiceFactory.get(TYPES_RESOURCE_URI);

            var opts = (options) ? options : {};

            return structureByModeRestService.get({
                code: typeCode,
                mode: mode
            }).then(function(result) {
                var structure = result.componentTypes[0];
                return (!structure || opts.getWholeStructure) ? structure : structure.attributes;
            });
        };

        /**
         * @ngdoc method
         * @name typeStructureRestServiceModule.service:typeStructureRestService#getStructuresByCategory
         * @methodOf typeStructureRestServiceModule.service:typeStructureRestService
         *
         * @param {String} category The componentType category of structures you wish to retrieve.
         * Ex: 'RESTRICTION', or 'COMPONENT'
         *
         * @returns {Array} An array of supported structures supported in this category.
         */
        this.getStructuresByCategory = function(category) {
            return structureRestService.get({
                category: category
            }).then(function(result) {
                return result.componentTypes;
            });
        };

    });
