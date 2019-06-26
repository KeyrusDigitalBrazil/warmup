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
angular.module('mediaMocks', ['ngMockE2E', 'functionsModule', 'resourceLocationsModule'])
    .run(function($httpBackend, filterFilter, parseQuery) {

        var medias = [{
            id: '1',
            code: 'contextualmenu_delete_off',
            uuid: 'contextualmenu_delete_off',
            description: 'contextualmenu_delete_off',
            altText: 'contextualmenu_delete_off alttext',
            realFileName: 'contextualmenu_delete_off.png',
            url: '/web/webroot/images/contextualmenu_delete_off.png'
        }, {
            id: '2',
            code: 'contextualmenu_delete_on',
            uuid: 'contextualmenu_delete_on',
            description: 'contextualmenu_delete_on',
            altText: 'contextualmenu_delete_on alttext',
            realFileName: 'contextualmenu_delete_on.png',
            url: '/web/webroot/images/contextualmenu_delete_on.png'
        }, {
            id: '3',
            code: 'contextualmenu_edit_off',
            uuid: 'contextualmenu_edit_off',
            description: 'contextualmenu_edit_off',
            altText: 'contextualmenu_edit_off alttext',
            realFileName: 'contextualmenu_edit_off.png',
            url: '/web/webroot/images/contextualmenu_edit_off.png'
        }, {
            id: '3',
            code: 'contextualmenu_edit_on',
            uuid: 'contextualmenu_edit_on',
            description: 'contextualmenu_edit_on',
            altText: 'contextualmenu_edit_on alttext',
            realFileName: 'contextualmenu_edit_on.png',
            url: '/web/webroot/images/contextualmenu_edit_on.png'
        }, {
            id: '4',
            code: 'clone4',
            uuid: 'clone4',
            description: 'Clone background',
            altText: 'clone alttext',
            realFileName: 'clone_bckg.png',
            url: '/web/webroot/static-resources/images/contextualmenu_more_on.png',
            format: 'widescreen'
        }, {
            id: '5',
            code: 'dnd5',
            uuid: 'dnd5',
            description: 'Drag and drop background',
            altText: 'dnd alttext',
            realFileName: 'dnd_bckg.png',
            url: '/web/webroot/static-resources/images/contextualmenu_more_on.png',
            format: 'desktop'
        }];

        $httpBackend.whenGET(/cmswebservices\/v1\/media\/(.+)/).respond(function(method, url, data, headers) {

            var identifier = /media\/(.+)/.exec(url)[1];
            var filtered = medias.filter(function(media) {
                return media.code === identifier;
            });
            if (filtered.length > 0) {
                return [200, filtered[0]];
            } else {
                return [404];
            }
        });

        $httpBackend.whenGET(/cmswebservices\/v1\/media/).respond(function(method, url, data, headers) {

            var queryString = parseQuery(url);
            var filtered = [];

            if (queryString.currentPage === "0") {
                var params = queryString.params;
                var search = params.split(",").reduce(function(accumulator, next) {
                    var valueLabel = next.split(":");
                    accumulator[valueLabel[0]] = valueLabel.length === 2 ? valueLabel[1] : "";
                    return accumulator;
                }, {});

                filtered = filterFilter(medias, search.code);
            }
            return [200, {
                media: filtered
            }];
        });

        $httpBackend.whenPOST(/cmswebservices\/v1\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/media/).respond(function(method, url, data, headers, params) {
            var media = {
                id: medias.length + '',
                uuid: 'more_bckg.png',
                code: 'more_bckg.png',
                description: 'more_bckg.png',
                altText: 'more_bckg.png',
                realFileName: 'more_bckg.png',
                url: '/web/webroot/static-resources/images/more_bckg.png'
            };
            medias.push(media);
            return [201, media];
        });

    });
try {
    angular.module('smarteditcontainer').requires.push('mediaMocks');
} catch (e) {}
