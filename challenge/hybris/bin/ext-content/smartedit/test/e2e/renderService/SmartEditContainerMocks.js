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
angular.module('SmartEditContainerMocksModule', [])
    .constant('SMARTEDIT_ROOT', 'web/webroot')
    .constant('SMARTEDIT_RESOURCE_URI_REGEXP', /^(.*)\/test\/e2e/)
    .value('CONFIGURATION_MOCK', [{
        "key": "i18nAPIRoot",
        "value": "\"somepath\""
    }, {
        "key": "applications.RenderDecoratorsModule",
        "value": "{\"smartEditLocation\":\"/test/e2e/utils/decorators/RenderDecorators.js\"}"
    }, {
        "key": "applications.OthersMockModule",
        "value": "{\"smartEditLocation\": \"/test/e2e/utils/commonMockedModules/OthersMock.js\"}"
    }, {
        "value": "[\"*\"]",
        "key": "whiteListedStorefronts"
    }]);

angular.module('smarteditloader').requires.push('SmartEditContainerMocksModule');
angular.module('smarteditcontainer').requires.push('SmartEditContainerMocksModule');
