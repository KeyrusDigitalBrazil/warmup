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
describe('abAnalyticsService', function() {

    var service;

    beforeEach(function() {
        var fixture = AngularUnitTestHelper.prepareModule('abAnalyticsServiceModule')
            .service('abAnalyticsService');
        service = fixture.service;
    });

    it('should return an object defining AB analytics for a component', function() {
        // Arrange

        // Act
        var promise = service.getABAnalyticsForComponent('anyComponentId');

        // Assert
        expect(promise).toBeResolvedWithData({
            aValue: 30,
            bValue: 70
        });
    });

});
