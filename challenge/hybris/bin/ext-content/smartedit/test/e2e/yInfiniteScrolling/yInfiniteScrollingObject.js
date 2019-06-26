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
module.exports = {
    assertListOfItems: function(expectedOptions) {
        var itemsSelector = by.xpath("//div[@class='infContainer']//div[contains(@class,'repeaterTemplate')]");
        browser.waitUntil(function() {
            return element.all(itemsSelector).map(function(element) {
                return element.getText().then(function(text) {
                    return text;
                }, function() {
                    return '';
                });
            }).then(function(actualOptions) {
                return actualOptions.join(',') === expectedOptions.join(',');
            });
        }, 'cannot load items');
    },

    getItemsScrollElement: function() {
        return element(by.xpath("//div[@class='infContainer']//y-infinite-scrolling/div"));
    },

    searchItems: function(searchKey) {
        return element(by.xpath("//input[@name='srch-term']")).sendKeys(searchKey);
    }
};
