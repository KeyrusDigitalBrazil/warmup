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
angular.module('clientPageListAppModule', ['coretemplates', 'yjqueryModule', 'clientPagedListModule', 'templateCacheDecoratorModule', 'smarteditServicesModule'])
    .run(function($q, permissionService) {
        permissionService.registerRule({
            names: ['se.some.rule'],
            verify: function() {
                return $q.when(window.sessionStorage.getItem("PERSPECTIVE_SERVICE_RESULT") === 'true');
            }
        });

        permissionService.registerPermission({
            aliases: ['se.edit.page'],
            rules: ['se.some.rule']
        });
    })
    .controller('defaultController', function(yjQuery) {
        this.searchKeys = ['name', 'uid', 'typeCode', 'template'];
        this.query = {
            value: ""
        };

        this.items = [{
            creationtime: "2016-04-08T21:16:41+0000",
            modifiedtime: "2016-04-08T21:16:41+0000",
            pk: "8796387968048",
            template: "PageTemplate",
            name: "page1TitleSuffix",
            typeCode: "ContentPage",
            uid: "auid1"
        }, {
            creationtime: "2016-04-08T21:16:41+0000",
            modifiedtime: "2016-04-08T21:16:41+0000",
            pk: "8796387968048",
            template: "ActionTemplate",
            name: "welcomePage",
            typeCode: "ActionPage",
            uid: "uid2"
        }, {
            creationtime: "2016-04-08T21:16:41+0000",
            modifiedtime: "2016-04-08T21:16:41+0000",
            pk: "8796387968048",
            template: "PageTemplate",
            name: "Advertise",
            typeCode: "MyCustomType",
            uid: "uid3"
        }, {
            creationtime: "2016-04-08T21:16:41+0000",
            modifiedtime: "2016-04-08T21:16:41+0000",
            pk: "8796387968048",
            template: "MyCustomPageTemplate",
            name: "page2TitleSuffix",
            typeCode: "HomePage",
            uid: "uid4"
        }, {
            creationtime: "2016-04-08T21:16:41+0000",
            modifiedtime: "2016-04-08T21:16:41+0000",
            pk: "8796387968048",
            template: "ZTemplate",
            name: "page3TitleSuffix",
            typeCode: "ProductPage",
            uid: "uid5"
        }, {
            creationtime: "2016-04-08T21:16:41+0000",
            modifiedtime: "2016-04-08T21:16:41+0000",
            pk: "8796387968048",
            template: "PageTemplate",
            name: "page3TitleSuffix",
            typeCode: "ProductPage",
            uid: "uid6"
        }, {
            creationtime: "2016-04-08T21:16:41+0000",
            modifiedtime: "2016-04-08T21:16:41+0000",
            pk: "8796387968048",
            template: "PageTemplate",
            name: "page4TitleSuffix",
            typeCode: "WallPage",
            uid: "uid7"
        }, {
            creationtime: "2016-04-08T21:16:41+0000",
            modifiedtime: "2016-04-08T21:16:41+0000",
            pk: "8796387968048",
            template: "PageTemplate",
            name: "page5TitleSuffix",
            typeCode: "CheckoutPage",
            uid: "uid8"
        }, {
            creationtime: "2016-04-08T21:16:41+0000",
            modifiedtime: "2016-04-08T21:16:41+0000",
            pk: "8796387968048",
            template: "PageTemplate",
            name: "page6TitleSuffix",
            typeCode: "PromoPage",
            uid: "uid9"
        }, {
            creationtime: "2016-04-08T21:16:41+0000",
            modifiedtime: "2016-04-08T21:16:41+0000",
            pk: "8796387968048",
            template: "PageTemplate",
            name: "page7TitleSuffix",
            typeCode: "ProfilePage",
            uid: "uid10"
        }, {
            creationtime: "2016-04-08T21:16:41+0000",
            modifiedtime: "2016-04-08T21:16:41+0000",
            pk: "8796387968048",
            template: "PageTemplate",
            name: "page3TitleSuffix",
            typeCode: "ProductPage",
            uid: "uid11"
        }, {
            creationtime: "2016-04-08T21:16:41+0000",
            modifiedtime: "2016-04-08T21:16:41+0000",
            pk: "8796387968048",
            template: "PageTemplate",
            name: "page3TitleSuffix",
            typeCode: "ProductPage",
            uid: "zuid12"
        }];

        this.dropdownItems = [{
            key: 'pagelist.dropdown.edit',
            callback: function() {
                this.items = [];
            }.bind(this)
        }];

        this.renderers = {
            name: function() {
                return "<a data-ng-click=\"injectedContext.changeColor($parent.$parent.$index)\">{{ item[key.property] }}</a>";
            },
            uid: function() {
                return "<span class='custom'> {{ item[key.property] }} </span>";
            }
        };

        // injectedContext Object. This object is passed to the client-paged-list directive.
        this.injectedContext = {
            changeColor: function(index) {
                var nth = index + 1;
                yjQuery('.paged-list-table tbody tr:nth-child(' + nth + ') .paged-list-item-name a').addClass('visited');
            }
        };

    });
