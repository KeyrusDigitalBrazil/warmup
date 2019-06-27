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
unit.mockData.pageDisplayCondition = function() {

    //function values() {
    this.PRIMARY = {
        label: 'page.displaycondition.primary',
        description: 'page.displaycondition.primary.description',
        isPrimary: true
    };
    this.VARIANT = {
        label: 'page.displaycondition.variation',
        description: 'page.displaycondition.variation.description',
        isPrimary: false
    };
    this.ALL = [{
        label: 'page.displaycondition.primary',
        description: 'page.displaycondition.primary.description',
        isPrimary: true
    }, {
        label: 'page.displaycondition.variation',
        description: 'page.displaycondition.variation.description',
        isPrimary: false
    }];
};
