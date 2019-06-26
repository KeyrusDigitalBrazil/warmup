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
angular.module('mockDataOverridesModule', ['backendMocksUtilsModule'])
    .run(function(backendMocksUtils) {

        backendMocksUtils.getBackendMock('pagesContentSlotsData').respond({
            pageContentSlotList: [{
                pageId: 'homepage',
                position: 'topHeader',
                slotId: 'topHeaderSlot',
                slotShared: true,
                slotStatus: 'TEMPLATE'
            }, {
                pageId: 'homepage',
                position: 'bottomHeader',
                slotId: 'bottomHeaderSlot',
                slotShared: false,
                slotStatus: 'OVERRIDE'
            }, {
                pageId: 'homepage',
                position: 'footer',
                slotId: 'footerSlot',
                slotShared: false,
                slotStatus: 'PAGE'
            }, {
                pageId: 'homepage',
                position: 'other',
                slotId: 'otherSlot',
                slotShared: false,
                slotStatus: 'PAGE'
            }]
        });
    });

try {
    angular.module('smarteditloader').requires.push('mockDataOverridesModule');
} catch (e) {}
try {
    angular.module('smarteditcontainer').requires.push('mockDataOverridesModule');
} catch (e) {}
