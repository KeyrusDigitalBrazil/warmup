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
import {IUriContext, SeInjectable} from 'smarteditcommons';
import {ManagePageService} from 'cmssmarteditcontainer/services/pages/ManagePageService';
import {ICMSPage} from 'cmscommons/dtos/ICMSPage';

/**
 * @ngdoc service
 * @name deletePageServiceModule.service:deletePageService
 *
 * @description
 * The delete page service provides the functionality necessary to handle
 * the soft deletion of a CMS page.
 */
@SeInjectable()
export class DeletePageService {

	constructor(
		private managePageService: ManagePageService,
		private pageService: any,
		private pageFacade: any
	) {}

    /**
     * @ngdoc method
     * @name deletePageServiceModule.service:deletePageService#deletePage
     * @methodOf deletePageServiceModule.service:deletePageService
     * 
     * @deprecated since 1808 - use {@link cmsSmarteditServicesModule.service:ManagePageService#softDeletePage softDeletePage} instead.
     *
     * @description
     * This method triggers the soft deletion of a CMS page.
     *
     * @param {Object} pageInfo The page object containing the uuid and the name of the page to be deleted.
     * @param {Object} uriContext A {@link resourceLocationsModule.object:UriContext uriContext}
     */
	deletePage(pageInfo: ICMSPage, uriContext: IUriContext): angular.IPromise<any> {
		return this.managePageService.softDeletePage(pageInfo, uriContext);
	}

    /**
     * @ngdoc method
     * @name deletePageServiceModule.service:deletePageService#isDeletePageEnabled
     * @methodOf deletePageServiceModule.service:deletePageService
     * 
     * @deprecated since 1808 - use {@link cmsSmarteditServicesModule.service:ManagePageService#isPageTrashable isPageTrashable} instead.
     *
     * @description
     * This method indicates whether the given page can be soft deleted.
     * Only the variation pages and the  primary pages associated with no
     * variation pages are eligible for soft deletion.
     *
     * @param {String} pageUid The unique page identifier for the page to be
     * soft deleted.
     *
     * @returns {Promise} A promise resolved with a boolean indicating
     * whether the selected page can be soft deleted.
     *
     */
	isDeletePageEnabled(pageUid: string): angular.IPromise<boolean> {
		return this.pageFacade.retrievePageUriContext().then((pageUriContext: IUriContext) => {
			return this.pageService.getPageById(pageUid).then((pageInfo: ICMSPage) => {
				return this.managePageService.isPageTrashable(pageInfo, pageUriContext);
			});
		});
	}

}