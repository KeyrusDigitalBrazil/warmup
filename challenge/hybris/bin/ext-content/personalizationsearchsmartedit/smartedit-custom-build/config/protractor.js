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

    const dirNames = [
        "personalizationsearchsmartedit",
        "personalizationsearchsmarteditcommons",
        "personalizationsearchsmarteditContainer"
    ];

    function getMaxName(name) {
        return `${name}_max`;
    }

    const targets = [];
    dirNames.forEach((dirName) => {
        targets.push(dirName);
        targets.push(getMaxName(dirName));
    });

    return {
        targets,
        config: function(data, conf) {
            const lodash = require('lodash');

            function createConfigFromFolderName(config, folderName) {

                const files = {
                    options: {
                        args: {
                            specs: [process.cwd() + '/jsTests/' + folderName + '/e2e/*Test.js']
                        }
                    }
                }

                // Regular 1 instance protrator
                const run = lodash.cloneDeep(conf.run);
                lodash.merge(run, files);
                config[folderName] = run;

                // Multi instance protractorMax
                const maxrun = lodash.cloneDeep(conf.maxrun);
                lodash.merge(maxrun, files);
                config[getMaxName(folderName)] = maxrun;
            }

            dirNames.forEach((name) => {
                createConfigFromFolderName(conf, name);
            });

            return conf;
        }
    };


};
