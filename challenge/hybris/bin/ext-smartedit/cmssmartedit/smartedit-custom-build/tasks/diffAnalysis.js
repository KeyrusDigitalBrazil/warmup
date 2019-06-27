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

const instrumentFunctionsAnalyzer = require('../test/diffAnalysis/instrumentFunctionsAnalyzer');
const instrumentBindVariablesAnalyzer = require('../test/diffAnalysis/instrumentBindVariablesAnalyzer');

module.exports = function(grunt) {

    grunt.registerTask("diffAnalysis", function() {

        const previousVersion = grunt.option('previousVersion')
        const nextVersion = grunt.option('nextVersion');

        if (nextVersion && previousVersion) {

            instrumentBindVariablesAnalyzer.execute(previousVersion, nextVersion);
            instrumentFunctionsAnalyzer.execute(previousVersion, nextVersion);
        } else {
            grunt.fail.fatal("diffAnalysis not activated, specify the next version with --nextVersion=nextVersion and previous version with --previousVersion=previousVersion");
        }
    });

};
