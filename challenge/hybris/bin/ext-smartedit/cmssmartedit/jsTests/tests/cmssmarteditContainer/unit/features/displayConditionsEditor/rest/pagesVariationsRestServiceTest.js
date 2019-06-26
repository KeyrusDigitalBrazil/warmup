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
describe('pagesVariationsRestService', function() {

    var pagesVariationsRestService;
    var $q;
    var mockRestService;
    var response = {
        uids: "garbage"
    };

    beforeEach(function() {

        angular.mock.module(function($provide) {
            $provide.value("CONTEXT_SITE_ID", "CONTEXT_SITE_ID");
            $provide.value("CONTEXT_CATALOG", "CONTEXT_CATALOG");
            $provide.value("CONTEXT_CATALOG_VERSION", "CONTEXT_CATALOG_VERSION");

            mockRestService = jasmine.createSpyObj('mockRestService', ['get']);
            var restServiceFactory = jasmine.createSpyObj('restServiceFactory', ['get']);
            restServiceFactory.get.and.returnValue(mockRestService);
            $provide.value("restServiceFactory", restServiceFactory);
        });

    });

    beforeEach(angular.mock.module('pagesVariationsRestServiceModule'));

    beforeEach(inject(function(_pagesVariationsRestService_, _$q_) {
        pagesVariationsRestService = _pagesVariationsRestService_;
        $q = _$q_;
    }));

    describe('getVariationsForPrimaryPageId', function() {

        it('should delegate to the resource GET method passing page UID and the current context if no context is provided', function() {
            mockRestService.get.and.returnValue($q.when(response));

            var result = pagesVariationsRestService.getVariationsForPrimaryPageId('somePageUid');

            expect(mockRestService.get).toHaveBeenCalledWith({
                pageId: 'somePageUid'
            });
            expect(result).toBeResolvedWithData(response.uids);
        });

        it('should delegate to the resource GET method passing the given context and page UID', function() {
            mockRestService.get.and.returnValue($q.when(response));
            var result = pagesVariationsRestService.getVariationsForPrimaryPageId('somePageUid', {
                a: 'b'
            });

            expect(mockRestService.get).toHaveBeenCalledWith({
                pageId: 'somePageUid',
                a: 'b'
            });
            expect(result).toBeResolvedWithData(response.uids);
        });

    });

});
