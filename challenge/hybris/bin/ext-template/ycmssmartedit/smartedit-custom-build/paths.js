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

    const paths = {};

    paths.getE2eFiles = function getE2eFiles() {
        return [
            ['jsTests/**/e2e/**/*Test.js']
        ];
    };

    return paths;

}();
