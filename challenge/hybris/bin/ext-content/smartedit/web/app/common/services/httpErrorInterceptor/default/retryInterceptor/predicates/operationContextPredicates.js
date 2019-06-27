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
angular.module('operationContextPredicatesModule', ['seConstantsModule', 'yLoDashModule'])
    .service('operationContextInteractivePredicate', function(OPERATION_CONTEXT) {
        return function(response, operationContext) {
            return operationContext === OPERATION_CONTEXT.INTERACTIVE;
        };
    })
    .service('operationContextNonInteractivePredicate', function(OPERATION_CONTEXT, lodash) {
        return function(response, operationContext) {
            return lodash.includes([OPERATION_CONTEXT.BACKGROUND_TASKS, OPERATION_CONTEXT.NON_INTERACTIVE, OPERATION_CONTEXT.BATCH_OPERATIONS], operationContext);
        };
    })
    .service('operationContextCMSPredicate', function(OPERATION_CONTEXT) {
        return function(response, operationContext) {
            return operationContext === OPERATION_CONTEXT.CMS;
        };
    })
    .service('operationContextToolingPredicate', function(OPERATION_CONTEXT) {
        return function(response, operationContext) {
            return operationContext === OPERATION_CONTEXT.TOOLING;
        };
    });
