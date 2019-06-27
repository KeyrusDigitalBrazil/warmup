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
angular.module("deletePageToolbarItemModule", [
        "pageServiceModule",
        "cmsSmarteditServicesModule",
        "smarteditServicesModule",
        "seConstantsModule",
        "pageFacadeModule"
    ])

    .controller('DeletePageToolbarItemController', function(
        $log,
        pageService,
        pageFacade,
        managePageService,
        systemEventService,
        EVENT_CONTENT_CATALOG_UPDATE
    ) {

        this.$onChanges = function() {
            this.ready = false;
            this.tooltipMesssage = null;
            this.isDeletePageEnabled = false;

            pageFacade.retrievePageUriContext().then(function(pageUriContext) {
                if (pageUriContext) {
                    this.uriContext = pageUriContext;
                    pageService.getCurrentPageInfo().then(function(pageInfo) {
                        this.pageInfo = pageInfo;
                        managePageService.isPageTrashable(this.pageInfo, pageUriContext).then(function(isEnabled) {
                            this.isDeletePageEnabled = isEnabled;
                            if (this.isDeletePageEnabled) {
                                this.tooltipMesssage = null;
                                this.ready = true;
                            } else {
                                managePageService.getDisabledTrashTooltipMessage(this.pageInfo, pageUriContext).then(function(tooltipMessage) {
                                    this.tooltipMesssage = tooltipMessage;
                                    this.ready = true;
                                }.bind(this));
                            }
                        }.bind(this));
                    }.bind(this));
                }
            }.bind(this));
        };

        this.onClickOnDeletePage = function() {
            return pageService.getCurrentPageInfo().then(function(pageInfo) {
                return managePageService.softDeletePage(pageInfo, this.uriContext).then(function(response) {
                    systemEventService.publishAsync(EVENT_CONTENT_CATALOG_UPDATE, response);
                }.bind(this));
            }.bind(this));
        };

        this.getTooltipTemplate = function() {
            return '<div class="popover-tooltip"><span data-translate="' + this.tooltipMesssage + '" /></div>';
        };

    })

    /**
     * @ngdoc directive
     * @name deletePageItemModule.directive:deletePageToolbarItem
     * @scope
     * @restrict E
     *
     * @description
     * deletePageToolbarItem provides a tooolbar action to Move a page to trash.
     *
     * @param {<Object} toolbarItem An object that represents the toolbar item.
     */
    .component(
        'deletePageToolbarItem', {
            controller: 'DeletePageToolbarItemController',
            templateUrl: 'deletePageToolbarItemTemplate.html',
            bindings: {
                toolbarItem: '<'
            }
        }
    );
