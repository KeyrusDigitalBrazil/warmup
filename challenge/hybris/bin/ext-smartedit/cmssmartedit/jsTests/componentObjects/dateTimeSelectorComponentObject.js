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
module.exports = (function() {

    var dateTimeSelectorObject = {
        constants: {
            MONTHS: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
        },

        elements: {
            getDateTimeFieldById: function(fieldId) {
                return element(by.id(fieldId));
            },
            getOpenWidgetButtonById: function(fieldId) {
                return dateTimeSelectorObject.elements.getDateTimeFieldById(fieldId).element(by.css('.se-date-field--button'));
            },
            timePicker: function() {
                return element(by.css('span[class*=\'glyphicon-time\']'));
            }
        },
        actions: {
            toggleWidget: function(fieldId) {
                return browser.click(dateTimeSelectorObject.elements.getOpenWidgetButtonById(fieldId));
            },
            openTimePicker: function() {
                return browser.click(dateTimeSelectorObject.elements.timePicker());
            },
            selectDate: function(dateStr) {
                var date = dateTimeSelectorObject.utils.getDateFromString(dateStr);

                return dateTimeSelectorObject.utils.selectYear(date.year).then(function() {
                    return dateTimeSelectorObject.utils.selectMonth(date.month);
                }).then(function() {
                    return dateTimeSelectorObject.utils.selectDay(date.day);
                }).then(function() {
                    return dateTimeSelectorObject.actions.openTimePicker();
                }).then(function() {
                    return dateTimeSelectorObject.utils.selectHour(date.hours);
                }).then(function() {
                    return dateTimeSelectorObject.utils.selectMinutes(date.minutes);
                }).then(function() {
                    return dateTimeSelectorObject.utils.selectPeriod(date.period);
                });
            },
            setDateTimeInField: function(fieldId, dateStr) {
                return dateTimeSelectorObject.actions.toggleWidget(fieldId).then(function() {
                    return dateTimeSelectorObject.actions.selectDate(dateStr).then(function() {
                        return dateTimeSelectorObject.actions.toggleWidget(fieldId);
                    });
                });
            }
        },
        utils: {
            // Note: The minutes can only be a multiple of 5.
            getDateFromString: function(dateStr) {
                var DATE_TIME_REGEX = /([0-1]?[0-9])\/([0-3]?[0-9])\/([0-9][0-9]) ([0-9]?[0-9]):([0-9][0-9]) (AM|PM)/g;
                var matches = DATE_TIME_REGEX.exec(dateStr);
                var month = dateTimeSelectorObject.constants.MONTHS[parseInt(matches[1]) - 1];

                return {
                    month: month,
                    day: matches[2],
                    year: "20" + matches[3],
                    hours: matches[4],
                    minutes: matches[5],
                    period: matches[6]
                };
            },
            selectYear: function(yearToSelect) {
                return browser.click(element(by.css('div[class*=\'datepicker-days\'] th[class*=\'picker-switch\']'))).then(function() {
                    return browser.click(element(by.css('div[class*=\'datepicker-months\'] th[class*=\'picker-switch\']'))).then(function() {
                        browser.click(element(by.cssContainingText('span[class*=\'year\']', yearToSelect)));
                    });
                });
            },
            selectMonth: function(monthToSelect) {
                return browser.click(element(by.cssContainingText('span[class*=\'month\']', monthToSelect)));
            },
            selectDay: function(dayToSelect) {
                return browser.click(element(by.cssContainingText('.datepicker-days td:not(.old)', dayToSelect)));
            },
            selectHour: function(hourToSelect) {
                return browser.click(element(by.css('div.timepicker-picker .timepicker-hour'))).then(function() {
                    return browser.click(element(by.cssContainingText('td[class*=\'hour\']', hourToSelect)));
                });
            },
            selectMinutes: function(minutesToSelect) {
                return browser.click(element(by.css('div[class=\'timepicker-picker\'] .timepicker-minute'))).then(function() {
                    browser.click(element(by.cssContainingText('td[class*=\'minute\']', minutesToSelect)));
                });
            },
            selectPeriod: function(periodToSelect) {
                return browser.isElementPresent(by.cssContainingText('button', 'AM')).then(function(isAMSelected) {
                    if (isAMSelected && periodToSelect === 'PM') {
                        return browser.click(by.cssContainingText('button', 'AM'));
                    } else if (!isAMSelected && periodToSelect === 'AM') {
                        return browser.click(by.cssContainingText('button', 'PM'));
                    }
                });
            }
        }
    };

    return dateTimeSelectorObject;

})();
