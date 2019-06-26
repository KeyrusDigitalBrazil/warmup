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
describe("GenericEditor Navigation Node Selector", function() {

    var navigationTree, navigationNodeSelector;

    beforeEach(function() {
        browser.bootstrap(__dirname);
        require("../commonFunctions.js");
        navigationTree = e2e.componentObjects.navigationTree;
        navigationNodeSelector = e2e.componentObjects.navigationNodeSelector;
    });

    describe('GIVEN the NavigationNodeSelector attribute is editable', function() {
        beforeEach(function(done) {
            navigationNodeSelector.prepareApp(done, true);
        });

        it('and the model has a qualifier of type NavigationNodeSelector, WHEN the generic editor has loaded, THEN the field shows a node print, a breadcrumb and a view of the node including the entries', function() {

            navigationNodeSelector.assertOnBreadCrumb([
                ["ROOT", "node1"],
                ["LEVEL 1", "node4"],
                ["LEVEL 2", "node8"]
            ]);

            expect(navigationTree.getChildrenNames()).toEqual(['node9']);

        });

        it('and the model has a qualifier of type NavigationNodeSelector, WHEN the generic editor has loaded, THEN the remove button is enabled', function() {
            expect(navigationNodeSelector.assertRemoveButtonIsEnabled());
        });
    });

    describe('GIVEN the NavigationNodeSelector attribute is not editable', function() {

        beforeEach(function(done) {
            navigationNodeSelector.prepareApp(done, false);
        });

        it('and the model has a qualifier of type NavigationNodeSelector, WHEN the generic editor has loaded, THEN the remove button is disabled', function() {
            expect(navigationNodeSelector.assertRemoveButtonIsDisabled());
        });
    });

    describe("GIVEN the model has a qualifier of type NavigationNodeSelector, WHEN I press remove ", function() {

        beforeEach(function(done) {
            navigationNodeSelector.prepareApp(done, true);
            navigationNodeSelector.pressRemove().then(function() {
                done();
            });
        });

        it('THEN I am presented with an invite to select a node and a node picker not including entries', function(done) {
            expect(navigationTree.getChildrenNames()).toEqual(['node1', 'node2']);
            navigationTree.expand('node1').then(function() {
                expect(navigationTree.getChildrenNames('node1')).toEqual(['node4', 'node5', 'node7']);
                done();
            });
        });

        it('and a new node is selected, THEN we switch back to an update view mode including entries', function(done) {

            navigationTree.expand('node1').then(function() {
                navigationNodeSelector.pickNode('node4');
                navigationNodeSelector.assertOnBreadCrumb([
                    ["ROOT", "node1"],
                    ["LEVEL 1", "node4"]
                ]);
                expect(navigationTree.getChildrenNames()).toEqual(['node8']);
                navigationTree.expand('node8');
                expect(navigationTree.getChildrenNames('node8')).toEqual(['node9']);
                done();
            });

        });

    });

});
