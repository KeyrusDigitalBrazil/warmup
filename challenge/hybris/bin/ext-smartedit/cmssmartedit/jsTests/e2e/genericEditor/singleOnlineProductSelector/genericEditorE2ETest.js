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
describe('Single Online Product Selector', function() {
    var component = e2e.componentObjects.singleProductCatalogAwareSelector;

    beforeEach(function() {
        require("../commonFunctions.js");
    });

    /**
     * One product catalog by default in e2e tests
     */
    afterEach(function() {
        setReturnOneCatalog(true);
    });

    describe('Component with one catalog', function() {
        beforeEach(function() {
            browser.bootstrap(__dirname).then(function() {
                setReturnOneCatalog(true);
            });
        });

        it('WHEN there is only one product catalog THEN Product Catalog selector is not present AND Product catalog name is displayed AND Product field is populated', function() {
            component.assertions.productCatalogLabelHasText('Apparel Product Catalog');
            component.assertions.productIsPopulated();
        });
    });

    describe('Component with more than one catalog', function() {
        beforeEach(function() {
            browser.bootstrap(__dirname).then(function() {
                setReturnOneCatalog(false);
            });
        });

        it('WHEN there are more than one product catalog THEN Product Catalog AND Product fields are visible AND Product Catalog is not selected AND Product is not populated',
            function() {
                component.assertions.productCatalogSelectorIsPresent();
                component.assertions.productCatalogIsNotSelected();
                component.assertions.productIsPresent();
                component.assertions.productIsNotPopulated();
            });

        it('WHEN there are more than one product catalog WHEN Product Catalog is selected THEN Product field is populated',
            function() {
                component.actions.selectProductCatalog().then(function() {
                    component.assertions.productIsPopulated();
                });
            });

    });

});
