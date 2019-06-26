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
module.exports = function() {
    // TODO: remove this file once smartedit e2e tests are aligned (/test folder renamed to /jsTests).
    return {
        config: function(data, conf) {
            conf.root = 'test/e2e';
            conf.dest = 'test/e2e/list.html';
            return conf;
        }
    };
};
