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
describe('positionRegistry', function() {

    var positionRegistry, componentHandlerService, body, element1, element2, element3;
    var bodyBoundingRect, element1BoundingRect, element2BoundingRect, element3BoundingRect;

    beforeEach(angular.mock.module('positionRegistryModule', function($provide) {

        bodyBoundingRect = {
            top: 5,
            left: 7
        };
        element1BoundingRect = {
            top: 1,
            left: 2
        };
        element2BoundingRect = {
            top: 2,
            left: 3
        };
        element3BoundingRect = {
            top: 3,
            left: 4
        };

        body = jasmine.createSpyObj('body', ['getBoundingClientRect']);
        body.getBoundingClientRect.and.returnValue(bodyBoundingRect);
        element1 = jasmine.createSpyObj('element1', ['getBoundingClientRect', 'height']);
        element1.getBoundingClientRect.and.returnValue(element1BoundingRect);
        element2 = jasmine.createSpyObj('element2', ['getBoundingClientRect', 'height']);
        element2.getBoundingClientRect.and.returnValue(element2BoundingRect);
        element3 = jasmine.createSpyObj('element3', ['getBoundingClientRect', 'height']);
        element3.getBoundingClientRect.and.returnValue(element3BoundingRect);

        componentHandlerService = jasmine.createSpyObj('componentHandlerService', ['getFromSelector']);
        componentHandlerService.getFromSelector.and.callFake(function(arg) {
            if (arg === 'body') {
                return [body];
            } else {
                return arg;
            }
        });
        $provide.value("componentHandlerService", componentHandlerService);

    }));

    beforeEach(inject(function(_positionRegistry_) {

        positionRegistry = _positionRegistry_;

        positionRegistry.register(element1);
        positionRegistry.register(element2);
        positionRegistry.register(element3);

    }));

    afterEach(function() {
        positionRegistry.dispose();
    });

    it('when element positions change they are detected', function() {

        element1BoundingRect.left = 2.01;
        element3BoundingRect.top = 10;

        var repositionedComponents = positionRegistry.getRepositionedComponents();

        expect(repositionedComponents.length).toEqualData(2);
        expect(repositionedComponents[0]).toBe(element1);
        expect(repositionedComponents[1]).toBe(element3);

    });

    it('reregistering an element is innocuous', function() {

        positionRegistry.register(element1);

        element1BoundingRect.left = 2.01;
        element3BoundingRect.top = 10;

        var repositionedComponents = positionRegistry.getRepositionedComponents();

        expect(repositionedComponents.length).toEqualData(2);
        expect(repositionedComponents[0]).toBe(element3);
        expect(repositionedComponents[1]).toBe(element1);

    });

    it('positions change less than a 100th pixel are not detected', function() {

        element1BoundingRect.left = 2.001;

        expect(positionRegistry.getRepositionedComponents().length).toEqualData(0);
    });

    it('positions change are only detected once', function() {

        element1BoundingRect.left = 2.01;
        element3BoundingRect.top = 10;

        positionRegistry.getRepositionedComponents();
        var repositionedComponents = positionRegistry.getRepositionedComponents();

        expect(repositionedComponents.length).toEqualData(0);
    });

    it('dispose will empty registry', function() {

        element1BoundingRect.left = 2.01;
        element2BoundingRect.left = 10;
        element3BoundingRect.top = 10;

        positionRegistry.dispose();

        expect(positionRegistry.getRepositionedComponents().length).toEqualData(0);
    });

});
