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
/* jshint esversion: 6 */
module.exports = function(grunt) {
    const path = require('path');
    class TaskUtil {
        /**
         * Task Util
         * Iterating in ./task-utils folder (first level only) and create this[filename] properties
         * 
         * @example:
         * const karmaTaskUtil = this.karma;
         * const coverageConfig = karmaTaskUtil.coverageConfig.config(...);
         */
        constructor() {
            require('glob').sync(__dirname + '/task-utils/*.js').forEach(function(file) {
                const dep = require(path.resolve(file));
                this[path.basename(file, '.js')] = (typeof dep === 'function') ? dep(grunt) : dep;
            }.bind(this));
        }
    }
    return new TaskUtil();
};
