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

    /**
     * @ngdoc overview
     * @name generateE2eListHtml(C)
     * @description
     * # generateE2eListHtml Configuration
     * The default generateE2eListHtml configuration is configured with a default template located in the bundle.
     *
     */
    return {
        config: function(data, conf) {
            return {
                root: global.smartedit.bundlePaths.test.e2e.root,
                tpl: global.smartedit.bundlePaths.test.e2e.listTpl,
                dest: global.smartedit.bundlePaths.test.e2e.listDest
            };
        }
    };
};
