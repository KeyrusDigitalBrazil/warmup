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
    grunt.registerTask('instrumentSmarteditCommons', 'Instrument TypeScript code to add values to the window.smarteditcommons namespace', function() {
        const path = require('path');
        const seCustomBuildPaths = require('../paths');
        const instrumentSmarteditCommons = require(path.resolve(seCustomBuildPaths.tools.seCommonsInstrumenter.js));
        instrumentSmarteditCommons(grunt.file.expand(seCustomBuildPaths.webAppTargetTs));
    });  
};
