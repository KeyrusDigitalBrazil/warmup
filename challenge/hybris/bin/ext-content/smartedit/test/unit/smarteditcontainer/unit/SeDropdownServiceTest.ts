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
import {promiseHelper, LogHelper} from 'testhelpers';
import {functionsUtils} from "../../../../web/app/common/utils/FunctionsUtils";
import {
	ISEDropdownServiceConstructor,
	SEDropdownServiceFactory
} from "smarteditcommons/components/genericEditor";
import {SystemEventService} from "smarteditcommons";
import {
	IDropdownPopulator,
} from "smarteditcommons/components/genericEditor/populators/types";

describe('seDropdownService', function() {

	let optionsDropdownPopulator: jasmine.SpyObj<IDropdownPopulator>;
	let uriDropdownPopulator: jasmine.SpyObj<IDropdownPopulator>;
	let SEDropdownService: ISEDropdownServiceConstructor;
	let componentXDropdownPopulator: any;
	let componentYdropdownADropdownPopulator: any;
	let systemEventService: jasmine.SpyObj<SystemEventService>;
	let getKeyHoldingDataFromResponse: any;

	const $q = promiseHelper.$q();

	const options = [{
		id: 'id1',
		label: 'label1 - sample'
	}, {
		id: 'id2',
		label: 'label2 - sample option'
	}, {
		id: 'id3',
		label: 'label3 - option'
	}] as any;

	const fetchPageResponse = {
		promise: 'some promise',
		someArray: ['A', 'B', 'C']
	} as any;

	const fieldWithUri = {
		cmsStructureType: 'EditableDropdown',
		qualifier: 'dropdownA',
		i18nKey: 'type.thesmarteditComponentType.dropdownA.name',
		uri: '/someuri',
		smarteditComponentType: 'componentX'
	} as any;

	const fieldWithDependsOn = {
		cmsStructureType: 'EditableDropdown',
		qualifier: 'dropdownA',
		i18nKey: 'type.thesmarteditComponentType.dropdownA.name',
		uri: '/someuri',
		dependsOn: 'dropdown1,dropdown2',
		smarteditComponentType: 'componentX'
	} as any;

	const fieldWithNoneNoPopulator = {
		cmsStructureType: 'EditableDropdown',
		qualifier: 'dropdownX',
		i18nKey: 'type.thesmarteditComponentType.dropdownA.name',
		smarteditComponentType: 'componentY'
	} as any;

	const fieldWithBoth = {
		cmsStructureType: 'EditableDropdown',
		qualifier: 'dropdownA',
		i18nKey: 'type.thesmarteditComponentType.dropdownA.name',
		uri: '/someuri',
		options: [],
		smarteditComponentType: 'componentX'
	} as any;

	const fieldWithPropertyType = {
		cmsStructureType: 'SingleProductSelector',
		propertyType: 'customPropertyType',
		qualifier: 'dropdownA',
		i18nKey: 'type.thesmarteditComponentType.product.name',
		required: true
	} as any;

	const model = {
		dropdown1: '1',
		dropdown2: '2',
		dropdownA: 'id1'
	} as any;

	const qualifier = 'dropdownA';
	const id = new Date().valueOf().toString(10);

	beforeEach(() => {
		uriDropdownPopulator = jasmine.createSpyObj<IDropdownPopulator>('uriDropdownPopulator', ['populate', 'fetchPage']);
		uriDropdownPopulator.populate.and.callFake(function() {
			return $q.when(options);
		});
		uriDropdownPopulator.fetchPage.and.callFake(function() {
			return $q.when(fetchPageResponse);
		});

		optionsDropdownPopulator = jasmine.createSpyObj<IDropdownPopulator>('optionsDropdownPopulator', ['populate']);

		componentYdropdownADropdownPopulator = jasmine.createSpyObj('componentYdropdownADropdownPopulator', ['populate']);
		componentYdropdownADropdownPopulator.populate.and.returnValue(options);

		componentXDropdownPopulator = jasmine.createSpyObj('componentXDropdownPopulator', ['populate']);

		componentXDropdownPopulator.populate.and.returnValue(options);
	});

	const injectProvider = (provider: string) => {
		switch (provider) {
			case 'uriDropdownPopulator':

				return uriDropdownPopulator;

			case 'optionsDropdownPopulator':

				return optionsDropdownPopulator;

			case 'customPropertyTypeDropdownPopulator':

				return {
					type: 'customPropertyTypeDropdownPopulator'
				};

			case 'componentYdropdownADropdownPopulator':

				return componentYdropdownADropdownPopulator;

			case 'componentXDropdownPopulator':

				return componentXDropdownPopulator;
		}

		throw Error('populator not found!');
	};

	let $injector: jasmine.SpyObj<angular.auto.IInjectorService>;
	const isBlank = (value: any) => !!value;

	beforeEach(() => {
		$injector = jasmine.createSpyObj<angular.auto.IInjectorService>('$injector', ['get', 'has']);
		$injector.get.and.callFake(injectProvider);
		$injector.has.and.callFake((provider: string) => {
			return ['uriDropdownPopulator',
				'optionsDropdownPopulator',
				'customPropertyTypeDropdownPopulator',
				'componentYdropdownADropdownPopulator',
				'componentXDropdownPopulator'].indexOf(provider) !== -1;
		});

		systemEventService = jasmine.createSpyObj('systemEventService', ['subscribe', 'publishAsync']);

		getKeyHoldingDataFromResponse = jasmine.createSpy('getKeyHoldingDataFromResponse');
		getKeyHoldingDataFromResponse.and.returnValue('someArray');

		SEDropdownService = SEDropdownServiceFactory(
			$q,
			$injector,
			new LogHelper(),
			isBlank,
			functionsUtils.isEmpty,
			'LinkedDropdown',
			'ClickDropdown',
			'DropdownPopulator',
			systemEventService,
			getKeyHoldingDataFromResponse,
			{
				VALIDATION_ERROR: 'ValidationError',
				WARNING: 'Warning'
			}
		);
	});

	it('seDropdown initializes fine', function() {

		const seDropdown = new SEDropdownService({
			field: fieldWithNoneNoPopulator,
			model,
			qualifier,
			id
		});

		expect((seDropdown as any).field).toEqual(fieldWithNoneNoPopulator);
		expect((seDropdown as any).model).toEqual(model);
		expect(seDropdown.qualifier).toEqual(qualifier);

	});

	describe('init method - ', function() {

		it('GIVEN SEDropdownService is initialized WHEN the field object has both options and uri attributes THEN it throws an error', function() {

			const seDropdown = new SEDropdownService({
				field: fieldWithBoth,
				model,
				qualifier,
				id
			});

			expect(function() {
				return seDropdown.init();
			}).toThrow(new Error('se.dropdown.contains.both.uri.and.options'));

		});

		it('GIVEN SEDropdownService is initialized WHEN the field object has dependsOn attribute THEN init method must register an event', function() {

			const seDropdown = new SEDropdownService({
				field: fieldWithDependsOn,
				model,
				qualifier,
				id
			});

			spyOn((seDropdown as any), '_respondToChange').and.callFake(function() {
				return;
			});
			uriDropdownPopulator.populate.and.returnValue($q.when(options));
			seDropdown.init();

			expect(systemEventService.subscribe).toHaveBeenCalledWith(id + 'LinkedDropdown', jasmine.any(Function));
			const respondToChangeCallback = systemEventService.subscribe.calls.argsFor(0)[1];
			respondToChangeCallback();
			expect((seDropdown as any)._respondToChange).toHaveBeenCalled();

		});

	});

	it('GIVEN SEDropdownService is initialized WHEN fetchAll is called THEN the respective populator is called with the correct payload', function() {

		const searchKey = 'sample';
		const selection = {
			a: 'b'
		};

		const seDropdown = new SEDropdownService({
			field: fieldWithUri,
			model,
			qualifier,
			id
		});

		uriDropdownPopulator.populate.and.returnValue($q.when(options.filter((option: any) => {
			return option.label.toUpperCase().indexOf(searchKey.toUpperCase()) > -1;
		})));
		seDropdown.init();
		(seDropdown as any).selection = selection;
		(seDropdown as any).fetchAll(searchKey);

		expect(uriDropdownPopulator.populate).toHaveBeenCalledWith({
			field: fieldWithUri,
			model,
			search: searchKey,
			selection
		});
		expect((seDropdown as any).items).toEqual([{
			id: 'id1',
			label: 'label1 - sample'
		}, {
			id: 'id2',
			label: 'label2 - sample option'
		}]);
	});

	it('GIVEN SEDropdownService is initialized WHEN triggerAction is called THEN publishAsync method is called with correct attributes', function() {

		const seDropdown = new SEDropdownService({
			field: fieldWithUri,
			model,
			qualifier,
			id
		});

		uriDropdownPopulator.populate.and.returnValue($q.when(options));
		seDropdown.init();
		(seDropdown as any).fetchAll();
		seDropdown.triggerAction();

		expect(systemEventService.publishAsync).toHaveBeenCalledWith(id + 'LinkedDropdown', {
			qualifier,
			optionObject: {
				id: 'id1',
				label: 'label1 - sample'
			}
		});

	});

	it('GIVEN SEDropdownService is initialized WHEN _respondToChange is called and if the fields dependsOn doesnot match the input qualifier THEN then nothing happens (populator not called)', function() {

		const seDropdown = new SEDropdownService({
			field: fieldWithUri,
			model,
			qualifier,
			id
		} as any);

		uriDropdownPopulator.populate.and.returnValue($q.when(options));

		(seDropdown as any)._respondToChange(qualifier, {
			id: 'id1',
			label: 'label1 - sample'
		});

		expect(uriDropdownPopulator.populate).not.toHaveBeenCalled();

	});

	it('GIVEN SEDropdownService is initialized WHEN _respondToChange is called and if the fields dependsOn matches the input qualifier THEN then reset is called on the child component and a selection is made ready for the next refresh', function() {

		const seDropdown = new SEDropdownService({
			field: fieldWithDependsOn,
			model,
			qualifier,
			id
		});

		// 2-way binding with child defined function
		seDropdown.reset = function() {
			return;
		};

		spyOn(seDropdown, 'reset');

		seDropdown.init();

		const changeObject = {
			qualifier: 'dropdown1',
			optionObject: {},
		};

		(seDropdown as any)._respondToChange('SomeKey', changeObject);
		expect(seDropdown.reset).toHaveBeenCalled();
		expect((seDropdown as any).selection).toBe(changeObject.optionObject);

	});

	it('GIVEN SEDropdownService is initialized with a field object that has a propertyType attribute WHEN fetchAll is called THEN the respective populator is called with the correct payload', function() {
		const seDropdown = new SEDropdownService({
			field: fieldWithPropertyType
		} as any);
		seDropdown.init();
		expect((seDropdown as any).populator.type).toEqual('customPropertyTypeDropdownPopulator');
	});

	it('GIVEN SEDropdown is initialized WHEN fetchPage is called THEN it retrieves and returns the result with the right format', function() {
		// GIVEN
		const seDropdown = new SEDropdownService({
			field: fieldWithUri,
			model,
			qualifier,
			id
		});

		const expectedResult = ['A', 'B', 'C'];

		// WHEN
		seDropdown.init();
		const result = (seDropdown as any).fetchPage();

		// THEN
		expect(getKeyHoldingDataFromResponse).toHaveBeenCalledWith(fetchPageResponse);
		result.then((value: any) => {
			expect(value.results).toEqualData(expectedResult);
		});
	});

});
