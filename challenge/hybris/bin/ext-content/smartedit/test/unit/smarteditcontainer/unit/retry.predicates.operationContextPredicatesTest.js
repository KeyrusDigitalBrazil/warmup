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
describe('operation context predicates', function() {
    var OPERATION_CONTEXT;
    var operationContextInteractivePredicate;
    var operationContextNonInteractivePredicate;
    var operationContextCMSPredicate;
    var operationContextToolingPredicate;

    beforeEach(angular.mock.module('smarteditCommonsModule'));

    beforeEach(angular.mock.module('operationContextPredicatesModule'));

    beforeEach(inject(function(_operationContextInteractivePredicate_, _operationContextNonInteractivePredicate_, _operationContextCMSPredicate_, _operationContextToolingPredicate_, _OPERATION_CONTEXT_) {
        operationContextInteractivePredicate = _operationContextInteractivePredicate_;
        operationContextNonInteractivePredicate = _operationContextNonInteractivePredicate_;
        operationContextCMSPredicate = _operationContextCMSPredicate_;
        operationContextToolingPredicate = _operationContextToolingPredicate_;
        OPERATION_CONTEXT = _OPERATION_CONTEXT_;
    }));

    it('operation context interactive predicate should match INTERACTIVE', function() {
        expect(operationContextInteractivePredicate(null, OPERATION_CONTEXT.INTERACTIVE)).toBeTruthy();
    });

    it('operation context non-interactive predicate should match BACKGROUND_TASKS, NON_INTERACTIVE and BATCH_OPERATIONS', function() {
        [OPERATION_CONTEXT.BACKGROUND_TASKS, OPERATION_CONTEXT.NON_INTERACTIVE, OPERATION_CONTEXT.BATCH_OPERATIONS].forEach(function(oc) {
            expect(operationContextNonInteractivePredicate(null, oc)).toBeTruthy();
        });
    });

    it('operation context CMS predicate should match CMS', function() {
        expect(operationContextCMSPredicate(null, OPERATION_CONTEXT.CMS)).toBeTruthy();
    });

    it('operation context tooling predicate should match TOOLING', function() {
        expect(operationContextToolingPredicate(null, OPERATION_CONTEXT.TOOLING)).toBeTruthy();
    });
});
