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
describe('http error predicates', function() {
    var serverErrorPredicate, SERVER_ERROR_PREDICATE_HTTP_STATUSES;
    var clientErrorPredicate, CLIENT_ERROR_PREDICATE_HTTP_STATUSES;
    var timeoutErrorPredicate, TIMEOUT_ERROR_PREDICATE_HTTP_STATUSES;
    var retriableErrorPredicate;
    var noInternetConnectionErrorPredicate;

    var HTTP_ERROR_CODES_NON_RETRIABLE = [400, 401, 403, 404, 501];

    beforeEach(angular.mock.module('httpErrorPredicatesModule'));

    beforeEach(inject(function(_serverErrorPredicate_, _SERVER_ERROR_PREDICATE_HTTP_STATUSES_, _clientErrorPredicate_, _CLIENT_ERROR_PREDICATE_HTTP_STATUSES_, _timeoutErrorPredicate_, _TIMEOUT_ERROR_PREDICATE_HTTP_STATUSES_, _retriableErrorPredicate_, _noInternetConnectionErrorPredicate_) {
        serverErrorPredicate = _serverErrorPredicate_;
        SERVER_ERROR_PREDICATE_HTTP_STATUSES = _SERVER_ERROR_PREDICATE_HTTP_STATUSES_;

        clientErrorPredicate = _clientErrorPredicate_;
        CLIENT_ERROR_PREDICATE_HTTP_STATUSES = _CLIENT_ERROR_PREDICATE_HTTP_STATUSES_;

        timeoutErrorPredicate = _timeoutErrorPredicate_;
        TIMEOUT_ERROR_PREDICATE_HTTP_STATUSES = _TIMEOUT_ERROR_PREDICATE_HTTP_STATUSES_;

        retriableErrorPredicate = _retriableErrorPredicate_;

        noInternetConnectionErrorPredicate = _noInternetConnectionErrorPredicate_;
    }));

    it('server error predicate should match only with http server errors', function() {
        SERVER_ERROR_PREDICATE_HTTP_STATUSES.forEach(function(status) {
            expect(serverErrorPredicate({
                status: status
            })).toBeTruthy();
        });
    });

    it('server error predicate should not match non retriable http error codes', function() {
        HTTP_ERROR_CODES_NON_RETRIABLE.forEach(function(status) {
            expect(serverErrorPredicate({
                status: status
            })).toBeFalsy();
        });
    });

    it('client error predicate should match only with http client errors', function() {
        CLIENT_ERROR_PREDICATE_HTTP_STATUSES.forEach(function(status) {
            expect(clientErrorPredicate({
                status: status
            })).toBeTruthy();
        });
    });

    it('client error predicate should not match non retriable http error codes', function() {
        HTTP_ERROR_CODES_NON_RETRIABLE.forEach(function(status) {
            expect(clientErrorPredicate({
                status: status
            })).toBeFalsy();
        });
    });

    it('timeout error predicate should match only with timeout client error', function() {
        TIMEOUT_ERROR_PREDICATE_HTTP_STATUSES.forEach(function(status) {
            expect(timeoutErrorPredicate({
                status: status
            })).toBeTruthy();
        });
    });

    it('timeout error predicate should not match non retriable http error codes', function() {
        HTTP_ERROR_CODES_NON_RETRIABLE.forEach(function(status) {
            expect(timeoutErrorPredicate({
                status: status
            })).toBeFalsy();
        });
    });

    it('retriable error predicate should match with http server errors', function() {
        SERVER_ERROR_PREDICATE_HTTP_STATUSES.forEach(function(status) {
            expect(retriableErrorPredicate({
                status: status
            })).toBeTruthy();
        });
    });

    it('retriable error predicate should match with http client errors', function() {
        CLIENT_ERROR_PREDICATE_HTTP_STATUSES.forEach(function(status) {
            expect(retriableErrorPredicate({
                status: status
            })).toBeTruthy();
        });
    });

    it('retriable error predicate should match with http timeout errors', function() {
        TIMEOUT_ERROR_PREDICATE_HTTP_STATUSES.forEach(function(status) {
            expect(retriableErrorPredicate({
                status: status
            })).toBeTruthy();
        });
    });

    it('retriable error predicate should not match non retriable http error codes', function() {
        HTTP_ERROR_CODES_NON_RETRIABLE.forEach(function(status) {
            expect(retriableErrorPredicate({
                status: status
            })).toBeFalsy();
        });
    });

    it('no internet connection error predicate should match http status code 0', function() {
        expect(noInternetConnectionErrorPredicate({
            status: 0
        })).toBeTruthy();
    });
});
