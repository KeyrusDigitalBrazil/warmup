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
     * @name jshint(C)
     * @description
     * # jshint Configuration
     *
     * http://jshint.com/
     *
     * The default jshint configuration needs to be extended with an **all** property that is an array of
     * glob source files.
     * ```js
     * config: function(data, conf) {
     *     conf.all = [ 'web/** /*.js' ];
     *     return conf;
     * }
     * ```
     */


    return {
        config: function(data, conf) {
            return {
                options: {
                    jshintrc: global.smartedit.bundlePaths.build.jshintrc,
                    reporterOutput: ""
                }
            };
        }
    };
};
