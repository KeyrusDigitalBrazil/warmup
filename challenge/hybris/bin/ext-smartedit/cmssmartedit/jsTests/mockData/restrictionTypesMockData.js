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
/* jshint unused:false, undef:false */
unit.mockData.restrictionTypes = function() {
    return {
        getMocks: function() {
            return {
                restrictionTypes: [{
                    code: 'CMSTimeRestriction',
                    name: {
                        de: 'DAS blabla',
                        en: 'Time Restriction'
                    }
                }, {
                    code: 'CMSCatalogRestriction',
                    name: {
                        en: 'Catalog Restriction'
                    }
                }, {
                    code: 'CMSCategoryRestriction',
                    name: {
                        en: 'category Restriction'
                    }
                }, {
                    code: 'CMSUserRestriction',
                    name: {
                        en: 'User Restriction'
                    }
                }]
            };
        }
    };
};
