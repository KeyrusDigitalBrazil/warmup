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
describe('yPopupOverlay - yPopupEngineService', function() {

    var yPopupEngineService,
        $rootScope,
        $document,
        $timeout,
        $window;

    var elementResizeDetectorMaker,
        service,
        popper,
        anchor;

    beforeEach(angular.mock.module('yPopupOverlayModule', function($provide) {
        popper = jasmine.createSpyObj('Popper', ['scheduleUpdate', 'destroy', 'update']);
        elementResizeDetectorMaker = jasmine.createSpyObj('elementResizeDetectorMaker', ['uninstall', 'listenTo']);

        $window = jasmine.createSpyObj('$window', ['elementResizeDetectorMaker', 'Popper']);

        $window.elementResizeDetectorMaker.and.callFake(function(arg) {
            if (angular.equals(arg, {
                    strategy: "scroll"
                })) {
                return elementResizeDetectorMaker;
            } else {
                return null;
            }
        });
        $window.Popper.and.returnValue(popper);
        $provide.value("$window", $window);
        $provide.value("$document", angular.element(document));
    }));


    beforeEach(inject(function(_yPopupEngineService_, _$document_, _$rootScope_, _$timeout_) {
        yPopupEngineService = _yPopupEngineService_;
        $rootScope = _$rootScope_.$new(true);
        $document = _$document_;
        $timeout = _$timeout_;
    }));

    beforeEach(function() {
        anchor = angular.element('<div>Anchor</div>')[0];
    });

    it('should initialize the engine with default attributes', function() {
        spyOn(yPopupEngineService.prototype, 'configure');

        service = new yPopupEngineService(anchor, '<div>Body</div>', $rootScope);

        expect(yPopupEngineService.prototype.configure).toHaveBeenCalled();

        expect(service.isOpen).toBe(false);
        expect(service.scope).toBe($rootScope);
        expect(service.anchorElement).toBe(anchor);
        expect(service.eventListeners.length).toBe(0);
        expect(service.template).toBe('<div>Body</div>');
    });

    it('should configure the engine with the configurations', function() {
        spyOn(yPopupEngineService.prototype, 'setTrigger');

        service = new yPopupEngineService(anchor, '<div>Body</div>', $rootScope);

        service.configure({
            placement: 'top',
            trigger: 'click',
            onShow: function() {
                this.isOpen = true;
            }.bind(this),
            onHide: function() {
                this.isOpen = false;
            }.bind(this),
            onChanges: function() {

            }.bind(this)
        });

        expect(yPopupEngineService.prototype.setTrigger).toHaveBeenCalled();
        expect(service.container).toBe($document[0].body);

        expect(service.config.placement).toBe('top');
        expect(service.config.trigger).toBe('click');
        expect(service.config.onClickOutside).toBe('close');
    });

    it('should show the popup', function() {
        var onShow = jasmine.createSpy('onShow');

        service = new yPopupEngineService(anchor, '<div>An awesome popup.</div>', $rootScope, {
            onShow: onShow
        });

        service.show();

        expect(service.isOpen).toBe(true);
        expect(service.onShow).toHaveBeenCalled();
        expect(elementResizeDetectorMaker.listenTo).toHaveBeenCalled();
        expect(service.popupInstance).toBeDefined();
        expect($document.find('body').html()).toContain('An awesome popup.');
    });

    it('should hide the popup', function() {
        var onHide = jasmine.createSpy('onHide');

        service = new yPopupEngineService(anchor, '<div>An awesome popup.</div>', $rootScope, {
            onHide: onHide
        });

        service.show();

        service.hide();

        expect(service.onHide).toHaveBeenCalled();
        expect(service.popupInstance.destroy).toHaveBeenCalled();
        expect(elementResizeDetectorMaker.uninstall).toHaveBeenCalled();
        expect($document.find('body').html()).not.toContain('An awesome popup.');
    });

    it('should set the trigger to click', function() {
        spyOn(yPopupEngineService.prototype, 'configure');
        spyOn(anchor, 'addEventListener');
        spyOn(anchor, 'removeEventListener');

        service = new yPopupEngineService(anchor, '<div>Body</div>', $rootScope);
        service.config = {};
        service.setTrigger('click');

        expect(service.config.trigger).toBe('click');
        expect(anchor.addEventListener.calls.argsFor(0)[0]).toBe('click');

        service.dispose();

        expect(anchor.removeEventListener.calls.argsFor(0)[0]).toBe('click');
        expect(service.eventListeners.length).toBe(0);
    });

    it('should set the trigger to focus', function() {
        spyOn(yPopupEngineService.prototype, 'configure');
        spyOn(anchor, 'addEventListener');
        spyOn(anchor, 'removeEventListener');

        service = new yPopupEngineService(anchor, '<div>Body</div>', $rootScope);
        service.config = {};
        service.setTrigger('focus');

        expect(service.config.trigger).toBe('focus');
        expect(anchor.addEventListener.calls.argsFor(0)[0]).toBe('focus');
        expect(anchor.addEventListener.calls.argsFor(1)[0]).toBe('blur');
        expect(service.eventListeners.length).toBe(2);

        service.dispose();

        expect(anchor.removeEventListener).toHaveBeenCalledTimes(2);
        expect(service.eventListeners.length).toBe(0);
    });

    it('should set the trigger to hover', function() {
        spyOn(yPopupEngineService.prototype, 'configure');
        spyOn(anchor, 'addEventListener');
        spyOn(anchor, 'removeEventListener');

        service = new yPopupEngineService(anchor, '<div>Body</div>', $rootScope);
        service.config = {};
        service.setTrigger('hover');

        expect(service.config.trigger).toBe('hover');
        expect(anchor.addEventListener.calls.argsFor(0)[0]).toBe('mouseenter');
        expect(anchor.addEventListener.calls.argsFor(1)[0]).toBe('mouseleave');
        expect(service.eventListeners.length).toBe(2);

        service.dispose();

        expect(anchor.removeEventListener).toHaveBeenCalledTimes(2);
        expect(service.eventListeners.length).toBe(0);
    });

    it('should change trigger from hover to false', function() {
        spyOn(yPopupEngineService.prototype, 'configure');
        spyOn(anchor, 'addEventListener');
        spyOn(anchor, 'removeEventListener');

        service = new yPopupEngineService(anchor, '<div>Body</div>', $rootScope);
        service.config = {};
        service.setTrigger('hover');

        expect(service.eventListeners.length).toBe(2);
        spyOn(service, 'hide');

        service.setTrigger(false);

        expect(anchor.removeEventListener).toHaveBeenCalledTimes(2);
        expect(service.hide).toHaveBeenCalled();
    });

    afterEach(function() {
        service.dispose();
    });

});
