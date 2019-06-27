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
angular.module('alertCollectionFacadesModule', [
        'alertCollectionModule'
    ])

    /**
     * The alertCollectionComponentFacade is the interface of alertCollection exposed to the component/view layer of the application
     */
    .factory('alertCollectionComponentFacade', function(alertCollection) {
        return {
            getAlerts: alertCollection.getAlerts
        };
    })

    /**
     * The alertCollectionServiceFacade is the interface of alertCollection exposed to the service layer of the application
     */
    .factory('alertCollectionServiceFacade', function(alertCollection) {
        return {
            addAlert: alertCollection.addAlert,
            removeAlert: alertCollection.removeAlert
        };
    });
