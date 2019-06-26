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
angular.module('restrictionsMocks', ['ngMockE2E', 'yLoDashModule', 'backendMocksUtilsModule'])
    .run(
        function($httpBackend, $filter, lodash, backendMocksUtils) {

            $httpBackend.whenGET(/\/restrictiontypes/).respond({
                restrictionTypes: [{
                    code: 'CMSTimeRestriction',
                    name: {
                        de: 'DAS blabla',
                        en: 'Time Restriction'
                    }
                }, {
                    code: 'CMSCatalogRestriction',
                    name: {
                        en: 'Catalog Restriction'
                    }
                }, {
                    code: 'CMSCategoryRestriction',
                    name: {
                        en: 'category Restriction'
                    }
                }, {
                    code: 'CMSUserRestriction',
                    name: {
                        en: 'User Restriction'
                    }
                }, {
                    code: 'CMSUserGroupRestriction',
                    name: {
                        en: 'User Group Restriction'
                    }
                }]
            });

            $httpBackend.whenGET(/pagetypesrestrictiontypes/).respond({
                "pageTypeRestrictionTypeList": [{
                    "pageType": "CatalogPage",
                    "restrictionType": "CMSCatalogRestriction"
                }, {
                    "pageType": "CatalogPage",
                    "restrictionType": "CMSTimeRestriction"
                }, {
                    "pageType": "CatalogPage",
                    "restrictionType": "CMSUserRestriction"
                }, {
                    "pageType": "CatalogPage",
                    "restrictionType": "CMSUserGroupRestriction"
                }, {
                    "pageType": "CatalogPage",
                    "restrictionType": "CMSUiExperienceRestriction"
                }, {
                    "pageType": "CategoryPage",
                    "restrictionType": "CMSCategoryRestriction"
                }, {
                    "pageType": "CategoryPage",
                    "restrictionType": "CMSTimeRestriction"
                }, {
                    "pageType": "CategoryPage",
                    "restrictionType": "CMSUserRestriction"
                }, {
                    "pageType": "CategoryPage",
                    "restrictionType": "CMSUserGroupRestriction"
                }, {
                    "pageType": "CategoryPage",
                    "restrictionType": "CMSUiExperienceRestriction"
                }, {
                    "pageType": "ContentPage",
                    "restrictionType": "CMSTimeRestriction"
                }, {
                    "pageType": "ContentPage",
                    "restrictionType": "CMSUserRestriction"
                }, {
                    "pageType": "ContentPage",
                    "restrictionType": "CMSUserGroupRestriction"
                }, {
                    "pageType": "ContentPage",
                    "restrictionType": "CMSUiExperienceRestriction"
                }, {
                    "pageType": "ProductPage",
                    "restrictionType": "CMSCategoryRestriction"
                }, {
                    "pageType": "ProductPage",
                    "restrictionType": "CMSProductRestriction"
                }, {
                    "pageType": "ProductPage",
                    "restrictionType": "CMSTimeRestriction"
                }, {
                    "pageType": "ProductPage",
                    "restrictionType": "CMSUserRestriction"
                }, {
                    "pageType": "ProductPage",
                    "restrictionType": "CMSUserGroupRestriction"
                }, {
                    "pageType": "ProductPage",
                    "restrictionType": "CMSUiExperienceRestriction"
                }]
            });

            var pageRestrictionMocks = $httpBackend.whenGET(/pagesrestrictions/);
            pageRestrictionMocks.respond({
                "pageRestrictionList": [{
                    "pageId": "uid3",
                    "restrictionId": "timeRestrictionIdA"
                }, {
                    "pageId": "uid3",
                    "restrictionId": "timeRestrictionIdB"
                }]
            });
            backendMocksUtils.storeBackendMock('pageRestrictionMocks', pageRestrictionMocks);


            $httpBackend.whenGET(/\/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\/homepage\/fallbacks/).respond({
                uids: ['secondpage']
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/types\?category=RESTRICTION/).respond({
                "componentTypes": [{
                    "attributes": [{
                        "cmsStructureType": "ShortString",
                        "collection": false,
                        "editable": true,
                        "i18nKey": "type.abstractrestriction.uid.name",
                        "localized": false,
                        "paged": false,
                        "qualifier": "uid",
                        "required": false
                    }, {
                        "cmsStructureType": "ShortString",
                        "collection": false,
                        "editable": true,
                        "i18nKey": "type.abstractrestriction.name.name",
                        "localized": false,
                        "paged": false,
                        "qualifier": "name",
                        "required": true
                    }, {
                        "cmsStructureType": "ShortString",
                        "collection": false,
                        "editable": true,
                        "i18nKey": "type.abstractrestriction.description.name",
                        "localized": false,
                        "paged": false,
                        "qualifier": "description",
                        "required": false
                    }],
                    "category": "RESTRICTION",
                    "code": "AbstractRestriction",
                    "i18nKey": "type.abstractrestriction.name",
                    "name": "Restriction",
                    "type": "abstractRestrictionData"
                }, {
                    "attributes": [{
                        "cmsStructureType": "EditableDropdown",
                        "collection": false,
                        "editable": true,
                        "i18nKey": "type.cmsusergrouprestriction.usergroups.name",
                        "localized": false,
                        "paged": false,
                        "qualifier": "userGroups",
                        "required": true
                    }, {
                        "cmsStructureType": "ShortString",
                        "collection": false,
                        "editable": true,
                        "i18nKey": "type.cmsusergrouprestriction.uid.name",
                        "localized": false,
                        "paged": false,
                        "qualifier": "uid",
                        "required": false
                    }, {
                        "cmsStructureType": "ShortString",
                        "collection": false,
                        "editable": true,
                        "i18nKey": "type.cmsusergrouprestriction.name.name",
                        "localized": false,
                        "paged": false,
                        "qualifier": "name",
                        "required": true
                    }, {
                        "cmsStructureType": "ShortString",
                        "collection": false,
                        "editable": true,
                        "i18nKey": "type.cmsusergrouprestriction.description.name",
                        "localized": false,
                        "paged": false,
                        "qualifier": "description",
                        "required": false
                    }],
                    "category": "RESTRICTION",
                    "code": "CMSUserGroupRestriction",
                    "i18nKey": "type.cmsusergrouprestriction.name",
                    "name": "Usergroup Restriction",
                    "type": "userGroupRestrictionData"
                }, {
                    "attributes": [{
                        "cmsStructureType": "DateTime",
                        "collection": false,
                        "editable": true,
                        "i18nKey": "type.cmstimerestriction.activefrom.name",
                        "localized": false,
                        "paged": false,
                        "qualifier": "activeFrom",
                        "required": true
                    }, {
                        "cmsStructureType": "DateTime",
                        "collection": false,
                        "editable": true,
                        "i18nKey": "type.cmstimerestriction.activeuntil.name",
                        "localized": false,
                        "paged": false,
                        "qualifier": "activeUntil",
                        "required": true
                    }, {
                        "cmsStructureType": "ShortString",
                        "collection": false,
                        "editable": true,
                        "i18nKey": "type.cmstimerestriction.uid.name",
                        "localized": false,
                        "paged": false,
                        "qualifier": "uid",
                        "required": false
                    }, {
                        "cmsStructureType": "ShortString",
                        "collection": false,
                        "editable": true,
                        "i18nKey": "type.cmstimerestriction.name.name",
                        "localized": false,
                        "paged": false,
                        "qualifier": "name",
                        "required": true
                    }, {
                        "cmsStructureType": "ShortString",
                        "collection": false,
                        "editable": true,
                        "i18nKey": "type.cmstimerestriction.description.name",
                        "localized": false,
                        "paged": false,
                        "qualifier": "description",
                        "required": false
                    }],
                    "category": "RESTRICTION",
                    "code": "CMSTimeRestriction",
                    "i18nKey": "type.cmstimerestriction.name",
                    "name": "Time Restriction",
                    "type": "timeRestrictionData"
                }, {
                    "attributes": [{
                        "cmsStructureType": "MultiCategorySelector",
                        "collection": false,
                        "editable": true,
                        "i18nKey": "type.cmscategoryrestriction.categories.name",
                        "localized": false,
                        "paged": false,
                        "qualifier": "categories",
                        "required": true
                    }, {
                        "cmsStructureType": "Boolean",
                        "collection": false,
                        "editable": true,
                        "i18nKey": "type.cmscategoryrestriction.recursive.name",
                        "localized": false,
                        "paged": false,
                        "qualifier": "recursive",
                        "required": false
                    }, {
                        "cmsStructureType": "ShortString",
                        "collection": false,
                        "editable": true,
                        "i18nKey": "type.cmscategoryrestriction.uid.name",
                        "localized": false,
                        "paged": false,
                        "qualifier": "uid",
                        "required": false
                    }, {
                        "cmsStructureType": "ShortString",
                        "collection": false,
                        "editable": true,
                        "i18nKey": "type.cmscategoryrestriction.name.name",
                        "localized": false,
                        "paged": false,
                        "qualifier": "name",
                        "required": true
                    }, {
                        "cmsStructureType": "ShortString",
                        "collection": false,
                        "editable": true,
                        "i18nKey": "type.cmscategoryrestriction.description.name",
                        "localized": false,
                        "paged": false,
                        "qualifier": "description",
                        "required": false
                    }],
                    "category": "RESTRICTION",
                    "code": "CMSCategoryRestriction",
                    "i18nKey": "type.cmscategoryrestriction.name",
                    "name": "Category Restriction",
                    "type": "categoryRestrictionData"
                }]
            });

            // Time Restrictions
            $httpBackend.whenGET(/cmswebservices\/v1\/types\?code=CMSTimeRestriction\&mode=ADD/).respond({
                "attributes": [{
                    "cmsStructureType": "ShortString",
                    "collection": false,
                    "editable": false,
                    "i18nKey": "type.cmstimerestriction.name.name",
                    "localized": false,
                    "paged": false,
                    "qualifier": "name",
                    "required": true
                }, {
                    "cmsStructureType": "DateTime",
                    "collection": false,
                    "editable": false,
                    "i18nKey": "type.cmstimerestriction.activefrom.name",
                    "localized": false,
                    "paged": false,
                    "qualifier": "activeFrom",
                    "required": true
                }, {
                    "cmsStructureType": "DateTime",
                    "collection": false,
                    "editable": false,
                    "i18nKey": "type.cmstimerestriction.activeuntil.name",
                    "localized": false,
                    "paged": false,
                    "qualifier": "activeUntil",
                    "required": true
                }],
                "category": "RESTRICTION",
                "code": "CMSTimeRestriction",
                "i18nKey": "type.cmstimerestriction.name",
                "name": "Time Restriction",
                "type": "timeRestrictionData"
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/types\?code=CMSTimeRestriction\&mode=CREATE/).respond({
                "attributes": [{
                    "cmsStructureType": "ShortString",
                    "collection": false,
                    "editable": true,
                    "i18nKey": "type.cmstimerestriction.name.name",
                    "localized": false,
                    "paged": false,
                    "qualifier": "name",
                    "required": true
                }, {
                    "cmsStructureType": "DateTime",
                    "collection": false,
                    "editable": true,
                    "i18nKey": "type.cmstimerestriction.activefrom.name",
                    "localized": false,
                    "paged": false,
                    "qualifier": "activeFrom",
                    "required": true
                }, {
                    "cmsStructureType": "DateTime",
                    "collection": false,
                    "editable": true,
                    "i18nKey": "type.cmstimerestriction.activeuntil.name",
                    "localized": false,
                    "paged": false,
                    "qualifier": "activeUntil",
                    "required": true
                }],
                "category": "RESTRICTION",
                "code": "CMSTimeRestriction",
                "i18nKey": "type.cmstimerestriction.name",
                "name": "Time Restriction",
                "type": "timeRestrictionData"
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/types\?code=CMSTimeRestriction\&mode=EDIT/).respond({
                "attributes": [{
                    "cmsStructureType": "ShortString",
                    "collection": false,
                    "editable": true,
                    "i18nKey": "type.cmstimerestriction.name.name",
                    "localized": false,
                    "paged": false,
                    "qualifier": "name",
                    "required": true
                }, {
                    "cmsStructureType": "DateTime",
                    "collection": false,
                    "editable": true,
                    "i18nKey": "type.cmstimerestriction.activefrom.name",
                    "localized": false,
                    "paged": false,
                    "qualifier": "activeFrom",
                    "required": true
                }, {
                    "cmsStructureType": "DateTime",
                    "collection": false,
                    "editable": true,
                    "i18nKey": "type.cmstimerestriction.activeuntil.name",
                    "localized": false,
                    "paged": false,
                    "qualifier": "activeUntil",
                    "required": true
                }],
                "category": "RESTRICTION",
                "code": "CMSTimeRestriction",
                "i18nKey": "type.cmstimerestriction.name",
                "name": "Time Restriction",
                "type": "timeRestrictionData"
            });

            // Category Restrictions
            $httpBackend.whenGET(/cmswebservices\/v1\/types\?code=CMSCategoryRestriction\&mode=ADD/).respond({
                "attributes": [{
                    "cmsStructureType": "ShortString",
                    "collection": false,
                    "editable": false,
                    "i18nKey": "type.cmscategoryrestriction.name.name",
                    "localized": false,
                    "paged": false,
                    "qualifier": "name",
                    "required": true
                }, {
                    "cmsStructureType": "Boolean",
                    "collection": false,
                    "editable": false,
                    "i18nKey": "type.cmscategoryrestriction.recursive.name",
                    "localized": false,
                    "paged": false,
                    "qualifier": "recursive",
                    "required": false
                }, {
                    "cmsStructureType": "MultiCategorySelector",
                    "collection": false,
                    "editable": false,
                    "i18nKey": "type.cmscategoryrestriction.categories.name",
                    "localized": false,
                    "paged": false,
                    "qualifier": "categories",
                    "required": true
                }],
                "category": "RESTRICTION",
                "code": "CMSCategoryRestriction",
                "i18nKey": "type.cmscategoryrestriction.name",
                "name": "Category Restriction",
                "type": "categoryRestrictionData"
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/types\?code=CMSCategoryRestriction\&mode=CREATE/).respond({
                "attributes": [{
                    "cmsStructureType": "ShortString",
                    "collection": false,
                    "editable": true,
                    "i18nKey": "type.cmscategoryrestriction.name.name",
                    "localized": false,
                    "paged": false,
                    "qualifier": "name",
                    "required": true
                }, {
                    "cmsStructureType": "Boolean",
                    "collection": false,
                    "editable": true,
                    "i18nKey": "type.cmscategoryrestriction.recursive.name",
                    "localized": false,
                    "paged": false,
                    "qualifier": "recursive",
                    "required": false
                }, {
                    "cmsStructureType": "MultiCategorySelector",
                    "collection": false,
                    "editable": true,
                    "i18nKey": "type.cmscategoryrestriction.categories.name",
                    "localized": false,
                    "paged": false,
                    "qualifier": "categories",
                    "required": true
                }],
                "category": "RESTRICTION",
                "code": "CMSCategoryRestriction",
                "i18nKey": "type.cmscategoryrestriction.name",
                "name": "Category Restriction",
                "type": "categoryRestrictionData"
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/types\?code=CMSCategoryRestriction\&mode=EDIT/).respond({
                "attributes": [{
                    "cmsStructureType": "ShortString",
                    "collection": false,
                    "editable": true,
                    "i18nKey": "type.cmscategoryrestriction.name.name",
                    "localized": false,
                    "paged": false,
                    "qualifier": "name",
                    "required": true
                }, {
                    "cmsStructureType": "Boolean",
                    "collection": false,
                    "editable": true,
                    "i18nKey": "type.cmscategoryrestriction.recursive.name",
                    "localized": false,
                    "paged": false,
                    "qualifier": "recursive",
                    "required": false
                }, {
                    "cmsStructureType": "MultiCategorySelector",
                    "collection": false,
                    "editable": true,
                    "i18nKey": "type.cmscategoryrestriction.categories.name",
                    "localized": false,
                    "paged": false,
                    "qualifier": "categories",
                    "required": true
                }],
                "category": "RESTRICTION",
                "code": "CMSCategoryRestriction",
                "i18nKey": "type.cmscategoryrestriction.name",
                "name": "Category Restriction",
                "type": "categoryRestrictionData"
            });

            // User Group Restrictions
            $httpBackend.whenGET(/cmswebservices\/v1\/types\?code=CMSUserGroupRestriction\&mode=ADD/).respond({
                "attributes": [{
                    "cmsStructureType": "ShortString",
                    "collection": false,
                    "editable": false,
                    "i18nKey": "type.cmsusergrouprestriction.name.name",
                    "localized": false,
                    "paged": false,
                    "qualifier": "name",
                    "required": true
                }, {
                    "cmsStructureType": "Boolean",
                    "collection": false,
                    "editable": false,
                    "i18nKey": "type.cmsusergrouprestriction.includesubgroups.name",
                    "localized": false,
                    "paged": false,
                    "qualifier": "includeSubgroups",
                    "required": false
                }, {
                    "cmsStructureType": "EditableDropdown",
                    "collection": true,
                    "editable": false,
                    "i18nKey": "type.cmsusergrouprestriction.usergroups.name",
                    "idAttribute": "uid",
                    "labelAttributes": ["name", "uid"],
                    "localized": false,
                    "paged": true,
                    "qualifier": "userGroups",
                    "required": true,
                    "uri": "/cmswebservices/v1/usergroups"
                }],
                "category": "RESTRICTION",
                "code": "CMSUserGroupRestriction",
                "i18nKey": "type.cmsusergrouprestriction.name",
                "name": "Usergroup Restriction",
                "type": "userGroupRestrictionData"
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/types\?code=CMSUserGroupRestriction\&mode=CREATE/).respond({
                "attributes": [{
                    "cmsStructureType": "ShortString",
                    "collection": false,
                    "editable": true,
                    "i18nKey": "type.cmsusergrouprestriction.name.name",
                    "localized": false,
                    "paged": false,
                    "qualifier": "name",
                    "required": true
                }, {
                    "cmsStructureType": "Boolean",
                    "collection": false,
                    "editable": true,
                    "i18nKey": "type.cmsusergrouprestriction.includesubgroups.name",
                    "localized": false,
                    "paged": false,
                    "qualifier": "includeSubgroups",
                    "required": false
                }, {
                    "cmsStructureType": "EditableDropdown",
                    "collection": true,
                    "editable": true,
                    "i18nKey": "type.cmsusergrouprestriction.usergroups.name",
                    "idAttribute": "uid",
                    "labelAttributes": ["name", "uid"],
                    "localized": false,
                    "paged": true,
                    "qualifier": "userGroups",
                    "required": true,
                    "uri": "/cmswebservices/v1/usergroups"
                }],
                "category": "RESTRICTION",
                "code": "CMSUserGroupRestriction",
                "i18nKey": "type.cmsusergrouprestriction.name",
                "name": "Usergroup Restriction",
                "type": "userGroupRestrictionData"
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/types\?code=CMSUserGroupRestriction\&mode=EDIT/).respond({
                "attributes": [{
                    "cmsStructureType": "ShortString",
                    "collection": false,
                    "editable": true,
                    "i18nKey": "type.cmsusergrouprestriction.name.name",
                    "localized": false,
                    "paged": false,
                    "qualifier": "name",
                    "required": true
                }, {
                    "cmsStructureType": "Boolean",
                    "collection": false,
                    "editable": true,
                    "i18nKey": "type.cmsusergrouprestriction.includesubgroups.name",
                    "localized": false,
                    "paged": false,
                    "qualifier": "includeSubgroups",
                    "required": false
                }, {
                    "cmsStructureType": "EditableDropdown",
                    "collection": true,
                    "editable": true,
                    "i18nKey": "type.cmsusergrouprestriction.usergroups.name",
                    "idAttribute": "uid",
                    "labelAttributes": ["name", "uid"],
                    "localized": false,
                    "paged": true,
                    "qualifier": "userGroups",
                    "required": true,
                    "uri": "/cmswebservices/v1/usergroups"
                }],
                "category": "RESTRICTION",
                "code": "CMSUserGroupRestriction",
                "i18nKey": "type.cmsusergrouprestriction.name",
                "name": "Usergroup Restriction",
                "type": "userGroupRestrictionData"
            });


            // Helpers
            var categoriesNames = {
                "eyJpdGVtSWQiOiJBcG8iLCJjYXRhbG9nSWQiOiJhcHBhcmVsUHJvZHVjdENhdGFsb2ciLCJjYXRhbG9nVmVyc2lvbiI6Ik9ubGluZSJ9Shoes": 'Shoes',
                "eyJpdGVtSWQiOiJBcG8iLCJjYXRhbG9nSWQiOiJhcHBhcmVsUHJvZHVjdENhdGFsb2ciLCJjYXRhbG9nVmVyc2lvbiI6Ik9ubGluZSJ9Shirts": "Shirts",
                "eyJpdGVtSWQiOiJBcG8iLCJjYXRhbG9nSWQiOiJhcHBhcmVsUHJvZHVjdENhdGFsb2ciLCJjYXRhbG9nVmVyc2lvbiI6Ik9ubGluZSJ9Pants": "Pants"
            };

            function getCategoryRestrictionDescription(categories) {
                var description = 'Page only applies on categories:';
                categories.forEach(function(categoryUID) {
                    description += ' ' + categoriesNames[categoryUID] + ';';
                });

                return description;
            }

            var userGroupNames = {
                'cmsmanagergroup': 'CMS Manager Group',
                'employeegroup': 'employeegroup'
            };

            function getUserGroupRestrictionDescription(userGroups) {
                var description = 'Page only applies on usergroups:';
                userGroups.forEach(function(userGroupId) {
                    description += ' (' + userGroupNames[userGroupId] + ');';
                });

                return description;
            }
        });
try {
    angular.module('smarteditloader').requires.push('restrictionsMocks');
} catch (e) {}
try {
    angular.module('smarteditcontainer').requires.push('restrictionsMocks');
} catch (e) {}
