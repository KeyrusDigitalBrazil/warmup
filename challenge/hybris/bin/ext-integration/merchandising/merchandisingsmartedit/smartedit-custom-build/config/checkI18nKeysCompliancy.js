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
        config: function(data, baseConf) {

            return {

                /**
                 * Check keys in the code to make sure they have the correct prefix
                 */
                prefix: {
                    ignored: [],
                    expected: ['merchandisingsmartedit.']
                },

                /**
                 * Check to make sure all keys are in the properties file
                 */
                paths: {
                    files: [
                        "web/features/**/*Template.html",
                        "web/features/**/*.js"
                    ],
                    properties: [
                        "resources/localization/merchandisingsmartedit-locales_en.properties"
                    ]
                }
            };
        }
    };

};
