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

    const fs = require('fs');

    const LOCAL_GITIGNORE = ".gitignore";
    const ALREADY_HANDLED_EXCLUSIONS = [global.smartedit.bundlePaths.bundleDirName, "node_modules"];

    const isLinkFromSmartEdit = (line) => {
        return !!ALREADY_HANDLED_EXCLUSIONS.find((pattern) => line.indexOf(pattern) > -1);
    };

    return {
        targets: [
            'target',
            'generated'
        ],
        config: function(data, conf) {
            let patterns = [];
            [LOCAL_GITIGNORE].forEach((gitignoreLocation) => {
                if (fs.existsSync(gitignoreLocation)) {
                    const fileContent = fs.readFileSync(gitignoreLocation, 'utf-8');
                    patterns = fileContent.split(/\r?\n/).filter((line) => {
                        return line && line.indexOf("#") === -1 && !isLinkFromSmartEdit(line);
                    });
                    console.info("cleaning with patterns found in .gitignore: ", patterns);
                }
            });

            return {
                target: {
                    src: ['temp', 'tmp', 'jsTarget']
                },
                generated: {
                    src: patterns
                }
            };
        }
    };
};
