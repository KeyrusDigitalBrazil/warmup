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
describe('sliderPanelService', function() {

    var harness,
        mocks,
        mockedDiv,
        sliderPanelService;

    beforeEach(function() {

        harness = AngularUnitTestHelper.prepareModule('sliderPanelModule').service('sliderPanelServiceFactory');
        mocks = harness.mocks;

        // building mock HTML structure and appending it to the body element
        mockedDiv = document.createElement("div");
        document.body.appendChild(mockedDiv);
        mockedDiv = angular.element(mockedDiv);

    });

    afterEach(function() {
        mockedDiv.remove();
    });

    describe('sliderPanel instantiation', function() {

        it('returns the correct default configuration', function() {

            sliderPanelService = harness.service.getNewServiceInstance(mockedDiv, window, {});
            expect(sliderPanelService.sliderPanelConfiguration).toEqual({
                slideFrom: "right",
                overlayDimension: "80%"
            });

        });

        it('returns the updated configuration', function() {

            var expectedConfiguration = {
                key: "value",
                slideFrom: "bottom",
                overlayDimension: "80%",
            };

            sliderPanelService = harness.service.getNewServiceInstance(mockedDiv, window, {
                key: "value",
                slideFrom: "bottom"
            });
            expect(sliderPanelService.sliderPanelConfiguration).toEqual(expectedConfiguration);

        });

        it('appends the slider panel as last child of the body element', function() {

            spyOn(document.body, "appendChild");
            sliderPanelService = harness.service.getNewServiceInstance(mockedDiv, window, {});
            expect(document.body.appendChild).toHaveBeenCalledWith(mockedDiv[0]);

        });

    });

});
