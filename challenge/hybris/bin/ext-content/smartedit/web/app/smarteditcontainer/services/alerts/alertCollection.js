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
angular.module('alertCollectionModule', [
        'yLoDashModule'
    ])

    /**
     * =============== Note: This collection SHOULD NOT BE USED directly. ==============
     * ============== Please use one of the facades in alertCollectionFacadesModule ==============
     *
     * The alertCollection represents a collection of alerts that are displayed to the user.
     */
    .service('alertCollection', function(lodash) {

        var alerts = [];

        this.getAlerts = function getAlerts() {
            return alerts;
        };

        this.addAlert = function addAlert(newAlert) {
            alerts.unshift(newAlert);
        };

        this.removeAlert = function removeAlert(alertToRemove) {
            lodash.remove(alerts, function(alert) {
                return alert === alertToRemove;
            });
        };

    })

    /**
     * The alertCollectionLegacySupport exposes an interface to the alertService to handle the
     * legacy removeAlertById function
     */
    .service('alertCollectionLegacySupport', function(alertCollection, lodash) {

        this.removeAlertById = function(id) {
            lodash.remove(alertCollection.getAlerts(), function(alert) {
                return alert.id === id;
            });
        };

    });
