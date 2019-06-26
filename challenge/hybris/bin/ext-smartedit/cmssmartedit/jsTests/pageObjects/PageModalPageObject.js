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
var FIELDS_MAPPING = {
    uid: 'uid-shortstring'
};

module.exports = {
    getField: function(fieldName) {
        var fieldID = FIELDS_MAPPING[fieldName] || fieldName;
        return element(by.css('#' + fieldID));
    },
    setFieldValue: function(fieldName, newValue) {
        return this.getField(fieldName).clear().sendKeys(newValue);
    },
    save: function() {
        return element(by.id('save')).click();
    }
};
