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
import * as angular from 'angular';
import * as lo from 'lodash';
import {pageDeletionEvictionTag, pageRestoredEvictionTag, rarelyChangingContent, Cached, CrossFrameEventService, IAlertService, IPageInfoService, IUriContext, IWaitDialogService, Page, SeInjectable, SystemEventService, TypedMap, ValidationError} from 'smarteditcommons';
import {ICMSPage} from 'cmscommons/dtos/ICMSPage';

import {PageRestoreModalService} from './pageRestore/PageRestoreModalService';
import {PageRestoredAlertService} from '../actionableAlert';
import {HomepageService, HomepageType} from '../pageDisplayConditions/HomepageService';

interface DeletePageRestServiceErrorResponse {
	data: {
		errors: ValidationError[]
	};
}

/**
 * @ngdoc service
 * @name cmsSmarteditServicesModule.service:ManagePageService
 * 
 * @description
 * This service is used to manage a page.
 */
@SeInjectable()
export class ManagePageService {

	constructor(
		private $location: ng.ILocationService,
		private $log: angular.ILogService,
		private $q: angular.IQService,
		private $translate: angular.translate.ITranslateService,
		private alertService: IAlertService,
		private cmsitemsRestService: any,
		private systemEventService: SystemEventService,
		private crossFrameEventService: CrossFrameEventService,
		private pageInfoService: IPageInfoService,
		private confirmationModalService: any,
		private pagesVariationsRestService: any,
		private waitDialogService: IWaitDialogService,
		private pageRestoreModalService: PageRestoreModalService,
		private pageRestoredAlertService: PageRestoredAlertService,
		private homepageService: HomepageService,
		private lodash: lo.LoDashStatic,
		private EVENTS: TypedMap<string>,
		private EVENT_CONTENT_CATALOG_UPDATE: string
	) {}

	// ------------------------------------------------------------------------
	// Service Methods
	// ------------------------------------------------------------------------

    /** 
     * @ngdoc method
     * @name cmsSmarteditServicesModule.service:ManagePageService#softDeletePage
     * @methodOf cmsSmarteditServicesModule.service:ManagePageService
     *
     * @description
     * This method triggers the soft deletion of a CMS page.
     *
     * @param {Object} pageInfo The page object containing the uuid and the name of the page to be deleted.
     * @param {Object} uriContext A {@link resourceLocationsModule.object:UriContext uriContext}
     */
	softDeletePage(pageInfo: ICMSPage, uriContext: IUriContext): angular.IPromise<any> {

		const _pageInfo: any = this.lodash.cloneDeep(pageInfo);

		const builtURIContext: IUriContext = {
			catalogId: uriContext.CURRENT_CONTEXT_CATALOG,
			catalogVersion: uriContext.CURRENT_CONTEXT_CATALOG_VERSION,
			siteId: uriContext.CURRENT_CONTEXT_SITE_ID
		};

		return this.getConfirmationModalDescription(_pageInfo, uriContext).then((confirmationModalDescription) => {
			return this.confirmationModalService.confirm({
				description: confirmationModalDescription,
				descriptionPlaceholders: {
					pageName: pageInfo.name
				},
				title: "se.cms.actionitem.page.trash.confirmation.title"
			}).then(() => {

				_pageInfo.identifier = pageInfo.uuid;
				_pageInfo.pageStatus = "DELETED";

				return this.cmsitemsRestService.update(_pageInfo).then(() => {
					this.crossFrameEventService.publish(this.EVENTS.PAGE_DELETED);
					this.alertService.showSuccess({
						message: "se.cms.actionitem.page.trash.alert.success.description",
						messagePlaceholders: {
							pageName: pageInfo.name
						}
					});

					this.$location.path("/pages/:siteId/:catalogId/:catalogVersion"
						.replace(":siteId", builtURIContext.siteId)
						.replace(":catalogId", builtURIContext.catalogId)
						.replace(":catalogVersion", builtURIContext.catalogVersion));

				}, (response: DeletePageRestServiceErrorResponse) => {
					if (response && response.data) {
						this.displayValidationErrors(response, pageInfo.name);
					}
					return this.$q.reject(response);
				});
			});
		});
	}

