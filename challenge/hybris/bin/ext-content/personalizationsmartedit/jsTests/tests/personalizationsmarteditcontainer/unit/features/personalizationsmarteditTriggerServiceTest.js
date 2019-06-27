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
describe('personalizationsmarteditTriggerService', function() {
    var mockModules = {};
    setupMockModules(mockModules); // jshint ignore:line

    var triggerService, scope;

    var container = {
        type: 'container',
        nodes: [{}]
    };
    var item = {
        type: 'item',
        selectedSegment: {}

    };
    var emptyContainer = {
        type: 'container',
        nodes: []
    };
    var dropzone = {
        type: 'dropzone'
    };


    beforeEach(module('personalizationsmarteditManageCustomizationViewModule'));
    beforeEach(inject(function(_$rootScope_, _$q_, _personalizationsmarteditTriggerService_) {
        scope = _$rootScope_.$new();
        triggerService = _personalizationsmarteditTriggerService_;
    }));

    describe('isContainer', function() {

        it('should be defined', function() {
            expect(triggerService.isContainer).toBeDefined();
        });

        it('should return true for a container', function() {
            expect(triggerService.isContainer(container)).toBe(true);
        });

        it('should return false for not a container', function() {
            expect(triggerService.isContainer(item)).toBe(false);
            expect(triggerService.isContainer(dropzone)).toBe(false);
        });

        it('should return false for undefined', function() {
            expect(triggerService.isContainer()).toBe(false);
        });

    });

    describe('isNotEmptyContainer', function() {

        it('should be defined', function() {
            expect(triggerService.isNotEmptyContainer).toBeDefined();
        });

        it('should return true for a not empty container', function() {
            expect(triggerService.isNotEmptyContainer(container)).toBe(true);
        });

        it('should return false for empty container', function() {
            expect(triggerService.isNotEmptyContainer(emptyContainer)).toBe(false);
        });

        it('should return false for undefined', function() {
            expect(triggerService.isNotEmptyContainer()).toBe(false);
        });

    });

    describe('isEmptyContainer', function() {

        it('should be defined', function() {
            expect(triggerService.isEmptyContainer).toBeDefined();
        });

        it('should return true for a empty container', function() {
            expect(triggerService.isEmptyContainer(emptyContainer)).toBe(true);
        });

        it('should return false for not empty container', function() {
            expect(triggerService.isEmptyContainer(container)).toBe(false);
        });

        it('should return false for undefined', function() {
            expect(triggerService.isEmptyContainer()).toBe(false);
        });
    });

    describe('isItem', function() {

        it('should be defined', function() {
            expect(triggerService.isItem).toBeDefined();
        });

        it('should return true for a item', function() {
            expect(triggerService.isItem(item)).toBe(true);
        });

        it('should return false for not a item', function() {
            expect(triggerService.isItem(container)).toBe(false);
            expect(triggerService.isItem(dropzone)).toBe(false);
        });

        it('should return false for undefined', function() {
            expect(triggerService.isItem()).toBe(false);
        });
    });

    describe('isDropzone', function() {

        it('should be defined', function() {
            expect(triggerService.isDropzone).toBeDefined();
        });

        it('should return true for a dropzone', function() {
            expect(triggerService.isDropzone(dropzone)).toBe(true);
        });

        it('should return false for not a dropzone', function() {
            expect(triggerService.isDropzone(container)).toBe(false);
            expect(triggerService.isDropzone(item)).toBe(false);
        });

        it('should return false for undefined', function() {
            expect(triggerService.isDropzone()).toBe(false);
        });
    });

    describe('actions', function() {

        it('should be defined', function() {
            expect(triggerService.actions).toBeDefined();
        });

        it('should have id and name', function() {
            triggerService.actions.forEach(function(item) {
                expect(item.id).toBeDefined();
                expect(item.name).toBeDefined();
            });

        });

    });

    describe('isValidExpression', function() {

        it('should be defined', function() {
            expect(triggerService.isValidExpression).toBeDefined();
        });

        it('should return true for simple expression', function() {
            var expression = {
                type: 'container',
                nodes: [item, item, item]
            };

            expect(triggerService.isValidExpression(expression)).toBe(true);
        });

        it('should return true for complex expresion', function() {
            var complexExpression = {
                type: 'container',
                nodes: [{
                    type: 'container',
                    nodes: [
                        item, {
                            type: 'container',
                            nodes: [item]
                        }, {
                            type: 'container',
                            nodes: [item]
                        }
                    ]
                }, item, item]
            };

            expect(triggerService.isValidExpression(complexExpression)).toBe(true);
        });

        it('should return false for expression with empty container', function() {
            var emptyExpression = {
                type: 'container',
                nodes: [] //empty container
            };

            expect(triggerService.isValidExpression(emptyExpression)).toBe(false);
        });

        it('should return false for complex expression with empty container', function() {
            var emptyComplexExpression = {
                type: 'container',
                nodes: [{
                    type: 'container',
                    nodes: [
                        item, {
                            type: 'container',
                            nodes: [item]
                        }, {
                            type: 'container',
                            nodes: [{}] //invalid item in container
                        }
                    ]
                }, item, item]
            };

            expect(triggerService.isValidExpression(emptyComplexExpression)).toBe(false);
        });

        it('should return false for undefined', function() {
            expect(triggerService.isValidExpression()).toBe(false);
        });

    });

    describe('buildTriggers', function() {

        var itemA = {
            type: 'item',
            operation: {},
            selectedSegment: {
                code: 'A'
            }
        };
        var itemB = {
            type: 'item',
            operation: {},
            selectedSegment: {
                code: 'B'
            }
        };
        var undefinedTrigger = {
            type: 'undefindedTriggerData',
            code: 'undefined'
        };

        it('should be defined', function() {
            expect(triggerService.buildTriggers).toBeDefined();
        });

        it('should build default trigger', function() {
            //TODO OCC-2552
            // expect(triggerService.buildTriggers).toBeDefined();
        });

        it('should build segment trigger', function() {
            var form = {
                expression: [{
                    type: 'group',
                    operation: {
                        id: 'AND'
                    },
                    nodes: [itemA, itemB]
                }]
            };

            var trigger = {
                type: 'segmentTriggerData',
                groupBy: 'AND',
                segments: [{
                    code: 'A'
                }, {
                    code: 'B'
                }]
            };

            expect(triggerService.buildTriggers(form)).toEqual([trigger]);
        });

        it('should build simple expression trigger', function() {
            var form = {
                expression: [{
                    type: 'container',
                    operation: {
                        id: 'NOT'
                    },
                    nodes: [itemA, itemB]
                }]
            };

            var trigger = {
                type: 'expressionTriggerData',
                expression: {
                    type: 'negationExpressionData',
                    element: {
                        type: 'groupExpressionData',
                        operator: 'AND',
                        elements: [{
                            type: 'segmentExpressionData',
                            code: 'A'
                        }, {
                            type: 'segmentExpressionData',
                            code: 'B'
                        }]
                    }
                }
            };

            expect(triggerService.buildTriggers(form)).toEqual([trigger]);
        });

        it('should build complex expression trigger', function() {
            var form = {
                expression: [{
                    type: 'container',
                    operation: {
                        id: 'OR'
                    },
                    nodes: [{
                        type: 'container',
                        operation: {
                            id: 'NOT'
                        },
                        nodes: [itemA, itemB]
                    }, itemB]
                }]
            };

            var trigger = {
                type: 'expressionTriggerData',
                expression: {
                    type: 'groupExpressionData',
                    operator: 'OR',
                    elements: [{
                        type: 'negationExpressionData',
                        element: {
                            type: 'groupExpressionData',
                            operator: 'AND',
                            elements: [{
                                type: 'segmentExpressionData',
                                code: 'A'
                            }, {
                                type: 'segmentExpressionData',
                                code: 'B'
                            }]
                        }
                    }, {
                        type: 'segmentExpressionData',
                        code: 'B'
                    }]
                }
            };

            expect(triggerService.buildTriggers(form)).toEqual([trigger]);
        });

        it('should merge default trigger', function() {
            //TODO OCC-2552
        });

        it('should merge segment trigger', function() {
            var form = {
                expression: [{
                    type: 'group',
                    operation: {
                        id: 'AND'
                    },
                    nodes: [itemA, itemB]
                }]
            };

            var trigger = {
                type: 'segmentTriggerData',
                code: 'code',
                groupBy: 'AND',
                segments: [{
                    code: 'A'
                }, {
                    code: 'B'
                }]
            };

            var existingTriggers = [{
                type: 'expressionTriggerData',
                code: 'codeA'
            }, {
                type: 'segmentTriggerData',
                code: 'code'
            }, undefinedTrigger];

            expect(triggerService.buildTriggers(form, existingTriggers)).toEqual([undefinedTrigger, trigger]);
        });

        it('should merge expression trigger', function() {
            var form = {
                expression: [{
                    type: 'container',
                    operation: {
                        id: 'NOT'
                    },
                    nodes: [itemA, itemB]
                }]
            };

            var trigger = {
                type: 'expressionTriggerData',
                code: 'code',
                expression: {
                    type: 'negationExpressionData',
                    element: {
                        type: 'groupExpressionData',
                        operator: 'AND',
                        elements: [{
                            type: 'segmentExpressionData',
                            code: 'A'
                        }, {
                            type: 'segmentExpressionData',
                            code: 'B'
                        }]
                    }
                }
            };

            var existingTriggers = [{
                type: 'expressionTriggerData',
                code: 'code'
            }, undefinedTrigger];

            expect(triggerService.buildTriggers(form, existingTriggers)).toEqual([undefinedTrigger, trigger]);
        });

        it('should build nothing', function() {
            expect(triggerService.buildTriggers()).toEqual([{}]);
        });

    });

    describe('buildData', function() {

        it('should be defined', function() {
            expect(triggerService.buildData).toBeDefined();
        });

        it('should build data from default trigger', function() {
            //TODO OCC-2552
        });

        it('should build data from segment trigger', function() {
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

            expect(triggerService.buildData([trigger])).toEqual(data);
        });

        it('should build data from expression trigger', function() {
            var trigger = {
                type: 'expressionTriggerData',
                code: 'code',
                expression: {
                    type: 'negationExpressionData',
                    element: {
                        type: 'groupExpressionData',
                        operator: 'AND',
                        elements: [{
                            type: 'segmentExpressionData',
                            code: 'A'
                        }, {
                            type: 'segmentExpressionData',
                            code: 'B'
                        }]
                    }
                }
            };

            var data = [{
                type: 'container',
                operation: {
                    id: 'NOT',
                    name: 'personalization.modal.customizationvariationmanagement.targetgrouptab.expression.not'
                },
                nodes: [{
                    type: 'item',
                    operation: '',
                    selectedSegment: {
                        code: 'A'
                    },
                    nodes: []
                }, {
                    type: 'item',
                    operation: '',
                    selectedSegment: {
                        code: 'B'
                    },
                    nodes: []
                }]
            }];

            expect(triggerService.buildData([trigger])).toEqual(data);
        });

        it('should build empty data', function() {
            var data = [{
                type: 'container',
                operation: {
                    id: 'AND',
                    name: 'personalization.modal.customizationvariationmanagement.targetgrouptab.expression.and'
                },
                nodes: []
            }];

            expect(triggerService.buildData({})).toEqual(data);
        });

    });

    describe('getExpressionAsString', function() {

        var dropzoneItem = {
            type: 'dropzone'
        };

        it('should be defined', function() {
            expect(triggerService.getExpressionAsString).toBeDefined();
        });

        it('should return empty string if called with undefined expression object', function() {
            expect(triggerService.getExpressionAsString()).toBe("");
        });

        it('should return proper string object for empty expression container', function() {

            var data = [{
                type: 'container',
                operation: {
                    id: 'OR',
                },
                nodes: [{
                    type: 'container',
                    operation: {
                        id: "OR"
                    },
                    nodes: [dropzoneItem]
                }]
            }];

            var stringExpression = "(( [] ))";

            expect(triggerService.getExpressionAsString(data[0])).toEqual(stringExpression);
        });

        it('should return proper string object for empty expression container with NOT operator', function() {

            var data = [{
                type: 'container',
                operation: {
                    id: 'NOT',
                },
                nodes: [dropzoneItem]
            }];

            var stringExpression = " NOT ( [] )";

            expect(triggerService.getExpressionAsString(data[0])).toEqual(stringExpression);
        });

        it('should return proper string object for none empty expression container with NOT operator', function() {

            var data = [{
                type: 'container',
                operation: {
                    id: 'NOT',
                },
                nodes: [{
                    type: 'container',
                    operation: {
                        id: "NOT"
                    },
                    nodes: [{
                        type: 'item',
                        operation: '',
                        selectedSegment: {
                            code: 'SegmentA'
                        },
                        nodes: []
                    }]
                }]
            }];

            var stringExpression = " NOT ( NOT (SegmentA))";

            expect(triggerService.getExpressionAsString(data[0])).toEqual(stringExpression);
        });

        it('should return proper string object for simple expression', function() {

            var data = [{
                type: 'container',
                operation: {
                    id: 'OR',
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

            var stringExpression = "(SegmentA OR SegmentB)";

            expect(triggerService.getExpressionAsString(data[0])).toEqual(stringExpression);
        });

        it('should return proper string object for expression with NOT container', function() {

            var data = [{
                type: 'container',
                operation: {
                    id: 'OR',
                },
                nodes: [{
                    type: 'item',
                    operation: '',
                    selectedSegment: {
                        code: 'SegmentA'
                    },
                    nodes: []
                }, {
                    type: 'container',
                    operation: {
                        id: 'NOT'
                    },
                    nodes: [{
                        type: 'item',
                        operation: '',
                        selectedSegment: {
                            code: 'SegmentC'
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
                }]
            }];

            var stringExpression = "(SegmentA OR  NOT (SegmentC AND SegmentB))";

            expect(triggerService.getExpressionAsString(data[0])).toEqual(stringExpression);
        });

    });

});
