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
describe('dragAndDropScrollingService', function() {

    // Variables
    var dragAndDropScrollingService, mocks, scrollAreas, rootScope, windowHeight;

    beforeEach(function() {
        // Variables
        windowHeight = 1000;

        // Test setup
        var harness = AngularUnitTestHelper.prepareModule('_dragAndDropScrollingModule')
            .mock('inViewElementObserver', 'addSelector')
            .mock('$window', 'requestAnimationFrame').and.returnValue(123)
            .mock('$window', 'height').and.returnValue(windowHeight)
            .mock('$window', 'cancelAnimationFrame')
            .withTranslations({
                'se.draganddrop.uihint.top': 'some top message',
                'se.draganddrop.uihint.bottom': 'some bottom message'
            })
            .service('_dragAndDropScrollingService');

        dragAndDropScrollingService = harness.service;
        mocks = harness.mocks;
        rootScope = harness.injected.$rootScope;

        scrollAreas = jasmine.createSpyObj('scrollAreas', ['on', 'off', 'appendTo', 'height', 'text', 'hide', 'show', 'remove', 'trigger']);

        spyOn(dragAndDropScrollingService, '_getScrollAreas').and.returnValue(scrollAreas);
        scrollAreas.appendTo.and.returnValue(scrollAreas);
    });

    describe('initialize', function() {
        var selector;

        beforeEach(function() {
            document.scrollingElement = null;

            selector = jasmine.createSpyObj('selector', ['appendTo']);

            spyOn(dragAndDropScrollingService, '_addScrollAreas');
            spyOn(dragAndDropScrollingService, '_addEventListeners').and.callThrough();
            spyOn(dragAndDropScrollingService, '_getSelector').and.returnValue(selector);
        });

        it('WHEN initialize is called THEN the service is initialized properly', function() {

            expect(mocks.inViewElementObserver.addSelector.calls.count()).toBe(2);
            expect(mocks.inViewElementObserver.addSelector).toHaveBeenCalledWith('#top_scroll_page');
            expect(mocks.inViewElementObserver.addSelector).toHaveBeenCalledWith('#bottom_scroll_page');
            // Arrange

            // Act
            dragAndDropScrollingService._initialize();

            // Assert
            expect(dragAndDropScrollingService._addScrollAreas).toHaveBeenCalled();
            expect(dragAndDropScrollingService._addEventListeners).toHaveBeenCalled();
            expect(dragAndDropScrollingService._scrollDelta).toBe(0);
            expect(dragAndDropScrollingService._initialized).toBe(true);
        });

        it('GIVEN the document.documentElement is defined THEN the scrollable area is the document.documentElement', function() {
            // Arrange
            expect(dragAndDropScrollingService._getSelector).not.toHaveBeenCalled();

            // Act
            dragAndDropScrollingService._initialize();

            // Assert
            expect(dragAndDropScrollingService._getSelector).toHaveBeenCalledWith(document.documentElement);
        });

        it('GIVEN the document.scrollingElement is defined THEN the scrollable area is the document.scrollingElement', function() {
            // Arrange
            expect(dragAndDropScrollingService._getSelector).not.toHaveBeenCalled();

            // Act
            document.documentElement = null;
            document.scrollingElement = {};
            dragAndDropScrollingService._initialize();

            // Assert
            expect(dragAndDropScrollingService._getSelector).toHaveBeenCalledWith(document.scrollingElement);
        });

    });

    it('WHEN _addEventListeners is called THEN the event listeners are set on the scroll areas', function() {
        // Arrange

        // Act
        dragAndDropScrollingService._addEventListeners();

        // Assert
        expect(scrollAreas.on).toHaveBeenCalledWith('dragenter', jasmine.any(Function));
        expect(scrollAreas.on).toHaveBeenCalledWith('dragover', jasmine.any(Function));
        expect(scrollAreas.on).toHaveBeenCalledWith('dragleave', jasmine.any(Function));
    });

    it('WHEN _removeEventListeners is called THEN the event listeners are set on the scroll areas', function() {
        // Arrange

        // Act
        dragAndDropScrollingService._removeEventListeners();

        // Assert
        expect(scrollAreas.off).toHaveBeenCalledWith('dragenter');
        expect(scrollAreas.off).toHaveBeenCalledWith('dragover');
        expect(scrollAreas.off).toHaveBeenCalledWith('dragleave');

        expect(scrollAreas.remove).toHaveBeenCalled();
    });

    it('WHEN _addScrollAreas is called THEN scroll areas are added', function() {
        // Arrange
        var topArea = jasmine.createSpyObj('topArea', ['css', 'appendTo', 'text']);
        var bottomArea = jasmine.createSpyObj('bottomArea', ['css', 'appendTo', 'text']);
        spyOn(dragAndDropScrollingService, '_getSelector').and.callFake(function(arg) {
            if (arg === '<div id="top_scroll_page" class="ySECmsScrollArea"></div>') {
                return topArea;
            } else if (arg === '<div id="bottom_scroll_page" class="ySECmsScrollArea"></div>') {
                return bottomArea;
            }
        });

        topArea.appendTo.and.returnValue(topArea);
        bottomArea.appendTo.and.returnValue(bottomArea);

        // Act
        dragAndDropScrollingService._addScrollAreas();
        rootScope.$digest();

        // Assert
        expect(dragAndDropScrollingService._getSelector).toHaveBeenCalledWith('<div id="top_scroll_page" class="ySECmsScrollArea"></div>');
        expect(dragAndDropScrollingService._getSelector).toHaveBeenCalledWith('<div id="bottom_scroll_page" class="ySECmsScrollArea"></div>');
        expect(scrollAreas.height).toHaveBeenCalledWith(jasmine.any(Number));

        expect(topArea.css).toHaveBeenCalledWith({
            top: 0
        });
        expect(bottomArea.css).toHaveBeenCalledWith({
            bottom: 0
        });

        expect(scrollAreas.hide).toHaveBeenCalled();
        expect(topArea.text).toHaveBeenCalledWith('some top message');
        expect(bottomArea.text).toHaveBeenCalledWith('some bottom message');
    });

    it('WHEN mouse enters the UI scrolling hints THEN animation starts', function() {
        // Arrange
        var isFnCalled = false;
        var event = jasmine.createSpyObj('event', ['target']);
        var scrollArea = jasmine.createSpyObj('scrollArea', ['attr']);
        spyOn(dragAndDropScrollingService, '_getSelector').and.returnValue(scrollArea);
        spyOn(dragAndDropScrollingService, '_scrollPage').and.callFake(function() {
            isFnCalled = true;
        });
        expect(dragAndDropScrollingService._animationFrameId).toBe(undefined);

        // Act
        dragAndDropScrollingService._onDragEnter(event);

        // Assert
        var callback = mocks.$window.requestAnimationFrame.calls.argsFor(0)[0];
        callback();

        expect(mocks.$window.requestAnimationFrame).toHaveBeenCalled();
        expect(isFnCalled).toBe(true);
        expect(dragAndDropScrollingService._animationFrameId).toBe(123);
    });

    describe('on mouse over', function() {
        var event, scrollArea;

        beforeEach(function() {
            dragAndDropScrollingService._scrollDelta = 0;

            scrollArea = jasmine.createSpyObj('scrollArea', ['attr']);
            spyOn(dragAndDropScrollingService, '_getSelector').and.callFake(function(arg) {
                if (arg === 'target') {
                    return scrollArea;
                } else {
                    return mocks.$window;
                }
            });

            event = {
                target: 'target'
            };
            event.originalEvent = event;
        });


        it('WHEN mouse is over top slow area THEN the page scrolls up slowly', function() {
            // Arrange
            event.clientY = dragAndDropScrollingService._FAST_SCROLLING_AREA_HEIGHT + 1;
            scrollArea.attr.and.returnValue('top_scroll_page');

            // Act
            dragAndDropScrollingService._onDragOver(event);

            // Assert
            expect(dragAndDropScrollingService._scrollDelta).toBe(-dragAndDropScrollingService._SCROLLING_STEP);
        });

        it('WHEN mouse is over top fast area THEN the page scrolls up quickly', function() {
            // Arrange
            event.clientY = dragAndDropScrollingService._FAST_SCROLLING_AREA_HEIGHT - 1;
            scrollArea.attr.and.returnValue('top_scroll_page');

            // Act
            dragAndDropScrollingService._onDragOver(event);

            // Assert
            expect(dragAndDropScrollingService._scrollDelta).toBe(-dragAndDropScrollingService._FAST_SCROLLING_STEP);
        });

        it('WHEN mouse is over bottom slow area THEN the page scrolls down slowly', function() {
            // Arrange
            event.clientY = windowHeight - dragAndDropScrollingService._FAST_SCROLLING_AREA_HEIGHT - 1;
            scrollArea.attr.and.returnValue('bottom_scroll_page');

            // Act
            dragAndDropScrollingService._onDragOver(event);

            // Assert
            expect(dragAndDropScrollingService._scrollDelta).toBe(dragAndDropScrollingService._SCROLLING_STEP);
        });

        it('WHEN mouse is over bottom fast area THEN the page scrolls down quickly', function() {
            // Arrange
            event.clientY = windowHeight - dragAndDropScrollingService._FAST_SCROLLING_AREA_HEIGHT + 1;
            scrollArea.attr.and.returnValue('bottom_scroll_page');

            // Act
            dragAndDropScrollingService._onDragOver(event);

            // Assert
            expect(dragAndDropScrollingService._scrollDelta).toBe(dragAndDropScrollingService._FAST_SCROLLING_STEP);
        });
    });

    it('WHEN mouse leaves scroll area THEN scrolling stops', function() {
        // Arrange
        var animationFrameId = 'some id';
        dragAndDropScrollingService._scrollDelta = 123;
        dragAndDropScrollingService._animationFrameId = animationFrameId;

        // Act
        dragAndDropScrollingService._onDragLeave();

        // Assert
        expect(dragAndDropScrollingService._scrollDelta).toBe(0);
        expect(mocks.$window.cancelAnimationFrame).toHaveBeenCalledWith(animationFrameId);
    });

    describe('_scrollPage', function() {
        var scrollLimit = 900;

        beforeEach(function() {
            dragAndDropScrollingService._scrollable = jasmine.createSpyObj('scrollable', ['scrollTop']);
            dragAndDropScrollingService._scrollLimitY = scrollLimit;
        });

        it('GIVEN scrollDelta is 0 WHEN _scrollPage is called THEN scrolling is not executed', function() {
            // Arrange
            dragAndDropScrollingService._scrollDelta = 0;
            spyOn(dragAndDropScrollingService, '_showScrollAreas');

            // Act
            dragAndDropScrollingService._scrollPage();

            // Assert
            expect(dragAndDropScrollingService._scrollable.scrollTop).not.toHaveBeenCalled();
            expect(mocks.$window.requestAnimationFrame).not.toHaveBeenCalled();
            expect(dragAndDropScrollingService._showScrollAreas).not.toHaveBeenCalled();
        });

        it('GIVEN the page is at the top WHEN _scrollPage is called to scroll up THEN scrolling is not executed', function() {
            // Arrange
            dragAndDropScrollingService._scrollDelta = -10;
            dragAndDropScrollingService._scrollable.scrollTop.and.returnValue(0);
            spyOn(dragAndDropScrollingService, '_showScrollAreas');

            // Act
            dragAndDropScrollingService._scrollPage();

            // Assert
            expect(mocks.$window.requestAnimationFrame).not.toHaveBeenCalled();
            expect(dragAndDropScrollingService._showScrollAreas).toHaveBeenCalled(); // Scrolling is not executed, yet areas are shown.
        });

        it('GIVEN the page is at the bottom WHEN _scrollPage is called to scroll down THEN scrolling is not executed', function() {
            // Arrange
            dragAndDropScrollingService._scrollDelta = 10;
            dragAndDropScrollingService._scrollable.scrollTop.and.returnValue(scrollLimit);
            spyOn(dragAndDropScrollingService, '_showScrollAreas');

            // Act
            dragAndDropScrollingService._scrollPage();

            // Assert
            expect(mocks.$window.requestAnimationFrame).not.toHaveBeenCalled();
            expect(dragAndDropScrollingService._showScrollAreas).toHaveBeenCalled(); // Scrolling is not executed, yet areas are shown.
        });

        it('GIVEN the page is not at the top WHEN _scrollPage is called to scroll up THEN scrolling is executed', function() {
            // Arrange
            dragAndDropScrollingService._scrollDelta = -10;
            dragAndDropScrollingService._scrollable.scrollTop.and.returnValue(10);
            spyOn(dragAndDropScrollingService, '_showScrollAreas');

            // Act
            dragAndDropScrollingService._scrollPage();

            // Assert
            expect(mocks.$window.requestAnimationFrame).toHaveBeenCalled();
            expect(dragAndDropScrollingService._showScrollAreas).toHaveBeenCalled(); // Scrolling is not executed, yet areas are shown.
        });

        it('GIVEN the page is not at the bottom WHEN _scrollPage is called to scroll down THEN scrolling is executed', function() {
            // Arrange
            dragAndDropScrollingService._scrollDelta = 10;
            dragAndDropScrollingService._scrollable.scrollTop.and.returnValue(scrollLimit - 10);
            spyOn(dragAndDropScrollingService, '_showScrollAreas');

            // Act
            dragAndDropScrollingService._scrollPage();

            // Assert
            expect(mocks.$window.requestAnimationFrame).toHaveBeenCalled();
            expect(dragAndDropScrollingService._showScrollAreas).toHaveBeenCalled(); // Scrolling is not executed, yet areas are shown.
        });
    });

    it('WHEN _deactivate is called THEN scrolling is completely removed.', function() {
        // Arrange
        spyOn(dragAndDropScrollingService, '_removeEventListeners');

        // Act
        dragAndDropScrollingService._deactivate();

        // Assert
        expect(dragAndDropScrollingService._removeEventListeners).toHaveBeenCalled();
        expect(dragAndDropScrollingService._scrollDelta).toBe(0);
        expect(dragAndDropScrollingService._initialized).toBe(false);
    });

    describe('_showScrollAreas', function() {
        var scrollLimit = 900;

        beforeEach(function() {
            dragAndDropScrollingService._scrollable = jasmine.createSpyObj('scrollable', ['scrollTop']);
            dragAndDropScrollingService._topScrollArea = jasmine.createSpyObj('topScrollArea', ['hide', 'show']);
            dragAndDropScrollingService._bottomScrollArea = jasmine.createSpyObj('topScrollArea', ['hide', 'show']);
            dragAndDropScrollingService._scrollLimitY = scrollLimit;
        });

        it('GIVEN page is at top WHEN _showScrollAreas is called THEN only bottom scroll area is shown', function() {
            // Arrange
            dragAndDropScrollingService._scrollable.scrollTop.and.returnValue(0);

            // Act
            dragAndDropScrollingService._showScrollAreas();

            // Assert
            expect(dragAndDropScrollingService._topScrollArea.show).not.toHaveBeenCalled();
            expect(dragAndDropScrollingService._topScrollArea.hide).toHaveBeenCalled();

            expect(dragAndDropScrollingService._bottomScrollArea.show).toHaveBeenCalled();
            expect(dragAndDropScrollingService._bottomScrollArea.hide).not.toHaveBeenCalled();
        });

        it('GIVEN page is at the bottom WHEN _showScrollAreas is called THEN only top scroll area is shown', function() {
            // Arrange
            dragAndDropScrollingService._scrollable.scrollTop.and.returnValue(scrollLimit);

            // Act
            dragAndDropScrollingService._showScrollAreas();

            // Assert
            expect(dragAndDropScrollingService._topScrollArea.show).toHaveBeenCalled();
            expect(dragAndDropScrollingService._topScrollArea.hide).not.toHaveBeenCalled();

            expect(dragAndDropScrollingService._bottomScrollArea.show).not.toHaveBeenCalled();
            expect(dragAndDropScrollingService._bottomScrollArea.hide).toHaveBeenCalled();
        });

        it('GIVEN page is not at top or bottom WHEN _showScrollAreas is called THEN both scroll area are shown', function() {
            // Arrange
            dragAndDropScrollingService._scrollable.scrollTop.and.returnValue(10);

            // Act
            dragAndDropScrollingService._showScrollAreas();

            // Assert
            expect(dragAndDropScrollingService._topScrollArea.show).toHaveBeenCalled();
            expect(dragAndDropScrollingService._topScrollArea.hide).not.toHaveBeenCalled();

            expect(dragAndDropScrollingService._bottomScrollArea.show).toHaveBeenCalled();
            expect(dragAndDropScrollingService._bottomScrollArea.hide).not.toHaveBeenCalled();
        });
    });

    it('WHEN enable is called THEN the scroll areas are shown', function() {
        // Arrange
        dragAndDropScrollingService._initialized = true;
        dragAndDropScrollingService._scrollable = {
            get: function() {
                return {
                    scrollHeight: 0
                };
            }
        };

        spyOn(dragAndDropScrollingService, '_getSelector').and.returnValue(mocks.$window);
        spyOn(dragAndDropScrollingService, '_showScrollAreas');

        // Act
        dragAndDropScrollingService._enable();

        // Assert
        expect(dragAndDropScrollingService._showScrollAreas).toHaveBeenCalled();
    });

    it('WHEN disable is called THEN the scroll areas are hidden the drag leave called (protection against IE bug where we lose track fo the mouse and drop must take care of firing leave)', function() {
        // Arrange
        dragAndDropScrollingService._initialized = true;

        // Act
        dragAndDropScrollingService._disable();

        // Assert
        expect(scrollAreas.trigger).toHaveBeenCalledWith('dragleave');
        expect(scrollAreas.hide).toHaveBeenCalled();
    });
});
