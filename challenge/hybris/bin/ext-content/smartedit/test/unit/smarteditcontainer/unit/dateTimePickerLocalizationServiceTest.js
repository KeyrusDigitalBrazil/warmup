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
describe('dateTimePickerLocalizationService', function() {
    var dateTimePickerLocalizationService, languageService, $q, $rootScope, tooltipsMap, resolvedLocaleToMomentLocaleMap, $translate, datetimepicker, momentLocale, otherLocale, untranslatedTooltips, translatedTooltips;

    beforeEach(angular.mock.module('dateTimePickerModule', function($provide) {
        languageService = jasmine.createSpyObj('languageService', ['getResolveLocale']);
        momentLocale = 'zz';
        otherLocale = 'aa';
        resolvedLocaleToMomentLocaleMap = {
            'en': momentLocale
        };
        $translate = jasmine.createSpyObj('$translate', ['instant']);
        $translate.instant.and.callFake(function(string) {
            return '_' + string;
        });
        datetimepicker = jasmine.createSpyObj('datetimepicker', ['locale', 'tooltips']);


        untranslatedTooltips = {
            today: 'se.datetimepicker.today',
            clear: 'se.datetimepicker.clear',
            close: 'se.datetimepicker.close',
            selectMonth: 'se.datetimepicker.selectmonth',
            prevMonth: 'se.datetimepicker.previousmonth',
            nextMonth: 'se.datetimepicker.nextmonth',
            selectYear: 'se.datetimepicker.selectyear',
            prevYear: 'se.datetimepicker.prevyear',
            nextYear: 'se.datetimepicker.nextyear',
            selectDecade: 'se.datetimepicker.selectdecade',
            prevDecade: 'se.datetimepicker.prevdecade',
            nextDecade: 'se.datetimepicker.nextdecade',
            prevCentury: 'se.datetimepicker.prevcentury',
            nextCentury: 'se.datetimepicker.nextcentury',
            pickHour: 'se.datetimepicker.pickhour',
            incrementHour: 'se.datetimepicker.incrementhour',
            decrementHour: 'se.datetimepicker.decrementhour',
            pickMinute: 'se.datetimepicker.pickminute',
            incrementMinute: 'se.datetimepicker.incrementminute',
            decrementMinute: 'se.datetimepicker.decrementminute',
            pickSecond: 'se.datetimepicker.picksecond',
            incrementSecond: 'se.datetimepicker.incrementsecond',
            decrementSecond: 'se.datetimepicker.decrementsecond',
            togglePeriod: 'se.datetimepicker.toggleperiod',
            selectTime: 'se.datetimepicker.selecttime'
        };

        translatedTooltips = {
            today: '_se.datetimepicker.today',
            clear: '_se.datetimepicker.clear',
            close: '_se.datetimepicker.close',
            selectMonth: '_se.datetimepicker.selectmonth',
            prevMonth: '_se.datetimepicker.previousmonth',
            nextMonth: '_se.datetimepicker.nextmonth',
            selectYear: '_se.datetimepicker.selectyear',
            prevYear: '_se.datetimepicker.prevyear',
            nextYear: '_se.datetimepicker.nextyear',
            selectDecade: '_se.datetimepicker.selectdecade',
            prevDecade: '_se.datetimepicker.prevdecade',
            nextDecade: '_se.datetimepicker.nextdecade',
            prevCentury: '_se.datetimepicker.prevcentury',
            nextCentury: '_se.datetimepicker.nextcentury',
            pickHour: '_se.datetimepicker.pickhour',
            incrementHour: '_se.datetimepicker.incrementhour',
            decrementHour: '_se.datetimepicker.decrementhour',
            pickMinute: '_se.datetimepicker.pickminute',
            incrementMinute: '_se.datetimepicker.incrementminute',
            decrementMinute: '_se.datetimepicker.decrementminute',
            pickSecond: '_se.datetimepicker.picksecond',
            incrementSecond: '_se.datetimepicker.incrementsecond',
            decrementSecond: '_se.datetimepicker.decrementsecond',
            togglePeriod: '_se.datetimepicker.toggleperiod',
            selectTime: '_se.datetimepicker.selecttime'
        };

        $provide.value('languageService', languageService);
        $provide.constant('resolvedLocaleToMomentLocaleMap', resolvedLocaleToMomentLocaleMap);
        $provide.value('$translate', $translate);

    }));

    beforeEach(inject(function(_dateTimePickerLocalizationService_, _$q_, _$rootScope_, _tooltipsMap_) {
        dateTimePickerLocalizationService = _dateTimePickerLocalizationService_;
        $q = _$q_;
        languageService.getResolveLocale.and.returnValue($q.when('en'));
        $rootScope = _$rootScope_;
        tooltipsMap = _tooltipsMap_;
    }));

    describe('localizeDateTimePicker', function() {
        it('should not localize the tool nor tooltips when both are already localized', function() {

            datetimepicker.locale.and.callFake(localeReturnSame);
            datetimepicker.tooltips.and.callFake(tooltipsReturnSame);

            dateTimePickerLocalizationService.localizeDateTimePicker(datetimepicker);
            $rootScope.$digest();

            expect(datetimepicker.locale).toHaveBeenCalledWith();
            expect(datetimepicker.locale).not.toHaveBeenCalledWith(momentLocale);
            expect(datetimepicker.tooltips).toHaveBeenCalledWith();
            expect(datetimepicker.tooltips).not.toHaveBeenCalledWith(translatedTooltips);

        });

        it('should localize only the tool not the tooltips when tooltips are already localized but the tool itself is not', function() {

            datetimepicker.locale.and.callFake(localeReturnDifferent);
            datetimepicker.tooltips.and.callFake(tooltipsReturnSame);


            dateTimePickerLocalizationService.localizeDateTimePicker(datetimepicker);
            $rootScope.$digest();

            expect(datetimepicker.locale).toHaveBeenCalledWith();
            expect(datetimepicker.locale).toHaveBeenCalledWith(momentLocale);
            expect(datetimepicker.locale.calls.count()).toEqual(2);
            expect(datetimepicker.tooltips).toHaveBeenCalledWith();
            expect(datetimepicker.tooltips).not.toHaveBeenCalledWith(translatedTooltips);


        });

        it('should localize only the tooltips nor the tool when the tool is already localized but not the tooltips', function() {

            datetimepicker.locale.and.callFake(localeReturnSame);
            datetimepicker.tooltips.and.callFake(tooltipsReturnDifferent);

            dateTimePickerLocalizationService.localizeDateTimePicker(datetimepicker);
            $rootScope.$digest();

            expect(datetimepicker.locale).toHaveBeenCalledWith();
            expect(datetimepicker.locale).not.toHaveBeenCalledWith(momentLocale);
            expect(datetimepicker.tooltips).toHaveBeenCalledWith();
            expect(datetimepicker.tooltips).toHaveBeenCalledWith(translatedTooltips);
            expect(datetimepicker.tooltips.calls.count()).toEqual(2);

        });


        it('should localize both the tool and tooltips when they are not already localized', function() {

            datetimepicker.locale.and.callFake(localeReturnDifferent);
            datetimepicker.tooltips.and.callFake(tooltipsReturnDifferent);

            dateTimePickerLocalizationService.localizeDateTimePicker(datetimepicker);
            $rootScope.$digest();

            expect(datetimepicker.locale).toHaveBeenCalledWith();
            expect(datetimepicker.locale).toHaveBeenCalledWith(momentLocale);
            expect(datetimepicker.locale.calls.count()).toEqual(2);
            expect(datetimepicker.tooltips).toHaveBeenCalledWith();
            expect(datetimepicker.tooltips).toHaveBeenCalledWith(translatedTooltips);
            expect(datetimepicker.tooltips.calls.count()).toEqual(2);

        });
    });

    var localeReturnSame = function(locale) {
        if (locale) {
            return;
        } else {
            return momentLocale;
        }
    };

    var localeReturnDifferent = function(locale) {
        if (locale) {
            return;
        } else {
            return otherLocale;
        }
    };

    var tooltipsReturnSame = function(tooltips) {
        if (tooltips) {
            return;
        } else {
            return translatedTooltips;
        }
    };

    var tooltipsReturnDifferent = function(tooltips) {
        if (tooltips) {
            return;
        } else {
            return untranslatedTooltips;
        }
    };



});
