/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

import 'jasmine';
import {promiseHelper} from "testhelpers";
import {PersonalizationsmarteditContextService} from 'personalizationsmartedit/service/PersonalizationsmarteditContextServiceInner';
import {PersonalizationsmarteditComponentHandlerService} from 'personalizationsmartedit/service/PersonalizationsmarteditComponentHandlerService';
import {PersonalizationsmarteditShowComponentInfoListComponent} from "personalizationsmartedit/contextMenu/ShowComponentInfoList/PersonalizationsmarteditShowComponentInfoList";

describe('showComponentInfoListModule', () => {

	// ======= Injected mocks =======
	const $q = promiseHelper.$q();
	let personalizationsmarteditContextService: PersonalizationsmarteditContextService;
	let personalizationSmartEditUtils: jasmine.SpyObj<any>;
	let personalizationsmarteditRestService: jasmine.SpyObj<any>;
	let personalizationsmarteditMessageHandler: jasmine.SpyObj<any>;
	let $filter: jasmine.SpyObj<angular.IFilterService>;
	let personalizationsmarteditComponentHandlerService: jasmine.SpyObj<PersonalizationsmarteditComponentHandlerService>;

	const CONTAINER_ID = '1234';

	const mockActions = {
		actions: [{
			actionCatalog: '1234',
			actionCatalogVersion: 'Staged'
		}, {
			actionCatalog: '1234',
			actionCatalogVersion: 'Online'
		}],
		pagination: {
			count: 2,
			page: 0,
			totalCount: 2,
			totalPages: 1
		}
	};

	// Service being tested
	let showComponentInfoListModule: PersonalizationsmarteditShowComponentInfoListComponent;


	beforeEach(() => {
		personalizationsmarteditContextService = jasmine.createSpyObj('personalizationsmarteditContextService', ['getSeData']);
		personalizationSmartEditUtils = jasmine.createSpyObj('personalizationSmartEditUtils', ['getAndSetCatalogVersionNameL10N']);
		personalizationsmarteditRestService = jasmine.createSpyObj('personalizationsmarteditRestService', ['getCxCmsAllActionsForContainer']);
		personalizationsmarteditMessageHandler = jasmine.createSpyObj('personalizationsmarteditMessageHandler', ['sendError']);
		$filter = jasmine.createSpyObj('angularFilter', ['mock']);
		personalizationsmarteditComponentHandlerService = jasmine.createSpyObj('personalizationsmarteditComponentHandlerService', ['getContainerSourceIdForContainerId']);

		personalizationsmarteditComponentHandlerService.getContainerSourceIdForContainerId.and.returnValue(CONTAINER_ID);
		personalizationsmarteditRestService.getCxCmsAllActionsForContainer.and.returnValue($q.when(mockActions));
		personalizationSmartEditUtils.getAndSetCatalogVersionNameL10N.and.callThrough();

		showComponentInfoListModule = new PersonalizationsmarteditShowComponentInfoListComponent(
			personalizationsmarteditContextService,
			personalizationSmartEditUtils,
			personalizationsmarteditRestService,
			personalizationsmarteditMessageHandler,
			$filter,
			personalizationsmarteditComponentHandlerService
		);
	});

	describe('Component API', function() {

		it('should have proper api when initialized without parameters', function() {
			expect(showComponentInfoListModule.isContainerIdEmpty).not.toBeDefined();
			expect(showComponentInfoListModule.actions).not.toBeDefined();
			expect(showComponentInfoListModule.moreCustomizationsRequestProcessing).not.toBeDefined();
			expect(showComponentInfoListModule.$onInit).toBeDefined();
		});

		it('should have proper api when initialized with parameters', function() {
			const bindings = {
				containerId: CONTAINER_ID
			};
			showComponentInfoListModule.component = bindings;
			showComponentInfoListModule.$onInit();
			expect(showComponentInfoListModule.component.containerId).toEqual(CONTAINER_ID);
			expect(showComponentInfoListModule.isContainerIdEmpty).toBeDefined();
			expect(showComponentInfoListModule.moreCustomizationsRequestProcessing).toBe(false);
			expect(showComponentInfoListModule.$onInit).toBeDefined();
		});

		it('should have actions when initialized with parameters', function() {
			const bindings = {
				containerId: CONTAINER_ID
			};
			showComponentInfoListModule.component = bindings;
			showComponentInfoListModule.$onInit();

			showComponentInfoListModule.addMoreItems();
			expect(showComponentInfoListModule.component.containerId).toEqual(CONTAINER_ID);
			expect(showComponentInfoListModule.isContainerIdEmpty).toBeDefined();
			expect(showComponentInfoListModule.pagination.getTotalCount()).toEqual(2);
			expect(showComponentInfoListModule.actions.length).toEqual(2);
			expect(showComponentInfoListModule.moreCustomizationsRequestProcessing).toBe(false);
		});
	});

});
