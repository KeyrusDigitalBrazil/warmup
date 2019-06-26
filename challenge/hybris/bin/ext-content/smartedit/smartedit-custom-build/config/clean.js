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

    return {
        targets: [
            'webroot',
            'postConcat'
        ],
        config: function(data, conf) {
            var paths = require('../paths');

            conf.webroot = {
                src: [
                    paths.web.webroot.staticResources.smartEdit.css.all,
                    paths.web.webroot.staticResources.smartEdit.dir,
                    paths.web.webroot.staticResources.dir + '/smartedit',
                    paths.web.webroot.staticResources.dir + '/smarteditcontainer',
                    paths.web.webroot.staticResources.dir + '/smarteditloader'
                ]
            };
            conf.postConcat = {
                src: paths.web.webroot.staticResources.smartEdit.css.temp.dir
            };

            /**
             * TODO - remove this target after 6.6 CF
             * This issue will not be exposed to people using the commerce suite, or the pipeline.
             * Only people that had a smartedit-extension dev setup and did a rebase.
             * In this case the symlinking fails due to existing directories.
             * This clean will fix the problem so that symlinking willwork again. So this should
             * be a 1 time execution fix.
             */
            conf.bundleForNewSymlinks = {
                src: [
                    global.smartedit.bundlePaths.bundleRoot + '/@types',
                    global.smartedit.bundlePaths.bundleRoot + '/localization',
                    global.smartedit.bundlePaths.bundleRoot + '/webroot/**/*'
                ]
            };

            return conf;
        }
    };
};
