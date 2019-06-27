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
import {GenericEditorStackService} from "smarteditcommons/components/genericEditor/services/GenericEditorStackService";
import * as lo from "lodash";
import {SystemEventService} from "smarteditcommons";
import {LogHelper} from 'testhelpers';

describe('genericEditorStackServiceModule -', () => {
	const lodash: lo.LoDashStatic = (window as any).smarteditLodash;

	// --------------------------------------------------------------------------------------
	// Constants
	// --------------------------------------------------------------------------------------
	let genericEditorStackService: GenericEditorStackService;

	let $log: LogHelper;
	const EDITOR_PUSH_TO_STACK_EVENT = 'EDITOR_PUSH_TO_STACK_EVENT';
	const EDITOR_POP_FROM_STACK_EVENT = 'EDITOR_POP_FROM_STACK_EVENT';
	let systemEventService: jasmine.SpyObj<SystemEventService>;

	let EDITOR_TO_PUSH_INFO: any;
	let COMPONENT: any;
	let COMPONENT_TYPE: any;
	let EDITOR_ID: any;
	let STACK_ID: any;

	beforeEach(() => {
		$log = new LogHelper();
		systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', ['subscribe']);

		systemEventService.subscribe.calls.reset();

		COMPONENT = 'some component';
		COMPONENT_TYPE = 'some component type';
		EDITOR_ID = 'some editor id';
		STACK_ID = 'some stack id';

		EDITOR_TO_PUSH_INFO = {
			component: COMPONENT,
			componentType: COMPONENT_TYPE,
			editorId: EDITOR_ID
		};

		genericEditorStackService = new GenericEditorStackService(
			$log,
			lodash,
			EDITOR_PUSH_TO_STACK_EVENT,
			EDITOR_POP_FROM_STACK_EVENT,
			systemEventService,
		);
	});

	// --------------------------------------------------------------------------------------
	// Tests
	// --------------------------------------------------------------------------------------
	it('WHEN the service is started THEN it is properly initialized', () => {
		// THEN
		expect(systemEventService.subscribe).toHaveBeenCalledWith(EDITOR_PUSH_TO_STACK_EVENT, jasmine.any(Function));
		expect(systemEventService.subscribe).toHaveBeenCalledWith(EDITOR_POP_FROM_STACK_EVENT, jasmine.any(Function));
	});

	it('GIVEN there are no editors in the stack WHEN I push a new editor THEN a new stack is created AND the editor is added', () => {
		// GIVEN 
		expect(genericEditorStackService.getEditorsStack(STACK_ID)).toBe(null);
		EDITOR_TO_PUSH_INFO.editorStackId = STACK_ID;

		// WHEN 
		(genericEditorStackService as any).pushEditorEventHandler(EDITOR_POP_FROM_STACK_EVENT, EDITOR_TO_PUSH_INFO);

		// THEN
		const stack = genericEditorStackService.getEditorsStack(STACK_ID);
		expect(stack).not.toBe(null);
		expect(stack.length).toBe(1);
		expect(stack[0].component).toBe(EDITOR_TO_PUSH_INFO.component);
		expect(stack[0].componentType).toBe(EDITOR_TO_PUSH_INFO.componentType);
		expect(stack[0].editorId).toBe(EDITOR_TO_PUSH_INFO.editorId);
	});

	it('GIVEN there are editors in a stack WHEN I push a new editor THEN it is added to the stack', () => {
		// GIVEN 
		(genericEditorStackService as any)._editorsStacks[STACK_ID] = ['some other component'];

		EDITOR_TO_PUSH_INFO.editorStackId = STACK_ID;

		// WHEN 
		(genericEditorStackService as any).pushEditorEventHandler(EDITOR_POP_FROM_STACK_EVENT, EDITOR_TO_PUSH_INFO);

		// THEN
		const stack = genericEditorStackService.getEditorsStack(STACK_ID);
		expect(stack).not.toBe(null);
		expect(stack.length).toBe(2);
		expect(stack[1].component).toBe(EDITOR_TO_PUSH_INFO.component);
		expect(stack[1].componentType).toBe(EDITOR_TO_PUSH_INFO.componentType);
		expect(stack[1].editorId).toBe(EDITOR_TO_PUSH_INFO.editorId);
	});

	it('GIVEN there are no editors in a stack WHEN I try to pop an editor THEN a warning is displayed', () => {

		// WHEN 
		(genericEditorStackService as any).popEditorEventHandler(EDITOR_PUSH_TO_STACK_EVENT, {
			editorStackId: STACK_ID
		});

		// THEN
		expect($log.warn).toHaveBeenCalledWith('genericEditorStackService - Stack of editors not found. Cannot pop editor.');
	});

	it('GIVEN there is an editor WHEN the editor is popped THEN it is removed from the stack', () => {
		// GIVEN
		(genericEditorStackService as any)._editorsStacks[STACK_ID] = ['some other component', 'comp 2'];
		// WHEN 
		(genericEditorStackService as any).popEditorEventHandler(EDITOR_PUSH_TO_STACK_EVENT, {
			editorStackId: STACK_ID
		});

		// THEN
		const stack = genericEditorStackService.getEditorsStack(STACK_ID);
		expect(stack.length).toBe(1);
	});

	it('GIVEN there are editors in a stack WHEN getEditorsStack is called THEN they are returned', () => {
		// GIVEN 
		const COMP_1 = 'some other component';
		const COMP_2 = 'comp 2';
		(genericEditorStackService as any)._editorsStacks[STACK_ID] = [COMP_1, COMP_2];

		// WHEN 
		const stackReturned = genericEditorStackService.getEditorsStack(STACK_ID);

		// THEN
		expect(stackReturned).not.toBe(null);
		expect(stackReturned.length).toBe(2);
		expect(stackReturned[0]).toBe(COMP_1 as any);
		expect(stackReturned[1]).toBe(COMP_2 as any);
	});

	it('GIVEN there are no editors in a stack WHEN isTopEditorInStack is called THEN it returns false', () => {
		// GIVEN  

		// WHEN
		const result = genericEditorStackService.isTopEditorInStack(STACK_ID, EDITOR_ID);

		// THEN
		expect(result).toBe(false);
	});

	it('GIVEN there are editors in a stack WHEN isTopEditorInStack is called AND it is the top editor THEN it returns true', () => {
		// GIVEN 
		(genericEditorStackService as any)._editorsStacks[STACK_ID] = [{
			editorId: 'some other editor'
		}, {
			editorId: EDITOR_ID
		}];

		// WHEN 
		const result = genericEditorStackService.isTopEditorInStack(STACK_ID, EDITOR_ID);

		// THEN
		expect(result).toBe(true);
	});

	it('GIVEN there are editors in a stack WHEN isTopEditorInStack is called AND it is not the top editor THEN it returns false', () => {
		// GIVEN 
		(genericEditorStackService as any)._editorsStacks[STACK_ID] = [{
			editorId: EDITOR_ID
		}, {
			editorId: 'some other editor'
		}];

		// WHEN 
		const result = genericEditorStackService.isTopEditorInStack(STACK_ID, EDITOR_ID);

		// THEN
		expect(result).toBe(false);
	});

	it('GIVEN there are no editors opened WHEN areMultipleGenericEditorsOpened is called THEN it returns false', () => {
		// GIVEN 

		// WHEN 
		const result = genericEditorStackService.areMultipleGenericEditorsOpened();

		// THEN 
		expect(result).toBe(false);
	});

	it('GIVEN there is only one stack with one editor WHEN areMultipleGenericEditorsOpened is called THEN it returns false', () => {
		// GIVEN 
		(genericEditorStackService as any)._editorsStacks[STACK_ID] = [{
			editorId: EDITOR_ID
		}];

		// WHEN 
		const result = genericEditorStackService.areMultipleGenericEditorsOpened();

		// THEN 
		expect(result).toBe(false);
	});

	it('GIVEN there are multiple stacks with one editor WHEN areMultipleGenericEditorsOpened is called THEN it returns true', () => {
		// GIVEN 
		(genericEditorStackService as any)._editorsStacks = {
			id1: {
				editorId: EDITOR_ID
			},
			id2: {
				editorId: 'some other editor'
			}
		};

		// WHEN 
		const result = genericEditorStackService.areMultipleGenericEditorsOpened();

		// THEN 
		expect(result).toBe(true);
	});

	it('GIVEN there is one stack with more than one editor WHEN areMultipleGenericEditorsOpened is called THEN it returns true', () => {
		// GIVEN 
		(genericEditorStackService as any)._editorsStacks[STACK_ID] = [{
			editorId: EDITOR_ID
		}, {
			editorId: 'some other editor'
		}];

		// WHEN 
		const result = genericEditorStackService.areMultipleGenericEditorsOpened();

		// THEN 
		expect(result).toBe(true);
	});
});
