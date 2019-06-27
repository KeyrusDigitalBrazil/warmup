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
     * @name generateKarmaConf(C)
     * @description
     * # generateKarmaConf Configuration
     *
     * The bundle config for generateKarmaConf provides 2 targets, **generateSmarteditKarmaConf** and
     * **generateSmarteditContainerKarmaConf**.
     *
     * Both targets have the same data, and missing the **files** property, which is extension-specific information.
     * The file destination for each is defined in the bundlePaths. See bundlePaths.external.generated.karma
     *
     * Note: The base karma.conf has no webpack configured.
     *
     * To use this configuration, simply add the files you need for your tests to one of the targets above, or create
     * a new target.
     *
     * Example:
     *
     * ```js
     * config: function(data, conf) {
     *    targets: [
     *        'generateSmarteditContainerKarmaConf'
     *    ],
     *    conf.generateSmarteditContainerKarmaConf.files = [
     *        return lodash.concat(
     *          global.smartedit.bundlePaths.test.unit.smarteditContainerUnitTestFiles,
     *          global.smartedit.bundlePaths.test.unit.commonUtilModules,
     *          'tests/** /*Test.js'
     *    ];
     *    return conf;
     * }
     * ```
     *
     * Then run: grunt generateKarmaConf:generateSmarteditContainerKarmaConf
     *
     */

    return {
        targets: [
            'generateSmarteditKarmaConf',
            'generateSmarteditContainerKarmaConf'
        ],
        config: function(data, conf) {

            const paths = global.smartedit.bundlePaths;
            const lodash = require('lodash');
            const karmaConfigTemplates = require('../templates').karmaConfigTemplates;

            conf.generateSmarteditKarmaConf = {
                dest: paths.external.generated.karma.smartedit,
                data: lodash.cloneDeep(karmaConfigTemplates.base)
            };

            conf.generateSmarteditContainerKarmaConf = {
                dest: paths.external.generated.karma.smarteditContainer,
                data: lodash.cloneDeep(karmaConfigTemplates.base)
            };

            return conf;
        }
    };

};
