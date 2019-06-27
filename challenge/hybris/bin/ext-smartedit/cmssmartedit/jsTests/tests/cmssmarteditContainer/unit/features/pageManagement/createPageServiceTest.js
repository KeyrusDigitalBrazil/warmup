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
describe('createPageService - ', function() {
    var createPageService, restServiceFactory, restPageService;
    var SERVICE_LOCATION = "Some Location";

    beforeEach(function() {
        angular.mock.module('createPageServiceModule', function($provide) {
            $provide.constant('PAGES_LIST_RESOURCE_URI', SERVICE_LOCATION);

            restServiceFactory = jasmine.createSpyObj('restServiceFactory', ['get']);
            $provide.value('restServiceFactory', restServiceFactory);

            restPageService = jasmine.createSpyObj('restPageService', ['save']);
            restServiceFactory.get.and.returnValue(restPageService);
        });

        inject(function(_createPageService_) {
            createPageService = _createPageService_;
        });
    });


    it('WHEN the createPageService is created THEN it will create a rest service with the right location', function() {
        // Assert
        expect(restServiceFactory.get).toHaveBeenCalledWith(SERVICE_LOCATION);
    });

    it('WHEN createPage is called THEN the  rest service is called with the expected payload', function() {
        // Arrange
        var page = {
            value: 'some value'
        };

        var uriContext = {
            dummy: "value"
        };

        // Act
        createPageService.createPage(uriContext, page);

        // Assert
        expect(restPageService.save).toHaveBeenCalledWith({
            value: 'some value',
            dummy: 'value'
        });
    });
});
