/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
describe('Test Personalizationsmartedit Segment View Module', function() {
    var mockModules = {};
    setupMockModules(mockModules); // jshint ignore:line

    var $componentController, rootScope;

    var mockSegment1 = {
        code: "testSegment1"
    };
    var mockSegment2 = {
        code: "testSegment2"
    };
    var mockSegment3 = {
        code: "testSegment3"
    };


    beforeEach(module('personalizationsmarteditServicesModule', function($provide) {
        mockModules.personalizationsmarteditRestService = jasmine.createSpyObj('personalizationsmarteditRestService', ['getSegments']);
        $provide.value('personalizationsmarteditRestService', mockModules.personalizationsmarteditRestService);
    }));

    beforeEach(module('personalizationsmarteditSegmentViewModule'));
    beforeEach(inject(function(_$componentController_, _$q_, _$rootScope_) {
        $componentController = _$componentController_;
        rootScope = _$rootScope_;

        mockModules.personalizationsmarteditRestService.getSegments.and.callFake(function() {
            var deferred = _$q_.defer();
            deferred.resolve({
                segments: [mockSegment1, mockSegment2, mockSegment3],
                pagination: {
                    count: 3,
                    page: 0,
                    totalCount: 3,
                    totalPages: 1
                }
            });
            return deferred.promise;
        });

    }));

    describe('Component API', function() {

        it('should have proper api when initialized without parameters', function() {
            var ctrl = $componentController('multiSegmentView', null);

            expect(ctrl.actions).toBeDefined();
            expect(ctrl.treeOptions).toBeDefined();
            expect(ctrl.isScrollZoneVisible).toBeDefined();
            expect(ctrl.getElementToScroll).toBeDefined();
            expect(ctrl.removeItem).toBeDefined();
            expect(ctrl.duplicateItem).toBeDefined();
            expect(ctrl.toggle).toBeDefined();
            expect(ctrl.newSubItem).toBeDefined();
            expect(ctrl.segments).toBeDefined();
            expect(ctrl.segmentPagination).toBeDefined();
            expect(ctrl.segmentFilter).toBeDefined();
            expect(ctrl.segmentSearchInputKeypress).toBeDefined();
            expect(ctrl.segmentSelectedEvent).toBeDefined();
            expect(ctrl.moreSegmentRequestProcessing).toBeDefined();
            expect(ctrl.addMoreSegmentItems).toBeDefined();
            expect(ctrl.isTopContainer).toBeDefined();
            expect(ctrl.isEmptyContainer).toBeDefined();
            expect(ctrl.isItem).toBeDefined();
            expect(ctrl.isContainer).toBeDefined();
            expect(ctrl.isDropzone).toBeDefined();
            expect(ctrl.$onInit).toBeDefined();
            expect(ctrl.expression).not.toBeDefined();

        });

        it('should have proper api when initialized with parameters', function() {
            var bindings = {
                triggers: {}
            };
            var ctrl = $componentController('multiSegmentView', null, bindings);
            ctrl.$onInit();

            expect(ctrl.actions).toBeDefined();
            expect(ctrl.treeOptions).toBeDefined();
            expect(ctrl.isScrollZoneVisible).toBeDefined();
            expect(ctrl.getElementToScroll).toBeDefined();
            expect(ctrl.removeItem).toBeDefined();
            expect(ctrl.duplicateItem).toBeDefined();
            expect(ctrl.toggle).toBeDefined();
            expect(ctrl.newSubItem).toBeDefined();
            expect(ctrl.segments).toBeDefined();
            expect(ctrl.segmentPagination).toBeDefined();
            expect(ctrl.segmentFilter).toBeDefined();
            expect(ctrl.segmentSearchInputKeypress).toBeDefined();
            expect(ctrl.segmentSelectedEvent).toBeDefined();
            expect(ctrl.moreSegmentRequestProcessing).toBeDefined();
            expect(ctrl.addMoreSegmentItems).toBeDefined();
            expect(ctrl.isTopContainer).toBeDefined();
            expect(ctrl.isEmptyContainer).toBeDefined();
            expect(ctrl.isItem).toBeDefined();
            expect(ctrl.isContainer).toBeDefined();
            expect(ctrl.isDropzone).toBeDefined();
            expect(ctrl.$onInit).toBeDefined();
            expect(ctrl.expression).toBeDefined();

        });

    });

    describe('addMoreSegmentItems', function() {

        it('should be defined', function() {
            var ctrl = $componentController('multiSegmentView', null);
            expect(ctrl.addMoreSegmentItems).toBeDefined();
        });

        it('should set properties if called', function() {
            //given
            var ctrl = $componentController('multiSegmentView', null);

            // when
            ctrl.addMoreSegmentItems();
            rootScope.$digest();

            //then
            expect(ctrl.segments).toEqual([mockSegment1, mockSegment2, mockSegment3]);
            expect(ctrl.segmentPagination.count).toBe(3);
            expect(ctrl.segmentPagination.page).toBe(0);
            expect(ctrl.segmentPagination.totalCount).toBe(3);
            expect(ctrl.segmentPagination.totalPages).toBe(1);
        });

    });

    describe('treeOptions', function() {

        it('should be defined with proper values', function() {
            var ctrl = $componentController('multiSegmentView', null);
            expect(ctrl.treeOptions).toBeDefined();
            expect(ctrl.treeOptions.dragStart).toBeDefined();
            expect(ctrl.treeOptions.dropped).toBeDefined();
            expect(ctrl.treeOptions.dragMove).toBeDefined();
        });

    });

    describe('$onInit', function() {

        it('should be defined', function() {
            var ctrl = $componentController('multiSegmentView', null);
            expect(ctrl.$onInit).toBeDefined();
        });

        it('should properly initialize expression if no triggers object', function() {
            // given
            var ctrl = $componentController('multiSegmentView', null);
            ctrl.triggers = [];
            var dropzoneItem = {
                type: 'dropzone'
            };
            var initExpression = [{
                'type': 'container',
                'operation': ctrl.actions[0],
                'nodes': [dropzoneItem]
            }];
            // when
            ctrl.$onInit();

            // then
            expect(ctrl.expression).toEqual(initExpression);
        });

        it('should properly initialize expression if triggers object passed', function() {
            var trigger = {
                type: 'segmentTriggerData',
                code: 'code',
                groupBy: 'OR',
                segments: [{
                    code: 'SegmentA'
                }, {
                    code: 'SegmentB'
                }]
            };
            var data = [{
                type: 'container',
                operation: {
                    id: 'OR',
                    name: 'personalization.modal.customizationvariationmanagement.targetgrouptab.expression.or'
                },
                nodes: [{
                    type: 'item',
                    operation: '',
                    selectedSegment: {
                        code: 'SegmentA'
                    },
                    nodes: []
                }, {
                    type: 'item',
                    operation: '',
                    selectedSegment: {
                        code: 'SegmentB'
                    },
                    nodes: []
                }]
            }];

            // given
            var ctrl = $componentController('multiSegmentView', null);
            ctrl.triggers = [trigger];

            // when
            ctrl.$onInit();

            // then
            expect(ctrl.expression).toEqual(data);
        });

    });

});
