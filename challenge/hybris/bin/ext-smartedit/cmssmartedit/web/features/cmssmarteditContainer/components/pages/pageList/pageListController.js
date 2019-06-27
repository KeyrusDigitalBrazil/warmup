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
angular.module('pageListControllerModule', [
        'addPageServiceModule',
        'functionsModule',
        'pageListSyncIconModule',
        'resourceLocationsModule',
        'hasOperationPermissionModule',
        'yLoDashModule',
        'dynamicPagedListModule',
        'pageListTemplatePrinterModule',
        'cmsitemsRestServiceModule',
        'seConstantsModule',
        'translationServiceModule',
        'cmsSmarteditServicesModule',
        'catalogVersionPermissionModule'
    ])
    .controller('pageListController', function(
        $routeParams, $location, $translate, $scope, $q, EVENT_CONTENT_CATALOG_UPDATE, TRASHED_PAGE_LIST_PATH, lodash, cmsitemsUri,
        urlService, catalogService, addPageWizardService, systemEventService, managePageService, experienceService, catalogVersionPermissionService) {

        this.isReady = false;
        this.siteUID = $routeParams.siteId;
        this.catalogId = $routeParams.catalogId;
        this.catalogVersion = $routeParams.catalogVersion;
        this.uriContext = urlService.buildUriContext(this.siteUID, this.catalogId, this.catalogVersion);
        this.pageUriContext = urlService.buildPageUriContext(this.siteUID, this.catalogId, this.catalogVersion);

        //page list uses cmsitems api along with a set of query params to retrieve the list of active pages. This is passed to the dynamic-paged-list component.
        this.pageListConfig = {
            sortBy: 'name',
            reversed: false,
            itemsPerPage: 10,
            displayCount: true
        };

        this.pageListConfig.uri = cmsitemsUri;
        this.pageListConfig.queryParams = {
            catalogId: this.catalogId,
            catalogVersion: this.catalogVersion,
            typeCode: 'AbstractPage',
            itemSearchParams: 'pageStatus:active'
        };

        $q.all([
            catalogService.isContentCatalogVersionNonActive(),
            catalogVersionPermissionService.hasSyncPermissionToActiveCatalogVersion(this.catalogId, this.catalogVersion)
        ]).then(function(values) {

            var isNonActive = values[0];
            var hasSyncPermission = values[1];

            this.goToTrash = function() {
                $location.path(TRASHED_PAGE_LIST_PATH
                    .replace(":siteId", this.siteUID)
                    .replace(":catalogId", this.catalogId)
                    .replace(":catalogVersion", this.catalogVersion));
            };

            this.catalogName = "";
            this.query = {
                value: ""
            };

            this.searchOptions = {
                debounce: 500
            };

            this.pageListConfig.keys = [{
                property: 'name',
                i18n: 'se.cms.pagelist.headerpagename',
                sortable: true
            }, {
                property: 'uid',
                i18n: 'se.cms.pagelist.headerpageid',
                sortable: true
            }, {
                property: 'itemtype',
                i18n: 'se.cms.pagelist.headerpagetype',
                sortable: true
            }, {
                property: 'template',
                i18n: 'se.cms.pagelist.headerpagetemplate'
            }, {
                property: 'numberOfRestrictions',
                i18n: 'se.cms.pagelist.headerrestrictions'
            }];
            if (isNonActive) {
                this.pageListConfig.keys.push({
                    property: 'syncStatus',
                    i18n: 'se.cms.actionitem.page.sync'
                });
            }

            this.pageListConfig.keys.push({
                property: 'dropdownitems',
                i18n: ''
            });

            this.reset = function() {
                this.query.value = '';
            };

            catalogService.getContentCatalogsForSite(this.siteUID).then(function(catalogs) {
                this.catalogName = catalogs.filter(function(catalog) {
                    return catalog.catalogId === this.catalogId;
                }.bind(this))[0].name;

            }.bind(this));

            // renderers Object that contains custom HTML renderers for a given key
            this.pageListConfig.renderers = {
                name: function() {
                    return '<a data-ng-click="$ctrl.config.injectedContext.onLink( item.uid )">' +
                        '<homepage-icon class="homepage-icon__page-list" data-cms-page="item" data-uri-context="$ctrl.config.injectedContext.uriContext"></homepage-icon> {{ item.name }}</a>';
                }.bind(this),
                template: function() {
                    return '<page-list-template-printer data-template-uuid="item.masterTemplate"></page-list-template-printer>';
                },
                numberOfRestrictions: function() {
                    return '<restrictions-page-list-icon data-number-of-restrictions="item.restrictions.length || 0"/>';
                },
                syncStatus: function() {
                    return '<div><page-list-sync-icon data-uri-context="$ctrl.config.injectedContext.uriContext" data-page-id="item.uuid" /></div>';
                },
                dropdownitems: function() {
                    return '<div has-operation-permission="$ctrl.config.injectedContext.permissionForDropdownItems" class="paged-list-table__body__td paged-list-table__body__td-menu"><y-drop-down-menu dropdown-items="$ctrl.config.injectedContext.dropdownItems" selected-item="item" class="y-dropdown pull-right" /></div>';
                }
            };

            this.pageListConfig.dropdownItems = [{
                template: "<edit-page-item data-page-info='$ctrl.selectedItem' />"
            }];

            if (hasSyncPermission) {
                this.pageListConfig.dropdownItems.push({
                    template: "<sync-page-item data-page-info='$ctrl.selectedItem' />"
                });
            }

            this.pageListConfig.dropdownItems.push({
                template: "<clone-page-item data-page-info='$ctrl.selectedItem' />"
            });

            this.pageListConfig.dropdownItems.push({
                template: "<delete-page-item data-page-info='$ctrl.selectedItem' />"
            });

            // injectedContext Object. This object is passed to the dynamic-paged-list directive.
            this.pageListConfig.injectedContext = {
                onLink: function(uid) {
                    if (uid) {
                        experienceService.loadExperience({
                            siteId: this.siteUID,
                            catalogId: this.catalogId,
                            catalogVersion: this.catalogVersion,
                            pageId: uid
                        });
                    }
                }.bind(this),

                uriContext: lodash.merge(this.uriContext, this.pageUriContext),

                dropdownItems: this.pageListConfig.dropdownItems,

                permissionForDropdownItems: 'se.edit.page'
            };

            this.openAddPageWizard = function() {
                addPageWizardService.openAddPageWizard().then(function() {
                    this.dynamicPagedListApi.reloadItems();
                }.bind(this));
            };

            this.getApi = function($api) {
                this.dynamicPagedListApi = $api;
            };

            // event subscriptions
            var eventSubscriptions = [];

            var onContentCatalogUpdate = function() {
                if (this.dynamicPagedListApi) {
                    this.dynamicPagedListApi.reloadItems();
                }
                this.updateTrashedPagesCount();
            }.bind(this);

            this.setEventSubscriptions = function() {
                eventSubscriptions.push(systemEventService.subscribe(EVENT_CONTENT_CATALOG_UPDATE, onContentCatalogUpdate));
            };

            this.updateTrashedPagesCount = function() {
                return managePageService.getSoftDeletedPagesCount(this.uriContext).then(function(trashedPagesCount) {

                    this.trashedPagesTranslationData = {
                        totalCount: trashedPagesCount
                    };

                }.bind(this));
            }.bind(this);

            this.setEventSubscriptions();

            $scope.$on('$destroy', function() {
                eventSubscriptions.forEach(function(unsubscribeEvent) {
                    unsubscribeEvent();
                });
            });

            this.updateTrashedPagesCount().then(function() {
                this.isReady = true;
                this.isCatalogVersionInactive = isNonActive;
            }.bind(this));

        }.bind(this));
    });
