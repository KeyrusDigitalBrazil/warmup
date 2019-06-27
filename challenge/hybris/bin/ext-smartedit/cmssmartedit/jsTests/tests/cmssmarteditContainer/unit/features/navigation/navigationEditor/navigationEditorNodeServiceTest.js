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
describe('navigationEditorNodeService - ', function() {

    var navigationEditorNodeService, navigationNodeRestService, navigationNodeAncestorsRestService;
    var $q;
    var uriParams = {
        siteId: 'siteId',
        catalogId: 'catalogId',
        catalogVersion: 'catalogVersion'
    };

    var node = {
        uid: "1",
        entries: [{
            itemId: "Item-ID-1.1",
            itemType: "CMSLinkComponent",
            navigationNodeUid: "1",
            uid: "1",
            name: "Entry 1.1",
            parent: {
                uid: "bla"
            }
        }, {
            itemId: "Item-ID-1.2",
            itemType: "CMSLinkComponent",
            navigationNodeUid: "1",
            uid: "2",
            name: "Entry 1.2",
            parent: {
                uid: "bla"
            }
        }, {
            itemId: "Item-ID-1.3",
            itemType: "CMSLinkComponent",
            navigationNodeUid: "1",
            uid: "3",
            name: "Entry 1.3",
            parent: {
                uid: "bla"
            }
        }],
        name: "node1",
        nodes: [],
        title: {
            en: "node1_en",
            fr: "node1_fr"
        },
        parentUid: "root",
        position: 0,
        parent: {
            uid: 'root',
        }
    };


    var nodes = [{
        uid: "1",
        entries: [],
        name: "node1",
        title: {
            en: "node1_en",
            fr: "node1_fr"
        },
        parentUid: "root",
        position: 0,
        hasChildren: true,
        hasEntries: true
    }, {
        uid: "2",
        entries: [],
        name: "node2",
        title: {
            en: "node2_en",
            fr: "node2_fr"
        },
        parentUid: "root",
        position: 1,
        hasChildren: true,
        hasEntries: false
    }, {
        uid: "4",
        entries: [],
        name: "node4",
        title: {
            "en": "nodeA",
            "fr": "nodeA"
        },
        parentUid: "1",
        position: 0,
        hasChildren: true,
        hasEntries: true
    }, {
        uid: "5",
        entries: [],
        name: "node5",
        title: {
            "en": "nodeB",
            "fr": "nodeB"
        },
        parentUid: "1",
        position: 1,
        hasChildren: false,
        hasEntries: false
    }, {
        uid: "6",
        entries: [],
        name: "node6",
        title: {
            "en": "nodeC",
            "fr": "nodeC"
        },
        parentUid: "2",
        position: 0,
        hasChildren: false,
        hasEntries: false
    }, {
        uid: "7",
        entries: [],
        name: "node7",
        title: {
            "en": "nodeC",
            "fr": "nodeC"
        },
        parentUid: "1",
        position: 2,
        hasChildren: false,
        hasEntries: false
    }, {
        uid: "8",
        entries: [],
        name: "node8",
        title: {
            "en": "nodeC",
            "fr": "nodeC"
        },
        parentUid: "4",
        position: 0,
        hasChildren: true,
        hasEntries: true
    }, {
        uid: "9",
        entries: [],
        name: "node9",
        title: {
            "en": "nodeC",
            "fr": "nodeC"
        },
        parentUid: "8",
        position: 0,
        hasChildren: false,
        hasEntries: false
    }];

    beforeEach(angular.mock.module('functionsModule'));

    beforeEach(angular.mock.module('navigationEditorNodeServiceModule', function($provide) {
        navigationNodeRestService = jasmine.createSpyObj('navigationNodeRestService', ['get', 'update']);
        $provide.value('navigationNodeRestService', navigationNodeRestService);
    }));

    beforeEach(angular.mock.module('cmsitemsRestServiceModule', function($provide) {
        cmsitemsRestService = jasmine.createSpyObj('cmsitemsRestService', ['getById', 'update']);
        $provide.value('cmsitemsRestService', cmsitemsRestService);
    }));

    beforeEach(inject(function(_navigationEditorNodeService_, _$q_) {
        navigationEditorNodeService = _navigationEditorNodeService_;
        $q = _$q_;
    }));


    describe('updateNavigationNodePosition', function() {

        var PARENT_UUID = "someParentUuuid";
        var UUID_A = "uuid_a";
        var UUID_B = "uuid_b";
        var UUID_C = "uuid_c";

        // create a node that by default points to the parent node returned by 
        // cmsitems getById below, and has some defaults
        function getBaseNode(uuid, position) {
            uuid = uuid || UUID_A;
            if (position === undefined) {
                position = 0;
            }
            return {
                uuid: uuid,
                position: position,
                parent: {
                    uuid: PARENT_UUID
                }
            };
        }

        beforeEach(function() {
            cmsitemsRestService.getById.and.returnValue($q.when({
                uuid: PARENT_UUID,
                children: [UUID_A, UUID_B, UUID_C]
            }));
            cmsitemsRestService.update.and.returnValue($q.when(true));
        });

        it('GIVEN a nodes parent node does not exist WHEN I try to update the position of that node THEN it fails ', function() {
            cmsitemsRestService.getById.and.returnValue($q.reject());
            expect(navigationEditorNodeService.updateNavigationNodePosition(node)).toBeRejected();
        });

        it('WHEN I try to update the position of a node AND the position is undefined THEN it fails ', function() {
            var baseNode = getBaseNode();
            baseNode.position = undefined;
            expect(navigationEditorNodeService.updateNavigationNodePosition(baseNode)).toBeRejected();
        });

        it('GIVEN a node is referencing the wrong parent (parent does not contain the node in its children) WHEN I try to update the position of that node THEN it fails', function() {
            var baseNode = getBaseNode();
            baseNode.uuid = "any value not a uuid in parent";
            expect(navigationEditorNodeService.updateNavigationNodePosition(baseNode)).toBeRejected();
        });

        describe('GIVEN a parent with 3 children A,B,C, with positions 0, 1 and 2 respectively', function() {

            it('WHEN I move A to 1 THEN parents chilren is updated correctly with B,A,C', function() {
                var data = getBaseNode(UUID_A, 1);
                expect(navigationEditorNodeService.updateNavigationNodePosition(data)).toBeResolved();
                expect(cmsitemsRestService.update).toHaveBeenCalledWith(jasmine.objectContaining({
                    children: [UUID_B, UUID_A, UUID_C]
                }));
            });

            it('WHEN I move A to 2 THEN parents chilren is updated correctly with B,C,A', function() {
                var data = getBaseNode(UUID_A, 2);
                expect(navigationEditorNodeService.updateNavigationNodePosition(data)).toBeResolved();
                expect(cmsitemsRestService.update).toHaveBeenCalledWith(jasmine.objectContaining({
                    children: [UUID_B, UUID_C, UUID_A]
                }));
            });

            it('WHEN I move B to same location (1) THEN parents chilren is updated correctly with A,B,C', function() {
                var data = getBaseNode(UUID_B, 1);
                expect(navigationEditorNodeService.updateNavigationNodePosition(data)).toBeResolved();
                expect(cmsitemsRestService.update).toHaveBeenCalledWith(jasmine.objectContaining({
                    children: [UUID_A, UUID_B, UUID_C]
                }));
            });

            it('WHEN I move B to 0 THEN parents chilren is updated correctly with B,A,C', function() {
                var data = getBaseNode(UUID_B, 0);
                expect(navigationEditorNodeService.updateNavigationNodePosition(data)).toBeResolved();
                expect(cmsitemsRestService.update).toHaveBeenCalledWith(jasmine.objectContaining({
                    children: [UUID_B, UUID_A, UUID_C]
                }));
            });

            it('WHEN I move B to 2 THEN parents chilren is updated correctly with A,C,B', function() {
                var data = getBaseNode(UUID_B, 2);
                expect(navigationEditorNodeService.updateNavigationNodePosition(data)).toBeResolved();
                expect(cmsitemsRestService.update).toHaveBeenCalledWith(jasmine.objectContaining({
                    children: [UUID_A, UUID_C, UUID_B]
                }));
            });

            it('WHEN I move C to same location (2) THEN parents chilren is updated correctly with A,B,C', function() {
                var data = getBaseNode(UUID_C, 2);
                expect(navigationEditorNodeService.updateNavigationNodePosition(data)).toBeResolved();
                expect(cmsitemsRestService.update).toHaveBeenCalledWith(jasmine.objectContaining({
                    children: [UUID_A, UUID_B, UUID_C]
                }));
            });

            it('WHEN I move C to 0 THEN parents chilren is updated correctly with C,A,B', function() {
                var data = getBaseNode(UUID_C, 0);
                expect(navigationEditorNodeService.updateNavigationNodePosition(data)).toBeResolved();
                expect(cmsitemsRestService.update).toHaveBeenCalledWith(jasmine.objectContaining({
                    children: [UUID_C, UUID_A, UUID_B]
                }));
            });

            it('WHEN I move C to 1 THEN parents chilren is updated correctly with A,C,B', function() {
                var data = getBaseNode(UUID_C, 1);
                expect(navigationEditorNodeService.updateNavigationNodePosition(data)).toBeResolved();
                expect(cmsitemsRestService.update).toHaveBeenCalledWith(jasmine.objectContaining({
                    children: [UUID_A, UUID_C, UUID_B]
                }));
            });


        });


    });

    it('WHEN the server returns too many nodes for the ancestry THEN the service still filters, orders and formats the list', function() {

        navigationNodeRestService.get.and.returnValue($q.when({
            sompropertyName: nodes
        }));

        expect(navigationEditorNodeService.getNavigationNodeAncestry("8", uriParams)).toBeResolvedWithData([{
            uid: "1",
            entries: [],
            name: "node1",
            title: {
                en: "node1_en",
                fr: "node1_fr"
            },
            parentUid: "root",
            position: 0,
            hasChildren: true,
            hasEntries: true,
            level: 0,
            formattedLevel: 'se.cms.navigationcomponent.management.node.level.root'
        }, {
            uid: '4',
            entries: [],
            name: 'node4',
            title: {
                en: 'nodeA',
                fr: 'nodeA'
            },
            parentUid: '1',
            position: 0,
            hasChildren: true,
            hasEntries: true,
            level: 1,
            formattedLevel: 'se.cms.navigationcomponent.management.node.level.non.root'
        }, {
            uid: '8',
            entries: [],
            name: 'node8',
            title: {
                en: 'nodeC',
                fr: 'nodeC'
            },
            parentUid: '4',
            position: 0,
            hasChildren: true,
            hasEntries: true,
            level: 2,
            formattedLevel: 'se.cms.navigationcomponent.management.node.level.non.root'
        }]);

        expect(navigationNodeRestService.get).toHaveBeenCalledWith({
            ancestorTrailFrom: '8',
            siteId: 'siteId',
            catalogId: 'catalogId',
            catalogVersion: 'catalogVersion'
        });

    });


});
