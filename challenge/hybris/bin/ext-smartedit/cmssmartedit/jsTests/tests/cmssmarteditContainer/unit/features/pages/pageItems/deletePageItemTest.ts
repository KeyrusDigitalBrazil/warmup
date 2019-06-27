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
import {DeletePageItemComponent} from "cmssmarteditcontainer/components/pages/pageItems/deletePageItem/DeletePageItemComponent";
import {ManagePageService} from "cmssmarteditcontainer/services/pages/ManagePageService";
import {ICatalogService, SystemEventService} from "smarteditcommons";
import {promiseHelper} from 'testhelpers';
import {ICMSPage} from "cmscommons";

describe('DeletePageItemController', () => {

	let controller: DeletePageItemComponent;

	const $q = promiseHelper.$q();

	let MockedManagePageService: any;
	let MockedSystemEventService: jasmine.SpyObj<SystemEventService>;
	let MockedCatalogService: jasmine.SpyObj<ICatalogService>;

	const MOCKED_EVENT_CONTENT_CATALOG_UPDATE = "EVENT_CONTENT_CATALOG_UPDATE";
	const MOCKED_IS_PAGE_TRASH_RESPONSE = true;
	const MOCKED_URI_CONTEXT = "URI_CONTEXT";
	const MOCKED_PAGE_INFO = {
		uid: "MOCKED_SELECTED_ITEM_UID",
	};

	beforeEach(() => {
		MockedManagePageService = jasmine.createSpyObj<ManagePageService>('MockedManagePageService', ['isPageTrashable', 'getDisabledTrashTooltipMessage', 'softDeletePage']);
		MockedSystemEventService = jasmine.createSpyObj<SystemEventService>('MockedSystemEventService', ['publishAsync']);
		MockedCatalogService = jasmine.createSpyObj<ICatalogService>('MockedCatalogService', ['retrieveUriContext']);

		controller = new DeletePageItemComponent(MockedManagePageService, MockedSystemEventService, MockedCatalogService, MOCKED_EVENT_CONTENT_CATALOG_UPDATE);
		controller.pageInfo = MOCKED_PAGE_INFO as ICMSPage;
	});

	it('$onInit sets "isPageTrashable" with result returned by deletePageService for a given pageUid', function() {

		// GIVEN
		MockedCatalogService.retrieveUriContext.and.returnValue($q.resolve(MOCKED_URI_CONTEXT));
		MockedManagePageService.isPageTrashable.and.returnValue($q.resolve(MOCKED_IS_PAGE_TRASH_RESPONSE));

		// WHEN
		controller.$onInit();

		// ASSERT
		expect(MockedManagePageService.isPageTrashable).toHaveBeenCalledWith(MOCKED_PAGE_INFO, MOCKED_URI_CONTEXT);
		expect(controller.isDeletePageEnabled).toBe(MOCKED_IS_PAGE_TRASH_RESPONSE);
		expect(controller.tooltipMessage).toBeNull();
	});

	it('onClickOnDeletePage sends an event when the soft deletion of a page is resolved', function() {

		// GIVEN
		MockedCatalogService.retrieveUriContext.and.returnValue($q.resolve(MOCKED_URI_CONTEXT));
		MockedManagePageService.softDeletePage.and.returnValue($q.resolve(MOCKED_IS_PAGE_TRASH_RESPONSE));

		// WHEN
		controller.onClickOnDeletePage();

		// ASSERT
		expect(MockedManagePageService.softDeletePage).toHaveBeenCalledWith(MOCKED_PAGE_INFO, MOCKED_URI_CONTEXT);
		expect(MockedSystemEventService.publishAsync).toHaveBeenCalledWith(MOCKED_EVENT_CONTENT_CATALOG_UPDATE, MOCKED_IS_PAGE_TRASH_RESPONSE);

	});

});