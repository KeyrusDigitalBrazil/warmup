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
describe('pagesFallbacksRestService', function() {

    var service;
    var resource;
    var uriContext = {
        a: 'b'
    };
    beforeEach(angular.mock.module(function($provide) {
        $provide.value("PAGE_CONTEXT_SITE_ID", "PAGE_CONTEXT_SITE_ID");
        $provide.value("PAGE_CONTEXT_CATALOG", "PAGE_CONTEXT_CATALOG");
        $provide.value("PAGE_CONTEXT_CATALOG_VERSION", "PAGE_CONTEXT_CATALOG_VERSION");
    }));

    beforeEach(function() {
        resource = jasmine.createSpyObj('resource', ['get']);
        var harness = AngularUnitTestHelper.prepareModule('pagesFallbacksRestServiceModule')
            .mock('restServiceFactory', 'get').and.returnValue(resource)
            .service('pagesFallbacksRestService');
        service = harness.service;

        resource.get.and.returnValue(harness.injected.$q.when({
            uids: ['someFallbackUid', 'someOtherFallbackUid']
        }));
    });

    describe('getFallbacksForPageId', function() {
        it('should delegate to the resource GET method passing page UID and the current context if no context is provided', function() {
            service.getFallbacksForPageId('somePageUid');

            expect(resource.get).toHaveBeenCalledWith({
                pageId: 'somePageUid'
            });
        });

        it('should delegate to the resource GET method passing the given context and page UID', function() {
            service.getFallbacksForPageId('somePageUid', uriContext);

            expect(resource.get).toHaveBeenCalledWith({
                pageId: 'somePageUid'
            });
        });

        it('should return the UID list from the response', function() {
            expect(service.getFallbacksForPageId('somePageUid')).toBeResolvedWithData(['someFallbackUid', 'someOtherFallbackUid']);
        });
    });

});
