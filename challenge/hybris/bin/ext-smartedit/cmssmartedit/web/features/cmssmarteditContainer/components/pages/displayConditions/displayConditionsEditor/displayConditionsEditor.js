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
/**
 * @ngdoc overview
 * @name displayConditionsEditorModule
 * @description
 * #displayConditionsEditorModule
 *
 * The displayConditionsEditorModule module contains the
 * {@link displayConditionsEditorModule.directive:displayConditionsEditor displayConditionsEditor} component
 *
 */
angular.module('displayConditionsEditorModule', [
        'displayConditionsEditorModelModule',
        'displayConditionsPageInfoModule',
        'displayConditionsPageVariationsModule',
        'displayConditionsPrimaryPageModule'
    ])
    .controller('displayConditionsEditorController', function(
        $log,
        $filter,
        $translate,
        displayConditionsEditorModel,
        homepageService,
        pageService) {

        this.showReplaceLabel = false;
        this.isPrimaryPage = false;
        this.hasFallback = false;

        var CatalogHomepageDetailsStatus = {
            PENDING: 'PENDING',
            NO_HOMEPAGE: 'NO_HOMEPAGE',
            LOCAL: 'LOCAL',
            OLD: 'OLD',
            PARENT: 'PARENT'
        };
        this.homepageDetails = {
            status: CatalogHomepageDetailsStatus.PENDING
        };

        this.$onInit = function() {
            displayConditionsEditorModel.initModel(this.page.uid);

            pageService.buildUriContextForCurrentPage().then(function(uriContext) {
                pageService.isPagePrimaryWithContext(this.page.uid, uriContext).then(function(boolResult) {
                    this.isPrimaryPage = boolResult;
                }.bind(this));
                homepageService.hasFallbackHomePage(uriContext).then(function(boolResult) {
                    this.hasFallback = boolResult;
                }.bind(this));
                homepageService.getHomepageDetailsForContext(uriContext).then(function(homepageDetails) {
                    this.homepageDetails = homepageDetails;
                }.bind(this));
            }.bind(this), function() {
                $log.error('displayConditionsEditorController::$onInit - unable to retrieve uriContext');
            });
        };

        this.getPageName = function() {
            return displayConditionsEditorModel.pageName;
        };

        this.getPageType = function() {
            return displayConditionsEditorModel.pageType;
        };

        this.isPagePrimary = function() {
            return displayConditionsEditorModel.isPrimary;
        };

        this.getVariations = function() {
            return displayConditionsEditorModel.variations;
        };

        this.getAssociatedPrimaryPage = function() {
            return displayConditionsEditorModel.associatedPrimaryPage;
        };

        this.getIsAssociatedPrimaryReadOnly = function() {
            return displayConditionsEditorModel.isAssociatedPrimaryReadOnly;
        };

        this.getPrimaryPages = function() {
            return displayConditionsEditorModel.primaryPages;
        };

        this.onPrimaryPageSelect = function(primaryPage) {
            this.page.label = primaryPage.label;
        };

        this.disableHomepageCheckbox = function() {

            // multi coutry with parent homepage
            if (this.hasFallback) {
                return false;
            }

            // multi or single with homepage in current catalog
            if (this.homepageDetails.status === CatalogHomepageDetailsStatus.LOCAL) {

                // editing existing local homepage
                if (this.homepageDetails.currentHomepageUid === this.page.uid) {
                    return true;
                }
                // editing old local homepage (you can put it back to homepage again)
                if (this.homepageDetails.oldHomepageUid === this.page.uid) {
                    return false;
                }

                // edting another page to be the new homepage
                return false;
            }

            // don't think this is reachable? 
            // If there is a fallback then we can also check/uncheck
            return true;
        };

        this.homePageChanged = function() {
            if (this.page.homepage === true) {
                switch (this.homepageDetails.status) {
                    case CatalogHomepageDetailsStatus.NO_HOMEPAGE:
                        homepageService.sendEventHideReplaceParentHomePageInfo();
                        this.showReplaceLabel = false;
                        break;
                    case CatalogHomepageDetailsStatus.PARENT:
                        homepageService.sendEventShowReplaceParentHomePageInfo({
                            description: $translate.instant("se.cms.display.conditions.homepage.replace.parent.info.header", {
                                parentCatalogName: $filter('l10n')(this.homepageDetails.parentCatalogName),
                                parentCatalogVersion: this.homepageDetails.parentCatalogVersion,
                                targetCatalogName: $filter('l10n')(this.homepageDetails.targetCatalogName),
                                targetCatalogVersion: this.homepageDetails.targetCatalogVersion,
                            })
                        });
                        this.showReplaceLabel = false;
                        break;
                    case CatalogHomepageDetailsStatus.LOCAL:
                        homepageService.sendEventHideReplaceParentHomePageInfo();
                        this.currentHomePageName = this.homepageDetails.currentHomepageName;
                        this.showReplaceLabel = this.page.uid !== this.homepageDetails.currentHomepageUid;
                        break;
                    default:
                        // do nothing
                        break;
                }
            } else {
                homepageService.sendEventHideReplaceParentHomePageInfo();
                this.showReplaceLabel = false;
            }
        };

        this.showHomePageWidget = function() {
            return this.homepageDetails.status !== CatalogHomepageDetailsStatus.PENDING &&
                this.page.typeCode === 'ContentPage' && this.isPrimaryPage;
        };

        this.getHomePageDisabledTooltipTemplate = function() {
            return '<span>' + $translate.instant('se.cms.display.conditions.homepage.disabled') + '</span>';
        };

    })

    /**
     * @ngdoc directive
     * @name displayConditionsEditorModule.directive:displayConditionsEditor
     * @scope
     * @restrict E
     * @element display-conditions-editor
     * 
     * @description
     * This component displays information about a page. For instance, it displays the 
     * page type, template, whether it's a primary or variation page, among others. 
     * 
     * @param {<Object} page The page for which to display its information
     */
    .component('displayConditionsEditor', {
        controller: 'displayConditionsEditorController',
        templateUrl: 'displayConditionsEditorTemplate.html',
        bindings: {
            page: '<'
        }
    });
