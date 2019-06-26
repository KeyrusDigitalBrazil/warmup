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
angular.module("componentVisibilityAlertServiceModule", [
        "alertServiceModule",
        "actionableAlertModule",
        "smarteditServicesModule",
        "componentVisibilityAlertServiceInterfaceModule",
        "editorModalServiceModule"
    ])

    .factory('componentVisibilityAlertService', function(
        alertService,
        actionableAlertService,
        sharedDataService,
        ComponentVisibilityAlertServiceInterface,
        editorModalService,
        extend,
        isBlank,
        gatewayProxy
    ) {

        var ComponentVisibilityAlertService = function() {
            this.gatewayId = 'ComponentVisibilityAlertService';
            gatewayProxy.initForService(this, ["checkAndAlertOnComponentVisibility"]);
        };

        ComponentVisibilityAlertService = extend(ComponentVisibilityAlertServiceInterface, ComponentVisibilityAlertService);

        ComponentVisibilityAlertService.prototype.checkAndAlertOnComponentVisibility = function(component) {

            var I18N = {
                ITEM_ALERT_HIDDEN: 'se.cms.component.visibility.alert.description.hidden',
                ITEM_ALERT_RESTRICTED: 'se.cms.component.visibility.alert.description.restricted'
            };

            if (!component.visible || component.restricted) {
                sharedDataService.get('experience').then(function(experience) {
                    var message = (!component.visible) ? I18N.ITEM_ALERT_HIDDEN : I18N.ITEM_ALERT_RESTRICTED;
                    var isExternal = component.catalogVersion !== experience.pageContext.catalogVersionUuid;

                    if (isExternal) {
                        alertService.showAlert({
                            message: message
                        });
                    } else {
                        actionableAlertService.displayActionableAlert({
                            controller: ["componentVisibilityAlertService", "editorModalService", function(componentVisibilityAlertService, editorModalService) {

                                this.onClick = function() {

                                    var checkProvidedArguments = function() {
                                        var checkedArguments = [component.itemId, component.itemType, component.slotId];
                                        if (checkedArguments.filter(function(value) {
                                                return value && !isBlank(value);
                                            }).length !== checkedArguments.length) {
                                            throw "componentVisibilityAlertService.checkAndAlertOnComponentVisibility - missing properly typed parameters";
                                        }
                                    };

                                    checkProvidedArguments();

                                    editorModalService.openAndRerenderSlot(
                                        component.itemType,
                                        component.itemId,
                                        "visibilityTab"
                                    ).then(function(item) {

                                        if (!item.visible || item.restricted) {
                                            componentVisibilityAlertService.checkAndAlertOnComponentVisibility({
                                                itemId: item.uuid,
                                                itemType: item.itemtype,
                                                catalogVersion: item.catalogVersion,
                                                restricted: item.restricted,
                                                slotId: component.slotId,
                                                visible: item.visible
                                            });
                                        }
                                    });

                                };

                                this.description = message;

                                this.hyperlinkLabel = "se.cms.component.visibility.alert.hyperlink";
                            }],
                            timeoutDuration: 6000
                        });
                    }
                }.bind(this));
            }
        };

        return new ComponentVisibilityAlertService();
    });
