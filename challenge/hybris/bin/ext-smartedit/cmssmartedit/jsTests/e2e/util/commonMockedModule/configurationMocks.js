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
angular
    .module('configurationMocksModule', [])
    .constant('CONFIGURATION_MOCKS', [{
        value: "\"/cmswebservices/v1/i18n/languages\"",
        key: "i18nAPIRoot"
    }, {
        value: "{\"smartEditLocation\":\"/jsTests/e2e/util/commonMockedModule/rerenderMocks.js\"}",
        key: "applications.rerenderMocks"
    }, {
        value: "{\"smartEditLocation\":\"/jsTests/e2e/util/commonMockedModule/miscellaneousMocks.js\"}",
        key: "applications.miscellaneousMocks"
    }, {
        value: "{\"smartEditContainerLocation\":\"/web/webroot/cmssmartedit/js/cmssmarteditContainer.js\"}",
        key: "applications.cmssmarteditContainer"
    }, {
        value: "{\"smartEditLocation\":\"/web/webroot/cmssmartedit/js/cmssmartedit.js\"}",
        key: "applications.cmssmartedit"
    }]);
