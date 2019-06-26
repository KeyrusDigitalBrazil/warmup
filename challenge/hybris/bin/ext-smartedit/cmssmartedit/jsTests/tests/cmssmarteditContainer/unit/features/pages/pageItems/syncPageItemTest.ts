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
import {promiseHelper} from 'testhelpers';
import {SyncPageItemComponent} from "cmssmarteditcontainer/components/pages/pageItems/syncPageItem/SyncPageItemComponent";
import {ICMSPage} from "cmscommons";
import {ICatalogService, SystemEventService} from "smarteditcommons";

describe('SyncPageItemController', () => {

	let controller: SyncPageItemComponent;

	const $q = promiseHelper.$q();

	let MockedSyncPageModalService: any;
	let MockedCatalogService: jasmine.SpyObj<ICatalogService>;
	let MockedSystemEventService: jasmine.SpyObj<SystemEventService>;

	const MOCKED_RESPONSE = "MOCKED_RESPONSE";
	const MOCKED_EVENT_CONTENT_CATALOG_UPDATE = "MOCKED_EVENT_CONTENT_CATALOG_UPDATE";
	const MOCKED_URI_CONTEXT = "MOCKED_URI_CONTEXT";

	beforeEach(function() {
		MockedSyncPageModalService = jasmine.createSpyObj('MockedSyncPageModalService', ['open']);
		MockedCatalogService = jasmine.createSpyObj<ICatalogService>('MockedCatalogService', ['retrieveUriContext']);
		MockedSystemEventService = jasmine.createSpyObj<SystemEventService>('MockedSystemEventService', ['publishAsync']);

		controller = new SyncPageItemComponent(MockedSyncPageModalService, MockedCatalogService, MOCKED_EVENT_CONTENT_CATALOG_UPDATE, MockedSystemEventService);
		controller.pageInfo = {
			uid: "MOCKED_PAGE_INFO_UID"
		} as ICMSPage;
	});

	it("calls syncPageModalService to open a 'sync page' modal", function() {
		MockedCatalogService.retrieveUriContext.and.returnValue($q.resolve(MOCKED_URI_CONTEXT));
		MockedSyncPageModalService.open.and.returnValue($q.resolve(MOCKED_RESPONSE));

		// WHEN
		controller.onClickOnSync();

		// ASSERT
		expect(MockedSyncPageModalService.open).toHaveBeenCalledWith(controller.pageInfo, MOCKED_URI_CONTEXT);
		expect(MockedSystemEventService.publishAsync).toHaveBeenCalledWith(MOCKED_EVENT_CONTENT_CATALOG_UPDATE, MOCKED_RESPONSE);
	});

});