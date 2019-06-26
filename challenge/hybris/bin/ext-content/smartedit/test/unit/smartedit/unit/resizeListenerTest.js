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
describe('resizeListener', function() {

    var internalStatePropertyName = "_erd";
    var resizeListener, $window, $document, $interval, yjQuery;
    var element1, element2, element3;
    var listener1, listener2, listener3;
    var stillAttachedContainer, detachedFullContainer, detachedEmptyContainer;
    var stillAttachedContainerClone, detachedFullContainerClone, detachedEmptyContainerClone;
    var containerChild1, containerChild2, containerChild3;
    var erd;

    beforeEach(angular.mock.module('resizeListenerModule', function($provide) {


        erd = jasmine.createSpyObj('erd', ['uninstall', 'listenTo']);

        yjQuery = jasmine.createSpyObj('yjQuery', ['contains']);
        yjQuery.contains.and.callFake(function(container, element) {
            if (container !== $document[0]) {
                throw "yjQuery.contains should have been the plain document object";
            }
            if (element === stillAttachedContainer) {
                return true;
            } else if (element === detachedFullContainer || element === detachedEmptyContainer) {
                return false;
            } else {
                throw "unexpected invocation of yjQuery.contains";
            }
        });
        yjQuery.fn = {
            extend: function() {}
        };

        $provide.value("yjQuery", yjQuery);

        $window = jasmine.createSpyObj('$window', ['elementResizeDetectorMaker']);
        $window.elementResizeDetectorMaker.and.callFake(function(arg) {
            if (angular.equals(arg, {
                    strategy: "scroll"
                })) {
                return erd;
            } else {
                return null;
            }
        });
        $provide.value("$window", $window);

        containerChild1 = {};
        containerChild2 = {};
        containerChild3 = {};

        listener1 = jasmine.createSpy('listener1');
        listener2 = jasmine.createSpy('listener2');
        listener3 = jasmine.createSpy('listener3');

        element1 = jasmine.createSpyObj('element1', ['appendChild']);
        element1.name = "element1";
        element2 = jasmine.createSpyObj('element2', ['appendChild']);
        element2.name = "element2";
        element3 = jasmine.createSpyObj('element3', ['appendChild']);
        element3.name = "element3";

        stillAttachedContainer = jasmine.createSpyObj('stillAttachedContainer', ['hasChildNodes', 'appendChild', 'cloneNode']);
        stillAttachedContainer.hasChildNodes.and.returnValue(true);
        stillAttachedContainerClone = jasmine.createSpyObj('stillAttachedContainerClone', ['hasChildNodes']);
        stillAttachedContainerClone.hasChildNodes.and.returnValue(true);
        stillAttachedContainerClone.childNodes = [containerChild1];

        stillAttachedContainer.cloneNode.and.callFake(function(arg) {
            if (arg === true) {
                return stillAttachedContainerClone;
            }
            return null;
        });

        detachedFullContainer = jasmine.createSpyObj('detachedFullContainer', ['hasChildNodes', 'appendChild', 'cloneNode']);
        detachedFullContainer.hasChildNodes.and.returnValue(true);
        detachedFullContainerClone = jasmine.createSpyObj('detachedFullContainerClone', ['hasChildNodes']);
        detachedFullContainerClone.hasChildNodes.and.returnValue(true);
        detachedFullContainerClone.childNodes = [containerChild2];

        detachedFullContainer.cloneNode.and.callFake(function(arg) {
            if (arg === true) {
                return detachedFullContainerClone;
            }
            return null;
        });

        detachedEmptyContainer = jasmine.createSpyObj('detachedEmptyContainer', ['hasChildNodes', 'appendChild', 'cloneNode']);
        detachedEmptyContainer.hasChildNodes.and.returnValue(false);
        detachedEmptyContainerClone = jasmine.createSpyObj('detachedEmptyContainerClone', ['hasChildNodes']);
        detachedEmptyContainerClone.hasChildNodes.and.returnValue(true);
        detachedEmptyContainerClone.childNodes = [containerChild3];

        detachedEmptyContainer.cloneNode.and.callFake(function(arg) {
            if (arg === true) {
                return detachedEmptyContainerClone;
            }
            return null;
        });

        element1[internalStatePropertyName] = {
            container: stillAttachedContainer
        };
        element2[internalStatePropertyName] = {
            container: detachedFullContainer
        };
        element3[internalStatePropertyName] = {
            container: detachedEmptyContainer
        };

    }));

    beforeEach(inject(function(_$document_, _$interval_, _resizeListener_) {
        $document = _$document_;
        $interval = _$interval_;
        spyOn($interval, 'cancel');

        resizeListener = _resizeListener_;

        resizeListener.init();

        resizeListener.register(element1, listener1);
        resizeListener.register(element2, listener2);
        resizeListener.register(element3, listener3);

        $interval.flush(500);

    }));

    afterEach(function() {
        resizeListener.dispose();
    });

    it('when registering an element it delegates to third party listener', function() {

        expect(erd.listenTo).toHaveBeenCalledWith(element1, listener1);
        expect(erd.listenTo).toHaveBeenCalledWith(element2, listener2);
        expect(erd.listenTo).toHaveBeenCalledWith(element3, listener3);

    });

    it('unregistering element with still attached container will just call uninstall in third party library', function() {

        resizeListener.unregister(element1);

        expect(erd.uninstall).toHaveBeenCalledWith(element1);

        expect(element1.appendChild).not.toHaveBeenCalled();

        expect(stillAttachedContainer.appendChild).not.toHaveBeenCalled();

    });

    it('unregistering element with detached full container will reappend it to the element before calling uninstall in third party library', function() {

        resizeListener.unregister(element2);

        expect(erd.uninstall).toHaveBeenCalledWith(element2);

        expect(element2.appendChild).toHaveBeenCalledWith(detachedFullContainer);

        expect(stillAttachedContainer.appendChild).not.toHaveBeenCalled();
    });

    it('unregistering element with detached empty container will restore its children, reappend it to the element before calling uninstall in third party library', function() {

        resizeListener.unregister(element3);

        expect(erd.uninstall).toHaveBeenCalledWith(element3);

        expect(element3.appendChild).toHaveBeenCalledWith(detachedEmptyContainer);

        expect(detachedEmptyContainer.appendChild).toHaveBeenCalledWith(containerChild3);
    });

    it('dispose will empty registry', function() {

        resizeListener.dispose();
        expect($interval.cancel).toHaveBeenCalledWith(jasmine.any(Object));

        expect(erd.uninstall.calls.count()).toEqual(3);
        expect(erd.uninstall).toHaveBeenCalledWith(element1);
        expect(erd.uninstall).toHaveBeenCalledWith(element2);
        expect(erd.uninstall).toHaveBeenCalledWith(element3);

        erd.uninstall.calls.reset();

        resizeListener.unregister(element1);
        resizeListener.unregister(element2);
        resizeListener.unregister(element3);
        expect(erd.uninstall).not.toHaveBeenCalled();

    });

});
