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
describe('i18nInterceptor', () => {
    let $rootScope;
    let $q;
    let $httpProvider;
    let $injector;
    let i18nInterceptor;
    let languageService;

    beforeEach(angular.mock.module('i18nInterceptorModule', ($provide, _$httpProvider_) => {
        $httpProvider = _$httpProvider_;

        $provide.constant("I18N_RESOURCE_URI", 'realI18nAPI');

        languageService = jasmine.createSpyObj('languageService', ['getResolveLocale', 'setInitialized']);
        $provide.constant("languageService", languageService);
    }));

    beforeEach(angular.mock.inject((_$injector_, _$q_, _$rootScope_, _$httpBackend_, _i18nInterceptor_) => {
        $injector = _$injector_;
        $q = _$q_;
        $rootScope = _$rootScope_;
        i18nInterceptor = _i18nInterceptor_;
    }));

    it('$httpProvider will be loaded with only one interceptor and that will be the i18nInterceptor', () => {

        expect($httpProvider.interceptors).toContain('i18nInterceptor');

    });
    it('will not rewrite url to i18nApiRoot URI from neither configuration nor liveedit namespace when i18n API call not detected', (done) => {
        var config = {
            url: 'somecall/en_CA',
            headers: {}
        };

        i18nInterceptor.request(config).then((response) => {
            expect(response).toBe(config);
            expect(config.url).toBe('somecall/en_CA');
            done();
        });

        $rootScope.$digest();
    });

    it('will rewrite url to i18nApiRoot URI from constants when i18n API call detected', (done) => {
        languageService.getResolveLocale.and.returnValue($q.when('en_CA'));

        const config = {
            url: 'i18nAPIRoot/en_CA',
            headers: {}
        };

        i18nInterceptor.request(config).then((response) => {
            expect(response).toBe(config);
            expect(config.url).toBe('realI18nAPI/en_CA');
            done();
        });

        $rootScope.$digest();

    });

    it('GIVEN request url indicates undefined locale THEN it swaps for the browser locale', (done) => {

        // WHEN
        languageService.getResolveLocale.and.returnValue($q.when('xx_YY'));
        const promise = i18nInterceptor.request({
            url: 'i18nAPIRoot/UNDEFINED'
        });

        //THEN
        promise.then((response) => {
            expect(response.url).toBe('realI18nAPI/xx_YY');
            done();
        });

        $rootScope.$digest();

    });

    it('GIVEN the i18nInterceptor response, when the response is a Map, i18nInterceptor response will return a map and initialise languageService', () => {

        const config = {
            url: 'realI18nAPI/en_CA',
            headers: {}
        };

        expect(i18nInterceptor.response({
            config: config,
            data: {
                key1: 'value1',
                key2: 'value2'
            }
        })).toBeResolvedWithData({
            config: {
                url: 'realI18nAPI/en_CA',
                headers: {}
            },
            data: {
                key1: 'value1',
                key2: 'value2'
            }
        });
        expect(languageService.setInitialized).toHaveBeenCalledWith(true);
    });

    it('GIVEN the i18nInterceptor response, when the response is an object that holds a Map, i18nInterceptor response will return a map and initialise languageService', () => {

        const config = {
            url: 'realI18nAPI/en_CA',
            headers: {}
        };

        expect(i18nInterceptor.response({
            config: config,
            data: {
                value: {
                    key1: 'value1',
                    key2: 'value2'
                }
            }
        })).toBeResolvedWithData({
            config: {
                url: 'realI18nAPI/en_CA',
                headers: {}
            },
            data: {
                key1: 'value1',
                key2: 'value2'
            }
        });
        expect(languageService.setInitialized).toHaveBeenCalledWith(true);
    });
});
