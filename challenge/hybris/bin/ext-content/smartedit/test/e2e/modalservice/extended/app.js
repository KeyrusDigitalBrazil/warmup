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
window.smartedit = {
    i18nAPIRoot: "somepath"
};

angular.module('app', ['templateCacheDecoratorModule', 'ngMockE2E', 'coretemplates', 'modalServiceModule', 'resourceLocationsModule', 'smarteditServicesModule'])
    .controller('defaultController', function($scope, $httpBackend, $log, modalService, I18N_RESOURCE_URI, languageService) {

        $httpBackend.whenGET(I18N_RESOURCE_URI + "/" + languageService.getBrowserLocale()).respond({
            "modal.title.lamp": "I love lamp"
        });
        $httpBackend.whenGET(/Template/).passThrough();

        $scope.openModalWithTitle = function() {
            modalService.open({
                title: "modal.title.lamp",
                controller: 'modalTestController',
                templateUrl: 'test/e2e/modalservice/extended/modalContentTemplate.html'
            }).then(function(result) {
                $log.log("Modal closed", result);
            }, function(failure) {
                $log.log("Modal dismissed", failure);
            });
        };

        $scope.openModalWithTitleAndButtons = function() {
            modalService.open({
                title: "modal.title.lamp",
                controller: 'modalTestController',
                templateUrl: 'test/e2e/modalservice/extended/modalContentTemplate.html',
                buttons: [{
                    id: 'modalButton'
                }]
            }).then(function(result) {
                $log.log("Modal closed", result);
            }, function(failure) {
                $log.log("Modal dismissed", failure);
            });
        };

        $scope.openModalWithDisabledButton = function() {
            modalService.open({
                templateInline: "<span>hello</span>",
                buttons: [{
                    id: 'modalButton',
                    disabled: true
                }, {
                    label: 'Close',
                    action: "close"
                }]
            }).then(function(result) {
                $log.log("Modal closed", result);
            }, function(failure) {
                $log.log("Modal dismissed", failure);
            });
        };

    })

    .controller('modalTestController', function($q, modalManager) {

        var counter = 0;

        function buttonHandlerFn(buttonId) {
            counter++;
            modalManager.title = modalManager.title + counter;

            modalManager.addButton({
                label: 'newbutton' + counter,
                id: 'newbutton' + counter
            });

            if (buttonId === 'newbutton3') {
                modalManager.removeButton.call(this, 'newbutton1');
            }
        }

        function dismissCallback() {
            return $q(function(resolve, reject) {
                if (confirm("Resolve the promise?")) {
                    resolve();
                } else {
                    reject();
                }
            });
        }

        this.modalManager = modalManager;
        this.showX = true;
        this.newButtonConfig = '{ "id": 1, "label": "someLabel" }';

        modalManager.setButtonHandler(buttonHandlerFn);
        modalManager.setDismissCallback(dismissCallback);

        this.addButton = function() {
            modalManager.addButton(JSON.parse(this.newButtonConfig));
        };

        this.removeButton = function() {
            modalManager.removeButton(this.buttonIdToRemove);
        };

        this.disableButton = function() {
            modalManager.disableButton(this.disableButtonId);
        };
        this.enableButton = function() {
            modalManager.enableButton(this.enableButtonId);
        };

    });
