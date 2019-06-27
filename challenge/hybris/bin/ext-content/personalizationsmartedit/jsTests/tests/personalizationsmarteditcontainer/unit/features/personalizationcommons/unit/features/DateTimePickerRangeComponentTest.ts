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
import 'jasmine';
import * as angular from 'angular';
import {DateTimePickerRangeComponent} from 'personalizationcommons/DateTimePickerRangeComponent';

describe('DateTimePickerRangeComponent', () => {

	// ======= Injected mocks =======
	let $element: any;
	let $scope: jasmine.SpyObj<angular.IScope>;
	let languageService: jasmine.SpyObj<any>;
	let DATE_CONSTANTS: jasmine.SpyObj<any>;
	let personalizationsmarteditDateUtils: jasmine.SpyObj<any>;

	// Service being tested
	let dateTimePickerRangeComponent: DateTimePickerRangeComponent;

	// === SETUP ===
	beforeEach(() => {
		$element = jasmine.createSpyObj('$element', ['querySelectorAll']);
		$scope = jasmine.createSpyObj('$scope', ['$watch']);

		personalizationsmarteditDateUtils = jasmine.createSpyObj('personalizationsmarteditDateUtils', ['formatDate', 'isDateValidOrEmpty', 'isDateRangeValid']);
		DATE_CONSTANTS = jasmine.createSpyObj('DATE_CONSTANTS', ['sendError']);
		languageService = jasmine.createSpyObj('languageService', ['getBrowserLocale']);

		dateTimePickerRangeComponent = new DateTimePickerRangeComponent(
			DATE_CONSTANTS,
			personalizationsmarteditDateUtils,
			$element,
			$scope,
			languageService
		);
	});

	it('Public API', () => {
		expect(dateTimePickerRangeComponent.getDateOrDefault).toBeDefined();
		expect(dateTimePickerRangeComponent.actionsIfisEditable).toBeDefined();
		expect(dateTimePickerRangeComponent.getMinToDate).toBeDefined();
		expect(dateTimePickerRangeComponent.getFromPickerNode).toBeDefined();
		expect(dateTimePickerRangeComponent.getFromDatetimepicker).toBeDefined();
		expect(dateTimePickerRangeComponent.getToPickerNode).toBeDefined();
		expect(dateTimePickerRangeComponent.getToDatetimepicker).toBeDefined();
	});

});
