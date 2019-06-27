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
import {COMPONENT_CONTAINER_TYPE_PROVIDER, CONTAINER_SOURCE_ID_ATTR_PROVIDER, PersonalizationsmarteditComponentHandlerService} from "personalizationsmartedit/service/PersonalizationsmarteditComponentHandlerService";
import {ComponentHandlerService} from 'smartedit';

describe('PersonalizationsmarteditComponentHandlerService', () => {

	const yjQuery: any = jasmine.createSpy('yjQuery');
	let componentHandlerService: jasmine.SpyObj<ComponentHandlerService>;

	const mockHtml = '<div data-smartedit-personalization-action-id="myAction" data-smartedit-component-id="myId" data-smartedit-component-type="myType"></div><div data-smartedit-container-source-id="myContainerSource" data-smartedit-container-id="myContainer" data-smartedit-container-type="CxCmsComponentContainer" data-smartedit-component-id="myId" data-smartedit-component-type="myType"></div>';
	const mockHtmlWithSlot = '<div data-smartedit-component-id="anotherIdSlot" data-smartedit-component-type="ContentSlot">' + mockHtml + '</div>';

	const CONTAINER_TYPE_ATTRIBUTE: string = 'data-smartedit-container-type';
	const TYPE_ATTRIBUTE: string = 'data-smartedit-component-type';
	const CONTENT_SLOT_TYPE: string = 'ContentSlot';
	const CONTAINER_ID_ATTRIBUTE: string = 'data-smartedit-container-id';
	const ID_ATTRIBUTE: string = 'data-smartedit-component-id';

	let personalizationsmarteditComponentHandlerService: PersonalizationsmarteditComponentHandlerService;


	// === SETUP ===
	beforeEach(() => {

		componentHandlerService = jasmine.createSpyObj('componentHandlerService', ['getParentSlotForComponent', 'getOriginalComponent', 'isExternalComponent', 'getCatalogVersionUuid', 'getAllSlotsSelector']);

		componentHandlerService.getAllSlotsSelector.and.callFake(function() {
			return "";
		});

		personalizationsmarteditComponentHandlerService = new PersonalizationsmarteditComponentHandlerService(
			componentHandlerService,
			yjQuery,
			CONTAINER_TYPE_ATTRIBUTE,
			CONTAINER_ID_ATTRIBUTE,
			TYPE_ATTRIBUTE,
			CONTENT_SLOT_TYPE,
			COMPONENT_CONTAINER_TYPE_PROVIDER.useValue,
			CONTAINER_SOURCE_ID_ATTR_PROVIDER.useValue
		);

		personalizationsmarteditComponentHandlerService.getFromSelector = (function(selector: JQuery) {
			return angular.element(mockHtmlWithSlot).find(selector);
		});

	});

	describe('getParentContainerIdForComponent', () => {

		it('should be defined', function() {
			expect(personalizationsmarteditComponentHandlerService.getParentContainerIdForComponent).toBeDefined();
		});

		it('should return container id if element exists', () => {
			// given
			const element = angular.element(mockHtml);
			// when
			const ret = personalizationsmarteditComponentHandlerService.getParentContainerIdForComponent(element);
			// then
			expect(ret).toBe("myContainer");
		});

		it('should return undefined if element doesnt exists', () => {
			// given
			const element = angular.element("");
			// when
			const ret = personalizationsmarteditComponentHandlerService.getParentContainerIdForComponent(element);
			// then
			expect(ret).toBe(undefined);
		});

	});

	describe('getParentContainerForComponent', () => {

		it('should be defined', () => {
			expect(personalizationsmarteditComponentHandlerService.getParentContainerForComponent).toBeDefined();
		});

		it('should return container id if element exists', () => {
			// given
			const element = angular.element(mockHtml);
			// when
			const ret = personalizationsmarteditComponentHandlerService.getParentContainerForComponent(element);
			// then
			expect(ret.attr(CONTAINER_ID_ATTRIBUTE)).toBe("myContainer");
		});

		it('should return empty array if element element doesnt exists', () => {
			// given
			const element = angular.element("");
			// when
			const ret = personalizationsmarteditComponentHandlerService.getParentContainerForComponent(element);
			// then
			expect(ret.length).toBe(0);
		});

	});

	describe('getParentSlotIdForComponent', () => {

		it('should be defined', () => {
			expect(personalizationsmarteditComponentHandlerService.getParentSlotIdForComponent).toBeDefined();
		});

		it('should return null if element doesnt exist', () => {
			// given
			const element = angular.element("");
			// when
			const ret = personalizationsmarteditComponentHandlerService.getParentSlotIdForComponent(element);
			// then
			expect(ret).toBe(undefined);
		});

	});

	describe('getParentSlotForComponent', () => {

		it('should be defined', () => {
			expect(personalizationsmarteditComponentHandlerService.getParentSlotForComponent).toBeDefined();
		});

		it('should return slot id if element exist', () => {
			// given
			const element = angular.element(mockHtmlWithSlot);
			// when
			const ret = personalizationsmarteditComponentHandlerService.getParentSlotForComponent(element);
			// then
			expect(ret.attr(ID_ATTRIBUTE)).toBe("anotherIdSlot");
		});

		it('should return empty array if element doesnt exist', () => {
			// given
			const element = angular.element("");
			// when
			const ret = personalizationsmarteditComponentHandlerService.getParentSlotForComponent(element);
			// then
			expect(ret.length).toBe(0);
		});

	});

	describe('getContainerSourceIdForContainerId', () => {

		it('should be defined', () => {
			expect(personalizationsmarteditComponentHandlerService.getContainerSourceIdForContainerId).toBeDefined();
		});

		it('should return container source id if container with specific id exist', () => {
			// when
			const ret = personalizationsmarteditComponentHandlerService.getContainerSourceIdForContainerId("myContainer");
			// then
			expect(ret).toBe("myContainerSource");
		});

		it('should return empty string if container with specific id doesnt exist', () => {
			// when
			const ret = personalizationsmarteditComponentHandlerService.getContainerSourceIdForContainerId("myContainerNotExist");
			// then
			expect(ret).toBe("");
		});

	});

});

