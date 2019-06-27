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
    const SHARDS = 'shards';
    const SHARD = 'shard';

    const LOGGER = require('../taskLogger')(grunt, 'e2eshard', 'Generates specs configuration for protractor e2e calls.');

    function getSpecs(allSpecs, shards, shard) {
        let shardSize = Math.floor(allSpecs.length / shards);
        let from = shardSize * shard;
        let to = shardSize * (shard + 1);

        const specs = allSpecs.slice(from, to);
        if (specs.length === 0 && shard >= shards) {
            LOGGER.info(` -> There are no e2e tests remainder left, executing previous shard`);
            return getSpecs(allSpecs, shards, shard - 1);
        }
        return specs;
    }

    /**
     * Usage with sharding: grunt e2e_max [--shards=N] [--shard=N] [--max_instances=N]
     * Where N is an integer number.
     * Options: 
     *  - shards: The total number of shards. 
     *    This option determines the maximum number of e2e tests on this current execution. 
     *    Default value: 1  
     *  - shard: The e2e test section that will be executed. 
     *    Default value: 1
     *  - max_instances: The maximum number of browser instances to run the tests.   
     *    Default value: 1 
     * Example: 
     * > grunt e2e_max --shards=10 --shard=1 --max_instances=2
     */
    return {
        getSpecs: function(e2ePaths) {

            // shards is the number of shards to slice the tests.
            // shard is in the range [0, shards]
            let shards = grunt.option(SHARDS) ? parseInt(grunt.option(SHARDS)) : 1;
            let shard = grunt.option(SHARD) ? parseInt(grunt.option(SHARD)) : 0;

            LOGGER.info(`Split: ${shard}/${shards}`);

            let specs = [];
            e2ePaths.forEach(function(path) {
                LOGGER.info(` -> collecting tests from [${path}]`);
                let dirs = grunt.file.expand({
                    filter: 'isFile'
                }, path);
                specs = specs.concat(dirs);
            });

            specs = getSpecs(specs, shards, shard);

            LOGGER.info(`Running ${specs.length} specs: ${specs}`);

            return specs;
        }
    };
};