    /** 
     * @ngdoc method
     * @name cmsSmarteditServicesModule.service:ManagePageService#hardDeletePage
     * @methodOf cmsSmarteditServicesModule.service:ManagePageService
     *
     * @description
     * This method triggers the permanent deletion of a CMS page.
     * 
     * @param {Object} pageInfo The page object containing the uuid and the name of the page to be deleted.
     */
	hardDeletePage(pageInfo: ICMSPage): angular.IPromise<void> {
		return this.confirmationModalService.confirm({
			title: "se.cms.actionitem.page.permanently.delete.confirmation.title",
			description: "se.cms.actionitem.page.permanently.delete.confirmation.description",
			descriptionPlaceholders: {
				pageName: pageInfo.name
			}
		}).then(() => {
			return this.cmsitemsRestService.delete(pageInfo.uuid).then((response: void) => {
				this.alertService.showSuccess('se.cms.page.permanently.delete.alert.success');
				this.systemEventService.publishAsync(this.EVENT_CONTENT_CATALOG_UPDATE, response);
				this.crossFrameEventService.publish(this.EVENTS.PAGE_DELETED);
			});
		});
	}

    /** 
     * @ngdoc method
     * @name cmsSmarteditServicesModule.service:ManagePageService#restorePage
     * @methodOf cmsSmarteditServicesModule.service:ManagePageService
     *
     * @description
     *  This method triggers the restoration a CMS page. 
     * 
     * @param {Object} pageInfo The page object containing the uuid and the name of the page to be restored.
     */
	restorePage(pageInfo: ICMSPage): angular.IPromise<void> {
		const _pageInfo: any = this.lodash.cloneDeep(pageInfo);

		_pageInfo.pageStatus = 'ACTIVE';
		_pageInfo.identifier = pageInfo.uuid;

		this.waitDialogService.showWaitModal(null);

		return this.cmsitemsRestService.update(_pageInfo).then((response: any) => {
			// show success
			this.waitDialogService.hideWaitModal();
			this.systemEventService.publishAsync(this.EVENT_CONTENT_CATALOG_UPDATE, response);

			this.pageRestoredAlertService.displayPageRestoredSuccessAlert(_pageInfo);
			this.crossFrameEventService.publish(this.EVENTS.PAGE_RESTORED);
		}, (result: any) => {
			// failure
			const errors: ValidationError[] = result.data.errors;
			this.waitDialogService.hideWaitModal();
			this.pageRestoreModalService.handleRestoreValidationErrors(_pageInfo, errors);
		});
	}

    /**
     * @ngdoc method
     * @name cmsSmarteditServicesModule.service:ManagePageService#isPageTrashable
     * @methodOf cmsSmarteditServicesModule.service:ManagePageService
     *
     * @description
     * This method indicates whether the given page can be soft deleted.
     * Only the variation pages and the primary pages associated with no variation pages are eligible for soft deletion.
     *
     * @param {ICMSPage} cmsPage The page content
     * @returns {Promise} A promise resolved with a boolean indicating whether the selected page can be soft deleted.
     */
	isPageTrashable(cmsPage: ICMSPage, uriContext: IUriContext): angular.IPromise<boolean> {
		return this.homepageService.getHomepageType(cmsPage, uriContext).then((homepageType: HomepageType) => {
			if (homepageType !== null || cmsPage.homepage) {
				return this.homepageService.hasFallbackHomePage(uriContext);
			} else {
				return this.pagesVariationsRestService.getVariationsForPrimaryPageId(cmsPage.uid).then((variationPagesUids: string[]) => {
					return (variationPagesUids.length === 0);
				});
			}
		});
	}

