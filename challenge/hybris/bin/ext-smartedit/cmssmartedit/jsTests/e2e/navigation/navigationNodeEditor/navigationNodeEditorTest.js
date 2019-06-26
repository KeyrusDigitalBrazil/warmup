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

describe('Navigation Node Editor - ', function() {

    var navigationNodeEditorModule = e2e.pageObjects.navigationNodeEditor;
    var navigationTree = e2e.componentObjects.navigationTree;
    var confirmationModal = e2e.componentObjects.confirmationModal;

    beforeEach(function() {
        browser.bootstrap(__dirname);
    });

    var navigationNodeEditor = new navigationNodeEditorModule();

    var EDIT = "Edit";
    var MOVE_UP = "Move Up";
    var MOVE_DOWN = "Move Down";
    var DELETE = "Delete";
    var ADD_A_SIBLING = "Add a Sibling";
    var ADD_A_CHILD = "Add a Child";

    beforeEach(function(done) {
        return navigationTree.navigateToFirstCatalogNavigationEditor().then(function() {
            done();
        });
    });

    describe('Add Nodes- ', function() {

        it('GIVEN I am on the navigation editor of the first catalog WHEN the I click on "Add New Top Level" THEN I expect to see a modal to add a node and the node is created when SAVE is clicked', function() {
            // GIVEN
            navigationTree.assertNodeHasOrderedChildren(undefined, ['node1', 'node2']);

            // WHEN
            navigationTree.clickAddNewTopLevel();
            navigationNodeEditor.editNodeName('new node name');
            navigationNodeEditor.editNodeTitle('new node title');
            navigationNodeEditor.clickSaveButton();

            // THEN
            navigationTree.assertNodeHasOrderedChildren(undefined, ['node1', 'node2', 'new node name']);
        });

        it('GIVEN I am on the navigation editor of the first catalog WHEN the I click on "Add a Sibling" on the top level nodes THEN I expect to see a modal to add a node and the node is not created on the top level', function() {
            // GIVEN
            navigationTree.assertNodeHasOrderedChildren(undefined, ['node1', 'node2']);

            // WHEN
            navigationTree.clickMoreMenu('node1');
            navigationTree.clickMoreMenuItem('node1', ADD_A_SIBLING);
            navigationNodeEditor.editNodeName('new node name');
            navigationNodeEditor.editNodeTitle('new node title');
            navigationNodeEditor.clickSaveButton();

            // THEN
            navigationTree.assertNodeHasOrderedChildren(undefined, ['node1', 'node2', 'new node name']);
        });

        it('GIVEN I am on the navigation editor of the first catalog WHEN the I click on "Add a Child" on the top level ' +
            'node has children and is expanded THEN I expect to see a modal to add a node and the node is created under the parent',
            function() {

                navigationTree.expand('node1');
                navigationTree.assertNodeHasOrderedChildren('node1', ['node4', 'node5', 'node7']);
                navigationTree.clickMoreMenu('node1');
                navigationTree.clickMoreMenuItem('node1', ADD_A_CHILD);

                navigationNodeEditor.editNodeName('new node name');
                navigationNodeEditor.editNodeTitle('new node title');
                navigationNodeEditor.clickSaveButton();

                navigationTree.assertNodeHasOrderedChildren('node1', ['node4', 'node5', 'node7', 'new node name']);
            });

        it('GIVEN I am on the navigation editor of the first catalog WHEN the I click on "Add a Child" on a node has no children ' +
            'THEN I expect to see a modal to add a node and the node is created under the parent and the parent is expanded',
            function() {

                navigationTree.clickAddNewTopLevel();
                navigationNodeEditor.editNodeName('new node name');
                navigationNodeEditor.editNodeTitle('new node title');
                navigationNodeEditor.clickSaveButton();

                navigationTree.clickMoreMenu('new node name');
                navigationTree.clickMoreMenuItem('new node name', ADD_A_CHILD);

                navigationNodeEditor.editNodeName('new child node name');
                navigationNodeEditor.editNodeTitle('new child node title');
                navigationNodeEditor.clickSaveButton();

                navigationTree.assertNodeHasOrderedChildren('new node name', ['new child node name']);

            });

    });

    describe('Deleting - ', function() {
        it('should delete node4 from the tree', function() {
            navigationTree.expand('node1');
            navigationTree.assertNodeHasOrderedChildren('node1', ['node4', 'node5', 'node7']);

            navigationTree.clickMoreMenu('node4');
            navigationTree.clickMoreMenuItem('node4', DELETE);
            navigationTree.clickOnDeleteConfirmation('Ok');

            navigationTree.assertNodeHasOrderedChildren('node1', ['node5', 'node7']);
        });
    });

    describe('Moving - ', function() {

        beforeEach(function(done) {
            navigationTree.expand('node1').then(function() {
                done();
            });
        });

        it('WHEN the More Menu "Move Down" button is clicked on node4, THEN the node should be moved down', function() {
            navigationTree.clickMoreMenu('node4');
            navigationTree.clickMoreMenuItem('node4', MOVE_DOWN);

            navigationTree.assertNodeHasOrderedChildren('node1', ['node5', 'node4', 'node7']);
        });

        it('WHEN the More Menu "Move Up" button is clicked on node7, THEN the node should be moved up.', function() {
            navigationTree.clickMoreMenu('node7');
            navigationTree.clickMoreMenuItem('node7', MOVE_UP);

            navigationTree.assertNodeHasOrderedChildren('node1', ['node4', 'node7', 'node5']);
        });

    });

});
