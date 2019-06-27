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
describe('functionsModule - injectJS', function() {

    var injectorMockHolder, injectJS;
    var appLocations = ['SEContainerLocationForAppA', 'SEContainerLocationForAppB'];

    beforeEach(angular.mock.module('functionsModule'));
    beforeEach(inject(function(_injectJS_) {
        injectJS = _injectJS_;
    }));

    it('injectJS will injects all sources in sequence and then call an optional callback', function() {

        injectorMockHolder = jasmine.createSpyObj('injectorMockHolder', ['scriptJSMock']);
        injectorMockHolder.scriptJSMock.and.callFake(function(url, scriptCallback) {
            scriptCallback();
        });

        var callback = jasmine.createSpy('callback');
        spyOn(injectJS, 'getInjector').and.returnValue(injectorMockHolder.scriptJSMock);
        injectJS.execute({
            srcs: appLocations,
            callback: callback
        });

        expect(injectorMockHolder.scriptJSMock.calls.count()).toBe(2);

        expect(injectorMockHolder.scriptJSMock.calls.argsFor(0)[0]).toEqualData('SEContainerLocationForAppA', jasmine.any(Function));
        expect(injectorMockHolder.scriptJSMock.calls.argsFor(1)[0]).toEqualData('SEContainerLocationForAppB', jasmine.any(Function));
        expect(callback).toHaveBeenCalled();
    });

});