    /** 
     * @ngdoc method
     * @name cmsSmarteditServicesModule.service:ManagePageService#getSoftDeletedPagesCount
     * @methodOf cmsSmarteditServicesModule.service:ManagePageService
     *
     * @description
     * Get the number of soft deleted pages for the provided context.
     * 
     * @param {Object} uriContext A  {@link resourceLocationsModule.object:UriContext uriContext}
     * @returns {object} containing the total number of soft deleted pages
     */
	@Cached({actions: [rarelyChangingContent], tags: [pageDeletionEvictionTag, pageRestoredEvictionTag]})
	getSoftDeletedPagesCount(uriContext: IUriContext): angular.IPromise<number> {
		const requestParams = {
			pageSize: 10,
			currentPage: 0,
			typeCode: 'AbstractPage',
			itemSearchParams: 'pageStatus:deleted',
			catalogId: uriContext.CONTEXT_CATALOG,
			catalogVersion: uriContext.CONTEXT_CATALOG_VERSION
		};

		return this.cmsitemsRestService.get(requestParams).then((result: Page<ICMSPage>) => {
			return result.pagination.totalCount;
		});
	}

	/**
	 * @ngdoc method
	 * @name cmsSmarteditServicesModule.service:ManagePageService#getDisabledTrashTooltipMessage
	 * @methodOf cmsSmarteditServicesModule.service:ManagePageService
	 * 
	 * @description
	 * Get the disabled trash tooltip message.
	 * 
	 * @param {ICMSPage} cmsPage The page content
	 * @returns {Promise} A promise resolved with the disabled trash tooltip message
	 */
	getDisabledTrashTooltipMessage(pageInfo: ICMSPage, uriContext: IUriContext): angular.IPromise<string> {
		let translate: string = 'se.cms.tooltip.movetotrash';
		return this.homepageService.getHomepageType(pageInfo, uriContext).then((homepageType: HomepageType) => {
			if (homepageType === HomepageType.CURRENT) {
				translate = 'se.cms.tooltip.current.homepage.movetotrash';
			} else if (homepageType === HomepageType.OLD) {
				translate = 'se.cms.tooltip.old.homepage.movetotrash';
			}
			return this.$q.when(translate);
		});
	}

	// ------------------------------------------------------------------------
	// Internal Methods
	// ------------------------------------------------------------------------
	/**
	 * Returns appropriate confirmation message key for page deletion.
	 */
	private getConfirmationModalDescription(pageInfo: ICMSPage, uriContext: IUriContext): angular.IPromise<{}> {
		const deferred = this.$q.defer();
		this.pageInfoService.getPageUUID().then((pageUUID: string) => {
			if (pageUUID) {
				this.homepageService.getHomepageType(pageInfo, uriContext).then((homepageType: HomepageType) => {
					if (homepageType !== null || pageInfo.homepage) {
						deferred.resolve("se.cms.actionitem.page.trash.confirmation.description.storefront.homepage");
					} else {
						deferred.resolve("se.cms.actionitem.page.trash.confirmation.description.storefront");
					}
				});
			} else {
				this.$log.error('deletePageService::deletePage - pageUUID is undefined');
				deferred.reject();
			}
		}, () => {
			deferred.resolve("se.cms.actionitem.page.trash.confirmation.description.pagelist");
		});
		return deferred.promise;
	}

	/**
	 * Displays validation errors for page deletion.
	 */
	private displayValidationErrors(response: DeletePageRestServiceErrorResponse, pageName: string): void {
		if (response && response.data) {
			response.data.errors.filter((error: ValidationError) => {
				return error.type === 'ValidationError';
			}).forEach((error: ValidationError) => {
				const alertMessage = this.$translate.instant('se.cms.actionitem.page.trash.alert.failure.prefix', {
					pageName
				}) + error.subject + " - " + error.message;

				this.alertService.showDanger({
					message: alertMessage,
					timeout: 5000
				});
			});
		}
	}

}