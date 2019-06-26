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
/* jshint unused:false */
angular
    .module('componentMocks', ['ngMockE2E', 'backendMocksUtilsModule'])
    .constant('navigationNodeNodes', [{
        uid: "root",
        name: "ROOT",
        uuid: "CMSNavigationNode-root",
        itemtype: "CMSNavigationNode",
        position: 0,
        hasChildren: true,
        children: ["CMSNavigationNode-1", "CMSNavigationNode-2"]
    }, {
        uid: "1",
        name: "node1",
        title: {
            en: "node1_en",
            fr: "node1_fr"
        },
        uuid: "CMSNavigationNode-1",
        itemtype: "CMSNavigationNode",
        parent: "CMSNavigationNode-root",
        parentUid: "root",
        position: 0,
        hasChildren: true,
        hasEntries: true,
        children: ["CMSNavigationNode-4", "CMSNavigationNode-5", "CMSNavigationNode-7"]
    }, {
        uid: "2",
        name: "node2",
        title: {
            en: "node2_en",
            fr: "node2_fr"
        },
        uuid: "CMSNavigationNode-2",
        itemtype: "CMSNavigationNode",
        parent: "CMSNavigationNode-root",
        parentUid: "root",
        position: 1,
        hasChildren: true,
        hasEntries: false,
        children: ["CMSNavigationNode-6"]
    }, {
        uid: "4",
        name: "node4",
        title: {
            "en": "node4_en",
            "fr": "node4_fr"
        },
        uuid: "CMSNavigationNode-4",
        itemtype: "CMSNavigationNode",
        parent: "CMSNavigationNode-1",
        parentUid: "1",
        position: 0,
        hasChildren: true,
        hasEntries: true,
        children: ["CMSNavigationNode-8"]
    }, {
        uid: "5",
        name: "node5",
        title: {
            "en": "node5_en",
            "fr": "node5_fr"
        },
        uuid: "CMSNavigationNode-5",
        itemtype: "CMSNavigationNode",
        parent: "CMSNavigationNode-1",
        parentUid: "1",
        position: 1,
        hasChildren: false,
        hasEntries: false,
        children: []
    }, {
        uid: "6",
        name: "node6",
        title: {
            "en": "node6_en",
            "fr": "node6_fr"
        },
        uuid: "CMSNavigationNode-6",
        itemtype: "CMSNavigationNode",
        parent: "CMSNavigationNode-2",
        parentUid: "2",
        position: 0,
        hasChildren: false,
        hasEntries: false,
        children: []
    }, {
        uid: "7",
        name: "node7",
        title: {
            "en": "node7_en",
            "fr": "node7_fr"
        },
        uuid: "CMSNavigationNode-7",
        itemtype: "CMSNavigationNode",
        parent: "CMSNavigationNode-1",
        parentUid: "1",
        position: 2,
        hasChildren: false,
        hasEntries: false,
        children: []
    }, {
        uid: "8",
        name: "node8",
        title: {
            "en": "node8_en",
            "fr": "node8_fr"
        },
        uuid: "CMSNavigationNode-8",
        itemtype: "CMSNavigationNode",
        parent: "CMSNavigationNode-4",
        parentUid: "4",
        position: 0,
        hasChildren: true,
        hasEntries: true,
        children: ["CMSNavigationNode-9"]
    }, {
        uid: "9",
        name: "node9",
        title: {
            "en": "node9_en",
            "fr": "node9_fr"
        },
        uuid: "CMSNavigationNode-9",
        itemtype: "CMSNavigationNode",
        parent: "CMSNavigationNode-8",
        parentUid: "8",
        position: 0,
        hasChildren: false,
        hasEntries: false,
        children: []
    }, {
        uuid: "navigationComponent",
        uid: "navigationComponent",
        navigationComponent: "CMSNavigationNode-8"
    }])
    .constant("navigationNodeEntries", [{
        name: "name-HomepageNavLink",
        itemId: "HomepageNavLink",
        itemType: "CMSLinkComponent",
        itemSuperType: "AbstractCMSComponent",
        navigationNodeUid: "1",
        uid: "1"
    }, {
        name: "name-69SlamLink",
        itemId: "69SlamLink",
        itemType: "CMSParagraphComponent",
        itemSuperType: "AbstractCMSComponent",
        navigationNodeUid: "1",
        uid: "2"
    }, {
        name: "name-DakineLink",
        itemId: "DakineLink",
        itemType: "CMSLinkComponent",
        itemSuperType: "AbstractCMSComponent",
        navigationNodeUid: "1",
        uid: "6"
    }, {
        name: "Entry 4.1",
        itemId: "Item-ID-4.1",
        itemType: "ItemType 4.1",
        itemSuperType: "AbstractCMSComponent",
        navigationNodeUid: "4",
        uid: "3",
    }, {
        name: "name-Item-ID-4.2",
        itemId: "Item-ID-4.2",
        itemType: "ItemType 4.2",
        itemSuperType: "AbstractCMSComponent",
        navigationNodeUid: "4",
        uid: "4"
    }, {
        name: "name-Item-ID-6.1",
        itemId: "Item-ID-6.1",
        itemType: "ItemType 6.1",
        itemSuperType: "AbstractCMSComponent",
        navigationNodeUid: "6",
        uid: "5"
    }, {
        name: "name-Item-ID-8.1",
        itemId: "Item-ID-8.1",
        itemType: "CMSLinkComponent",
        itemSuperType: "AbstractCMSComponent",
        navigationNodeUid: "8",
        uid: "6"
    }, {
        name: "name-Item-ID-8.2",
        itemId: "Item-ID-8.2",
        itemType: "CMSParagraphComponent",
        itemSuperType: "AbstractCMSComponent",
        navigationNodeUid: "8",
        uid: "7"
    }])
    .factory('navigationNodePOST', function(navigationNodeNodes) {
        return function(data) {
            var payload = JSON.parse(data);
            var uid = "" + new Date().getTime();

            var parent = navigationNodeNodes.find(function(node) {
                return node.uuid === payload.parent;
            });
            if (parent) {
                payload.parentUid = parent.uid;
            }

            var nodeCountWithSameParent = navigationNodeNodes.filter(function(node) {
                return node.parentUid === payload.parentUid;
            }).length;

            if (!payload.name) {
                return [400, {
                    "errors": [{
                        "message": "name cannot be empty",
                        "subject": "name",
                        "type": "ValidationError"
                    }]
                }];
            }

            payload.uid = uid;
            payload.uuid = "CMSNavigationNode-" + uid;
            payload.itemtype = "CMSNavigationNode";
            payload.hasChildren = false;
            payload.entries = payload.entries || [];
            payload.position = nodeCountWithSameParent;

            if (parent) {
                parent.children = parent.children || [];
                parent.children.push(payload.uuid);
            }

            navigationNodeNodes.push(payload);
            return [200, payload];
        };
    })
    .run(
        function($httpBackend, parseQuery, backendMocksUtils, navigationNodePOST, navigationNodeNodes) {

            var items = {
                componentItems: navigationNodeNodes.concat([{
                    'creationtime': '2016-08-17T16:05:47+0000',
                    'modifiedtime': '2016-08-17T16:05:47+0000',
                    'name': 'Component1',
                    'typeCode': 'AbstractCMSComponent',
                    'uid': 'component1',
                    'uuid': 'component1',
                    'visible': true
                }, {
                    'creationtime': '2016-08-17T16:05:47+0000',
                    'modifiedtime': '2016-08-17T16:05:47+0000',
                    'name': 'Component2',
                    'typeCode': 'AbstractCMSComponent',
                    'uid': 'component2',
                    'uuid': 'component2',
                    'visible': true
                }, {
                    'creationtime': '2016-08-17T16:05:47+0000',
                    'modifiedtime': '2016-08-17T16:05:47+0000',
                    'name': 'Component3',
                    'typeCode': 'AbstractCMSComponent',
                    'uid': 'component3',
                    'uuid': 'component3',
                    'visible': true
                }, {
                    'creationtime': '2016-08-17T16:05:47+0000',
                    'modifiedtime': '2016-08-17T16:05:47+0000',
                    'name': 'Component4',
                    'typeCode': 'AbstractCMSComponent',
                    'uid': 'component4',
                    'uuid': 'component4',
                    'visible': true
                }, {
                    'creationtime': '2016-08-17T16:05:47+0000',
                    'modifiedtime': '2016-08-17T16:05:47+0000',
                    'name': 'Component5',
                    'typeCode': 'AbstractCMSComponent',
                    'uid': 'component5',
                    'uuid': 'component5',
                    'visible': true
                }, {
                    'name': 'Component6',
                    'typeCode': 'AbstractCMSComponent',
                    'uid': 'Component6',
                    'uuid': 'Component6',
                }, {
                    'name': 'Component7',
                    'typeCode': 'AbstractCMSComponent',
                    'uid': 'Component7',
                    'uuid': 'Component7',
                }, {
                    'name': 'Component8',
                    'typeCode': 'AbstractCMSComponent',
                    'uid': 'Component8',
                    'uuid': 'Component8',
                }, {
                    'name': 'Component9',
                    'typeCode': 'AbstractCMSComponent',
                    'uid': 'Component9',
                    'uuid': 'Component9',
                }, {
                    'name': 'Component10',
                    'typeCode': 'AbstractCMSComponent',
                    'uid': 'Component10',
                    'uuid': 'Component10',
                    'cloneable': true
                }, {
                    'name': 'Component11',
                    'typeCode': 'AbstractCMSComponent',
                    'uid': 'Component11',
                    'uuid': 'Component11',
                }, {
                    'name': 'Component12',
                    'typeCode': 'AbstractCMSComponent',
                    'uid': 'Component12',
                    'uuid': 'Component12',
                }, {
                    'name': 'Component13',
                    'typeCode': 'AbstractCMSComponent',
                    'uid': 'Component13',
                    'uuid': 'Component13',
                }, {
                    'name': 'Component14',
                    'typeCode': 'AbstractCMSComponent',
                    'uid': 'Component14',
                    'uuid': 'Component14',
                }, {
                    'name': 'Component15',
                    'typeCode': 'AbstractCMSComponent',
                    'uid': 'Component15',
                    'uuid': 'Component15',
                }, {
                    'name': 'Component16',
                    'typeCode': 'AbstractCMSComponent',
                    'uid': 'Component16',
                    'uuid': 'Component16',
                }, {
                    'name': 'Component17',
                    'typeCode': 'AbstractCMSComponent',
                    'uid': 'Component17',
                    'uuid': 'Component6',
                }, {
                    'name': 'Component18',
                    'typeCode': 'AbstractCMSComponent',
                    'uid': 'Component18',
                    'uuid': 'Component18',
                }, {
                    'name': 'Component19',
                    'typeCode': 'AbstractCMSComponent',
                    'uid': 'Component19',
                    'uuid': 'Component19',
                }, {
                    'name': 'Component20',
                    'typeCode': 'AbstractCMSComponent',
                    'uid': 'Component20',
                    'uuid': 'Component20',
                }, {
                    'name': 'Component21',
                    'typeCode': 'AbstractCMSComponent',
                    'uid': 'Component21',
                    'uuid': 'Component21',
                }, {
                    'name': 'Component22',
                    'typeCode': 'AbstractCMSComponent',
                    'uid': 'Component22',
                    'uuid': 'Component22',
                }, {
                    'name': 'Component23',
                    'typeCode': 'AbstractCMSComponent',
                    'uid': 'Component23',
                    'uuid': 'Component23',
                }, {
                    'id': 'MyParagraph',
                    'uid': 'Comp_0006456345634',
                    'uuid': 'Comp_0006456345634',
                    'name': 'Custom Paragraph Component',
                    'headline': 'The Headline',
                    'active': true,
                    'content': 'the content to edit',
                    'activationDate': new Date().getTime(),
                    'creationtime': new Date().getTime(),
                    'modifiedtime': new Date().getTime(),
                    'media': '4',
                    'external': false,
                    'visible': true,
                    'restrictions': [],
                    'onlyOneRestrictionMustApply': false,
                    'slots': ['mocked_slot_id']
                }, {
                    'uid': 'componentWithMedia',
                    'uuid': 'componentWithMedia',
                    'media': {
                        'en': 'contextualmenu_delete_off'
                    }
                }, {
                    'uid': 'componentWithMediaContainer',
                    'uuid': 'componentWithMediaContainer',
                    'media': {
                        'en': {
                            'widescreen': 'clone4',
                            'desktop': 'dnd5'
                        }
                    }
                }, {

                    'uid': 'componentToValidateId',
                    'uuid': 'componentToValidateId',
                    'headline': 'The Headline',
                    'active': true,
                    'content': {
                        'en': 'the content to edit',
                        'fr': 'le contenu a editer',
                        'pl': 'tresc edytowac',
                        'it': 'il contenuto da modificare',
                        'hi': 'Sampaadit karanee kee liee saamagree'
                    },
                    'media': {
                        'en': 'contextualmenu_delete_off',
                        'hi': 'contextualmenu_delete_on',

                    },
                    'orientation': 'vertical',
                    'linkToggle': {
                        'external': false,
                        'urlLink': '/url-link'
                    }
                }, {
                    'uid': '8',
                    'uuid': '8',
                    'navigationComponent': '8'
                }, {
                    'uid': '8',
                    'uuid': 'navigationComponent',
                    'navigationComponent': '8'
                }, {
                    'uid': '4',
                    'uuid': 'node4'
                }, {
                    'uid': 'HomepageNavLink',
                    'uuid': 'HomepageNavLink',
                    'typeCode': 'CMSLinkComponent',
                    'linkName': {
                        'en': 'ABC Entry'
                    }
                }, {
                    'uid': '69SlamLink',
                    'typeCode': '69SlamLink',
                    'name': {
                        'en': 'DEF Entry'
                    }
                }, {
                    'uid': 'DakineLink',
                    'uuid': 'DakineLink',
                    'typeCode': 'CMSParagraphComponent',
                    'name': 'GHI Entry'
                }, {
                    'uid': 'AlMerrickLink',
                    'uuid': 'AlMerrickLink',
                    'typeCode': 'CMSParagraphComponent',
                    'name': 'XYZ Entry'
                }, {
                    'uid': 'Item-ID-8.1',
                    'uuid': 'Item-ID-8.1',
                    'typeCode': 'CMSLinkComponent',
                    'linkName': {
                        'en': 'JKL Entry'
                    }
                }, {
                    'uid': 'Item-ID-8.2',
                    'uuid': 'Item-ID-8.2',
                    'typeCode': 'CMSParagraphComponent',
                    'name': 'MNO Entry'
                }, {
                    'uid': 'Item-ID-4.1',
                    'uuid': 'Item-ID-4.1',
                    'typeCode': 'CMSParagraphComponent',
                    'name': 'PQR Entry'
                }, {
                    'uid': 'Item-ID-4.2',
                    'uuid': 'Item-ID-4.2',
                    'typeCode': 'CMSParagraphComponent',
                    'name': 'STU Entry'
                }, {
                    'name': 'Home Page Nav Link',
                    'typeCode': 'CMSLinkComponent',
                    'uid': 'HomepageNavLink',
                    'uuid': 'HomepageNavLink',
                    'visible': true
                }, {
                    'name': 'Al Merrick Link',
                    'typeCode': 'CMSLinkComponent',
                    'uid': 'AlMerrickLink',
                    'uuid': 'AlMerrickLink',
                    'visible': true
                }, {
                    'name': 'Nike Link',
                    'typeCode': 'CMSLinkComponent',
                    'uid': 'NikeLink',
                    'uuid': 'NikeLink',
                    'visible': true
                }, {
                    'name': '69 Slam Link',
                    'typeCode': 'CMSLinkComponent',
                    'uid': '69SlamLink',
                    'uuid': '69SlamLink',
                    'visible': true
                }, {
                    'name': 'Dakine Link',
                    'typeCode': 'CMSLinkComponent',
                    'uid': 'DakineLink',
                    'uuid': 'DakineLink',
                }, {
                    'typeCode': 'SingleOnlineProductSelector',
                    'uid': 'singleOnlineProductSelectorEditComponentId',
                    'uuid': 'singleOnlineProductSelectorEditComponentId',
                    'product': '300738116',
                }, {
                    'type': 'contentPageData',
                    'creationtime': '2016-06-28T15:23:37+0000',
                    'defaultPage': false,
                    'modifiedtime': '2016-06-28T15:25:51+0000',
                    'name': 'Homepage',
                    'masterTemplate': 'AccountPageTemplateUuid',
                    'title': {
                        'de': 'Mes lovens pendas',
                        'en': 'I love pandas'
                    },
                    'typeCode': 'ContentPage',
                    'itemtype': 'ContentPage',
                    'uid': 'homepage',
                    'uuid': 'homepage',
                    'label': 'i-love-pandas',
                    'restrictions': [{
                        'uid': 'timeRestrictionIdA',
                        'uuid': 'timeRestrictionIdA',
                        'name': 'Some Time restriction A',
                        'typeCode': 'CMSTimeRestriction',
                        'itemtype': 'CMSTimeRestriction',
                        'description': 'some description'
                    }, {
                        'uid': 'timeRestrictionIdB',
                        'uuid': 'timeRestrictionIdB',
                        'name': 'another time B',
                        'typeCode': 'CMSTimeRestriction',
                        'itemtype': 'CMSTimeRestriction',
                        'description': 'some description'
                    }],
                    'pageStatus': 'ACTIVE',
                    'catalogVersion': 'apparel-ukContentCatalog/Staged'
                }, {
                    'type': 'contentPageData',
                    'creationtime': '2016-06-28T15:23:37+0000',
                    'defaultPage': true,
                    'modifiedtime': '2016-06-28T15:25:51+0000',
                    'name': 'Some Other Page',
                    'masterTemplate': 'ProductPageTemplateUuid',
                    'title': {
                        'de': 'Mes hatens pendas',
                        'en': 'I hate pandas'
                    },
                    'typeCode': 'ProductPage',
                    'itemtype': 'ProductPage',
                    'uid': 'secondpage',
                    'uuid': 'secondpage',
                    'label': 'i-hate-pandas',
                    'pageStatus': 'ACTIVE',
                    'restrictions': [],
                    'catalogVersion': 'apparel-ukContentCatalog/Staged'
                }, {
                    'type': 'contentPageData',
                    'creationtime': '2018-06-28T15:23:37+0000',
                    'defaultPage': true,
                    'modifiedtime': '2018-06-28T15:25:51+0000',
                    'name': 'Third Page',
                    'masterTemplate': 'ProductPageTemplateUuid',
                    'title': {
                        'en': 'Third page'
                    },
                    'typeCode': 'ProductPage',
                    'itemtype': 'ProductPage',
                    'uid': 'thirdpage',
                    'uuid': 'thirdpage',
                    'label': 'third-page',
                    'pageStatus': 'ACTIVE',
                    'restrictions': [],
                    'catalogVersion': 'apparel-ukContentCatalog/Staged'
                }, {
                    'type': 'contentPageData',
                    'creationtime': '2016-06-28T15:23:37+0000',
                    'defaultPage': true,
                    'modifiedtime': '2016-06-28T15:25:51+0000',
                    'name': 'Synched Page',
                    'masterTemplate': 'ProductPageTemplateUuid',
                    'title': {
                        'en': 'synched page'
                    },
                    'typeCode': 'ProductPage',
                    'itemtype': 'ProductPage',
                    'uid': 'synchedPage',
                    'uuid': 'synchedPage',
                    'label': 'synched page',
                    'pageStatus': 'ACTIVE'
                }, {
                    'type': 'contentPageData',
                    'creationtime': "2016-04-08T21:16:41+0000",
                    'modifiedtime': "2016-04-08T21:16:41+0000",
                    'masterTemplate': "PageTemplate",
                    'name': "productPage1",
                    'label': 'productPage1',
                    'typeCode': "ProductPage",
                    'title': {
                        'en': 'productPage1'
                    },
                    'uid': "auid2",
                    'uuid': 'auid2',
                    'defaultPage': true,
                    'pageStatus': 'ACTIVE',
                    'itemtype': 'ProductPage',
                }, {
                    'uid': 'AccountPageTemplate',
                    'uuid': 'AccountPageTemplateUuid'
                }, {
                    'uid': 'ProductPageTemplate',
                    'uuid': 'ProductPageTemplateUuid'
                }, { // RESTRICTIONS
                    'uid': 'timeRestrictionIdA',
                    'uuid': 'timeRestrictionIdA',
                    'name': 'Some Time restriction A',
                    'typeCode': 'CMSTimeRestriction',
                    'itemtype': 'CMSTimeRestriction',
                    'description': 'some description'
                }, {
                    'uid': 'timeRestrictionIdB',
                    'uuid': 'timeRestrictionIdB',
                    'name': 'another time B',
                    'typeCode': 'CMSTimeRestriction',
                    'itemtype': 'CMSTimeRestriction',
                    'description': 'some description'
                }, {
                    'uid': 'timeRestrictionIdC',
                    'uuid': 'timeRestrictionIdC',
                    'name': 'yet another',
                    'typeCode': 'CMSTimeRestriction',
                    'itemtype': 'CMSTimeRestriction',
                    'description': 'some description'
                }, {
                    'uid': 'catalogRestrictionIdD',
                    'uuid': 'catalogRestrictionIdD',
                    'name': 'some cat restriction',
                    'typeCode': 'CMSCategoryRestriction',
                    'itemtype': 'CMSCategoryRestriction',
                    'description': 'some description',
                    'categories': ['categoryA']
                }, {
                    'uid': 'catalogRestrictionIdE',
                    'uuid': 'catalogRestrictionIdE',
                    'name': "I'm a skatman",
                    'typeCode': 'CMSCatalogRestriction',
                    'itemtype': 'CMSCatalogRestriction',
                    'description': 'some description'
                }, {
                    'uid': 'catalogRestrictionIdF',
                    'uuid': 'catalogRestrictionIdF',
                    'name': 'Cat restriction E',
                    'typeCode': 'CMSCatalogRestriction',
                    'itemtype': 'CMSCatalogRestriction',
                    'description': 'some description'
                }, {
                    'uid': 'catalogRestrictionIdG',
                    'uuid': 'catalogRestrictionIdG',
                    'name': 'catalogRestrictionNameG',
                    'typeCode': 'CMSCatalogRestriction',
                    'itemtype': 'CMSCatalogRestriction',
                    'description': 'some description'
                }, {
                    'uid': 'catalogRestrictionIdH',
                    'uuid': 'catalogRestrictionIdH',
                    'name': 'Some User restriciton 1',
                    'typeCode': 'CMSCatalogRestriction',
                    'itemtype': 'CMSCatalogRestriction',
                    'description': 'some description'
                }, {
                    'uid': 'userRestrictionIdI',
                    'uuid': 'userRestrictionIdI',
                    'name': 'User restriction 2',
                    'typeCode': 'CMSUserRestriction',
                    'itemtype': 'CMSUserRestriction',
                    'description': 'some description'
                }, {
                    'uid': 'userGroupRestrictionId1',
                    'uuid': 'userGroupRestrictionId1',
                    'name': 'User Group Restriction 1',
                    'typeCode': 'CMSUserGroupRestriction',
                    'itemtype': 'CMSUserGroupRestriction',
                    'description': 'some description'
                }, { //page for restrictions
                    'type': 'contentPageData',
                    'uid': 'add-edit-address',
                    'uuid': 'add-edit-address',
                    'onlyOneRestrictionMustApply': false,
                    'creationtime': '2016-07-15T23:35:21+0000',
                    'defaultPage': true,
                    'modifiedtime': '2016-07-15T23:38:01+0000',
                    'name': 'Add Edit Address Page',
                    'template': 'AccountPageTemplate',
                    'title': {
                        'de': 'Adresse hinzufügen/bearbeiten'
                    },
                    'label': 'add-edit-address',
                    'restrictions': [{
                        'uid': 'timeRestrictionIdA',
                        'uuid': 'timeRestrictionIdA',
                        'name': 'Some Time restriction A',
                        'typeCode': 'CMSTimeRestriction',
                        'itemtype': 'CMSTimeRestriction',
                        'description': 'some description'
                    }, {
                        'uid': 'catalogRestrictionIdD',
                        'uuid': 'catalogRestrictionIdD',
                        'name': 'some cat restriction',
                        'typeCode': 'CMSCategoryRestriction',
                        'itemtype': 'CMSCategoryRestriction',
                        'description': 'some description',
                        'categories': ['categoryA']
                    }]
                }, { //PAGES
                    creationtime: "2016-04-08T21:16:41+0000",
                    modifiedtime: "2016-04-08T21:16:41+0000",
                    template: "PageTemplate",
                    name: "page1TitleSuffix",
                    label: 'page1TitleSuffix',
                    typeCode: "ContentPage",
                    uid: "auid1",
                    uuid: "auid1",
                    defaultPage: true,
                    pageStatus: 'ACTIVE',
                    itemtype: "ContentPage"
                }, {
                    creationtime: "2016-04-08T21:16:41+0000",
                    modifiedtime: "2016-04-08T21:16:41+0000",
                    template: "ActionTemplate",
                    name: "welcomePage",
                    typeCode: "ActionPage",
                    uid: "uid2",
                    uuid: "uid2"
                }, {
                    creationtime: "2016-04-08T21:16:41+0000",
                    modifiedtime: "2016-04-08T21:16:41+0000",
                    template: "PageTemplate",
                    name: "Advertise",
                    typeCode: "MyCustomType",
                    uid: "uid3",
                    uuid: "uid3"
                }, {
                    creationtime: "2016-04-08T21:16:41+0000",
                    modifiedtime: "2016-04-08T21:16:41+0000",
                    template: "MyCustomPageTemplate",
                    name: "page2TitleSuffix",
                    typeCode: "HomePage",
                    uid: "uid4",
                    uuid: "uid4"
                }, {
                    creationtime: "2016-04-08T21:16:41+0000",
                    modifiedtime: "2016-04-08T21:16:41+0000",
                    template: "ZTemplate",
                    name: "page3TitleSuffix",
                    typeCode: "ProductPage",
                    uid: "uid5",
                    uuid: "uid5"
                }, {
                    creationtime: '2016-07-07T14:33:37+0000',
                    defaultPage: true,
                    modifiedtime: '2016-07-12T01:23:41+0000',
                    name: 'My Little Primary Content Page',
                    onlyOneRestrictionMustApply: true,
                    template: 'SomePageTemplate',
                    title: {
                        en: 'Primary Content Page'
                    },
                    typeCode: 'ContentPage',
                    itemtype: 'ContentPage',
                    uid: 'primaryContentPage',
                    uuid: "primaryContentPage",
                    label: 'primary-content-page',
                    pageStatus: 'ACTIVE'
                }, {
                    creationtime: '2016-07-03T14:33:37+0000',
                    defaultPage: true,
                    modifiedtime: '2016-07-11T01:23:41+0000',
                    name: 'Some Other Primary Content Page',
                    onlyOneRestrictionMustApply: true,
                    template: 'SomePageTemplate',
                    title: {
                        en: 'Primary Content Page'
                    },
                    typeCode: 'ContentPage',
                    itemtype: 'ContentPage',
                    uid: 'someOtherPrimaryPageContent',
                    uuid: "someOtherPrimaryPageContent",
                    label: 'some-other-primary-page-content',
                    pageStatus: 'ACTIVE'
                }, {
                    creationtime: '2016-07-02T14:33:37+0000',
                    defaultPage: true,
                    modifiedtime: '2016-07-10T01:23:41+0000',
                    name: 'Special Primary Content Page',
                    onlyOneRestrictionMustApply: true,
                    template: 'SomePageTemplate',
                    title: {
                        en: 'Primary Content Page'
                    },
                    typeCode: 'ContentPage',
                    itemtype: 'ContentPage',
                    uid: 'specialPrimaryPageContent',
                    uuid: "specialPrimaryPageContent",
                    label: 'special-primary-page-content',
                    pageStatus: 'ACTIVE'
                }, {
                    creationtime: '2016-07-07T14:33:37+0000',
                    defaultPage: true,
                    modifiedtime: '2016-07-12T01:23:41+0000',
                    name: 'My Little Variation Content Page',
                    onlyOneRestrictionMustApply: true,
                    template: 'SomePageTemplate',
                    title: {
                        en: 'Variation Content Page'
                    },
                    typeCode: 'ContentPage',
                    itemtype: 'ContentPage',
                    uid: 'variationContentPage',
                    uuid: "variationContentPage",
                    label: 'variation-content-page',
                    pageStatus: 'ACTIVE'
                }, {
                    creationtime: '2016-07-07T14:33:37+0000',
                    defaultPage: true,
                    modifiedtime: '2016-07-12T01:23:41+0000',
                    name: 'My Little Primary Category Page',
                    onlyOneRestrictionMustApply: true,
                    template: 'SomePageTemplate',
                    title: {
                        en: 'Primary Category Page'
                    },
                    typeCode: 'CategoryPage',
                    itemtype: 'CategoryPage',
                    uid: 'primaryCategoryPage',
                    uuid: "primaryCategoryPage",
                    label: 'primary-category-page',
                    pageStatus: 'ACTIVE'
                }, {
                    creationtime: '2016-07-07T14:33:37+0000',
                    defaultPage: true,
                    modifiedtime: '2016-07-12T01:23:41+0000',
                    name: 'My Little Variation Category Page',
                    onlyOneRestrictionMustApply: true,
                    template: 'SomePageTemplate',
                    title: {
                        en: 'Variation Category Page'
                    },
                    typeCode: 'CategoryPage',
                    itemtype: 'CategoryPage',
                    uid: 'variationCategoryPage',
                    uuid: "variationCategoryPage",
                    label: 'variation-category-page',
                    pageStatus: 'ACTIVE'
                }, {
                    creationtime: '2016-07-07T14:33:37+0000',
                    defaultPage: true,
                    modifiedtime: '2016-07-12T01:23:41+0000',
                    name: 'My Little Primary Product Page',
                    onlyOneRestrictionMustApply: true,
                    template: 'SomePageTemplate',
                    title: {
                        en: 'Primary Product Page'
                    },
                    typeCode: 'ProductPage',
                    itemtype: 'ProductPage',
                    uid: 'primaryProductPage',
                    uuid: "primaryProductPage",
                    label: 'primary-product-page',
                    pageStatus: 'ACTIVE'
                }, {
                    creationtime: '2016-07-07T14:33:37+0000',
                    defaultPage: true,
                    modifiedtime: '2016-07-12T01:23:41+0000',
                    name: 'My Little Variation Product Page',
                    onlyOneRestrictionMustApply: true,
                    template: 'SomePageTemplate',
                    title: {
                        en: 'Variation Product Page'
                    },
                    typeCode: 'ProductPage',
                    itemtype: 'ProductPage',
                    uid: 'variationProductPage',
                    uuid: "variationProductPage",
                    label: 'variation-product-page',
                    pageStatus: 'ACTIVE'
                }, {
                    'type': 'contentPageData',
                    'defaultPage': true,
                    'name': 'Homepage_uk_online',
                    'typeCode': 'ContentPage',
                    'itemtype': 'ContentPage',
                    'uid': 'homepage_uk_online',
                    'uuid': 'homepage_uk_online',
                    'catalogVersion': 'apparel-ukContentCatalog/Online',
                    'label': 'i-hate-pandas',
                    'restrictions': [],
                    'pageStatus': 'ACTIVE'
                }, {
                    'type': 'contentPageData',
                    'defaultPage': true,
                    'name': 'Homepage_global_online',
                    'typeCode': 'ContentPage',
                    'itemtype': 'ContentPage',
                    'uid': 'homepage_global_online',
                    'uuid': 'homepage_global_online',
                    'catalogVersion': 'apparelContentCatalog/Online',
                    'label': 'i-hate-pandas',
                    'restrictions': [],
                    'pageStatus': 'ACTIVE'
                }, {
                    'type': 'contentPageData',
                    'defaultPage': true,
                    'name': 'Homepage_global_staged',
                    'typeCode': 'ContentPage',
                    'itemtype': 'ContentPage',
                    'uid': 'homepage_global_staged',
                    'uuid': 'homepage_global_staged',
                    'catalogVersion': 'apparelContentCatalog/Staged',
                    'restrictions': [],
                    'pageStatus': 'ACTIVE'
                }, {
                    'type': 'contentPageData',
                    'defaultPage': false,
                    'name': 'Homepage_gloabl_online_variation',
                    'typeCode': 'ContentPage',
                    'itemtype': 'ContentPage',
                    'uid': 'homepage_gloabl_online_variation',
                    'uuid': 'homepage_gloabl_online_variation',
                    'catalogVersion': 'apparelContentCatalog/Online',
                    'restrictions': [],
                    'pageStatus': 'ACTIVE'
                }, {
                    'type': 'contentPageData',
                    'defaultPage': true,
                    'name': 'Homepage_gloabl_online_copy_disabled',
                    'copyToCatalogsDisabled': true,
                    'typeCode': 'ContentPage',
                    'itemtype': 'ContentPage',
                    'uid': 'homepage_gloabl_online_copy_disabled',
                    'uuid': 'homepage_gloabl_online_copy_disabled',
                    'catalogVersion': 'apparelContentCatalog/Online',
                    'restrictions': [],
                    'pageStatus': 'ACTIVE'
                }, {
                    'id': 'MyParagraph',
                    'uid': 'staticDummyComponent',
                    'uuid': 'staticDummyComponent',
                    'name': 'Static Dummy Component',
                    'headline': 'The Headline',
                    'active': true,
                    'content': 'the content to edit',
                    'creationtime': new Date().getTime(),
                    'modifiedtime': new Date().getTime(),
                    'type': 'componentType1',
                    'media': '4',
                    'external': false,
                    'visible': true,
                    'cloneable': true,
                    'restrictions': [],
                    'onlyOneRestrictionMustApply': false,
                    'slots': ['mocked_slot_id']
                }, {
                    catalogVersion: "apparel-ukContentCatalog/Staged",
                    creationtime: '2016-07-07T14:33:37+0000',
                    modifiedtime: '2016-07-12T01:23:41+0000',
                    name: 'Trashed Content Page',
                    template: 'SomePageTemplate',
                    title: {
                        en: 'Variation Content Page'
                    },
                    typeCode: 'ContentPage',
                    itemtype: 'ContentPage',
                    uid: 'trashedContentPage',
                    uuid: "trashedContentPage",
                    label: 'trashedContentPage',
                    pageStatus: 'DELETED',
                    restrictions: []
                }, {
                    catalogVersion: "apparel-ukContentCatalog/Staged",
                    creationtime: '2016-07-07T14:33:37+0000',
                    modifiedtime: '2016-06-28T15:25:51+0000',
                    name: 'Trashed Category Page',
                    template: 'SomePageTemplate',
                    title: {
                        en: 'Variation Content Page'
                    },
                    typeCode: 'CategoryPage',
                    itemtype: 'CategoryPage',
                    uid: 'trashedCategoryPage',
                    uuid: "trashedCategoryPage",
                    label: 'trashedCategoryPage',
                    pageStatus: 'DELETED',
                    restrictions: []
                }, {
                    creationtime: '2016-07-07T14:33:37+0000',
                    modifiedtime: '2017-09-26T15:22:37+0000',
                    name: 'Trashed Product Page',
                    template: 'SomePageTemplate',
                    title: {
                        en: 'Variation Content Page'
                    },
                    typeCode: 'ProductPage',
                    itemtype: 'ProductPage',
                    uid: 'trashedProductPage',
                    uuid: "trashedProductPage",
                    label: 'trashedProductPage',
                    pageStatus: 'DELETED',
                    restrictions: []
                }, {
                    uid: "hiddenComponent1",
                    uuid: "hiddenComponent1",
                    name: "Hidden Component 1",
                    visible: false,
                    cloneable: true,
                    typeCode: 'AbstractCMSComponent',
                    catalogVersion: "apparel-ukContentCatalog/Staged",
                    restrictions: []
                }, {
                    uid: "hiddenComponent2",
                    uuid: "hiddenComponent2",
                    name: "Hidden Component 2",
                    visible: false,
                    cloneable: true,
                    typeCode: 'AbstractCMSComponent',
                    catalogVersion: "apparel-ukContentCatalog/Staged"
                }, {
                    uid: "hiddenComponent3",
                    uuid: "hiddenComponent3",
                    name: "Hidden Component 3",
                    visible: false,
                    cloneable: false,
                    typeCode: 'AbstractCMSComponent',
                    catalogVersion: "apparel-ukContentCatalog/Staged"
                }])
            };

            var commonSlots = [{
                uid: "component1",
                uuid: "component1",
                name: "Component 1",
                visible: true,
                typeCode: "SimpleResponsiveBannerComponent",
                catalogVersion: "apparel-ukContentCatalog/Staged"
            }, {
                uid: "component2",
                uuid: "component2",
                name: "Component 2",
                visible: true,
                typeCode: "componentType2",
                catalogVersion: "apparel-ukContentCatalog/Staged"
            }, {
                uid: "component3",
                uuid: "component3",
                name: "Component 3",
                visible: true,
                typeCode: "componentType3",
                catalogVersion: "apparel-ukContentCatalog/Staged"
            }, {
                uid: "component4",
                uuid: "component4",
                name: "Component 4",
                visible: true,
                typeCode: "componentType4",
                catalogVersion: "apparel-ukContentCatalog/Staged"
            }, {
                uid: "component5",
                uuid: "component5",
                name: "Component 5",
                visible: true,
                typeCode: "componentType5",
                catalogVersion: "apparel-ukContentCatalog/Staged"
            }, {
                uid: "hiddenComponent1",
                uuid: "hiddenComponent1",
                name: "Hidden Component 1",
                visible: false,
                typeCode: "AbstractCMSComponent",
                catalogVersion: "apparel-ukContentCatalog/Staged"
            }, {
                uid: "hiddenComponent2",
                uuid: "hiddenComponent2",
                name: "Hidden Component 2",
                visible: false,
                typeCode: "AbstractCMSComponent",
                catalogVersion: "apparel-ukContentCatalog/Staged"
            }, {
                uid: "hiddenComponent3",
                uuid: "hiddenComponent3",
                name: "Hidden Component 3",
                visible: false,
                typeCode: "AbstractCMSComponent",
                catalogVersion: "apparel-ukContentCatalog/Staged"
            }];

            sessionStorage.setItem('componentMocks', JSON.stringify(items));

            // FIXME: ids are the components in pagesContentSlotsComponentsMocks
            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/cmsitems\?catalogId=apparel-ukContentCatalog&catalogVersion=Staged&uuids=component1,component2,component3,hiddenComponent1,hiddenComponent2,component4,component5,hiddenComponent3/).respond({
                response: commonSlots
            });

            var componentGETMock = $httpBackend.whenGET(/\/cmswebservices\/v1\/sites\/.*\/cmsitems\/.*/);
            componentGETMock.respond(function(method, url, data, headers) {
                var uuid = /cmsitems\/(.*)/.exec(url)[1];
                return [200, JSON.parse(sessionStorage.getItem('componentMocks')).componentItems.find(function(item) {
                    return item.uuid === uuid;
                })];
            });
            backendMocksUtils.storeBackendMock('componentGETMock', componentGETMock);

            //old mock to be removed with navigation refactoring
            $httpBackend.whenGET(/\/cmswebservices\/v1\/sites\/.*\/catalogs\/.*\/versions\/.*\/items\/.*/).respond(function(method, url, data, headers) {
                var uid = /items\/(.*)/.exec(url)[1];
                return [200, JSON.parse(sessionStorage.getItem('componentMocks')).componentItems.find(function(item) {
                    return item.uid === uid;
                })];
            });

            var validationErrors_No_Media = {
                'errors': [{
                    'message': 'A Media must be selected. Language: [en]',
                    'reason': 'missing',
                    'subject': 'media',
                    'subjectType': 'parameter',
                    'type': 'ValidationError'
                }]
            };

            var validationErrors_Cannot_Delete_Page = {
                'errors': [{
                    'message': 'This field is required.',
                    'reason': 'missing',
                    'subject': 'title',
                    'subjectType': 'parameter',
                    'type': 'ValidationError'
                }]
            };

            //pushed to global namespace for easy override by specific mocks
            var componentPUTMock = $httpBackend.whenPUT(/\/cmswebservices\/v1\/sites\/.*\/cmsitems\/.*/);
            window.itemPUTDefaultResponse = function(method, url, data, headers) {
                var uuid = /cmsitems\/(.*)/.exec(url)[1];

                var payload = JSON.parse(data);

                var key = Object.keys(payload).find(function(key) {
                    return JSON.stringify(payload[key]).indexOf('trump') > -1;
                });

                if (key) {
                    return [400, {
                        'errors': [{
                            'language': 'en',
                            'message': 'No Trump jokes plz.',
                            'reason': 'invalid',
                            'subject': 'media',
                            'subjectType': 'parameter',
                            'type': 'ValidationError'
                        }]
                    }];
                }

                items.componentItems = items.componentItems.map(function(item) {
                    if (item.uuid === payload.uuid) {
                        return payload;
                    } else {
                        return item;
                    }
                });

                sessionStorage.setItem('componentMocks', JSON.stringify(items));

                if (uuid === 'componentToValidateId' && !payload.media.en) {
                    return [400, validationErrors_No_Media];
                } else if (uuid === 'primaryCategoryPage') {
                    return [400, validationErrors_Cannot_Delete_Page];
                } else {
                    return [204];
                }
            };

            window.itemPUT = componentPUTMock;
            componentPUTMock.respond(window.itemPUTDefaultResponse);
            backendMocksUtils.storeBackendMock('componentPUTMock', componentPUTMock);


            // DELETE Mocks
            var componentDELETEMock = $httpBackend.whenDELETE(/\/cmswebservices\/v1\/sites\/.*\/cmsitems\/.*/);
            componentDELETEMock.respond(function(method, url, data, headers) {
                var uuid = /cmsitems\/(.*)/.exec(url)[1];

                var item = JSON.parse(sessionStorage.getItem('componentMocks')).componentItems.find(function(item) {
                    return item.uuid === uuid;
                });

                if (item) {
                    items.componentItems = items.componentItems.filter(function(item) {
                        return item.uuid !== uuid;
                    });
                    sessionStorage.setItem('componentMocks', JSON.stringify(items));
                    return [200];
                } else {
                    return [400];
                }
            });
            backendMocksUtils.storeBackendMock('componentDELETEMock', componentDELETEMock);


            // "/cmswebservices/v1/sites/CURRENT_CONTEXT_SITE_ID/cmsitems"
            // cmsitems/?dryRun=true
            var componentPOSTMock = $httpBackend.whenPOST(/\/cmswebservices\/v1\/sites\/.*\/cmsitems(?!\/uuids)/);
            componentPOSTMock.respond(function(method, url, data, headers) {
                var dataObject = angular.fromJson(data);
                if (dataObject.uid === 'trump') {
                    return [400, {
                        'errors': [{
                            'message': 'No Trump jokes plz.',
                            'reason': 'invalid',
                            'subject': 'uid',
                            'subjectType': 'parameter',
                            'type': 'ValidationError'
                        }]
                    }];
                } else if (dataObject.itemtype === 'CMSNavigationNode') {
                    return navigationNodePOST(data);
                } else {
                    return [200, {
                        uid: 'valid'
                    }];
                }
            });
            backendMocksUtils.storeBackendMock('componentPOSTMock', componentPOSTMock);

            $httpBackend.whenPOST(/\/cmswebservices\/v1\/sites\/.*\/cmsitems\/uuids/).respond(function(method, url, data, headers) {
                var dataObject = angular.fromJson(data);
                var items = JSON.parse(sessionStorage.getItem('componentMocks'));
                items = items && items.componentItems ? items.componentItems : [];
                var response = items.concat(commonSlots).filter(function(item) {
                    return dataObject.uuids.indexOf(item.uuid) > -1;
                });
                return [200, {
                    response: response
                }];
            });

            var componentsListGETMock = $httpBackend.whenGET(/\/cmswebservices\/v1\/sites\/.*\/cmsitems/).respond(function(method, url, data, headers) {
                var params = parseQuery(url);
                var currentPage = params.currentPage;
                var mask = params.mask;
                var pageSize = params.pageSize;
                var typeCode = params.typeCode;
                var uuids = params.uuids && params.uuids.split(',');
                var itemSearchParams = params.itemSearchParams && params.itemSearchParams.split(',');
                var sort = params.sort;

                var filteredItems = JSON.parse(sessionStorage.getItem('componentMocks')).componentItems;

                if (uuids) {
                    filteredItems = items.componentItems.filter(function(item) {
                        return uuids.indexOf(item.uuid) > -1;
                    });

                    return [200, {
                        response: filteredItems
                    }];
                }

                if (typeCode) {
                    filteredItems = typeCode === 'AbstractPage' ? items.componentItems.filter(function(item) {
                        return item.typeCode === 'ContentPage' || item.typeCode === 'ProductPage' || item.typeCode === 'CategoryPage';
                    }) : items.componentItems.filter(function(item) {
                        return item.typeCode === typeCode || item.itemtype === typeCode;
                    });
                }

                if (itemSearchParams) {
                    filteredItems = filteredItems.filter(function(item) {
                        var filtered = false;
                        itemSearchParams.forEach(function(param) {
                            var paramParsed = param.split(':');
                            if (paramParsed.length === 2 && item[paramParsed[0]] && item[paramParsed[0]].toString().toUpperCase() === paramParsed[1].toUpperCase()) {
                                filtered = true;
                            }
                        });

                        return filtered;
                    });
                }


                if (sort) {
                    var sortColumn = sort.split(':')[0];
                    var sortdirection = sort.split(':')[1];

                    filteredItems = filteredItems.sort(function(a, b) {
                        var nameA = a[sortColumn].toUpperCase(); // ignore upper and lowercase
                        var nameB = b[sortColumn].toUpperCase(); // ignore upper and lowercase
                        if (nameA < nameB) {
                            return sortdirection === 'asc' ? -1 : 1;
                        }
                        if (nameA > nameB) {
                            return sortdirection === 'asc' ? 1 : -1;
                        }
                        return 0;
                    });
                }

                filteredItems = filteredItems.filter(function(item) {
                    return mask ? ((item.name && typeof item.name === 'string' && item.name.toUpperCase().indexOf(mask.toUpperCase()) > -1) || item.uid.toUpperCase().indexOf(mask.toUpperCase()) > -1) : true;
                });

                var results = filteredItems.slice(currentPage * 10, currentPage * 10 + 10);

                var pagedResults = {
                    pagination: {
                        count: 10,
                        page: currentPage,
                        totalCount: filteredItems.length,
                        totalPages: 3
                    },
                    response: results
                };

                return [200, pagedResults];
            });
            backendMocksUtils.storeBackendMock('componentsListGETMock', componentsListGETMock);

            //old mock to be removed with navigation refactoring
            $httpBackend.whenGET(/\/cmswebservices\/v1\/sites\/.*\/catalogs\/.*\/versions\/.*\/items/).respond(function(method, url, data, headers) {
                var params = parseQuery(url);
                var currentPage = params.currentPage;
                var mask = params.mask;
                var pageSize = params.pageSize;

                var filtered = items.componentItems.filter(function(item) {
                    return mask ? ((item.name && typeof item.name === 'string' && item.name.toUpperCase().indexOf(mask.toUpperCase()) > -1) || item.uid.toUpperCase().indexOf(mask.toUpperCase()) > -1) : true;
                });

                var results = filtered.slice(currentPage * 10, currentPage * 10 + 10);

                var pagedResults = {
                    pagination: {
                        count: 10,
                        page: currentPage,
                        totalCount: filtered.length,
                        totalPages: 3
                    },
                    componentItems: results
                };

                return [200, pagedResults];
            });

            $httpBackend.expectGET(/cmssmartedit\/images\/component_default.png/).respond({});

        });
try {
    angular.module('smarteditloader').requires.push('componentMocks');
} catch (e) {}
try {
    angular.module('smarteditcontainer').requires.push('componentMocks');
} catch (e) {}
