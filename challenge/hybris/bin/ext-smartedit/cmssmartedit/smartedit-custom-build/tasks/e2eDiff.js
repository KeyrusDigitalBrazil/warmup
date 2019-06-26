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
const lodash = require('lodash');

module.exports = function(grunt) {

    grunt.registerTask('e2eDiff', 'runs instrumented code through e2e max then runs a diff analysis on code usage', function() {

        var instrument = grunt.option('instrument');
        var compareTo = grunt.option('compareTo');

        if (!lodash.isString(instrument) || !lodash.isString(compareTo)) {
            grunt.fail.fatal("e2eDiff requires command line arguments --instrument=nextVersion and --compareTo=previousVersion");
        }

        grunt.task.run(['e2e_max']);

        grunt.option('previousVersion', compareTo);
        grunt.option('nextVersion', instrument);

        grunt.task.run('diffAnalysis');

    });


};
