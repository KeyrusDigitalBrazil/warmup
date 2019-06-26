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
describe('singleActiveCatalogAwareSelector', function() {

    // ---------------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------------
    var SELECTOR_ID = "some selector id";
    var QUALIFIER = "some qualifier id";
    var CONTEXT_SITE_ID = "some site ID";

    var SELECTOR_TYPE = {
        PRODUCT_SELECTOR: 'SingleOnlineProductSelector',
        CATEGORY_SELECTOR: 'SingleOnlineCategorySelector',
        INVALID: 'something invalid'
    };

    var PRODUCT_CATALOG_ID = "some product catalog id";
    var PRODUCT_CATALOG_ID_2 = "some product catalog id 2";
    var PRODUCT_CATALOG_NAME = "some product catalog name";
    var PRODUCT_CATALOG_NAME_2 = "some product catalog name 2";
    var ITEM_TYPE_MISSING = "Item type not supported";

    var $compile, $q;
    var element, scope, productCatalogs, catalogService;

    // ---------------------------------------------------------------------------------
    // Test Setup 
    // ---------------------------------------------------------------------------------
    beforeEach(angular.mock.module('cmssmarteditContainerTemplates'));
    beforeEach(angular.mock.module('pascalprecht.translate', function($translateProvider, $provide) {
        $translateProvider.translations('en', {
            'ctrl.mainDropDownI18nKey': 'Item',
            'ctrl.catalogName': 'Product Catalog',
            'se.cms.catalogawareitem.itemtype.notsupported': ITEM_TYPE_MISSING
        });
        $translateProvider.preferredLanguage('en');

        var l10nFilter = jasmine.createSpy('l10nFilter');
        $provide.value('l10nFilter', l10nFilter);
    }));

    beforeEach(angular.mock.module('resourceLocationsModule', function($provide) {
        $provide.constant("CONTEXT_SITE_ID", CONTEXT_SITE_ID);
    }));

    beforeEach(angular.mock.module('singleActiveCatalogAwareItemSelectorModule', function($provide, $compileProvider) {
        // Product Catalogs 
        productCatalogs = [];
        addProductCatalog(PRODUCT_CATALOG_ID, PRODUCT_CATALOG_NAME);
        catalogService = jasmine.createSpyObj('catalogService', ['getProductCatalogsForSite']);
        catalogService.getProductCatalogsForSite.and.callFake(function() {
            return $q.when(productCatalogs);
        });

        $compileProvider.directive('seDropdown', function() {
            var def = {
                restrict: 'E',
                template: '<div>this is a mock</div>',
            };
            return def;
        });
        $provide.value('CONTEXT_SITE_ID', CONTEXT_SITE_ID);
        $provide.value('catalogService', catalogService);
    }));

    beforeEach(inject(function(_$compile_, _$q_, $rootScope) {
        $compile = _$compile_;
        $q = _$q_;

        scope = $rootScope.$new();
        window.smarteditJQuery.extend(scope, {
            id: SELECTOR_ID,
            qualifier: QUALIFIER,
            field: {
                editable: true
            },
            editor: {
                pristine: {

                }
            },
            model: {

            }
        });
    }));

    // ---------------------------------------------------------------------------------
    // Tests
    // ---------------------------------------------------------------------------------
    it('WHEN singleActiveCatalogAwareSelector renders THEN it initializes properly', function() {
        // WHEN
        renderCatalogAwareItemSelector(SELECTOR_TYPE.CATEGORY_SELECTOR);

        // THEN
        expect(catalogService.getProductCatalogsForSite).toHaveBeenCalledTimes(1);
        expect(catalogService.getProductCatalogsForSite).toHaveBeenCalledWith(CONTEXT_SITE_ID);
    });

    it('GIVEN only one product catalog WHEN singleActiveCatalogAwareSelector renders THEN it does not display the catalog selector', function() {
        // GIVEN

        // WHEN 
        renderCatalogAwareItemSelector(SELECTOR_TYPE.CATEGORY_SELECTOR);

        // THEN 
        assertProductCatalogSelectorIsNotDisplayed();
        assertItemTypeNotSupportedIsMissing();
    });

    it('GIVEN more than one product catalog WHEN singleActiveCatalogAwareSelector renders THEN it does displays the catalog selector', function() {
        // GIVEN
        addProductCatalog(PRODUCT_CATALOG_ID_2, PRODUCT_CATALOG_NAME_2);

        // WHEN 
        renderCatalogAwareItemSelector(SELECTOR_TYPE.CATEGORY_SELECTOR);

        // THEN 
        assertProductCategorySelectorIsDisplayed();
        assertItemTypeNotSupportedIsMissing();
    });

    it('GIVEN a category selector WHEN singleActiveCatalogAwareSelector renders THEN it displays an se selector to choose a category', function() {
        // GIVEN

        // WHEN 
        renderCatalogAwareItemSelector(SELECTOR_TYPE.CATEGORY_SELECTOR);

        // THEN 
        assertItemSelectorWithRightPropertyTypeIsDisplayed(SELECTOR_TYPE.CATEGORY_SELECTOR);
        assertItemTypeNotSupportedIsMissing();
    });

    it('GIVEN a product selector WHEN singleActiveCatalogAwareSelector renders THEN it displays an se selector to choose a product', function() {
        // GIVEN

        // WHEN 
        renderCatalogAwareItemSelector(SELECTOR_TYPE.PRODUCT_SELECTOR);

        // THEN 
        assertItemSelectorWithRightPropertyTypeIsDisplayed(SELECTOR_TYPE.PRODUCT_SELECTOR);
        assertItemTypeNotSupportedIsMissing();
    });

    it('GIVEN an item with an invalid type singleActiveCatalogAwareSelector renders THEN it must display an error message', function() {
        // GIVEN

        // WHEN 
        renderCatalogAwareItemSelector(SELECTOR_TYPE.INVALID);

        // THEN 
        assertProductCatalogSelectorIsNotDisplayed();
        assertItemSelectorIsNotDisplayed();
        assertItemTypeNotSupportedIsDisplayed();
    });

    // ---------------------------------------------------------------------------------
    // Helper Functions
    // ---------------------------------------------------------------------------------
    function addProductCatalog(productCatalogId, productCatalogName) {
        productCatalogs.push({
            catalogId: productCatalogId,
            name: productCatalogName
        });
    }

    function renderCatalogAwareItemSelector(selectorType) {
        scope.field.cmsStructureType = selectorType;
        element = $compile('<single-active-catalog-aware-item-selector ' +
            'id="id"' +
            'data-field="field"' +
            'data-model="model"' +
            'data-qualifier="qualifier"' +
            'data-editor="editor"></single-active-catalog-aware-item-selector>')(scope);
        scope.$digest();
    }

    function getProductCatalogSelector() {
        return element.find("#product-catalog se-dropdown");
    }

    function assertProductCatalogSelectorIsNotDisplayed() {
        var productSelector = getProductCatalogSelector();
        expect(productSelector).not.toExist();
    }

    function assertProductCategorySelectorIsDisplayed() {
        var productSelector = getProductCatalogSelector();
        expect(productSelector).toExist();
        expect(productSelector.length).toBe(1);
    }

    function getItemSelector() {
        return element.find("[data-id='se-items-selector-dropdown']");
    }

    function getPropertyTypeForCmsType(cmsType) {
        return (cmsType === SELECTOR_TYPE.PRODUCT_SELECTOR) ? 'product' : 'category';
    }

    function assertItemSelectorWithRightPropertyTypeIsDisplayed(selectorCmsType) {
        var itemSelector = getItemSelector();
        expect(itemSelector).toExist();

        var itemSelectorCtrl = itemSelector.scope().$ctrl;
        expect(itemSelectorCtrl.field.propertyType).toBe(getPropertyTypeForCmsType(selectorCmsType));
    }

    function assertItemSelectorIsNotDisplayed() {
        var itemSelector = getItemSelector();
        expect(itemSelector).not.toExist();
    }

    function getItemTypeNotSupportedElement() {
        return element.find('.product-catalog-item-type-missing');
    }

    function assertItemTypeNotSupportedIsMissing() {
        var msgElement = getItemTypeNotSupportedElement();
        expect(msgElement).not.toExist();
    }

    function assertItemTypeNotSupportedIsDisplayed() {
        var msgElement = getItemTypeNotSupportedElement();
        expect(msgElement).toExist();
        expect(msgElement.text()).toBe(ITEM_TYPE_MISSING);
    }
});
