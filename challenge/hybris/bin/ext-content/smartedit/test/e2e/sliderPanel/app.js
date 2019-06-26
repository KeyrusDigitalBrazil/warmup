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
angular.module('app', [
        'templateCacheDecoratorModule',
        'modalServiceModule',
        'sliderPanelModule',
        'confirmationModalServiceModule',
        'smarteditServicesModule'
    ])

    .controller('modalController', function($log, confirmationModalService) {

        var self = this;

        this.isDirtyStatus = false;

        this.sliderPanelConfigurationModal = {
            modal: {
                showDismissButton: true,
                cancel: {
                    onClick: function() {
                        self.cancelSliderPanel();
                    },
                    label: "Cancel"
                },
                save: {
                    onClick: function() {
                        self.saveSliderPanel();
                    },
                    label: "Save",
                    isDisabledFn: function() {
                        return self.isSaveDisabled();
                    }

                },
                dismiss: {
                    onClick: function() {
                        self.dismissSliderPanel();
                    }
                },
                title: "Slider Panel Title"
            },
            cssSelector: "#y-modal-dialog"
        };

        this.sliderPanelConfigurationModalSecondary = {
            modal: {
                showDismissButton: true,
                title: "Secondary Slider Panel"
            },
            cssSelector: "#y-modal-dialog"
        };

        this.saveSliderPanel = function() {
            self.sliderPanelHideModal();
        };

        this.cancelSliderPanel = function() {
            self.sliderPanelHideModal();
        };

        this.dismissSliderPanel = function() {

            var message = {
                title: 'Dismiss',
                description: 'Do you want to dismiss ?'
            };
            confirmationModalService.confirm(message).then(function() {
                self.sliderPanelHideModal();
            });
        };

        this.isSaveDisabled = function() {
            return !self.isDirtyStatus;
        };

    })

    .controller('defaultController', function($log, modalService) {

        var self = this;

        this.sliderPanelConfiguration1 = {
            modal: {
                showDismissButton: true,
                title: "Header",
                cancel: {
                    onClick: function() {
                        self.sliderPanelHide1();
                    },
                    label: "Cancel"
                },
                save: {
                    onClick: function() {
                        self.sliderPanelHide1();
                    },
                    label: "Save"
                }
            }
        };

        this.sliderPanelConfiguration3 = {
            modal: {
                showDismissButton: true,
                title: "Header",
                cancel: {
                    onClick: function() {
                        self.sliderPanelHide3();
                    },
                    label: "Cancel"
                },
                save: {
                    onClick: function() {
                        self.sliderPanelHide3();
                    },
                    label: "Save"
                }
            },
            slideFrom: "top"
        };

        this.sliderPanelConfiguration4 = {
            modal: {
                showDismissButton: true,
                title: "Primary panel"
            }
        };

        this.sliderPanelConfiguration5 = {
            cssSelector: "#target1"
        };

        this.sliderPanelConfiguration6 = {
            cssSelector: "#target2"
        };


        this.sliderPanelConfiguration7 = {
            modal: {
                showDismissButton: true,
                cancel: {
                    onClick: function() {
                        self.sliderPanelHide7();
                    },
                    label: "se.cms.component.confirmation.modal.cancel"
                },
                save: {
                    onClick: function() {
                        self.sliderPanelHide7();
                    },
                    label: "se.cms.component.confirmation.modal.save"
                },
                title: "Awesome, a secondary panel!"
            }
        };

        this.openModal = function() {
            modalService.open({
                title: "Modal Title",
                controller: 'defaultController',
                templateUrl: './modalTemplate.html'
            }).then(function(result) {
                $log.log("Modal closed", result);
            }, function(failure) {
                $log.log("Modal dismissed", failure);
            });
        };

        this.sliderPanelHide1 = this.sliderPanelHide2 = this.sliderPanelHide3 = this.sliderPanelHide4 = this.sliderPanelHide5 = this.sliderPanelHide6 = this.sliderPanelHide7 = function() {};
        this.sliderPanelShow1 = this.sliderPanelShow2 = this.sliderPanelShow3 = this.sliderPanelShow4 = this.sliderPanelShow5 = this.sliderPanelShow6 = this.sliderPanelShow7 = function() {};

    });
