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
import {IRestService} from "smarteditcommons";
import {FetchEnumDataHandler} from "smarteditcommons/components/genericEditor/services/FetchEnumDataHandler";

describe('fetchEnumDataHandler', () => {

	let $q;

	let fetchEnumDataHandler: FetchEnumDataHandler;
	let enumRestService: jasmine.SpyObj<IRestService<any>>;
	let restServiceFactory: jasmine.SpyObj<IRestService<any>>;

	const field = {
		cmsStructureEnumType: 'de.mypackage.Orientation'
	} as any;

	const data = [{
		code: 'code1',
		label: 'Vertical'
	}, {
		code: 'code2',
		label: 'Horizontal'
	}];

	beforeEach(() => {
		$q = promiseHelper.$q();

		enumRestService = jasmine.createSpyObj<IRestService<any>>('enumRestService', ['get']);
		enumRestService.get.and.returnValue($q.when({
			enums: data
		}));

		restServiceFactory = jasmine.createSpyObj<IRestService<any>>('restServiceFactory', ['get']);
		restServiceFactory.get.and.returnValue(enumRestService);

		const isBank = (value: any) => {
			return !value;
		};

		fetchEnumDataHandler = new FetchEnumDataHandler(
			$q,
			restServiceFactory,
			isBank,
			'ENUM_RESOURCE_URI'
		);

		FetchEnumDataHandler.resetForTests();
	});

	it('GIVEN enum REST call succeeds WHEN I findByMask with no mask, promise resolves to the full list', () => {

		// WHEN
		const promise = fetchEnumDataHandler.findByMask(field);

		// THEN
		expect(promise).toBeResolvedWithData(data);
	});

	it('GIVEN enum REST call succeeds WHEN I findByMask with a mask, promise resolves to the relevant filtered list', () => {

		// WHEN
		const promise = fetchEnumDataHandler.findByMask(field, 'zo');

		// THEN
		expect(promise).toBeResolvedWithData([{
			code: 'code2',
			label: 'Horizontal'
		}]);
	});

	it('GIVEN a first search, second uses cache', () => {

		// WHEN
		fetchEnumDataHandler.findByMask(field, 'zo');
		expect(enumRestService.get.calls.count()).toBe(1);

		fetchEnumDataHandler.findByMask(field, 'zon');

		expect(enumRestService.get.calls.count()).toBe(1);

	});

});
