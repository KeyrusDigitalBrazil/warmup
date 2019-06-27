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
describe('PerspectiveSelectorController', function() {

    var $componentController, PerspectiveSelectorController,
        iframeClickDetectionService, systemEventService, crossFrameEventService, perspectiveService, $document,
        EVENT_PERSPECTIVE_ADDED, EVENT_PERSPECTIVE_CHANGED, EVENT_PERSPECTIVE_REFRESHED, ALL_PERSPECTIVE, EVENTS,
        unRegOverlayDisabledFn, unRegPerspectiveAddedFn, unRegPerspectiveChgFn, unRegPerspectiveRefreshFn, unRegUserHasChanged, $q;

    var PERSPECTIVE_SELECTOR_CLOSE = 'perspectiveSelectorClose';
    var OVERLAY_DISABLED = 'OVERLAY_DISABLED';


    beforeEach(angular.mock.module('smarteditCommonsModule', function($provide) {
        var gatewayFactory = jasmine.createSpyObj('gatewayFactory', ['initListener']);
        $provide.value('gatewayFactory', gatewayFactory);

        var gatewayProxy = jasmine.createSpyObj('gatewayProxy', ['initForService']);
        $provide.value('gatewayProxy', gatewayProxy);
    }));

    /*
     * In order to test the perspective selector component's controller, several event handlers and
     * event services need to be mocked.
     *
     * The controller registers an event handler for when the overlay is disabled, when a new perspective
     * is added, registers a callback to close the dropdown when a click occurs in the iFrame and subscribes
     * to a cross-frame event to listen for perspective changes.
     *
     * When event handlers are registered on the systemEventService and when we subscribe to an event on
     * the crossFrameEventService, an un-registration function is returned. This function must be called when
     * the controller is destroyed. In the perspective selector's controller, these functions are stored in
     * private variables. Therefore, to test them, we create three spies, which are returned when the
     * systemEventService and crossFrameEventService mocks are called to register and subscribe. This way,
     * it is possible to verify that these functions are called when the controller is destroyed.
     */
    beforeEach(angular.mock.module('perspectiveSelectorModule', function($provide) {
        unRegOverlayDisabledFn = jasmine.createSpy('unRegOverlayDisabledFn');
        unRegPerspectiveAddedFn = jasmine.createSpy('unRegPerspectiveAddedFn');
        unRegPerspectiveChgFn = jasmine.createSpy('unRegPerspectiveChgFn');
        unRegPerspectiveRefreshFn = jasmine.createSpy('unRegPerspectiveRefreshFn');
        unRegUserHasChanged = jasmine.createSpy('unRegUserHasChanged');

        iframeClickDetectionService = jasmine.createSpyObj('iframeClickDetectionService', ['registerCallback']);
        $provide.value('iframeClickDetectionService', iframeClickDetectionService);

        systemEventService = jasmine.createSpyObj('systemEventService', ['subscribe']);
        systemEventService.subscribe.and.callFake(function(identifier) {
            switch (identifier) {
                case OVERLAY_DISABLED:
                    return unRegOverlayDisabledFn;

                case EVENT_PERSPECTIVE_ADDED:
                    return unRegPerspectiveAddedFn;
            }
        });
        $provide.value('systemEventService', systemEventService);

        crossFrameEventService = jasmine.createSpyObj('crossFrameEventService', ['subscribe']);
        crossFrameEventService.subscribe.and.callFake(function(identifier) {
            switch (identifier) {
                case EVENT_PERSPECTIVE_CHANGED:
                    return unRegPerspectiveChgFn;

                case EVENT_PERSPECTIVE_REFRESHED:
                    return unRegPerspectiveRefreshFn;

                case EVENTS.USER_HAS_CHANGED:
                    return unRegUserHasChanged;
            }
        });
        $provide.value('crossFrameEventService', crossFrameEventService);

        $document = jasmine.createSpyObj('$document', ['on']);
        $provide.value('$document', $document);

        perspectiveService = jasmine.createSpyObj('perspectiveService', ['getPerspectives', 'switchTo', 'isEmptyPerspectiveActive', 'getActivePerspective']);

        $provide.value('perspectiveService', perspectiveService);
    }));

    beforeEach(inject(function(_$componentController_, _EVENT_PERSPECTIVE_ADDED_, _EVENT_PERSPECTIVE_CHANGED_, _EVENT_PERSPECTIVE_REFRESHED_, _ALL_PERSPECTIVE_, _EVENTS_, _$q_) {
        $componentController = _$componentController_;
        PerspectiveSelectorController = $componentController('perspectiveSelector', null);

        EVENT_PERSPECTIVE_ADDED = _EVENT_PERSPECTIVE_ADDED_;
        EVENT_PERSPECTIVE_CHANGED = _EVENT_PERSPECTIVE_CHANGED_;
        EVENT_PERSPECTIVE_REFRESHED = _EVENT_PERSPECTIVE_REFRESHED_;
        ALL_PERSPECTIVE = _ALL_PERSPECTIVE_;
        EVENTS = _EVENTS_;
        $q = _$q_;
    }));

    beforeEach(function() {
        perspectiveService.getPerspectives.and.returnValue($q.when([]));
    });

    describe('refreshPerspectives', function() {
        it('should call perspectiveService.getPerspectives() to make sure the correct perspectives are populated', function() {
            PerspectiveSelectorController.refreshPerspectives();
            expect(perspectiveService.getPerspectives).toHaveBeenCalled();
        });
    });

    describe('initialization', function() {
        it('controller is initialized with correct data', function() {

            expect(PerspectiveSelectorController.isOpen).toBe(false);
            expect(PerspectiveSelectorController.getDisplayedPerspectives()).toEqual([]);
            expect(PerspectiveSelectorController.getActivePerspectiveName()).toBeFalsy();
            expect(PerspectiveSelectorController.isTooltipVisible()).toBe(false);
        });

        it('$onInit registers callback on iframeClickDetectionService', function() {

            // Given/When
            PerspectiveSelectorController.$onInit();

            // Then
            expect(iframeClickDetectionService.registerCallback).toHaveBeenCalled();
            expect(iframeClickDetectionService.registerCallback.calls.argsFor(0).length).toEqual(2);
            expect(iframeClickDetectionService.registerCallback.calls.argsFor(0)[0]).toEqual(PERSPECTIVE_SELECTOR_CLOSE);
            expect(iframeClickDetectionService.registerCallback.calls.argsFor(0)[1]).toBeTruthy();
        });

        it('$onInit registers event handler on systemEventService for OVERLAY_DISABLED', function() {

            // Given/When
            PerspectiveSelectorController.$onInit();

            // Then
            expect(systemEventService.subscribe).toHaveBeenCalled();
            expect(systemEventService.subscribe.calls.argsFor(0).length).toEqual(2);
            expect(systemEventService.subscribe.calls.argsFor(0)[0]).toEqual(OVERLAY_DISABLED);
            expect(systemEventService.subscribe.calls.argsFor(0)[1]).toBeTruthy();
        });

        it('$onInit registers event handler on systemEventService for EVENT_PERSPECTIVE_ADDED', function() {

            // Given/When
            PerspectiveSelectorController.$onInit();

            // Then
            expect(systemEventService.subscribe).toHaveBeenCalled();
            expect(systemEventService.subscribe.calls.argsFor(1).length).toEqual(2);
            expect(systemEventService.subscribe.calls.argsFor(1)[0]).toEqual(EVENT_PERSPECTIVE_ADDED);
            expect(systemEventService.subscribe.calls.argsFor(1)[1]).toBeTruthy();
        });

        it('$onInit subscribes to EVENT_PERSPECTIVE_CHANGED on crossFrameEventService', function() {

            // Given/When
            PerspectiveSelectorController.$onInit();

            // Then
            expect(crossFrameEventService.subscribe).toHaveBeenCalled();
            expect(crossFrameEventService.subscribe.calls.argsFor(0).length).toEqual(2);
            expect(crossFrameEventService.subscribe.calls.argsFor(0)[0]).toEqual(EVENT_PERSPECTIVE_CHANGED);
            expect(crossFrameEventService.subscribe.calls.argsFor(0)[1]).toBeTruthy();
        });

        it('$onInit subscribes to EVENT_PERSPECTIVE_REFRESHED on crossFrameEventService', function() {

            // Given/When
            PerspectiveSelectorController.$onInit();

            // Then
            expect(crossFrameEventService.subscribe).toHaveBeenCalled();
            expect(crossFrameEventService.subscribe.calls.argsFor(1).length).toEqual(2);
            expect(crossFrameEventService.subscribe.calls.argsFor(1)[0]).toEqual(EVENT_PERSPECTIVE_REFRESHED);
            expect(crossFrameEventService.subscribe.calls.argsFor(1)[1]).toBeTruthy();
        });

        it('$onInit subscribes to USER_HAS_CHANGED on crossFrameEventService', function() {

            // Given/When
            PerspectiveSelectorController.$onInit();

            // Then
            expect(crossFrameEventService.subscribe).toHaveBeenCalled();
            expect(crossFrameEventService.subscribe.calls.argsFor(2).length).toEqual(2);
            expect(crossFrameEventService.subscribe.calls.argsFor(2)[0]).toEqual(EVENTS.USER_HAS_CHANGED);
            expect(crossFrameEventService.subscribe.calls.argsFor(2)[1]).toBeTruthy();
        });

        it('$onInit registers callback on $document to detect clicks', function() {

            // Given/When
            PerspectiveSelectorController.$onInit();

            // Then
            expect($document.on).toHaveBeenCalled();
            expect($document.on.calls.argsFor(0).length).toEqual(2);
            expect($document.on.calls.argsFor(0)[0]).toEqual('click');
            expect($document.on.calls.argsFor(0)[1]).toBeTruthy();
        });
    });

    describe('destruction', function() {
        it('$onDestroy un-registers the OVERLAY_DISABLED event handler on systemEventService', function() {

            // Given
            PerspectiveSelectorController.$onInit();

            // When
            PerspectiveSelectorController.$onDestroy();

            // Then
            expect(unRegOverlayDisabledFn).toHaveBeenCalled();
        });

        it('$onDestroy un-registers the EVENT_PERSPECTIVE_ADDED event handler on systemEventService', function() {

            // Given
            PerspectiveSelectorController.$onInit();

            // When
            PerspectiveSelectorController.$onDestroy();

            // Then
            expect(unRegPerspectiveAddedFn).toHaveBeenCalled();
        });

        it('$onDestroy un-registers the EVENT_PERSPECTIVE_CHANGED from crossFrameEventService', function() {

            // Given
            PerspectiveSelectorController.$onInit();

            // When
            PerspectiveSelectorController.$onDestroy();

            // Then
            expect(unRegPerspectiveChgFn).toHaveBeenCalled();
        });

        it('$onDestroy un-registers the EVENTS.USER_HAS_CHANGED from crossFrameEventService', function() {

            // Given
            PerspectiveSelectorController.$onInit();

            // When
            PerspectiveSelectorController.$onDestroy();

            // Then
            expect(unRegUserHasChanged).toHaveBeenCalled();
        });
    });

    describe('_filterPerspectives', function() {
        it('expect active perspective to not be displayed', function() {

            // Given
            var PERSPECTIVE_1 = {
                key: "perspective1"
            };

            var PERSPECTIVE_2 = {
                key: "perspective2"
            };

            var perspectives = [PERSPECTIVE_1, PERSPECTIVE_2];

            perspectiveService.getActivePerspective.and.returnValue(PERSPECTIVE_1);
            PerspectiveSelectorController._refreshActivePerspective();

            // When
            var displayedPerspectives = PerspectiveSelectorController._filterPerspectives(perspectives);

            // Then
            expect(displayedPerspectives).toEqual([PERSPECTIVE_2]);

        });

        it('expect all perspective to display when there is no active perspectives', function() {

            // Given
            var PERSPECTIVE_1 = {
                key: "perspective1"
            };

            var PERSPECTIVE_2 = {
                key: "perspective2"
            };

            var perspectives = [PERSPECTIVE_1, PERSPECTIVE_2];

            // When
            var displayedPerspectives = PerspectiveSelectorController._filterPerspectives(perspectives);

            // Then
            expect(displayedPerspectives).toEqual([PERSPECTIVE_1, PERSPECTIVE_2]);

        });

        it('expect all perspective not to display', function() {

            // Given
            var PERSPECTIVE_1 = {
                key: "perspective1"
            };

            var PERSPECTIVE_2 = {
                key: "perspective2"
            };

            var PERSPECTIVE_3 = {
                key: ALL_PERSPECTIVE
            };

            var perspectives = [PERSPECTIVE_1, PERSPECTIVE_2, PERSPECTIVE_3];

            perspectiveService.getActivePerspective.and.returnValue(PERSPECTIVE_1);
            PerspectiveSelectorController._refreshActivePerspective();

            // When
            var displayedPerspectives = PerspectiveSelectorController._filterPerspectives(perspectives);

            // Then
            expect(displayedPerspectives).toEqual([PERSPECTIVE_2]);
        });
    });

    describe('isTooltipVisible', function() {
        it('returns false when no perspective is active', function() {
            // When
            var isTooltipVisible = PerspectiveSelectorController.isTooltipVisible();

            // Then
            expect(isTooltipVisible).toEqual(false);

        });

        it('returns false when a perspective is active but has no description key', function() {
            // Given
            var PERSPECTIVE_1 = {
                key: "perspective1"
            };

            perspectiveService.getActivePerspective.and.returnValue(PERSPECTIVE_1);
            PerspectiveSelectorController._refreshActivePerspective();

            // When
            var isTooltipVisible = PerspectiveSelectorController.isTooltipVisible();

            // Then
            expect(isTooltipVisible).toEqual(false);
        });

        it('returns true when no perspective is active and has a description key', function() {
            // Given
            var PERSPECTIVE_1 = {
                key: "perspective1",
                descriptionI18nKey: 'some.description.key'
            };

            perspectiveService.getActivePerspective.and.returnValue(PERSPECTIVE_1);
            PerspectiveSelectorController._refreshActivePerspective();

            // When
            var isTooltipVisible = PerspectiveSelectorController.isTooltipVisible();

            // Then
            expect(isTooltipVisible).toEqual(true);
        });

    });

});
