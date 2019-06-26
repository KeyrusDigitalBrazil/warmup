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
describe('actionableAlert', function() {

    var actionableAlertService;
    var fixture;
    var mockAlertService;
    var $q;

    var MOCK_PAYLOAD_DEFAULT = {
        controller: "mocked_controller",
        description: "mocked_description_i18n",
        hyperlinkLabel: "mocked_hyperlinkLabel_i18n"
    };

    beforeEach(function() {
        fixture = AngularUnitTestHelper.prepareModule('actionableAlertModule')
            .mock('alertService', 'showInfo')
            .mock('alertService', 'showSuccess')
            .service('actionableAlertService');

        actionableAlertService = fixture.service;
        mockAlertService = fixture.mocks.alertService;
        $q = fixture.injected.$q;

    });

    describe('displayActionableAlert', function() {

        it("get the custom content template displayed in an 'info' alert by default", function() {

            // Act
            actionableAlertService.displayActionableAlert(MOCK_PAYLOAD_DEFAULT);
            fixture.detectChanges();

            // Assert
            expect(mockAlertService.showInfo).toHaveBeenCalledWith({
                closeable: true,
                controller: "mocked_controller",
                template: "<div><p>{{ $alertInjectedCtrl.description | translate: $alertInjectedCtrl.descriptionDetails }}</p><div><a data-ng-click='alert.hide(); $alertInjectedCtrl.onClick();'>{{ $alertInjectedCtrl.hyperlinkLabel | translate: $alertInjectedCtrl.hyperlinkDetails }}</a></div></div>",
                timeout: 20000
            });

        });

        it("WHEN displayActionableAlert is called with an invalid alert type THEN the alert is displayed as info", function() {
            // Given
            var invalidAlertType = 'Something invalid';

            // Act
            actionableAlertService.displayActionableAlert(MOCK_PAYLOAD_DEFAULT, invalidAlertType);
            fixture.detectChanges();

            // Assert
            expect(mockAlertService.showInfo).toHaveBeenCalledWith({
                closeable: true,
                controller: "mocked_controller",
                template: "<div><p>{{ $alertInjectedCtrl.description | translate: $alertInjectedCtrl.descriptionDetails }}</p><div><a data-ng-click='alert.hide(); $alertInjectedCtrl.onClick();'>{{ $alertInjectedCtrl.hyperlinkLabel | translate: $alertInjectedCtrl.hyperlinkDetails }}</a></div></div>",
                timeout: 20000
            });

        });

        it("WHEN displayActionableAlert is called with an invalid alert type THEN the alert is displayed as info", function() {
            // Given
            var validAlertType = 'SUCCESS';

            // Act
            actionableAlertService.displayActionableAlert(MOCK_PAYLOAD_DEFAULT, validAlertType);
            fixture.detectChanges();

            // Assert
            expect(mockAlertService.showSuccess).toHaveBeenCalledWith({
                closeable: true,
                controller: "mocked_controller",
                template: "<div><p>{{ $alertInjectedCtrl.description | translate: $alertInjectedCtrl.descriptionDetails }}</p><div><a data-ng-click='alert.hide(); $alertInjectedCtrl.onClick();'>{{ $alertInjectedCtrl.hyperlinkLabel | translate: $alertInjectedCtrl.hyperlinkDetails }}</a></div></div>",
                timeout: 20000
            });

        });

    });

});
