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

import {PersonalizationsmarteditShowActionListComponent} from "personalizationsmartedit/contextMenu/ShowActionList/PersonalizationsmarteditShowActionListComponent";
import {PersonalizationsmarteditContextService} from "personalizationsmartedit/service/PersonalizationsmarteditContextServiceInner";
import {PersonalizationsmarteditComponentHandlerService} from "personalizationsmartedit/service/PersonalizationsmarteditComponentHandlerService";

import 'jasmine';

describe('PersonalizationsmarteditShowActionListComponent', () => {

	let showActionListComponent: PersonalizationsmarteditShowActionListComponent;

	let personalizationsmarteditContextService: jasmine.SpyObj<PersonalizationsmarteditContextService>;
	let personalizationsmarteditComponentHandlerService: jasmine.SpyObj<PersonalizationsmarteditComponentHandlerService>;
	let personalizationsmarteditUtils: jasmine.SpyObj<any>;

	const mockSelectedItems = [{
		containerId: '1234',
		visible: false
	}, {
		containerId: '5678',
		visible: false
	}];

	beforeEach(() => {

		personalizationsmarteditUtils = jasmine.createSpyObj('personalizationsmarteditUtils', ['getClassForElement', 'getLetterForElement']);
		personalizationsmarteditContextService = jasmine.createSpyObj('personalizationsmarteditContextService', ['getCombinedView']);
		personalizationsmarteditComponentHandlerService = jasmine.createSpyObj('personalizationsmarteditComponentHandlerService', ['getContainerSourceIdForContainerId']);

		personalizationsmarteditContextService.getCombinedView.and.callFake(() => {
			return {
				selectedItems: mockSelectedItems
			};
		});

		personalizationsmarteditUtils.getClassForElement.and.returnValue(() => {
			return 'classForElement';
		});
		personalizationsmarteditUtils.getLetterForElement.and.returnValue('letterForElement');

		showActionListComponent = new PersonalizationsmarteditShowActionListComponent(
			personalizationsmarteditContextService,
			personalizationsmarteditUtils,
			personalizationsmarteditComponentHandlerService
		);
	});

	describe('Component API', () => {

		it('should have proper api when initialized without parameters', () => {
			expect(showActionListComponent.getClassForElement).toBeDefined();
			expect(showActionListComponent.getLetterForElement).toBeDefined();
			expect(showActionListComponent.initItem).toBeDefined();
			expect(showActionListComponent.isCustomizationFromCurrentCatalog).toBeDefined();
			expect(showActionListComponent.$onInit).toBeDefined();
		});

		it('should have proper api when initialized with parameters', () => {
			// given
			const bindings = {
				containerId: '1234'
			};
			showActionListComponent.component = bindings;

			// when
			showActionListComponent.$onInit();

			// then
			expect(showActionListComponent.selectedItems.length).toBe(2);
			expect(showActionListComponent.getClassForElement).toBeDefined();
			expect(showActionListComponent.getLetterForElement).toBeDefined();
			expect(showActionListComponent.$onInit).toBeDefined();
			expect(showActionListComponent.component.containerId).toEqual('1234');
		});

	});
});
