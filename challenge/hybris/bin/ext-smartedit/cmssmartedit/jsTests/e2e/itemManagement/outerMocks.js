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
/* jshint unused:false, undef:false */
angular.module('RestrictionsMocks', ['ngMockE2E'])
    .run(function($httpBackend, languageService, I18N_RESOURCE_URI) {

        $httpBackend.whenGET(/cmssmarteditwebservices\/v1\/types\?code=CMSTimeRestriction\&mode=add/).respond({
            "attributes": [{
                "cmsStructureType": "ShortString",
                "i18nKey": "type.abstractrestriction.name.name",
                "localized": false,
                "qualifier": "name",
                "required": true,
                "editable": false
            }, {
                "cmsStructureType": "DateTime",
                "i18nKey": "type.cmstimerestriction.activefrom.name",
                "localized": false,
                "qualifier": "activeFrom",
                "required": true,
                "editable": false
            }, {
                "cmsStructureType": "DateTime",
                "i18nKey": "type.cmstimerestriction.activeuntil.name",
                "localized": false,
                "qualifier": "activeUntil",
                "required": true,
                "editable": false
            }],
            "category": "RESTRICTION",
            "code": "CMSTimeRestriction",
            "i18nKey": "type.cmstimerestriction.name",
            "name": "Time Restriction"
        });

        $httpBackend.whenGET(/cmssmarteditwebservices\/v1\/types\?code=CMSTimeRestriction\&mode=edit/).respond({
            "attributes": [{
                "cmsStructureType": "ShortString",
                "i18nKey": "type.abstractrestriction.name.name",
                "localized": false,
                "qualifier": "name",
                "required": true
            }, {
                "cmsStructureType": "DateTime",
                "i18nKey": "type.cmstimerestriction.activefrom.name",
                "localized": false,
                "qualifier": "activeFrom",
                "required": true
            }, {
                "cmsStructureType": "DateTime",
                "i18nKey": "type.cmstimerestriction.activeuntil.name",
                "localized": false,
                "qualifier": "activeUntil",
                "required": true
            }],
            "category": "RESTRICTION",
            "code": "CMSTimeRestriction",
            "i18nKey": "type.cmstimerestriction.name",
            "name": "Time Restriction"
        });

        $httpBackend.whenGET(/cmssmarteditwebservices\/v1\/types\?code=CMSTimeRestriction\&mode=create/).respond({
            "attributes": [{
                "cmsStructureType": "ShortString",
                "i18nKey": "type.abstractrestriction.name.name",
                "localized": false,
                "qualifier": "name",
                "required": true
            }, {
                "cmsStructureType": "DateTime",
                "i18nKey": "type.cmstimerestriction.activefrom.name",
                "localized": false,
                "qualifier": "activeFrom",
                "required": true
            }, {
                "cmsStructureType": "DateTime",
                "i18nKey": "type.cmstimerestriction.activeuntil.name",
                "localized": false,
                "qualifier": "activeUntil",
                "required": true
            }],
            "category": "RESTRICTION",
            "code": "CMSTimeRestriction",
            "i18nKey": "type.cmstimerestriction.name",
            "name": "Time Restriction"
        });

        $httpBackend.whenPUT(/cmswebservices\/v1\/sites\/mySite\/catalogs\/myCatalog\/versions\/myCatalogVersion\/restrictions\/editId/).respond({});

        $httpBackend.whenPOST(/cmswebservices\/v1\/sites\/mySite\/catalogs\/myCatalog\/versions\/myCatalogVersion\/restrictions/).respond({
            uid: "some new restriction ID",
            name: "some new restriction",
            typeCode: "CMSTimeRestriction",
            description: "some time restriction description"
        });

        $httpBackend.whenGET(/cmswebservices\/v1\/sites\/languages/).respond({

        });

        $httpBackend.whenGET(I18N_RESOURCE_URI + "/" + languageService.getBrowserLocale()).respond({
            'page.restrictions.list.empty': 'This page has no restrictions',
            'se.cms.page.restrictions.list.title': 'List of restrictions',
            'se.cms.page.restrictions.editor.button.add.new': 'ADD NEW',
            'se.cms.page.restrictions.editor.tab': 'Restrictions',
            'se.cms.page.restrictions.picker.type.label': 'Restriction Type',
            'se.cms.page.restrictions.picker.type.placeholder': 'Select restriction type',
            'se.cms.page.restrictions.picker.search.label': 'Restriction Name',
            'se.cms.page.restrictions.picker.search.placeholder': 'Search',
            'se.cms.page.restrictions.criteria': 'Criteria:',
            'se.cms.page.restrictions.criteria.all': 'Match all',
            'se.cms.page.restrictions.criteria.any': 'Match any',
            'se.cms.page.restrictions.criteria.select.all': 'Apply all restrictions',
            'se.cms.page.restrictions.criteria.select.any': 'Apply any restriction',
            'se.cms.page.restrictions.toolbar.menu': 'Restrictions',
            'se.cms.toolbaritem.navigationmenu.name': 'Navigation',
            'se.cms.page.restrictions.item.remove': 'Remove',
            'se.icon.tooltip.visibility': '{{numberOfRestrictions}} restrictions on this page'
        });

    });

angular.module('pageRestrictionsEditorModule').requires.push('RestrictionsMocks');
