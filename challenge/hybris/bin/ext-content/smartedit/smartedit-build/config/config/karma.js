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
     * @name karma(C)
     * @description
     * # karma Configuration
     *
     * The default karma configuration has 2 targets that point grunt-karma to the generated karma conf.
     * **unitSmartedit** and **unitSmarteditContainer**.
     *
     * See bundlePaths.external.generated.karma
     */

    return {
        targets: [
            'unitSmartedit',
            'unitSmarteditContainer'
        ],
        config: function(data, conf) {
            const paths = global.smartedit.bundlePaths;
            return {
                unitSmartedit: {
                    options: {
                        configFile: paths.external.generated.karma.smartedit,
                    }
                },

                unitSmarteditContainer: {
                    options: {
                        configFile: paths.external.generated.karma.smarteditContainer,
                    }
                }
            };
        }
    };

};
