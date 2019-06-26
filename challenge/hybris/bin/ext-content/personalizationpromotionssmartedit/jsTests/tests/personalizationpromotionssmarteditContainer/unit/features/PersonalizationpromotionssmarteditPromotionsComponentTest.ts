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
import {promiseHelper} from 'testhelpers';
import {PersonalizationpromotionssmarteditPromotionsComponent} from 'personalizationpromotionssmarteditcontainer/management/commerceCustomizationView/promotionsComponent/PersonalizationpromotionssmarteditPromotionsComponent';

describe('PersonalizationpromotionssmarteditPromotionsComponent', () => {

	// ======= Injected mocks =======
	const $q: angular.IQService = promiseHelper.$q();
	let $filter: jasmine.SpyObj<angular.IFilterService>;
	let personalizationpromotionssmarteditRestService: jasmine.SpyObj<any>;
	let personalizationsmarteditMessageHandler: jasmine.SpyObj<any>;
	let actionsDataFactory: jasmine.SpyObj<any>;
	let experienceService: jasmine.SpyObj<any>;

	// Service being tested
	let PromotionsComponent: PersonalizationpromotionssmarteditPromotionsComponent;

	// === SETUP ===
	beforeEach(() => {
		$filter = jasmine.createSpyObj('$filter', ['debug']);

		personalizationpromotionssmarteditRestService = jasmine.createSpyObj('personalizationpromotionssmarteditRestService', ['getPromotions']);
		personalizationsmarteditMessageHandler = jasmine.createSpyObj('personalizationsmarteditMessageHandler', ['sendError']);

		actionsDataFactory = jasmine.createSpyObj('actionsDataFactory', ['isItemInSelectedActions', 'addAction']);
		experienceService = jasmine.createSpyObj('experienceService', ['getCurrentExperience']);

		PromotionsComponent = new PersonalizationpromotionssmarteditPromotionsComponent(
			$q,
			$filter,
			personalizationpromotionssmarteditRestService,
			personalizationsmarteditMessageHandler,
			actionsDataFactory,
			experienceService
		);
	});

	it('Public API', () => {
		expect(PromotionsComponent.getCatalogs).toBeDefined();
		expect(PromotionsComponent.getPromotions).toBeDefined();
		expect(PromotionsComponent.getAvailablePromotions).toBeDefined();
		expect(PromotionsComponent.buildAction).toBeDefined();
		expect(PromotionsComponent.comparer).toBeDefined();
		expect(PromotionsComponent.promotionSelected).toBeDefined();
		expect(PromotionsComponent.isItemInSelectDisabled).toBeDefined();
		expect(PromotionsComponent.initUiSelect).toBeDefined();
	});

});
