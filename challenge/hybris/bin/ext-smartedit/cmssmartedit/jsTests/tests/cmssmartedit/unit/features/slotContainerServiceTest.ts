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
import {ContainerInfo, SlotContainerService} from 'cmssmartedit/services/slotContainerService';
import {IExperienceService, IRestServiceFactory} from 'smarteditcommons';

describe('slotContainerService', () => {

	// --------------------------------------------------------------------------------------
	// Constants
	// --------------------------------------------------------------------------------------
	const PAGE_ID = 'some page ID';
	const PAGE_CONTEXT_SITE_ID = 'some site ID';
	const PAGE_CONTEXT_CATALOG = 'some catalog';
	const PAGE_CONTEXT_CATALOG_VERSION = 'some catalog version';

	const SLOT_ID = 'some slot';
	const CONTAINER_ID = 'some container ID';
	const COMPONENT_IN_CONTAINER_ID = 'withContainer';
	const COMPONENT_WITHOUT_CONTAINER_ID = 'withoutContainer';

	// --------------------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------------------
	let slotContainerService: SlotContainerService;
	let experienceService: jasmine.SpyObj<IExperienceService>;
	let containersRestService: any;
	let restServiceFactory: jasmine.SpyObj<IRestServiceFactory>;
	let sampleContainerInfo: ContainerInfo;

	let $q: angular.IQService;
	let $rootScope: angular.IRootScopeService;

	// --------------------------------------------------------------------------------------
	// Tests
	// --------------------------------------------------------------------------------------
	beforeEach(angular.mock.module('smarteditServicesModule', ($provide: any) => {
		sampleContainerInfo = {
			containerId: CONTAINER_ID,
			containerType: 'some container type',
			pageId: PAGE_ID,
			slotId: SLOT_ID,
			components: [COMPONENT_IN_CONTAINER_ID]
		};

		containersRestService = jasmine.createSpyObj('containersRestService', ['get']);
		containersRestService.get.and.callFake(() => {
			return $q.when({
				pageContentSlotContainerList: [sampleContainerInfo]
			});
		});

		restServiceFactory = jasmine.createSpyObj<IRestServiceFactory>('restServiceFactory', ['get']);
		restServiceFactory.get.and.returnValue(containersRestService);

		$provide.value('restServiceFactory', restServiceFactory);
	}));

	beforeEach(() => {
		const fixture = AngularUnitTestHelper.prepareModule('cmsSmarteditServicesModule')
			.mock('experienceService', 'getCurrentExperience')
			.mockConstant('PAGE_CONTEXT_SITE_ID', PAGE_CONTEXT_SITE_ID)
			.mockConstant('PAGE_CONTEXT_CATALOG', PAGE_CONTEXT_CATALOG)
			.mockConstant('PAGE_CONTEXT_CATALOG_VERSION', PAGE_CONTEXT_CATALOG_VERSION)
			.service('slotContainerService');

		$rootScope = fixture.injected.$rootScope;
		$q = fixture.injected.$q;

		experienceService = fixture.mocks.experienceService;
		experienceService.getCurrentExperience.and.returnValue($q.when({pageId: PAGE_ID}));

		slotContainerService = fixture.service;
	});

	it(`WHEN the service is called
        THEN it calls the right web service`, () => {
			// GIVEN
			const expectedServiceEndpoint = `/cmswebservices/v1/sites/${PAGE_CONTEXT_SITE_ID}/catalogs/${PAGE_CONTEXT_CATALOG}/versions/${PAGE_CONTEXT_CATALOG_VERSION}/pagescontentslotscontainers?pageId=:pageId`;

			// WHEN/THEN
			expect(restServiceFactory.get).toHaveBeenCalledWith(expectedServiceEndpoint);
		});

	it(`GIVEN container information is retrieved before
        WHEN getComponentContainer is called again
        THEN it retrieves the information from cached values`, () => {
			// GIVEN
			expect(containersRestService.get.calls.count()).toBe(0);

			// WHEN/THEN
			slotContainerService.getComponentContainer(SLOT_ID, COMPONENT_IN_CONTAINER_ID);
			$rootScope.$digest();
			expect(containersRestService.get.calls.count()).toBe(1);

			slotContainerService.getComponentContainer(SLOT_ID, COMPONENT_IN_CONTAINER_ID);
			$rootScope.$digest();
			expect(containersRestService.get.calls.count()).toBe(1);
		});

	it(`GIVEN a component is not inside a container
        WHEN getComponentContainer is called
        THEN it returns null`, () => {
			// GIVEN

			// WHEN
			const promise = slotContainerService.getComponentContainer(SLOT_ID, COMPONENT_WITHOUT_CONTAINER_ID);

			// THEN
			promise.then((containerInfo: ContainerInfo) => {
				expect(containerInfo).toBeNull();
			});
			$rootScope.$digest();
		});

	it(`GIVEN a component is inside a container
        WHEN getComponentContainer is called
        THEN it returns the container information`, () => {
			// GIVEN

			// WHEN
			const promise = slotContainerService.getComponentContainer(SLOT_ID, COMPONENT_IN_CONTAINER_ID);

			// THEN
			promise.then((containerInfo: ContainerInfo) => {
				expect(containerInfo).toEqual(sampleContainerInfo);
			});
			$rootScope.$digest();
		});

});
