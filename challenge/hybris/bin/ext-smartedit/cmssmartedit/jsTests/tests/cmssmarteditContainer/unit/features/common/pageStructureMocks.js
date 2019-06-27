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
module.exports = function() {

    var PageStructureMocks = function() {

        function restPayload() {
            return {
                "attributes": [{
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.abstractpage.uid.name",
                    "localized": false,
                    "qualifier": "uid"
                }, {
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.abstractpage.name.name",
                    "localized": false,
                    "qualifier": "name"
                }, {
                    "cmsStructureType": "DateTime",
                    "i18nKey": "type.abstractpage.creationtime.name",
                    "localized": false,
                    "qualifier": "creationtime"
                }, {
                    "cmsStructureType": "DateTime",
                    "i18nKey": "type.abstractpage.modifiedtime.name",
                    "localized": false,
                    "qualifier": "modifiedtime"
                }, {
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.abstractpage.title.name",
                    "localized": true,
                    "qualifier": "title"
                }, {
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.contentpage.label.name",
                    "localized": false,
                    "qualifier": "label"
                }],
                "category": "PAGE",
                "code": "MockPage",
                "i18nKey": "type.mockpage.name",
                "name": "Mock Page"
            };
        }

        function restPayloadWithReadOnlyField() {
            return {
                "attributes": [{
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.abstractpage.uid.name",
                    "localized": false,
                    "qualifier": "uid"
                }, {
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.abstractpage.name.name",
                    "localized": false,
                    "qualifier": "name"
                }, {
                    "cmsStructureType": "DateTime",
                    "i18nKey": "type.abstractpage.creationtime.name",
                    "localized": false,
                    "qualifier": "creationtime"
                }, {
                    "cmsStructureType": "DateTime",
                    "i18nKey": "type.abstractpage.modifiedtime.name",
                    "localized": false,
                    "qualifier": "modifiedtime"
                }, {
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.abstractpage.title.name",
                    "localized": true,
                    "qualifier": "title"
                }, {
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.contentpage.label.name",
                    "localized": false,
                    "qualifier": "label",
                    "editable": false
                }],
                "category": "PAGE",
                "code": "MockPage",
                "i18nKey": "type.mockpage.name",
                "name": "Mock Page"
            };
        }


        return {
            getFields: function() {
                return restPayload().attributes;
            },
            getFieldsWithReadOnly: function() {
                return restPayloadWithReadOnlyField().attributes;
            }
        };

    }();

    return PageStructureMocks;

}();
