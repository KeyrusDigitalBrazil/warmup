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
module.exports = function(grunt) {
    /**
     * @ngdoc overview
     * @name default
     * @description
     * # default Task
     * Shows help when grunt is called with no args
     */
    grunt.registerTask('default', function() {
        var done = this.async();
        grunt.util.spawn({
                cmd: 'grunt',
                args: ['--help']
            },
            function(error, result, code) {
                grunt.log.writeln(result.stdout);
                done(!error);
            }
        );
    });
}
