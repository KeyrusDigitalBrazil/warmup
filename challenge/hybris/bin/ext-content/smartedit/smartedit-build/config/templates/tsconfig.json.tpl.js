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
module.exports = function() {

    const lodash = require('lodash');

    const baseConfig = {
        "compilerOptions": {
            "target": "es5",
            "module": "es2015",
            "moduleResolution": "node",
            "lib": [
                "dom",
                "es5",
                "scripthost",
                "es2015",
                "es2015.iterable"
            ],
            "allowJs": false,
            "checkJs": false,
            "noImplicitAny": true,
            "noImplicitReturns": true,
            "noUnusedLocals": true,
            "noUnusedParameters": false,
            "strictNullChecks": false,
            "forceConsistentCasingInFileNames": true,
            "noEmitOnError": true,
            "baseUrl": "../../jsTarget/",
            "typeRoots": ["../../node_modules/@types"],
            "traceResolution": true,
            "listEmittedFiles": true,
            "skipLibCheck": true,
            "pretty": true,
            "declaration": false,
            "experimentalDecorators": true,
            "paths": {}
        }
    };

    // ====== Prod =====
    const prodSmartedit = lodash.cloneDeep(baseConfig);
    const prodSmarteditContainer = lodash.cloneDeep(baseConfig);

    // ====== Dev =====
    const devSmartedit = lodash.cloneDeep(baseConfig);
    const devSmarteditContainer = lodash.cloneDeep(baseConfig);

    // ====== Karma =====
    const karmaSmartedit = lodash.cloneDeep(baseConfig);
    const karmaSmarteditContainer = lodash.cloneDeep(baseConfig);

    // ====== IDE =====
    const ide = lodash.cloneDeep(baseConfig);

    // TODO 1) - types array to limit types, for example, no jasmine in production build
    // TODO 2) - split smartedit vs smarteditContainer types in bundle

    return {
        baseConfig, // include base for partners/customers
        prodSmartedit,
        prodSmarteditContainer,
        devSmartedit,
        devSmarteditContainer,
        karmaSmartedit,
        karmaSmarteditContainer,
        ide
    };
}();
