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
     *
     * Configuration for the karma grunt task
     *
     * In this extension, karma is triggered programmatically by the multiKarma task.
     * multiKarma executes karma once for each directory in /jsTests, and executes karma
     * with the directory name as the target.
     *
     */

    return {
        /**
         * The multiTarget-targets.
         * This should match the names of the directories in jsTests.
         */
        targets: [
            'personalizationsearchsmartedit',
            'personalizationsearchsmarteditContainer',
            'personalizationsearchsmarteditcommons'
        ],
        config: function(data, conf) {

            /**
             * The keys of this object should match the directories and targets above.
             * The base smartedit configuration provides unitSmartedit, and unitSmarteditContainer OOTB.
             * These configurations load all the smartedit and thirparty files that you would have in the
             * real application.
             */
            return {
                personalizationsearchsmartedit: conf.unitSmartedit,
                personalizationsearchsmarteditContainer: conf.unitSmarteditContainer,

                /**
                 * Smartedit does not provide a commons target OOTB, so one is generated.
                 * See ./generateKarmaConf.js
                 */
                personalizationsearchsmarteditcommons: {
                    options: {
                        configFile: global.smartedit.bundlePaths.external.generated.karma.smarteditCommons,
                    }
                }
            };
        }
    };

};
