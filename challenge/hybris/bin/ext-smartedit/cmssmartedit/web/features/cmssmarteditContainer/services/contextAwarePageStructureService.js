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
 * @name contextAwarePageStructureServiceModule
 * @description
 * # The contextAwarePageStructureServiceModule
 *
 * The contextAwarePageStructureServiceModule provides page structure functionality that is context aware,
 * ie: that changes depending on front-end needs.
 *
 */
angular.module('contextAwarePageStructureServiceModule', ['typeStructureRestServiceModule', 'pageServiceModule'])

    /**
     * @ngdoc object
     * @name contextAwarePageStructureServiceModule.object:PAGE_STRUCTURE_PRE_ORDER
     *
     * @description
     * Injectable angular array of strings, that are page structure field qualifiers.<br/>
     * After {@link contextAwarePageStructureServiceModule.service:contextAwarePageStructureService contextAwarePageStructureService}
     * fetches the page structure, it will rearrange the fields based on this pre-order. Any fields matching the qualifiers specified
     * in this array will be positioned at the beginning of the returned structure, and matching the order specified.
     *
     */
    .value('PAGE_STRUCTURE_PRE_ORDER', ['typeCode', 'template', 'name', 'label', 'uid', 'title'])

    /**
     * @ngdoc object
     * @name contextAwarePageStructureServiceModule.object:PAGE_STRUCTURE_POST_ORDER
     *
     * @description
     * Injectable angular array of strings, that are page structure field qualifiers.<br/>
     * After {@link contextAwarePageStructureServiceModule.service:contextAwarePageStructureService contextAwarePageStructureService}
     * fetches the page structure, it will rearrange the fields based on this post-order. Any fields matching the qualifiers specified
     * in this array will be positioned at the end of the returned structure, and matching the order specified.
     *
     */
    .value('PAGE_STRUCTURE_POST_ORDER', ['creationtime', 'modifiedtime'])


    /**
     * @ngdoc service
     * @name contextAwarePageStructureServiceModule.service:contextAwarePageStructureService
     *
     * @description
     * The contextAwarePageStructureServiceModule is a layer on top of the
     * {@link typeStructureRestServiceModule.service:typeStructureRestService typeStructureRestService}
     * that allows for modifying the structure for front-end needs, such as disabling fields or changing field order.
     *
     * Note: The field order is defined by {@link contextAwarePageStructureServiceModule.object:PAGE_STRUCTURE_POST_ORDER PAGE_STRUCTURE_POST_ORDER}
     * and {@link contextAwarePageStructureServiceModule.object:PAGE_STRUCTURE_POST_ORDER PAGE_STRUCTURE_POST_ORDER}, and can
     * be replaced/overriden.
     *
     */
    .service('contextAwarePageStructureService', function($q, typeStructureRestService, pageService, PAGE_STRUCTURE_PRE_ORDER, PAGE_STRUCTURE_POST_ORDER) {

        function moveElement(array, oldPosition, newPosition) {
            if (newPosition >= array.length) {
                newPosition = Math.max(0, array.length - 1);
            }
            array.splice(newPosition, 0, array.splice(oldPosition, 1)[0]);
            return array;
        }

        function getOrderedFields(unorderedFields) {

            var i;
            var index;

            function isPreMatching(field) {
                return field.qualifier === PAGE_STRUCTURE_PRE_ORDER[i];
            }

            function isPostMatching(field) {
                return field.qualifier === PAGE_STRUCTURE_POST_ORDER[i];
            }

            if (PAGE_STRUCTURE_PRE_ORDER) {
                for (i = PAGE_STRUCTURE_PRE_ORDER.length - 1; i >= 0; i--) {
                    index = unorderedFields.findIndex(isPreMatching);
                    moveElement(unorderedFields, index, 0);
                }
            }
            if (PAGE_STRUCTURE_POST_ORDER) {
                for (i = 0; i < PAGE_STRUCTURE_POST_ORDER.length; i++) {
                    index = unorderedFields.findIndex(isPostMatching);
                    moveElement(unorderedFields, index, unorderedFields.length);
                }
            }
            return unorderedFields;
        }

        function setLabelEditability(fields, isPrimary) {
            var labelFieldIndex = fields.findIndex(function(field) {
                return field.qualifier === 'label';
            });

            // Leave the attribute uneditable if user does not have "change" attribute permission
            if (labelFieldIndex !== -1 && fields[labelFieldIndex].editable !== false) {
                fields[labelFieldIndex].editable = isPrimary;
            }
        }

        function getFields(pageTypeCode) {
            return typeStructureRestService.getStructureByType(pageTypeCode).then(function(structure) {
                structure.push({
                    cmsStructureType: "DisplayConditionEditor",
                    i18nKey: "type.abstractpage.displayCondition.name",
                    qualifier: "displayCondition"
                }, {
                    cmsStructureType: "ShortString",
                    i18nKey: "se.cms.pageinfo.page.type",
                    qualifier: "typeCode",
                    editable: false
                });
                return structure;
            });
        }

        function removeField(fields, fieldQualifier) {
            var index = fields.findIndex(
                function(field) {
                    return field.qualifier === fieldQualifier;
                });
            if (index !== -1) {
                fields.splice(index, 1);
            }
        }

        /**
         * @ngdoc method
         * @name contextAwarePageStructureServiceModule.service:contextAwarePageStructureService#getPageStructureForNewPage
         * @methodOf contextAwarePageStructureServiceModule.service:contextAwarePageStructureService
         *
         * @description
         * Return the CMS page structure with some modifications for the context of creating a new page.
         * The field order is modified, the created/modified time fields are removed, and the label field for variation content pages is disabled.
         *
         * @param {String} pageTypeCode The page type of the new page to be created
         * @param {Boolean} isPrimary Flag indicating if the new page will be a primary or variation page
         *
         * @returns {Array} A modified page structure
         */
        this.getPageStructureForNewPage = function getPageStructureForNewPage(pageTypeCode, isPrimary) {
            return getFields(pageTypeCode).then(function(fields) {
                if (pageTypeCode === 'ContentPage') {
                    setLabelEditability(fields, isPrimary);
                }
                removeField(fields, 'creationtime');
                removeField(fields, 'modifiedtime');
                removeField(fields, 'displayCondition');
                removeField(fields, 'restrictions');
                return {
                    attributes: getOrderedFields(fields),
                    category: 'PAGE'
                };
            });
        };

        /**
         * @ngdoc method
         * @name contextAwarePageStructureServiceModule.service:contextAwarePageStructureService#getPageStructureForPageEditing
         * @methodOf contextAwarePageStructureServiceModule.service:contextAwarePageStructureService
         *
         * @description
         * Return the CMS page structure with some modifications for the context of editing the info of an existing page.
         * The field order is modified, and the label field for variation content pages is disabled.
         *
         * @param {String} pageTypeCode The page type of the page to be edited
         * @param {String} pageId The ID of the existing page to be modified
         *
         * @returns {Array} A modified page structure
         */
        this.getPageStructureForPageEditing = function getPageStructureForPageEditing(pageTypeCode, pageId) {
            return getFields(pageTypeCode).then(function(fields) {
                var readOnlyFieldNames = ['uid', 'creationtime', 'modifiedtime'];
                fields.filter(function(field) {
                    return readOnlyFieldNames.indexOf(field.qualifier) >= 0;
                }).forEach(function(field) {
                    field.editable = false;
                });

                if (pageTypeCode === 'ContentPage') {
                    return pageService.isPagePrimary(pageId).then(function(isPrimary) {
                        setLabelEditability(fields, isPrimary);
                        return {
                            attributes: getOrderedFields(fields),
                            category: 'PAGE'
                        };
                    });
                } else {
                    return {
                        attributes: getOrderedFields(fields),
                        category: 'PAGE'
                    };
                }
            });
        };

        /**
         * @ngdoc method
         * @name contextAwarePageStructureServiceModule.service:contextAwarePageStructureService#getPageStructureForViewing
         * @methodOf contextAwarePageStructureServiceModule.service:contextAwarePageStructureService
         *
         * @description
         * Return the CMS page structure with some modifications for the context of viewing the info of an existing page.
         * The field order is modified, and the label field for variation content pages is disabled.
         *
         * @param {String} pageTypeCode The page type of the existing page
         *
         * @returns {Array} A modified page structure
         */
        this.getPageStructureForViewing = function getPageStructureForViewing(pageTypeCode) {
            return getFields(pageTypeCode).then(function(fields) {
                fields.forEach(function(field) {
                    field.editable = false;
                });
                removeField(fields, 'typeCode');
                removeField(fields, 'template');
                removeField(fields, 'displayCondition');
                removeField(fields, 'restrictions');
                return {
                    attributes: getOrderedFields(fields),
                    category: 'PAGE'
                };
            });
        };

    });
