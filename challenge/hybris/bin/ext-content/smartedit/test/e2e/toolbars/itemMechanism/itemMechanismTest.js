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
var itemMechanismPath = 'test/e2e/toolbars/itemMechanism';
var itemMechanismIconPath = '../../' + itemMechanismPath;
var toolbar = require("../../utils/components/WhiteToolbarComponentObject.js");

describe("Configure toolbar", function() {

    describe("through outer toolbarservice", function() {

        beforeEach(function() {
            browser.get(itemMechanismPath);
            browser.driver.manage().timeouts().implicitlyWait(0);
        });

        it("items of type 'ACTION' and 'HYBRID_ACTION' will be added", function() {
            expect(element.all(by.css('.yTemplateToolbar')).count()).toBe(0);
            browser.click(by.id('sendActionsOuter'));
            expect(element.all(by.css('.yTemplateToolbar')).count()).toBe(4);

            expect(element(by.id('toolbar_option_toolbar.action.action5')).getAttribute('alt')).toBe('action5');
            expect(element(by.id('toolbar_option_toolbar.action.action5')).getAttribute('data-ng-src')).toBe(itemMechanismIconPath + '/icon5.png');
            expect(element(by.id('toolbar_option_toolbar.action.action6')).getAttribute('alt')).toBe('action6');
            expect(element(by.id('toolbar_option_toolbar.action.action6')).getAttribute('data-ng-src')).toBe(itemMechanismIconPath + '/icon6.png');
            expect(element(by.id('toolbar_option_toolbar.action.action8_btn')).getAttribute('type')).toBe('button');
            expect(element(by.id('toolbar_option_toolbar.action.action8_btn_iconclass')).getAttribute('class')).toContain('hyicon hyicon-clone se-toolbar-menu-ddlb--button__icon');
            expect(element(by.id('toolbar_option_toolbar.action.action8_btn_lbl')).getText()).toBe('ICON TEST');
        });


        it("item of type 'HYBRID_ACTION' will display its template", function() {
            expect(element.all(by.css('.yTemplateToolbar')).count()).toBe(0);
            browser.click(by.id('sendActionsOuter'));
            expect(element.all(by.css('.yTemplateToolbar')).count()).toBe(4);

            browser.click(by.xpath('//*[contains(@class, "toolbar-action--hybrid")]/button'));
            expect(element(by.id('hybridActiontemplate')).getText()).toBe('HYBRID ACTION TEMPLATE');
        });


        it("Callbacks will be executed successfully when items of type 'ACTION' and 'HYBRID_ACTION", function() {
            browser.click(by.id('sendActionsOuter'));

            browser.click(by.id('toolbar_option_toolbar.action.action5'));
            expect(element(by.id('message')).getText()).toBe('Action 5 called');
            browser.switchToIFrame();
            expect(element(by.id('message')).getText()).toBe('');
            browser.switchToParent();

            browser.click(by.id('toolbar_option_toolbar.action.action6'));
            expect(element(by.id('message')).getText()).toBe('Action 6 called');
            browser.switchToIFrame();
            expect(element(by.id('message')).getText()).toBe('');
        });

        it("item of type 'TEMPLATE' will display its template", function() {
            expect(element.all(by.css('.yTemplateToolbar')).count()).toBe(0);
            browser.click(by.id('sendActionsOuter'));
            expect(element.all(by.css('.yTemplateToolbar')).count()).toBe(4);

            expect(element(by.id('standardTemplate')).getText()).toBe('STANDARD TEMPLATE');
        });

        it('can remove toolbar items', function() {
            // Arrange
            browser.click(by.id('sendActionsOuter'));
            expect(element(by.id('standardTemplate')).isPresent()).toBe(true);
            expect(element(by.css('button img[title="action5"]')).isPresent()).toBe(true);
            expect(element(by.css('button img[title="action6"]')).isPresent()).toBe(true);

            // Act
            browser.click(by.id('removeActionsOuter'));

            // Assert
            browser.waitForAbsence(element(by.id('standardTemplate')));
            browser.waitForAbsence(element(by.css('button img[title="action5"]')));
            expect(element(by.css('button img[title="action6"]')).isPresent()).toBe(true);
        });
    });

    describe("through inner toolbarservice", function() {

        beforeEach(function() {
            browser.get(itemMechanismPath);
        });

        it("items of type 'ACTION' will be added", function() {
            expect(element.all(by.css('.yTemplateToolbar')).count()).toBe(0);
            browser.switchToIFrame();
            browser.click(by.id('sendActionsInner'));
            browser.switchToParent();
            expect(element.all(by.css('.yTemplateToolbar')).count()).toBe(2);

            expect(element(by.id('toolbar_option_toolbar.action.action3')).getAttribute('alt')).toBe('action3');
            expect(element(by.id('toolbar_option_toolbar.action.action4')).getAttribute('alt')).toBe('action4');
        });


        it("Callbacks will be executed successfully when items of type 'ACTION'", function() {
            browser.switchToIFrame();
            browser.click(by.id('sendActionsInner'));

            browser.switchToParent();
            browser.click(by.id('toolbar_option_toolbar.action.action3'));
            expect(element(by.id('message')).getText()).toBe('');
            browser.switchToIFrame(false);
            expect(element(by.id('message')).getText()).toBe('Action 3 called');

            browser.switchToParent();
            browser.click(by.id('toolbar_option_toolbar.action.action4')).then(function() {
                expect(element(by.id('message')).getText()).toBe('');
                browser.switchToIFrame(false);
                expect(element(by.id('message')).getText()).toBe('Action 4 called');
            });
        });

        it(' can remove toolbar items', function() {
            // Arrange
            browser.switchToIFrame();
            browser.click(by.id('sendActionsInner'));

            browser.switchToParent();
            expect(element(by.css('button img[title="action3"]')).isPresent()).toBe(true);
            expect(element(by.css('button img[title="action4"]')).isPresent()).toBe(true);
            browser.switchToIFrame();

            // Act
            browser.click(by.id('removeAction'));

            // Assert
            browser.switchToParent();
            expect(element(by.css('button img[title="action3"]')).isPresent()).toBe(true);
            browser.waitForAbsence(element(by.css('button img[title="action4"]')));
        });
    });


    describe("through inner AND outer toolbarservice", function() {

        beforeEach(function() {
            browser.get(itemMechanismPath);
        });


        it('Actions will not conflict', function() {
            browser.click(by.id('sendActionsOuter'));
            browser.switchToIFrame();
            browser.click(by.id('sendActionsInner'));
            browser.switchToParent();
            expect(element.all(by.css('.yTemplateToolbar')).count()).toBe(6);

            expect(element(by.id('toolbar_option_toolbar.action.action3')).getAttribute('alt')).toBe('action3');
            expect(element(by.id('toolbar_option_toolbar.action.action4')).getAttribute('alt')).toBe('action4');
            expect(element(by.id('toolbar_option_toolbar.action.action5')).getAttribute('alt')).toBe('action5');
            expect(element(by.id('toolbar_option_toolbar.action.action6')).getAttribute('alt')).toBe('action6');
        });

        it('removing items does not conflict with each other', function() {
            // Arrange
            browser.switchToIFrame();
            browser.click(by.id('sendActionsInner'));

            browser.switchToParent();
            browser.click(by.id('sendActionsOuter'));

            expect(element(by.id('standardTemplate')).isPresent()).toBe(true);
            expect(element(by.css('button img[title="action3"]')).isPresent()).toBe(true);
            expect(element(by.css('button img[title="action4"]')).isPresent()).toBe(true);
            expect(element(by.css('button img[title="action5"]')).isPresent()).toBe(true);
            expect(element(by.css('button img[title="action6"]')).isPresent()).toBe(true);
            browser.switchToIFrame();

            // Act
            browser.click(by.id('removeAction'));
            browser.switchToParent();
            browser.click(by.id('removeActionsOuter'));

            // Assert
            browser.waitForAbsence(element(by.id('standardTemplate')));
            expect(element(by.css('button img[title="action3"]')).isPresent()).toBe(true);
            browser.waitForAbsence(element(by.css('button img[title="action4"]')));
            browser.waitForAbsence(element(by.css('button img[title="action5"]')));
            expect(element(by.css('button img[title="action6"]')).isPresent()).toBe(true);
        });

    });

    describe("Toolbar Context", function() {

        beforeEach(function() {
            browser.get(itemMechanismPath);
        });

        it('item will show the toolbar context template provided', function() {

            browser.click(by.id('sendActionsOuter'));
            toolbar.actions.clickShowActionToolbarContext5();
            expect(toolbar.elements.getToolbarItemContextByKey('toolbar.action.action5').isPresent()).toBeTruthy();
            expect(toolbar.elements.getToolbarItemContextTextByKey('toolbar.action.action5')).toBe('Action 5 - Context');

        });

        it('item will show the content of the toolbar context template url provided', function() {

            browser.click(by.id('sendActionsOuter'));
            toolbar.actions.clickShowHybridActionToolbarContext();
            expect(toolbar.elements.getToolbarItemContextByKey('toolbar.action.action6').isPresent()).toBeTruthy();
            expect(toolbar.elements.getToolbarItemContextTextByKey('toolbar.action.action6')).toBe('Hybrid Action 6 - Context');

        });

        it('item will show and hide the content of the toolbar context based on the event', function() {

            browser.click(by.id('sendActionsOuter'));
            toolbar.actions.clickShowHybridActionToolbarContext();

            expect(toolbar.elements.getToolbarItemContextByKey('toolbar.action.action6').isPresent()).toBeTruthy();
            toolbar.actions.clickHideHybridActionToolbarContext().then(function() {
                browser.waitForAbsence(toolbar.elements.getToolbarItemContextByKey('toolbar.action.action6'));

                toolbar.actions.clickShowHybridActionToolbarContext();
                expect(toolbar.elements.getToolbarItemContextByKey('toolbar.action.action6').isPresent()).toBeTruthy();
            });

        });


    });

});
