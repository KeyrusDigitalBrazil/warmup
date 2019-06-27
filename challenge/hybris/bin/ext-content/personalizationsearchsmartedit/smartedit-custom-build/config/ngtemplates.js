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
            'personalizationsearchsmartedit',
            'personalizationsearchsmarteditcommons',
            'personalizationsearchsmarteditContainer'
        ],
        config: function(data, conf) {
            const sourcesRoot = 'web/features/';

            function generateConfigForFolder(folderName) {
                return {
                    src: [sourcesRoot + folderName + '/**/*Template.html'],
                    dest: 'jsTarget/' + sourcesRoot + folderName + '/templates.js',
                    options: {
                        standalone: true, //to declare a module as opposed to binding to an existing one
                        module: folderName + 'Templates'
                    }
                };
            }

            conf.personalizationsearchsmartedit = generateConfigForFolder('personalizationsearchsmartedit');
            conf.personalizationsearchsmarteditcommons = generateConfigForFolder('personalizationsearchsmarteditcommons');
            conf.personalizationsearchsmarteditContainer = generateConfigForFolder('personalizationsearchsmarteditContainer');

            return conf;
        }
    };

};
