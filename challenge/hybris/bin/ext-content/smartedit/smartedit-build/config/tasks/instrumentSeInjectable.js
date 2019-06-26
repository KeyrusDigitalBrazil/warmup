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
    grunt.registerTask('instrumentSeInjectable', 'Instrument TypeScript code that contains @SeInjectable()/@SeComponent() annotations', function() {
        const path = require('path');
        const seInjectableInstrumenter = require(path.resolve(global.smartedit.bundlePaths.tools.seInjectableInstrumenter.js));
        seInjectableInstrumenter(grunt.file.expand(global.smartedit.bundlePaths.webAppTargetTs), ['SeInjectable', 'SeDirective', 'SeComponent']);
    });
};
