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
describe('componentInfoService', function() {
    var harness;
    var $q;
    var $rootScope;
    var componentInfoService;
    var mockCmsitemsRestService;
    var crossFrameEventService;
    var systemEventService;

    var COMPONENT1 = {
        id: 1,
        attr: function() {
            return 'uuid-001';
        }
    };
    var COMPONENT2 = {
        id: 2,
        attr: function() {
            return 'uuid-002';
        }
    };
    var MOCK_COMPONENTS = [COMPONENT1, COMPONENT2];

    var COMPONENTS_DATA = [{
        uuid: COMPONENT1.uuid
    }, {
        uuid: COMPONENT2.uuid
    }];

    beforeEach(function() {
        harness = AngularUnitTestHelper.prepareModule('componentInfoServiceModule')
            .mock('cmsitemsRestService', 'getByIds')
            .mock('crossFrameEventService', 'subscribe')
            .mockConstant('OVERLAY_RERENDERED_EVENT', 'OVERLAY_RERENDERED_EVENT')
            .mockConstant('UUID_ATTRIBUTE', 'data-smartedit-component-uuid')
            .mockConstant('EVENTS', {
                PAGE_CHANGE: 'PAGE_CHANGE',
                USER_HAS_CHANGED: 'USER_HAS_CHANGED'
            })
            .service('componentInfoService');

        $q = harness.injected.$q;
        $rootScope = harness.injected.$rootScope;

        componentInfoService = harness.service;
        crossFrameEventService = harness.mocks.crossFrameEventService;
        systemEventService = harness.mocks.systemEventService;

        mockCmsitemsRestService = harness.mocks.cmsitemsRestService;
        mockCmsitemsRestService.getByIds.and.callFake(function() {
            return $q.when({
                response: COMPONENTS_DATA
            });
        });
    });

    it('WHEN components are added it should call _getComponentsDataByUUIDs', function() {
        spyOn(componentInfoService, '_getComponentsDataByUUIDs');

        componentInfoService._onComponentsAdded(MOCK_COMPONENTS);

        expect(componentInfoService._getComponentsDataByUUIDs).toHaveBeenCalledWith([COMPONENT1.uuid, COMPONENT2.uuid]);
    });

    it('WHEN components are added THEN getById should return the component data', function(done) {
        componentInfoService._onComponentsAdded(MOCK_COMPONENTS);

        componentInfoService.getById(COMPONENT1.uuid).then(function(data) {
            expect(data).toEqual({
                uuid: COMPONENT1.uuid
            });
            done();
        });
        $rootScope.$digest();
    });

    it('WHEN getById is called and the component data is not cached, it should resolve when the component data is ready', function(done) {
        componentInfoService.getById(COMPONENT2.uuid).then(function(data) {
            expect(data).toEqual({
                uuid: COMPONENT2.uuid
            });
            done();
        });
        componentInfoService._getComponentsDataByUUIDs([COMPONENT2.uuid]);
        $rootScope.$digest();
    });

    it('WHEN getById is called and the component data fetch failed, it should reject the getById promise', function(done) {
        mockCmsitemsRestService.getByIds.and.callFake(function() {
            return $q.reject({
                message: 'error while retrieving cmsitems'
            });
        });

        componentInfoService.getById(COMPONENT2.uuid).then(function() {}, function(e) {
            expect(e.message).toEqual('error while retrieving cmsitems');
            done();
        });
        componentInfoService._getComponentsDataByUUIDs([COMPONENT2.uuid]);
        $rootScope.$digest();
    });

    it('should subscribe on PAGE_CHANGE event', function() {
        expect(crossFrameEventService.subscribe).toHaveBeenCalledWith("PAGE_CHANGE", jasmine.any(Function));
    });

    it('should subscribe on OVERLAY_RERENDERED_EVENT event', function() {
        expect(crossFrameEventService.subscribe).toHaveBeenCalledWith("OVERLAY_RERENDERED_EVENT", jasmine.any(Function));
    });

    it('should subscribe on USER_HAS_CHANGED event', function() {
        expect(crossFrameEventService.subscribe).toHaveBeenCalledWith("USER_HAS_CHANGED", jasmine.any(Function));
    });
});
