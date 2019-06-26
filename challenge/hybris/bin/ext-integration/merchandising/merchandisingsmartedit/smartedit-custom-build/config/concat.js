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
            'merchandisingsmartedit',
            'merchandisingsmarteditContainer',
            'styling'
        ],
        config: function(data, baseConf) {
            const targetBase = "jsTarget/web/features/";

            function generateConfigFromFolderName(folderName) {
                return {
                    src: [
                        targetBase + "merchandisingsmarteditcommons/**/*.js",
                        targetBase + folderName + '/**/*.js'
                    ],
                    dest: targetBase + folderName + '/' + folderName + '_bundle.js'
                };
            }

            baseConf.merchandisingsmartedit = generateConfigFromFolderName("merchandisingsmartedit");
            baseConf.merchandisingsmarteditContainer = generateConfigFromFolderName("merchandisingsmarteditContainer");

            baseConf.styling = {
                separator: ';',
                src: ["web/webroot/css/*.css"],
                dest: "web/webroot/css/style.css",
            };

            return baseConf;
        }
    };

};
