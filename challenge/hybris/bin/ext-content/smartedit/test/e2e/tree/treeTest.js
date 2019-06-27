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
describe('Integration of yTree directive', function() {

    var tree = require("../utils/components/Tree.js");
    var alertsComponent = require("../utils/components/systemAlertsComponentObject");

    beforeEach(function() {
        tree.getAndWaitForWholeApp();
    });

    it('WHEN page is loaded, tree displays its first level children', function() {

        expect(tree.getChildrenNames()).toEqual(['node1', 'node2']);
    });

    it('html outside ytree can have access to the yTree feature', function() {

        browser.click(element(by.id('outsideActionButton')));
        expect(tree.getChildrenNames()).toEqual(['node1', 'node2', 'root2']);

    });

    describe('at 1st level', function() {

        it('will add sibling', function() {

            tree.addSibling('node1');
            expect(tree.getChildrenCount('node1')).toEqual(0);
            expect(tree.getChildrenCount()).toEqual(3);
            tree.getChildrenNames().then(function(children) {
                expect(children.slice(0, 2)).toEqual(['node1', 'node2']);
            });

        });

        it('will remove a node', function() {

            tree.remove('node1');
            expect(tree.getChildrenCount()).toEqual(1);
            tree.getChildrenNames().then(function(children) {
                expect(children).toEqual(['node2']);
            });

        });

        it('will trigger custom action having access to node data', function() {

            tree.clickOnLinkHavingClass('node1', 'glyphicon-th');
            expect(element(by.id('someTreeEditorMessage')).getText()).toBe("edit node1");

        });

        it('WHEN add child node that was never expanded THEN node expands and child is added and it will be stable after collapsing and expanding', function() {

            tree.addChild('node1');
            expect(tree.getChildrenCount()).toEqual(2);
            expect(tree.getChildrenCount('node1')).toEqual(4);
            tree.getChildrenNames('node1').then(function(children) {
                expect(children.slice(0, 2)).toEqual(['node4', 'node5']);
            });
            tree.collapse('node1');
            tree.expand('node1');
            expect(tree.getChildrenCount('node1')).toEqual(4);
            tree.getChildrenNames('node1').then(function(children) {
                expect(children.slice(0, 2)).toEqual(['node4', 'node5']);
            });

        });

        it('WHEN add child to first level node that is expanded THEN child is added and children are the same after collapsing and expanding', function() {

            tree.expand('node1');
            tree.addChild('node1');
            expect(tree.getChildrenCount()).toEqual(2);
            expect(tree.getChildrenCount('node1')).toEqual(4);
            tree.getChildrenNames('node1').then(function(children) {
                expect(children.slice(0, 2)).toEqual(['node4', 'node5']);
            });
            tree.collapse('node1');
            tree.expand('node1');
            expect(tree.getChildrenCount('node1')).toEqual(4);
            tree.getChildrenNames('node1').then(function(children) {
                expect(children.slice(0, 2)).toEqual(['node4', 'node5']);
            });

        });

        it('WHEN I drag a node to position 0, a confirmation modal will pop-up as configured in the beforeDropCallback', function() {
            tree.expand('node1').then(function() {
                tree.startDraggingNode('node5').then(function() {
                    tree.mouseUp().then(function() {
                        expect(tree.modalDescription().getAttribute('innerHTML')).toBe('se.tree.confirm.message');
                    });
                });
            });
        });

        it('WHEN I confirm the modal after being prompted, the modification will happen', function() {
            tree.expand('node1').then(function() {
                tree.getChildrenNames('node1').then(function(children) {
                    expect(children[0]).toBe('node4');
                    expect(children[1]).toBe('node5');
                    expect(children[2]).toBe('node3');
                    tree.startDraggingNode('node3').then(function() {
                        tree.moveMouseToNodeOffsetUp('node5').then(function() {
                            tree.mouseUp().then(function() {
                                tree.confirmModal().then(function() {
                                    tree.confirmModal().then(function() {
                                        tree.getChildrenNames('node1').then(function(children) {
                                            expect(children[0]).toBe('node4');
                                            expect(children[1]).toBe('node3');
                                            expect(children[2]).toBe('node5');
                                        });
                                    });
                                });
                            });
                        });
                    });
                });
            });
        });

        it('WHEN I cancel the modal after being prompted, the modification will be reverted', function() {
            tree.expand('node1').then(function() {
                tree.getChildrenNames('node1').then(function(children) {
                    expect(children[0]).toBe('node4');
                    expect(children[1]).toBe('node5');
                    expect(children[2]).toBe('node3');
                    tree.startDraggingNode('node4').then(function() {
                        tree.moveMouseToNode('node5').then(function() {
                            tree.mouseUp().then(function() {
                                tree.cancelModal().then(function() {
                                    tree.getChildrenNames('node1').then(function(children) {
                                        expect(children[0]).toBe('node4');
                                        expect(children[1]).toBe('node5');
                                        expect(children[2]).toBe('node3');
                                    });
                                });
                            });
                        });
                    });
                });
            });
        });

        it('WHEN I drag a node to position >1, the modification will not happen as configured in the beforeDropCallback', function() {
            tree.expand('node1').then(function() {
                tree.getChildrenNames('node1').then(function(children) {
                    expect(children[0]).toBe('node4');
                    expect(children[1]).toBe('node5');
                    expect(children[2]).toBe('node3');
                    tree.startDraggingNode('node5').then(function() {
                        tree.moveMouseToNodeOffsetDown('node3').then(function() {
                            tree.mouseUp().then(function() {
                                alertsComponent.assertions.assertTotalNumberOfAlerts(1);
                                tree.getChildrenNames('node1').then(function(children) {
                                    expect(children[0]).toBe('node4');
                                    expect(children[1]).toBe('node5');
                                    expect(children[2]).toBe('node3');
                                });
                            });
                        });
                    });
                });

            });
        });

        it('WHEN I drag a node to a new parent, it will not allow this operation because of allowDropCallback', function() {
            tree.expand('node1').then(function() {
                tree.getChildrenNames('node1').then(function(children) {
                    expect(children[0]).toBe('node4');
                    expect(children[1]).toBe('node5');
                    expect(children[2]).toBe('node3');
                    tree.startDraggingNode('node3').then(function() {
                        tree.moveMouseToNode('node2').then(function() {
                            tree.mouseUp().then(function() {
                                tree.getChildrenNames('node1').then(function(children) {
                                    expect(children[0]).toBe('node4');
                                    expect(children[1]).toBe('node5');
                                    expect(children[2]).toBe('node3');
                                });
                            });
                        });
                    });
                });

            });
        });

        it('WHEN I drag and drop a node without any failures from beforeDropCallback or acceptDropCallback, the dropCallback action is triggered', function() {
            tree.expand('node1').then(function() {
                tree.startDraggingNode('node4').then(function() {
                    tree.mouseUp().then(function() {
                        expect(tree.modalDescription().getAttribute('innerHTML')).toBe('dropped node');
                    });
                });
            });
        });

    });

    describe('at 2nd level', function() {

        var newNodeName;

        beforeEach(function() {
            tree.expand('node1');
            tree.addChild('node1');
            tree.getNthChild('node1', 3).then(function(_newNodeName) {
                newNodeName = _newNodeName;
            });

        });

        it('will add sibling', function() {

            tree.addSibling(newNodeName);
            expect(tree.getChildrenCount('node1')).toEqual(5);
            tree.getChildrenNames('node1').then(function(children) {
                expect(children.slice(0, 4)).toEqual(['node4', 'node5', 'node3', newNodeName]);
            });

        });

        it('will remove a node', function() {
            tree.remove(newNodeName);
            expect(tree.getChildrenCount('node1')).toEqual(3);
            tree.getChildrenNames('node1').then(function(children) {
                expect(children).toEqual(['node4', 'node5', 'node3']);
            });

        });

        it('will trigger custom action having access to node data', function() {

            tree.clickOnLinkHavingClass(newNodeName, 'glyphicon-th');
            expect(element(by.id('someTreeEditorMessage')).getText()).toBe("edit " + newNodeName);

        });

        it('WHEN add child to node that is expanded THEN child is added and children are the same after collapsing and expanding', function() {
            var subChild;

            tree.addChild(newNodeName);
            expect(tree.getChildrenCount(newNodeName)).toEqual(1);
            expect(tree.getChildrenCount('node1')).toEqual(4);
            expect(tree.getChildrenCount()).toEqual(2);
            tree.getChildrenNames(newNodeName).then(function(children) {
                subChild = children[0];

                tree.collapse(newNodeName);
                tree.expand(newNodeName);
                expect(tree.getChildrenCount(newNodeName)).toEqual(1);
                expect(tree.getChildrenCount('node1')).toEqual(4);
                expect(tree.getChildrenCount()).toEqual(2);
                tree.getChildrenNames(newNodeName).then(function(children) {
                    expect(children[0]).toBe(subChild);
                });

            });


        });

    });

});
