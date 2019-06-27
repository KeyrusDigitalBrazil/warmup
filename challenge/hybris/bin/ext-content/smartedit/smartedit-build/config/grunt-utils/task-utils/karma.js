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
module.exports = {
    /**
     * karma base coverage config - using 'istanbul' by default.
     * https://github.com/istanbuljs/istanbuljs/blob/aae256fb8b9a3d19414dcf069c592e88712c32c6/packages/istanbul-api/lib/config.js#L33-L39
     */
    coverageConfig: {
        config: (dir, subdir) => {
            return {
                reports: ['html', 'lcovonly', 'text-summary'],
                dir,
                fixWebpackSourcePaths: true,
                skipFilesWithNoCoverage: true,
                'report-config': {
                    html: {
                        subdir
                    }
                }
            };
        }
    }
};
