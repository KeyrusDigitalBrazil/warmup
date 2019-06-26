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
var navigationTree = e2e.componentObjects.navigationTree;

describe('navigation Editor - ', function() {

    var ADD_NEW_TOP_LEVEL_BUTTON = 'Add New Top Level';
    var sitesLink = e2e.componentObjects.sitesLink;

    beforeEach(function(done) {
        browser.bootstrap(__dirname).then(function() {
            navigationTree.navigateToFirstCatalogNavigationEditor().then(function() {
                done();
            });
        });
    });

    it("GIVEN I am on the navigation editor of the first catalog WHEN I expand the first node THEN both the children nodes and entries should be displayed", function() {
        navigationTree.expand('node1');
        navigationTree.assertNodeHasOrderedChildren('node1', ['node4', 'node5', 'node7']);
    });

    it("GIVEN I am on the navigation editor of the first catalog WHEN I expand the first node THEN then the title of all true nodes are displayed", function() {
        navigationTree.expand('node1');
        navigationTree.getNodeTitles(['node4', 'node5', 'node7']).then(function(titles) {
            expect(titles).toEqual(['node4_en', 'node5_en', 'node7_en']);
        });
    });

    it('GIVEN I am on the navigation editor of the first catalog WHEN I select the more menu for the first node THEN a dropdown should be displayed with 5 options (with missing MOVE UP)', function() {

        navigationTree.expand('node1');
        navigationTree.clickMoreMenu('node4');
        expect(navigationTree.getMoreMenuOptionsCount('node4')).toEqual(5);
        navigationTree.getMoreMenuOptions('node4').then(function(options) {
            expect(options).toEqual(['Edit', 'Delete', 'Move Down', 'Add a Child', 'Add a Sibling']);
        });

    });

    //more menu options list
    it('GIVEN I am on the navigation editor of the first catalog WHEN I select the more menu for the node middle in the stack THEN a dropdown should be displayed with 6 options', function() {

        navigationTree.expand('node1');
        navigationTree.clickMoreMenu('node5');
        expect(navigationTree.getMoreMenuOptionsCount('node5')).toEqual(6);
        navigationTree.getMoreMenuOptions('node5').then(function(options) {
            expect(options).toEqual(['Edit', 'Delete', 'Move Up', 'Move Down', 'Add a Child', 'Add a Sibling']);
        });

    });

    it('GIVEN I am on the navigation editor of the first catalog WHEN I select the more menu for the last node THEN a dropdown should be displayed with 5 options (with missing MOVE DOWN)', function() {

        navigationTree.expand('node1');
        navigationTree.clickMoreMenu('node7');
        expect(navigationTree.getMoreMenuOptionsCount('node7')).toEqual(5);
        navigationTree.getMoreMenuOptions('node7').then(function(options) {
            expect(options).toEqual(['Edit', 'Delete', 'Move Up', 'Add a Child', 'Add a Sibling']);
        });

    });

    //move up and down
    it('GIVEN I am on the navigation editor of the first catalog WHEN I select "Move Up" from the more menu of node5 THEN it should be moved up the order', function() {

        navigationTree.expand('node1');
        navigationTree.assertNodeHasOrderedChildren('node1', ['node4', 'node5', 'node7']);
        navigationTree.clickMoreMenu('node5');
        navigationTree.clickMoreMenuItem('node5', 'Move Up');
        navigationTree.assertNodeHasOrderedChildren('node1', ['node5', 'node4', 'node7']);
    });

    it('GIVEN I am on the navigation editor of the first catalog WHEN I select "Move Down" from the more menu of node5 THEN it should be moved down the order', function() {

        navigationTree.expand('node1');
        navigationTree.assertNodeHasOrderedChildren('node1', ['node4', 'node5', 'node7']);

        navigationTree.clickMoreMenu('node5');
        navigationTree.clickMoreMenuItem('node5', 'Move Down');

        navigationTree.assertNodeHasOrderedChildren('node1', ['node4', 'node7', 'node5']);
    });

    //delete
    it('GIVEN I am on the navigation editor of the first catalog WHEN I select "Delete" from the more menu of node4 THEN it should be deleted when the user presses "Ok" in the confirmation modal', function() {

        navigationTree.expand('node1');
        navigationTree.assertNodeHasOrderedChildren('node1', ['node4', 'node5', 'node7']);

        navigationTree.clickMoreMenu('node4');
        navigationTree.clickMoreMenuItem('node4', 'Delete');

        expect(navigationTree.getDeleteConfirmationMessage()).toBe('Deleting a node will delete all its child nodes and entries');
        navigationTree.clickOnDeleteConfirmation('Ok');

        navigationTree.assertNodeHasOrderedChildren('node1', ['node5', 'node7']);
    });

    it('GIVEN I am on the navigation editor of the first catalog WHEN I select "Delete" from the more menu of node4 THEN it should not be deleted when the user presses "Cancel" in the confirmation modal', function() {

        navigationTree.expand('node1');
        navigationTree.assertNodeHasOrderedChildren('node1', ['node4', 'node5', 'node7']);

        navigationTree.clickMoreMenu('node4');
        navigationTree.clickMoreMenuItem('node4', 'Delete');

        expect(navigationTree.getDeleteConfirmationMessage()).toBe('Deleting a node will delete all its child nodes and entries');
        navigationTree.clickOnDeleteConfirmation('Cancel');

        navigationTree.assertNodeHasOrderedChildren('node1', ['node4', 'node5', 'node7']);
    });

    it('GIVEN I am on the navigation editor of the first catalog WHEN I drag and drop a node on the same level, its position will be updated', function() {
        navigationTree.expand('node1');
        navigationTree.startDraggingNode('node7').then(function() {
            navigationTree.moveMouseToNode('node4').then(function() {
                navigationTree.mouseUp().then(function() {
                    navigationTree.assertNodeHasOrderedChildren('node1', ['node7', 'node4', 'node5']);
                });
            });
        });

    });

    it('GIVEN I am on the navigation editor of the first catalog WHEN I drag and drop a node to a new parent, I will be prompted with a modal confirmation', function() {
        navigationTree.expand('node2');
        navigationTree.startDraggingNode('node6').then(function() {
            navigationTree.moveMouseToNode('node1').then(function() {
                navigationTree.mouseUp().then(function() {
                    expect(navigationTree.modalIsOpen()).toBe(true);
                });
            });
        });
    });

    it('GIVEN I am on the navigation editor of the first catalog WHEN I drag and drop a node to a new parent and confirm the modal, its position will be unchanged', function() {
        navigationTree.expand('node1');
        navigationTree.expand('node2');
        navigationTree.startDraggingNode('node4').then(function() {
            navigationTree.moveMouseToNode('node6').then(function() {
                navigationTree.mouseUp().then(function() {
                    navigationTree.assertNodeHasOrderedChildren('node1', ['node4', 'node5', 'node7']);
                    navigationTree.assertNodeHasOrderedChildren('node2', ['node6']);
                });
            });
        });
    });

    it('GIVEN I am on the navigation editor of the first catalog WHEN I click on Sites link THEN I expect to go to sites page', function() {
        sitesLink.actions.openSitesPage();
        sitesLink.assertions.waitForUrlToMatch();
    });
});
