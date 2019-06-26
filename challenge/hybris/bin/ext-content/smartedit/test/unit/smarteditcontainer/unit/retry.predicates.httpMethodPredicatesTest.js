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
describe('http method predicates', function() {
    var updatePredicate, HTTP_METHODS_UPDATE;
    var readPredicate, HTTP_METHODS_READ;

    beforeEach(angular.mock.module('httpMethodPredicatesModule'));

    beforeEach(inject(function(_updatePredicate_, _HTTP_METHODS_UPDATE_, _readPredicate_, _HTTP_METHODS_READ_) {
        updatePredicate = _updatePredicate_;
        HTTP_METHODS_UPDATE = _HTTP_METHODS_UPDATE_;

        readPredicate = _readPredicate_;
        HTTP_METHODS_READ = _HTTP_METHODS_READ_;
    }));

    it('update predicate should match only with http methods update', function() {
        HTTP_METHODS_UPDATE.forEach(function(method) {
            expect(updatePredicate({
                config: {
                    method: method
                }
            })).toBeTruthy();
        });
    });

    it('read predicate should match only with http methods read', function() {
        HTTP_METHODS_READ.forEach(function(method) {
            expect(readPredicate({
                config: {
                    method: method
                }
            })).toBeTruthy();
        });
    });
});
