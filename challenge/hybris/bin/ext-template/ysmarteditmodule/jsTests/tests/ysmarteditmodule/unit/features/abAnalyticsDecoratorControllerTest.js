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
describe('abAnalyticsDecoratorController', function() {

    var fixture;
    var controller;

    beforeEach(function() {
        fixture = AngularUnitTestHelper.prepareModule('abAnalyticsDecoratorControllerModule')
            .mock('abAnalyticsService', 'getABAnalyticsForComponent').and.returnResolvedPromise({
                aValue: 30,
                bValue: 70
            })
            .controller('abAnalyticsDecoratorController');

        controller = fixture.controller;
        fixture.detectChanges();
    });

    it('should bind a title to the controller scope', function() {
        // Arrange

        // Act

        // Assert
        expect(controller.title).toBe('AB Analytics');
    });

    it('should bind an inner content template to the controller scope', function() {
        // Arrange

        // Act

        // Assert
        expect(controller.contentTemplate).toBe('abAnalyticsDecoratorContentTemplate.html');
    });

    it('should build a human readable AB analytics string on the controller scope', function() {
        // Arrange

        // Act
        controller.$onInit();
        fixture.detectChanges();

        // Assert
        expect(controller.abAnalytics).toBe('A: 30 B: 70');
    });

});
