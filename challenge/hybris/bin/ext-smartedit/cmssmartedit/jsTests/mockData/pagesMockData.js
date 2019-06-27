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
unit.mockData.pages = {};
unit.mockData.pages.PagesMocks = [{
    "approvalStatus": "APPROVED",
    "availableContentSlots": "SiteLogo; HeaderLinks; SearchBox; MiniCart; NavigationBar; Section1; Section2; Section3; Section4; VariantSelector; AddToCart; CrossSelling; UpSelling; Footer; Tabs; TopHeaderSlot; BottomHeaderSlot; PlaceholderContentSlot",
    "catalogVersion": "electronics-deContentCatalog/Staged",
    "contentSlots": [{
        "catalogVersion": "electronics-deContentCatalog/Staged",
        "contentSlot": "eyJpdGVtSWQiOiJTZWN0aW9",
        "creationtime": "2018-10-26T16:03:02+0000",
        "itemtype": "ContentSlotForPage",
        "modifiedtime": "2018-10-26T16:03:02+0000",
        "page": "eyJpdGVtSWQiOiJjbXNpdGVtXzAwMDAyM",
        "position": "Section4",
        "uid": "contentSlotForPage-00000003"
    }],
    "copyToCatalogsDisabled": false,
    "creationtime": "2018-10-26T16:03:02+0000",
    "defaultPage": true,
    "itemtype": "ProductPage",
    "localizedPages": [],
    "masterTemplate": "eyJpdGVtSWQiOiJQcm9kdWN",
    "missingContentSlots": "",
    "modifiedtime": "2018-10-26T16:03:02+0000",
    "name": "dfgdfg",
    "navigationNodeList": [],
    "onlyOneRestrictionMustApply": false,
    "pageStatus": "ACTIVE",
    "restrictions": [],
    "title": {
        "de": "dgf"
    },
    "type": {
        "de": "Produktseite"
    },
    "typeCode": "ProductPage",
    "uid": "cmsitem_00002000",
    "uuid": "eyJpdGVtSWQiOiJjbXNpdGVtXzA",
    "view": "product/productLayout2Page"
}];

unit.mockData.pages.PagesRestMocks = {
    "pages": {
        "pagination": {
            "count": 1,
            "page": 0,
            "totalCount": 1,
            "totalPages": 1
        },
        "response": unit.mockData.pages.PagesMocks
    }
};

unit.mockData.pages.PrimaryPageMocks = {
    getMocksForType: function(type) {
        return [{
            "type": type + 'Data',
            "creationtime": "2016-08-02T21:10:25+0000",
            "defaultPage": true,
            "modifiedtime": "2016-08-02T21:13:52+0000",
            "name": "Update Email Page",
            "onlyOneRestrictionMustApply": true,
            "pk": "8796093678640",
            "template": "AccountPageTemplate",
            "title": {
                "en": "Update Email",
                "ja": "電子メールの更新",
                "de": "E-Mail aktualisieren",
                "zh": "更新电子邮件"
            },
            "typeCode": type,
            "uid": "update-email",
            "label": "update-email"
        }, {
            "type": type + 'Data',
            "creationtime": "2016-08-02T21:10:25+0000",
            "defaultPage": true,
            "modifiedtime": "2016-08-02T21:11:56+0000",
            "name": "Update Forgotten Password Page",
            "onlyOneRestrictionMustApply": true,
            "pk": "8796093613104",
            "template": "AccountPageTemplate",
            "title": {
                "en": "Update Forgotten Password",
                "ja": "忘れたパスワードの更新",
                "de": "Vergessenes Kennwort erneuern",
                "zh": "更新忘记的密码"
            },
            "typeCode": type,
            "uid": "updatePassword",
            "label": "updatePassword"
        }];
    }
};
