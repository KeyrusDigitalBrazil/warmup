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
describe('test renderService', function() {
    var RenderServiceInterface, gatewayFactory, renderService, gatewayProxy, perspectiveService, crossFrameEventService;

    beforeEach(angular.mock.module('renderServiceModule', function($provide) {

        gatewayFactory = jasmine.createSpyObj('gatewayFactory', ['initListener', 'createGateway']);
        $provide.value('gatewayFactory', gatewayFactory);

        gatewayProxy = jasmine.createSpyObj('gatewayProxy', ['initForService']);
        $provide.value('gatewayProxy', gatewayProxy);

        crossFrameEventService = jasmine.createSpyObj('crossFrameEventService', ['publish']);
        $provide.value('crossFrameEventService', crossFrameEventService);

        perspectiveService = jasmine.createSpyObj('perspectiveService', ['isEmptyPerspectiveActive']);
        $provide.value('perspectiveService', perspectiveService);

    }));

    beforeEach(inject(function(_RenderServiceInterface_, _renderService_) {
        RenderServiceInterface = _RenderServiceInterface_;
        renderService = _renderService_;
    }));

    it('extends RenderServiceInterface', function() {
        expect(renderService instanceof RenderServiceInterface).toBe(true);
    });

    it('initializes and invokes gatewayProxy', function() {
        expect(renderService.gatewayId).toBe("Renderer");
        expect(gatewayProxy.initForService).toHaveBeenCalledWith(renderService, ["blockRendering", "isRenderingBlocked", "renderSlots", "renderComponent", "renderRemoval", "toggleOverlay", "refreshOverlayDimensions", "renderPage"]);
    });

    it('leaves the expected set of functions empty', function() {

        expect(renderService.renderSlots).toBeEmptyFunction();
        expect(renderService.renderComponent).toBeEmptyFunction();
        expect(renderService.renderRemoval).toBeEmptyFunction();
        expect(renderService.toggleOverlay).toBeEmptyFunction();
        expect(renderService.refreshOverlayDimensions).toBeEmptyFunction();
    });

    describe('blockRendering ', function() {

        it('will set RenderingBlocked to true when true is passed', function() {
            renderService.blockRendering(true);
            expect(renderService.RenderingBlocked).toBe(true);
        });

        it('will set RenderingBlocked to false when false is passed', function() {
            renderService.blockRendering(false);
            expect(renderService.RenderingBlocked).toBe(false);
        });

    });

    describe('isRenderingBlocked ', function() {

        it('will return false if nothing is set', function() {
            var promise = renderService.isRenderingBlocked();
            expect(promise).toBeResolvedWithData(false);
        });

        it('will return true if rendering is blocked', function() {
            renderService.blockRendering(true);
            expect(renderService.isRenderingBlocked()).toBeResolvedWithData(true);
        });

        it('will return false if rendering is not blocked', function() {
            renderService.blockRendering(false);
            expect(renderService.isRenderingBlocked()).toBeResolvedWithData(false);
        });

    });


});
