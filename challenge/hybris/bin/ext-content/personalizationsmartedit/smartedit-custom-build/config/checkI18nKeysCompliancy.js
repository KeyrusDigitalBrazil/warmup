/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
module.exports = function() {

    return {
        config: function(data, conf) {
            return {

                prefix: {
                    ignored: [
                        'page.displaycondition.', // keys provided by back-end
                        'se.', // keys provided by smartedit-locales_en.properties
                        'type.' // keys provided by back-end
                    ],
                    expected: ['personalization.']
                },

                paths: {
                    files: [
                        "web/features/**/*Template.html",
                        "web/features/**/*.js"
                    ],
                    properties: [
                        "resources/localization/personalizationsmartedit-locales_en.properties",
                        "smartedit-build/localization/smartedit-locales_en.properties"
                    ]
                }

            };
        }
    };
};
