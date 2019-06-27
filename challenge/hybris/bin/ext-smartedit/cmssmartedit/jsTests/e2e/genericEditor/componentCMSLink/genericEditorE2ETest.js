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
describe('CMSLinkComponent type', function() {
    var cmsLinkComponent = e2e.componentObjects.cmsLink;

    beforeEach(function() {
        require("../commonFunctions.js");
    });

    /**
     * One product catalog by default in e2e tests
     */
    afterEach(function() {
        setReturnOneCatalog(true);
    });

    describe('Content Page mode', function() {
        beforeEach(function() {
            browser.bootstrap(__dirname);
            opendEditorModal();
        });

        it('GIVEN CMSLinkComponent AND component name is entered WHEN Content mode is selected THEN Content Page field is present and populated AND the component name is saved',
            function() {
                //GIVEN
                var urlLinkName = 'TEST NAME';
                cmsLinkComponent.actions.enterComponentName(urlLinkName);

                //WHEN
                cmsLinkComponent.actions.chooseMode('Content');

                // THEN
                cmsLinkComponent.assertions.contentPageIsPresent();
                cmsLinkComponent.assertions.contentPageIsPopulated();
                cmsLinkComponent.assertions.componentNameContainsText(urlLinkName);
            });
    });

    describe('External Link mode', function() {
        beforeEach(function() {
            browser.bootstrap(__dirname);
            opendEditorModal();
        });

        it('GIVEN CMSLinkComponent WHEN External mode is selected THEN External Link field is present',
            function() {
                //WHEN
                cmsLinkComponent.actions.chooseMode('External');

                // THEN
                cmsLinkComponent.assertions.externalLinkIsPresent();
            });

        it('GIVEN CMSLinkComponent AND External mode is selected AND the external url link is entered WHEN I select another mode AND go back to External mode THEN the external url link should be empty',
            function() {
                //GIVEN
                cmsLinkComponent.actions.chooseMode('External');
                cmsLinkComponent.actions.enterExternalLinkUrlField('some url');

                //WHEN
                cmsLinkComponent.actions.chooseMode('Content');
                cmsLinkComponent.actions.chooseMode('External');

                //THEN
                cmsLinkComponent.assertions.externalLinkIsEmpty();
            });
    });

    describe('Product mode', function() {
        beforeEach(function() {
            browser.bootstrap(__dirname).then(function() {
                setReturnOneCatalog(false);
                opendEditorModal();
            });
            browser.waitForPresence(by.css('generic-editor'));
        });

        it('GIVEN CMSLinkComponent AND there are more than one product catalog WHEN Product mode is selected THEN Product Catalog AND Product fields are visible AND Product Catalog is not selected AND Product is not populated',
            function() {
                // WHEN
                cmsLinkComponent.actions.chooseMode('Product');

                // THEN
                cmsLinkComponent.assertions.productCatalogSelectorIsPresent();
                cmsLinkComponent.assertions.productIsPresent();
            });
    });

    describe('Category mode', function() {
        beforeEach(function() {
            browser.bootstrap(__dirname).then(function() {
                setReturnOneCatalog(false);
                opendEditorModal();
            });
            browser.waitForPresence(by.css('generic-editor'));
        });

        it('GIVEN CMSLinkComponent AND there are more than one product catalog WHEN Category mode is selected THEN Product Catalog AND Product fields are visible AND Product Catalog is not selected AND Category is not populated',
            function() {
                // WHEN
                cmsLinkComponent.actions.chooseMode('Category');

                // THEN
                cmsLinkComponent.assertions.productCatalogSelectorIsPresent();
                cmsLinkComponent.assertions.categoryIsPresent();
            });
    });


    function opendEditorModal() {
        return browser.click('#openCMSLinkComponentEditor');
    }
});
