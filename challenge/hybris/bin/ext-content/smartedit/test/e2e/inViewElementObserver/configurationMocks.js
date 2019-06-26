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
angular.module('OuterMocksModule', ['ngMockE2E', 'resourceLocationsModule', 'smarteditServicesModule'])

    .constant('SMARTEDIT_ROOT', 'web/webroot')

    .value('CONFIGURATION_MOCK', [{
        "value": "\"thepreviewTicketURI\"",
        "key": "previewTicketURI"
    }, {
        "value": "{\"smartEditLocation\":\"/test/e2e/inViewElementObserver/scanner.js\"}",
        "key": "applications.scanner"
    }, {
        "value": "\"somepath\"",
        "key": "i18nAPIRoot"
    }, {
        "value": "[\"*\"]",
        "key": "whiteListedStorefronts"
    }]);

try {
    angular.module('smarteditloader').requires.push('OuterMocksModule');
    angular.module('smarteditcontainer').requires.push('OuterMocksModule');
} catch (ex) {}
