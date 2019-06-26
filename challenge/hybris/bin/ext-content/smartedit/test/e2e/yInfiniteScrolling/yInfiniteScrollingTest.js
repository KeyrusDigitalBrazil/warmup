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
describe("yInfiniteScrolling", function() {

    var yInfiniteScrolling = require("./yInfiniteScrollingObject.js");

    var ITEMS_SET_1 = ['1 : item1',
        '2 : item2',
        '3 : item3',
        '4 : item4',
        '5 : item5',
        '6 : item6',
        '7 : item7',
        '8 : item8',
        '9 : item9',
        '10 : item10'
    ];

    var ITEMS_SET_2 = ['11 : item11',
        '12 : item12',
        '13 : item13',
        '14 : item14',
        '15 : item15',
        '16 : item16',
        '17 : item17',
        '18 : item18',
        '19 : item19',
        '20 : item20'
    ];

    var ITEMS_SET_3 = ['21 : item21',
        '22 : item22',
        '23 : item23',
        '24 : item24',
        '25 : item25'
    ];

    var ITEMS_SET_4 = ['1 : item1',
        '10 : item10',
        '11 : item11',
        '12 : item12',
        '13 : item13',
        '14 : item14',
        '15 : item15',
        '16 : item16',
        '17 : item17',
        '18 : item18'
    ];




    beforeEach(function() {
        browser.get('test/e2e/yInfiniteScrolling/yInfiniteScrollingTest.html');
    });

    it('WILL display more results as we scroll the list of existing items names/types in second tab', function() {

        yInfiniteScrolling.assertListOfItems(ITEMS_SET_1);

        browser.scrollToBottom(yInfiniteScrolling.getItemsScrollElement()).then(function() {

            yInfiniteScrolling.assertListOfItems(ITEMS_SET_1.concat(ITEMS_SET_2));

            browser.scrollToBottom(yInfiniteScrolling.getItemsScrollElement()).then(function() {
                yInfiniteScrolling.assertListOfItems(ITEMS_SET_1.concat(ITEMS_SET_2).concat(ITEMS_SET_3));

            });

        });

    });

    it('WILL filter and create a paged the list of items by name when a search key is entered', function() {


        yInfiniteScrolling.searchItems('item1').then(function() {
            yInfiniteScrolling.assertListOfItems(ITEMS_SET_4);

            browser.scrollToBottom(yInfiniteScrolling.getItemsScrollElement()).then(function() {
                yInfiniteScrolling.assertListOfItems(ITEMS_SET_4.concat('19 : item19'));

            });
        });
    });

});
