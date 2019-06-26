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
describe('renderService', function() {

    // Mocks and Injected Services
    var $window, $q, $rootScope, $http, $location;
    var crossFrameEventService, systemEventService, alertService, componentHandlerService, extractFromElement,
        gatewayFactory, gatewayProxy, unsafeParseHTML, renderService, RenderServiceInterface, sakExecutor,
        smarteditBootstrapGateway, perspectiveService, resizeCallback, CONTRACT_CHANGE_LISTENER_PROCESS_EVENTS,
        experienceService, sharedDataService;

    // Mock Test Data
    var COMPONENT_ID_ATTRIBUTE = 'data-smartedit-component-id';
    var COMPONENT_UUID_ATTRIBUTE = 'data-smartedit-component-uuid';
    var COMPONENT_TYPE_ATTRIBUTE = 'data-smartedit-component-type';
    var COMPONENT_CATALOG_VERSION_ATTRIBUTE = 'data-smartedit-catalog-version-uuid';
    var COMPONENT_ID = 'someComponentId';
    var COMPONENT_UUID = 'someComponentUuid';
    var COMPONENT_TYPE = 'someComponentType';
    var COMPONENT_CATALOG_VERSION = 'someComponentCatalogVersion';
    var SLOT_ID_1 = 'slot1';
    var SLOT_ID_2 = 'slot2';
    var MOCK_STOREFRONT_PREVIEW_URL = 'someMockPreviewStorefronUrl';

    beforeEach(angular.mock.module('smarteditCommonsModule', function($provide) {
        gatewayFactory = jasmine.createSpyObj('gatewayFactory', ['initListener', 'createGateway']);
        smarteditBootstrapGateway = jasmine.createSpyObj('smarteditBootstrapGateway', ['publish']);
        gatewayFactory.createGateway.and.callFake(function(gatewayId) {
            if (gatewayId === 'smartEditBootstrap') {
                return smarteditBootstrapGateway;
            }
        });
        $provide.value('gatewayFactory', gatewayFactory);

        gatewayProxy = jasmine.createSpyObj('gatewayProxy', ['initForService']);
        $provide.value('gatewayProxy', gatewayProxy);
    }));

    beforeEach(angular.mock.module('alertServiceModule', function($provide) {
        alertService = jasmine.createSpyObj('alertService', ['showDanger']);
        $provide.value('alertService', alertService);
    }));

    beforeEach(angular.mock.module('smarteditServicesModule', function($provide) {
        sakExecutor = jasmine.createSpyObj('sakExecutorService', ['areAllDecoratorsProcessed', 'destroyAllScopes', 'destroyScope']);
        $provide.value('sakExecutorService', sakExecutor);

        experienceService = jasmine.createSpyObj('experienceService', ['buildRefreshedPreviewUrl']);
        $provide.value('experienceService', experienceService);

        componentHandlerService = jasmine.createSpyObj('componentHandlerService', ['getFromSelector', 'getOverlay', 'isOverlayOn', 'getParent', 'getComponentUnderSlot', 'getComponent', 'getOriginalComponent', 'getComponentInOverlay', 'getParentSlotForComponent', 'getOverlayComponentWithinSlot', 'getOverlayComponent', 'isSmartEditComponent', 'getFirstSmartEditComponentChildren', 'getComponentCloneInOverlay']);

        $provide.value('componentHandlerService', componentHandlerService);

        perspectiveService = jasmine.createSpyObj('perspectiveService', ['isEmptyPerspectiveActive']);
        perspectiveService.isEmptyPerspectiveActive.and.returnValue();
        $provide.value('perspectiveService', perspectiveService);

        sharedDataService = jasmine.createSpyObj('sharedDataService', ['get']);
        $provide.value('sharedDataService', sharedDataService);
    }));

    beforeEach(angular.mock.module('renderServiceModule', function($provide) {
        $http = jasmine.createSpy('$http');
        $provide.value('$http', $http);

        $location = jasmine.createSpyObj('$location', ['absUrl']);
        $provide.value('$location', $location);

        crossFrameEventService = jasmine.createSpyObj('crossFrameEventService', ['publish', 'subscribe']);
        $provide.value('crossFrameEventService', crossFrameEventService);

        systemEventService = jasmine.createSpyObj('systemEventService', ['publish', 'subscribe']);
        $provide.value('systemEventService', systemEventService);

        unsafeParseHTML = jasmine.createSpy('unsafeParseHTML');
        $provide.value('unsafeParseHTML', unsafeParseHTML);

        extractFromElement = jasmine.createSpy('extractFromElement');
        $provide.value('extractFromElement', extractFromElement);


        $provide.value('CONTRACT_CHANGE_LISTENER_PROCESS_EVENTS', CONTRACT_CHANGE_LISTENER_PROCESS_EVENTS = {
            PROCESS_COMPONENTS: 'contractChangeListenerProcessComponents',
            RESTART_PROCESS: 'contractChangeListenerRestartProcess'
        });

    }));

    beforeEach(inject(function(_$window_) {
        $window = _$window_;

        spyOn($window, "addEventListener").and.callFake(function(event, callback) {
            resizeCallback = callback;
        });
    }));

    beforeEach(inject(function(_RenderServiceInterface_, _renderService_, _$q_, _$rootScope_) {
        RenderServiceInterface = _RenderServiceInterface_;
        renderService = _renderService_;

        $q = _$q_;
        $rootScope = _$rootScope_;

        spyOn(renderService, "_reprocessPage");

        // Setup $q spies once we have _$q_
        experienceService.buildRefreshedPreviewUrl.and.callFake(function() {
            return $q.when(MOCK_STOREFRONT_PREVIEW_URL);
        });

        sharedDataService.get.and.returnValue($q.when({
            siteDescriptor: {
                name: "some name",
                previewUrl: "/someURI/?someSite=site",
                uid: "some uid"
            },
            catalogDescriptor: {
                name: "some cat name",
                catalogId: "some cat uid",
                catalogVersion: "some cat version"
            },
            languageDescriptor: {
                isocode: "some language isocode"
            },
            time: null
        }));

    }));

    describe('<<init>>', function() {
        beforeEach(function() {
            spyOn(renderService, "renderPage");
        });

        it('implements the RenderServiceInterface interface', function() {
            expect(renderService.renderSlots).toBeDefined();
            expect(renderService.renderComponent).toBeDefined();
            expect(renderService.renderRemoval).toBeDefined();
            expect(renderService.renderPage).toBeDefined();
            expect(renderService.toggleOverlay).toBeDefined();
            expect(renderService.refreshOverlayDimensions).toBeDefined();

            expect(renderService.blockRendering).toBeEmptyFunction();
            expect(renderService.isRenderingBlocked).toBeEmptyFunction();
        });

        it('invokes gatewayProxy', function() {
            expect(renderService.gatewayId).toBe("Renderer");
            expect(gatewayProxy.initForService).toHaveBeenCalledWith(renderService, ["blockRendering", "isRenderingBlocked", "renderSlots", "renderComponent", "renderRemoval", "toggleOverlay", "refreshOverlayDimensions", "renderPage"]);
        });
    });

    describe('renderRemoval', function() {
        var element;
        var actual;

        beforeEach(function() {
            element = jasmine.createSpyObj('element', ['remove']);
        });

        beforeEach(function() {
            componentHandlerService.getComponentUnderSlot.and.returnValue(element);
            spyOn(renderService, "refreshOverlayDimensions");
        });

        beforeEach(function() {
            actual = renderService.renderRemoval(COMPONENT_ID, COMPONENT_TYPE, SLOT_ID_1);
        });

        it('should remove the element', function() {
            expect(componentHandlerService.getComponentUnderSlot).toHaveBeenCalledWith(COMPONENT_ID, COMPONENT_TYPE, SLOT_ID_1, null);
            expect(element.remove).toHaveBeenCalled();
        });

        it('should refresh overlay dimensions', function() {
            expect(renderService.refreshOverlayDimensions).toHaveBeenCalled();
        });
    });

    describe('renderComponent', function() {
        var slotElement, componentElement, renderSlotsPromise;

        beforeEach(function() {
            slotElement = jasmine.createSpyObj('slotElement', ['attr']);
            slotElement.attr.and.returnValue(SLOT_ID_1);
            componentElement = {};
            renderSlotsPromise = {};

            spyOn(renderService, 'renderSlots').and.returnValue(renderSlotsPromise);
            componentHandlerService.getComponent.and.returnValue(componentElement);
            componentHandlerService.getParent.and.returnValue(slotElement);
        });

        it('extracts the slot ID for the given component ID and type', function() {
            renderService.renderComponent(COMPONENT_ID, COMPONENT_TYPE);
            expect(componentHandlerService.getComponent).toHaveBeenCalledWith(COMPONENT_ID, COMPONENT_TYPE);
            expect(componentHandlerService.getParent).toHaveBeenCalledWith(componentElement);
            expect(slotElement.attr).toHaveBeenCalledWith("data-smartedit-component-id");
        });

        it('delegates to renderSlots', function() {
            expect(renderService.renderComponent(COMPONENT_ID, COMPONENT_TYPE)).toBe(renderSlotsPromise);
            expect(renderService.renderSlots).toHaveBeenCalledWith(SLOT_ID_1);
        });
    });

    describe('renderSlots', function() {
        var EXPECTED_EXCEPTION_NO_SLOT_IDS = 'renderService.renderSlots.slotIds.required';
        var CURRENT_URL = 'currentUrl';
        var actual;

        beforeEach(function() {
            spyOn(renderService, "renderPage");
        });

        describe('when no slot ids are provided', function() {
            beforeEach(function() {
                actual = renderService.renderSlots();
            });

            it('returns a rejected promise with an error message', function() {
                expect(actual).toBeRejectedWithData(EXPECTED_EXCEPTION_NO_SLOT_IDS);
            });

            it('should not build refreshed preview url', function() {
                expect(experienceService.buildRefreshedPreviewUrl).not.toHaveBeenCalled();
            });

            it('should not fetch the page', function() {
                expect($http).not.toHaveBeenCalled();
            });

            it('should not reprocess and render the page', function() {
                expect(renderService._reprocessPage).not.toHaveBeenCalled();
                expect(renderService.renderPage).not.toHaveBeenCalled();
            });
        });

        describe('when an empty array of slot ids is provided', function() {
            beforeEach(function() {
                actual = renderService.renderSlots([]);
            });

            it('returns a rejected promise with an error message', function() {
                expect(actual).toBeRejectedWithData(EXPECTED_EXCEPTION_NO_SLOT_IDS);
            });

            it('should not build refreshed preview url when an empty array of slot ids is provided', function() {
                expect(experienceService.buildRefreshedPreviewUrl).not.toHaveBeenCalled();
            });

            it('should not fetch the page when an empty array of slot ids is provided', function() {
                expect($http).not.toHaveBeenCalled();
            });

            it('should not reprocess and render the page when an empty array of slot ids is provided', function() {
                expect(renderService._reprocessPage).not.toHaveBeenCalled();
                expect(renderService.renderPage).not.toHaveBeenCalled();
            });
        });

        describe('when http request fails', function() {
            var EXPECTED_HTML_ERROR_MESSAGE = 'ExpectedHTMLErrorMessage';
            var actual;

            beforeEach(function() {
                $location.absUrl.and.returnValue(CURRENT_URL);
                $http.and.returnValue($q.reject({
                    message: EXPECTED_HTML_ERROR_MESSAGE
                }));
                actual = renderService.renderSlots(SLOT_ID_1);
                $rootScope.$digest();
            });

            it('should spawn an alert', function() {
                expect(alertService.showDanger).toHaveBeenCalledWith({
                    message: EXPECTED_HTML_ERROR_MESSAGE
                });
            });

            it('should not reprocess and render the page', function() {
                expect(renderService._reprocessPage).not.toHaveBeenCalled();
                expect(renderService.renderPage).not.toHaveBeenCalled();
            });

            it('should return a rejected promise with the message in the error response', function() {
                expect(actual).toBeRejectedWithData(EXPECTED_HTML_ERROR_MESSAGE);
            });
        });

        describe('when multiple slot IDs are provided and http request succeeds', function() {
            var EXPECTED_SLOT_1_SELECTOR = ".smartEditComponent" +
                "[data-smartedit-component-type='ContentSlot']" +
                "[data-smartedit-component-id='" + SLOT_ID_1 + "']";
            var EXPECTED_SLOT_2_SELECTOR = ".smartEditComponent" +
                "[data-smartedit-component-type='ContentSlot']" +
                "[data-smartedit-component-id='" + SLOT_ID_2 + "']";
            var EXPECTED_FETCHED_SLOT_1_HTML = 'html1';
            var EXPECTED_FETCHED_SLOT_2_HTML = 'html2';
            var EXPECTED_HTTP_REQUEST_OBJECT = {
                method: 'GET',
                url: MOCK_STOREFRONT_PREVIEW_URL,
                headers: {
                    Pragma: 'no-cache'
                }
            };

            var successHtmlResponse, root;
            var originalSlotToReplace1, originalSlotToReplace2, fetchedSlotToRender1, fetchedSlotToRender2;
            var actual;

            beforeEach(function() {
                originalSlotToReplace1 = jasmine.createSpyObj('originalSlotToReplace1', ['html', 'css', 'data']);
                originalSlotToReplace2 = jasmine.createSpyObj('originalSlotToReplace2', ['html', 'css', 'data']);
                fetchedSlotToRender1 = jasmine.createSpyObj('fetchedSlotToRender1', ['html', 'css', 'data']);
                fetchedSlotToRender2 = jasmine.createSpyObj('fetchedSlotToRender2', ['html', 'css', 'data']);
            });

            beforeEach(function() {
                successHtmlResponse = {
                    data: '<html>some html</html>'
                };
                root = {};
                $location.absUrl.and.returnValue(CURRENT_URL);
                $http.and.returnValue($q.when(successHtmlResponse));
                unsafeParseHTML.and.returnValue(root);
                extractFromElement.and.callFake(function(root, selector) {
                    if (selector === EXPECTED_SLOT_1_SELECTOR) {
                        return fetchedSlotToRender1;
                    } else if (selector === EXPECTED_SLOT_2_SELECTOR) {
                        return fetchedSlotToRender2;
                    }
                });
                componentHandlerService.getFromSelector.and.callFake(function(selector) {
                    if (selector === EXPECTED_SLOT_1_SELECTOR) {
                        return originalSlotToReplace1;
                    } else if (selector === EXPECTED_SLOT_2_SELECTOR) {
                        return originalSlotToReplace2;
                    }
                });
                fetchedSlotToRender1.html.and.returnValue(EXPECTED_FETCHED_SLOT_1_HTML);
                fetchedSlotToRender2.html.and.returnValue(EXPECTED_FETCHED_SLOT_2_HTML);
                actual = renderService.renderSlots([SLOT_ID_1, SLOT_ID_2]);
                $rootScope.$digest();
            });

            it('should build refreshed preview url', function() {
                expect(experienceService.buildRefreshedPreviewUrl).toHaveBeenCalled();
            });

            it('should call the $http service with the current page URL', function() {
                expect($http).toHaveBeenCalledWith(EXPECTED_HTTP_REQUEST_OBJECT);
            });

            it('should parse the response data', function() {
                expect(unsafeParseHTML.calls.count()).toBe(1);
                expect(unsafeParseHTML).toHaveBeenCalledWith(successHtmlResponse.data);
            });

            it('should extract the slots to re-render from the DOM of the fetched page', function() {
                expect(extractFromElement.calls.count()).toBe(2);
                expect(extractFromElement.calls.argsFor(0)[0]).toBe(root);
                expect(extractFromElement.calls.argsFor(0)[1]).toBe(EXPECTED_SLOT_1_SELECTOR);
                expect(extractFromElement.calls.argsFor(1)[0]).toBe(root);
                expect(extractFromElement.calls.argsFor(1)[1]).toBe(EXPECTED_SLOT_2_SELECTOR);
            });

            it('should get the existing slots elements to replace from the current storefront', function() {
                expect(componentHandlerService.getFromSelector.calls.count()).toBe(2);
                expect(componentHandlerService.getFromSelector.calls.argsFor(0)[0]).toBe(EXPECTED_SLOT_1_SELECTOR);
                expect(componentHandlerService.getFromSelector.calls.argsFor(1)[0]).toBe(EXPECTED_SLOT_2_SELECTOR);
            });

            it('should fetch the html of the new slot elements', function() {
                expect(fetchedSlotToRender1.html).toHaveBeenCalled();
                expect(fetchedSlotToRender2.html).toHaveBeenCalled();
            });

            it('should replace the html of the existing slot elements with that of the new slot elements', function() {
                expect(originalSlotToReplace1.html).toHaveBeenCalledWith(EXPECTED_FETCHED_SLOT_1_HTML);
                expect(originalSlotToReplace2.html).toHaveBeenCalledWith(EXPECTED_FETCHED_SLOT_2_HTML);
            });

            it('should re-process the page responsiveness', function() {
                expect(renderService._reprocessPage.calls.count()).toBe(1);
            });

            it('should return a resolved promise', function() {
                expect(actual).toBeResolved();
            });
        });
    });

    describe('_markSmartEditAsReady', function() {

        it('should publish smartEditReady event on the smartEditBootstrap gateway', function() {
            renderService._markSmartEditAsReady();
            expect(smarteditBootstrapGateway.publish).toHaveBeenCalledWith('smartEditReady');
        });

    });

    describe('toggleOverlay', function() {
        var overlay;

        beforeEach(function() {
            overlay = jasmine.createSpyObj('overlay', ['css']);
            componentHandlerService.getOverlay.and.returnValue(overlay);
        });

        it('should make the SmartEdit overlay visible when passed true', function() {
            renderService.toggleOverlay(true);
            expect(overlay.css).toHaveBeenCalledWith("visibility", "visible");
        });

        it('should make the SmartEdit overlay hidden when passed false', function() {
            renderService.toggleOverlay(false);
            expect(overlay.css).toHaveBeenCalledWith("visibility", "hidden");
        });
    });

    describe('refreshOverlayDimensions', function() {
        var BODY_TAG = 'body';

        var element, wrappedElement = {},
            parentOverlay, originalComponents;

        beforeEach(function() {
            element = {};
            parentOverlay = {};
            originalComponents = jasmine.createSpyObj('originalComponents', ['each']);
        });

        beforeEach(function() {
            componentHandlerService.getFromSelector.and.callFake(function(element) {
                if (element === 'body') {
                    return element;
                } else if (element === element) {
                    return wrappedElement;
                }
            });
            componentHandlerService.getFirstSmartEditComponentChildren.and.returnValue(originalComponents);
            spyOn(renderService, '_getParentInOverlay').and.returnValue(parentOverlay);
            spyOn(renderService, '_updateComponentSizeAndPosition');
        });

        it('should fetch the body as the element when given no parameters', function() {
            renderService.refreshOverlayDimensions();
            expect(componentHandlerService.getFromSelector).toHaveBeenCalledWith(BODY_TAG);
        });

        it('should use the element provided instead of the body when provided with an element', function() {
            renderService.refreshOverlayDimensions(element);
            expect(componentHandlerService.getFromSelector).not.toHaveBeenCalled();
        });

        it('refreshOverlayDimensions should update the overlay dimensions for each child and call itself on each child', function() {
            renderService.refreshOverlayDimensions(element);
            spyOn(renderService, 'refreshOverlayDimensions');
            var eachCallback = originalComponents.each.calls.argsFor(0)[0];
            eachCallback(0, element);
            expect(renderService._updateComponentSizeAndPosition).toHaveBeenCalledWith(wrappedElement);
            expect(renderService.refreshOverlayDimensions).toHaveBeenCalledWith(wrappedElement);
        });

    });

    describe('_updateComponentSizeAndPosition', function() {
        var COMPONENT_ID_ATTRIBUTE = 'data-smartedit-component-id';
        var COMPONENT_TYPE_ATTRIBUTE = 'data-smartedit-component-type';
        var ORIGINAL_ELEMENT_BOUNDING_CLIENT_RECT = {
            top: 123,
            left: 456
        };
        var PARENT_ELEMENT_BOUNDING_CLIENT_RECT = {
            top: 5,
            left: 10
        };

        var shallowCopy, componentInOverlay, componentInOverlayList, originalElement, parentOverlay, unwrappedOriginalElement, unwrappedParentOverlay;

        beforeEach(function() {
            shallowCopy = jasmine.createSpyObj('shallowCopy', ['width', 'height', 'css']);
            componentInOverlay = jasmine.createSpyObj('componentInOverlay', ['find']);
            componentInOverlay.style = {};
            componentInOverlayList = jasmine.createSpyObj('componentInOverlayList', ['get', 'find']);
            originalElement = jasmine.createSpyObj('originalElement', ['attr', 'get']);
            parentOverlay = jasmine.createSpyObj('parentOverlay', ['get']);
            parentOverlay.length = 1;
            unwrappedOriginalElement = jasmine.createSpyObj('unwrappedOriginalElement', ['getBoundingClientRect']);
            unwrappedParentOverlay = jasmine.createSpyObj('unwrappedParentOverlay', ['getBoundingClientRect']);

            spyOn(renderService, '_getParentInOverlay').and.callFake(function(element) {
                if (element === originalElement) {
                    return parentOverlay;
                }
                return null;
            });
        });

        beforeEach(function() {
            unwrappedOriginalElement.getBoundingClientRect.and.returnValue(ORIGINAL_ELEMENT_BOUNDING_CLIENT_RECT);
            unwrappedOriginalElement.offsetHeight = 789;
            unwrappedOriginalElement.offsetWidth = 890;

            originalElement.attr.and.callFake(function(attribute) {
                if (attribute === COMPONENT_ID_ATTRIBUTE) {
                    return COMPONENT_ID;
                } else if (attribute === COMPONENT_TYPE_ATTRIBUTE) {
                    return COMPONENT_TYPE;
                }
            });
            originalElement.get.and.returnValue(unwrappedOriginalElement);
            unwrappedParentOverlay.getBoundingClientRect.and.returnValue(PARENT_ELEMENT_BOUNDING_CLIENT_RECT);
            parentOverlay.get.and.returnValue(unwrappedParentOverlay);
            componentInOverlayList.get.and.returnValue(componentInOverlay);
            componentInOverlay.find.and.returnValue(shallowCopy);
            componentHandlerService.getComponentCloneInOverlay.and.returnValue(componentInOverlayList);
            componentHandlerService.getFromSelector.and.callFake(function(object) {
                return object; //to simplify testing
            });
        });

        it('_updateComponentSizeAndPosition should fetch the component in overlay', function() {
            renderService._updateComponentSizeAndPosition(originalElement);
            expect(componentHandlerService.getComponentCloneInOverlay).toHaveBeenCalledWith(originalElement);
            expect(componentInOverlayList.get).toHaveBeenCalledWith(0);
        });

        it('should not fetch the component in overlay if it is provided', function() {
            renderService._updateComponentSizeAndPosition(originalElement, componentInOverlay);
            expect(componentHandlerService.getComponentCloneInOverlay).not.toHaveBeenCalled();
            expect(componentInOverlayList.get).not.toHaveBeenCalled();
        });
        it('should update position and dimensions of the given overlay clone from the original component', function() {
            renderService._updateComponentSizeAndPosition(originalElement, componentInOverlay, parentOverlay);
            expect(componentInOverlay.find).toHaveBeenCalledWith('[id="someComponentId_someComponentType_overlay"]');
            expect(componentInOverlay.style).toEqual({
                position: 'absolute',
                top: '118px',
                left: '446px',
                width: '890px',
                height: '789px',
                minWidth: '51px',
                minHeight: '48px'
            });

            expect(shallowCopy.width).toHaveBeenCalledWith(890);
            expect(shallowCopy.height).toHaveBeenCalledWith(789);
            expect(shallowCopy.css.calls.allArgs()).toEqual([
                ['min-height', 49],
                ['min-width', 51]
            ]);
        });
    });

    describe('_cloneAndCompileComponent', function() {
        var EXPECTED_SHALLOW_COPY_ID = 'someComponentId_someComponentType_overlay';
        var EXPECTED_Z_INDEX_FOR_NAVIGATION = '7';
        var EXPECTED_OVERLAY_CLASS = 'smartEditComponentX';

        var componentInOverlay, element,
            parentOverlay, document, shallowCopy, smartEditWrapper, componentDecorator, compiled;
        var attrMap = {};

        beforeEach(function() {
            componentInOverlay = jasmine.createSpyObj('componentInOverlay', ['remove']);
            element = jasmine.createSpyObj('element', ['attr', 'get', 'parent', 'is']);
            element.is.and.returnValue(true);

            attrMap[COMPONENT_ID_ATTRIBUTE] = COMPONENT_ID;
            attrMap[COMPONENT_UUID_ATTRIBUTE] = COMPONENT_UUID;
            attrMap[COMPONENT_TYPE_ATTRIBUTE] = COMPONENT_TYPE;
            attrMap[COMPONENT_CATALOG_VERSION_ATTRIBUTE] = COMPONENT_CATALOG_VERSION;

            element.attr.and.callFake(function(attribute) {
                return attrMap[attribute];
            });
            element.get.and.returnValue({
                attributes: [{
                    nodeName: 'nonsmarteditattribute',
                    nodeValue: 'somevalue'
                }, {
                    nodeName: 'data-smartedit-component-id',
                    nodeValue: COMPONENT_ID
                }, {
                    nodeName: 'data-smartedit-component-type',
                    nodeValue: COMPONENT_TYPE
                }]
            });

            parentOverlay = jasmine.createSpyObj('parentOverlay', ['append']);
            parentOverlay.length = 1;
            document = jasmine.createSpyObj('document', ['createElement']);
            shallowCopy = {};
            smartEditWrapper = {};
            smartEditWrapper.style = {};
            componentDecorator = jasmine.createSpyObj('componentDecorator', ['addClass', 'attr', 'append']);
            compiled = {};

            var callCount = 0;
            document.createElement.and.callFake(function() {
                callCount++;
                return callCount === 1 ? shallowCopy : smartEditWrapper;
            });

            spyOn(renderService, '_getDocument').and.returnValue(document);
            spyOn(renderService, '_updateComponentSizeAndPosition');
            spyOn(renderService, '_compile').and.returnValue(compiled);

            componentHandlerService.getFromSelector.and.callFake(function(element) {
                if (element === smartEditWrapper) {
                    return componentDecorator;
                } else {
                    return element; //to ease testing
                }
            });

            spyOn(renderService, '_getParentInOverlay').and.callFake(function(element) {
                if (element === element) {
                    return parentOverlay;
                }
                return null;
            });

            renderService._cloneAndCompileComponent(element);
        });


        it('should create a shallow copy of the component and a SmartEdit decorator wrapper', function() {
            expect(renderService._getDocument.calls.count()).toBe(2);
            expect(document.createElement.calls.count()).toBe(2);
            expect(document.createElement.calls.argsFor(0)[0]).toBe('div');
            expect(document.createElement.calls.argsFor(1)[0]).toBe('smartedit-element');
        });

        it('should add an overlay identifier to the shallow copy element', function() {
            expect(shallowCopy.id).toBe(EXPECTED_SHALLOW_COPY_ID);
        });

        it('should update the overlay dimensions for the newly created SmartEdit decorator wrapper', function() {
            expect(renderService._updateComponentSizeAndPosition).toHaveBeenCalledWith(element, smartEditWrapper);
        });

        it('should fetch a wrapped SmartEdit decorator wrapper', function() {
            expect(componentHandlerService.getFromSelector).toHaveBeenCalledWith(smartEditWrapper);
        });

        it('should add the overlay class to the wrapped SmartEdit decorator wrapper', function() {
            expect(componentDecorator.addClass).toHaveBeenCalledWith(EXPECTED_OVERLAY_CLASS);
        });

        it('should copy attributes with "data-smartedit" prefix from the element to the wrapped SmartEdit decorator wrapper', function() {
            expect(element.get).toHaveBeenCalledWith(0);
            expect(componentDecorator.attr.calls.count()).toBe(2);
            expect(componentDecorator.attr.calls.argsFor(0)).toEqual(['data-smartedit-component-id', COMPONENT_ID]);
            expect(componentDecorator.attr.calls.argsFor(1)).toEqual(['data-smartedit-component-type', COMPONENT_TYPE]);
        });

        it('should append the shallow copy onto the SmartEdit decorator wrapper', function() {
            expect(componentDecorator.append).toHaveBeenCalledWith(shallowCopy);
        });

        it('should compile the SmartEdit decorator wrapper against the root scope', function() {
            expect(renderService._compile).toHaveBeenCalledWith(smartEditWrapper, $rootScope);
        });

        it('should append the compiled element on the parent overlay', function() {
            expect(parentOverlay.append).toHaveBeenCalledWith(compiled);
        });

        describe('when component is a NavigationBarCollectionComponent', function() {
            beforeEach(function() {
                attrMap[COMPONENT_TYPE_ATTRIBUTE] = 'NavigationBarCollectionComponent';
                renderService._cloneAndCompileComponent(element, parentOverlay);
            });

            it('should add a specific z-index to the styling of the SmartEdit decorator wrapper', function() {
                expect(smartEditWrapper.style.zIndex).toBe(EXPECTED_Z_INDEX_FOR_NAVIGATION);
            });
        });
    });

    describe('_getParentInOverlay', function() {
        var element, parent;

        beforeEach(function() {
            element = jasmine.createSpyObj('element', ['attr']);
            parent = {
                length: 1
            };
        });

        describe('when called with a SmartEdit component', function() {
            var expectedOverlay = {};
            var actualOverlay = {};
            var overlay = {};
            beforeEach(function() {
                expectedOverlay = {};
                componentHandlerService.getOverlayComponent.and.returnValue(expectedOverlay);
                componentHandlerService.getOverlay.and.returnValue(overlay);
            });

            beforeEach(function() {

            });

            it('should fetch the component in the overlay', function() {

                componentHandlerService.getParent.and.returnValue(parent);

                actualOverlay = renderService._getParentInOverlay(element);

                expect(componentHandlerService.getParent).toHaveBeenCalledWith(element);
                expect(componentHandlerService.getOverlayComponent).toHaveBeenCalledWith(parent);
                expect(actualOverlay).toBe(expectedOverlay);
            });

            it('should fetch the overlay', function() {

                componentHandlerService.getParent.and.returnValue({});

                actualOverlay = renderService._getParentInOverlay(element);

                expect(componentHandlerService.getParent).toHaveBeenCalledWith(element);
                expect(componentHandlerService.getOverlay).toHaveBeenCalled();
                expect(actualOverlay).toBe(overlay);
            });
        });

    });

    describe('_createComponent', function() {
        var element, parentOverlay, elements, children, emptyChildren;

        beforeEach(function() {
            element = {};
            parentOverlay = {};
            elements = ["child1", "child2"];
            children = jasmine.createSpyObj('children', ['each']);
            children.each.and.callFake(function(callback) {
                elements.forEach(function(child) {
                    callback(0, child);
                });
            });

            emptyChildren = jasmine.createSpyObj('emptyChildren', ['each']);
            emptyChildren.each.and.returnValue();


            spyOn(renderService, '_createComponent').and.callThrough();

            componentHandlerService.getFirstSmartEditComponentChildren.and.callFake(function(arg) {
                return arg === element ? children : emptyChildren;
            });

            spyOn(renderService, '_getParentInOverlay').and.returnValue(parentOverlay);
            spyOn(renderService, '_cloneAndCompileComponent');
            componentHandlerService.isOverlayOn.and.returnValue(true);
            componentHandlerService.getFromSelector.and.callFake(function(element) {
                return element;
            });
        });

        it('should clone and compile the targeted element only', function() {
            // Arrange
            spyOn(renderService, '_isComponentVisible').and.returnValue(true);

            // Act
            renderService._createComponent(element);

            // Assert
            expect(renderService._isComponentVisible.calls.count()).toBe(1);
            expect(renderService._cloneAndCompileComponent).toHaveBeenCalledWith(element);
            expect(renderService._createComponent.calls.count()).toBe(1);
        });

        it('should not clone if element is not visible', function() {
            // Arrange
            spyOn(renderService, '_isComponentVisible').and.callFake(function(_element) {
                if (element === _element) {
                    return false;
                }

                return true;
            });

            // Act
            renderService._createComponent(element);

            // Assert
            expect(renderService._isComponentVisible.calls.count()).toBe(1);
            expect(renderService._cloneAndCompileComponent).not.toHaveBeenCalled();
            expect(renderService._createComponent.calls.count()).toBe(1);
        });
    });

    describe('renderPage', function() {
        var overlay;

        beforeEach(function() {
            overlay = jasmine.createSpyObj('overlay', ['hide', 'show']);
        });

        beforeEach(function() {
            componentHandlerService.getOverlay.and.returnValue(overlay);
            perspectiveService.isEmptyPerspectiveActive.and.returnValue($q.when(true));

            spyOn(renderService, '_resizeSlots');
            spyOn(renderService, '_markSmartEditAsReady');
            spyOn(renderService, 'isRenderingBlocked').and.returnValue($q.when(true));
        });

        it('will publish overlay rerendered event', function() {
            renderService.renderPage(false);
            $rootScope.$digest();
            expect(crossFrameEventService.publish).toHaveBeenCalledWith('overlayRerendered');
        });

        it('call with isRerender:false will publish restartProcess', function() {
            renderService.renderPage(false);
            $rootScope.$digest();
            expect(systemEventService.publish).toHaveBeenCalledWith(CONTRACT_CHANGE_LISTENER_PROCESS_EVENTS.RESTART_PROCESS);
        });

        it('call with isRerender:true will publish restartProcess', function() {
            renderService.renderPage(true);
            $rootScope.$digest();
            expect(systemEventService.publish).toHaveBeenCalledWith(CONTRACT_CHANGE_LISTENER_PROCESS_EVENTS.RESTART_PROCESS);
        });

        it('should remove the overlay', function() {
            renderService.renderPage(false);
            $rootScope.$digest();
            expect(overlay.hide).toHaveBeenCalled();
        });

        it('when current perspective is "Preview" should not mark SmartEdit as ready', function() {
            renderService.renderPage(false);
            $rootScope.$digest();
            expect(renderService._markSmartEditAsReady).toHaveBeenCalled();
        });

        it('should resize the slots', function() {
            renderService.renderPage(false);
            $rootScope.$digest();

            expect(renderService._resizeSlots).toHaveBeenCalled();
        });
    });

    describe('_destroyComponent', function() {

        var elements;
        var child1;
        var child2;
        var component;
        var parent;
        var componentInOverlay;
        var slotId = "someSlotId";

        beforeEach(function() {
            parent = jasmine.createSpyObj('parent', ['attr']);
            component = jasmine.createSpyObj('component', ['attr']);
            child1 = jasmine.createSpyObj('child1', ['attr']);
            child2 = jasmine.createSpyObj('child2', ['attr']);
            elements = [child1, child2];
            componentInOverlay = jasmine.createSpyObj('componentInOverlay', ['remove']);

            component.attr.and.callFake(function(element) {
                if (element === COMPONENT_ID_ATTRIBUTE) {
                    return COMPONENT_ID;
                } else if (element === COMPONENT_TYPE_ATTRIBUTE) {
                    return COMPONENT_TYPE;
                }
                return null;
            });

            componentHandlerService.getOverlayComponentWithinSlot.and.returnValue(componentInOverlay);
            componentHandlerService.getComponentInOverlay.and.returnValue(componentInOverlay);

            componentHandlerService.getFromSelector.and.callFake(function(element) {
                return element;
            });


            var children = jasmine.createSpyObj('children', ['each']);
            children.each.and.callFake(function(callback) {
                elements.forEach(function(arg1) {
                    callback(0, arg1);
                });
            });
            var emptyChildren = jasmine.createSpyObj('emptyChildren', ['each']);
            emptyChildren.each.and.returnValue();

            spyOn(renderService, '_destroyComponent').and.callThrough();

            componentHandlerService.getFirstSmartEditComponentChildren.and.callFake(function(arg) {
                return arg === component ? children : emptyChildren;
            });

        });

        it('will destroy scope and overlay component with id and type from component', function() {
            parent.attr.and.returnValue(slotId);

            renderService._destroyComponent(component, parent);

            expect(sakExecutor.destroyScope).toHaveBeenCalledWith(component);

            expect(componentHandlerService.getOverlayComponentWithinSlot).toHaveBeenCalledWith(COMPONENT_ID, COMPONENT_TYPE, slotId);

            expect(componentInOverlay.remove).toHaveBeenCalled();
            expect(parent.attr).toHaveBeenCalledWith(COMPONENT_ID_ATTRIBUTE);
        });

        it('will destroy scope and overlay component with id and type from slot', function() {
            parent.attr.and.returnValue(undefined);

            renderService._destroyComponent(component, parent);

            expect(sakExecutor.destroyScope).toHaveBeenCalledWith(component);

            expect(componentHandlerService.getComponentInOverlay).toHaveBeenCalledWith(COMPONENT_ID, COMPONENT_TYPE);

            expect(componentInOverlay.remove).toHaveBeenCalled();
            expect(parent.attr).toHaveBeenCalledWith(COMPONENT_ID_ATTRIBUTE);
        });

        it('will destroy scope and overlay component with id and type from component from old attributes', function() {
            parent.attr.and.returnValue(slotId);

            var oldAttributes = {};
            oldAttributes[COMPONENT_ID_ATTRIBUTE] = 'oldId';
            oldAttributes[COMPONENT_TYPE_ATTRIBUTE] = 'oldType';

            renderService._destroyComponent(component, parent, oldAttributes);

            expect(sakExecutor.destroyScope).toHaveBeenCalledWith(component);

            expect(componentHandlerService.getOverlayComponentWithinSlot).toHaveBeenCalledWith('oldId', 'oldType', slotId);

            expect(componentInOverlay.remove).toHaveBeenCalled();
            expect(parent.attr).toHaveBeenCalledWith(COMPONENT_ID_ATTRIBUTE);
        });

        it('will call destroy on a component', function() {

            renderService._destroyComponent(component, parent, null);

            expect(renderService._destroyComponent.calls.count()).toBe(1);
            expect(renderService._destroyComponent).toHaveBeenCalledWith(component, parent, null);
        });

    });

});
