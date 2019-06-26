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
import {SeInjectable, SystemEventService, TypedMap} from "smarteditcommons";
import * as lo from "lodash";
import {GenericEditorInfo} from "smarteditcommons/components/genericEditor/types";

/* @internal  */
export const DEFAULT_EDITOR_PUSH_TO_STACK_EVENT = 'EDITOR_PUSH_TO_STACK_EVENT';

/* @internal  */
export const DEFAULT_EDITOR_POP_FROM_STACK_EVENT = 'EDITOR_POP_FROM_STACK_EVENT';

/* @internal */
@SeInjectable()
export class GenericEditorStackService {

	private _editorsStacks: TypedMap<GenericEditorInfo[]>;

	constructor(
		private $log: angular.ILogService,
		private lodash: lo.LoDashStatic,
		private EDITOR_PUSH_TO_STACK_EVENT: string,
		private EDITOR_POP_FROM_STACK_EVENT: string,
		private systemEventService: SystemEventService
	) {
		this._editorsStacks = {};

		this.systemEventService.subscribe(this.EDITOR_PUSH_TO_STACK_EVENT, this.pushEditorEventHandler.bind(this));
		this.systemEventService.subscribe(this.EDITOR_POP_FROM_STACK_EVENT, this.popEditorEventHandler.bind(this));
	}

	// --------------------------------------------------------------------------------------
	// API
	// --------------------------------------------------------------------------------------
	areMultipleGenericEditorsOpened(): boolean {
		return this.lodash.size(this._editorsStacks) > 1 || this.lodash.some(this._editorsStacks, (stack) => {
			return stack.length > 1;
		});
	}

	getEditorsStack(editorStackId: string): GenericEditorInfo[] {
		return this._editorsStacks[editorStackId] || null;
	}

	isTopEditorInStack(editorStackId: string, editorId: string): boolean {
		let result = false;
		const stack = this._editorsStacks[editorStackId];
		if (stack) {
			const topEditor = stack[stack.length - 1];
			result = topEditor && (topEditor.editorId === editorId);
		}

		return result;
	}

	// --------------------------------------------------------------------------------------
	// Helper Methods
	// --------------------------------------------------------------------------------------
	private pushEditorEventHandler(eventId: string, editorToPushInfo: GenericEditorInfo): void {
		this.validateId(editorToPushInfo);

		const stackId = editorToPushInfo.editorStackId;
		if (!this._editorsStacks[stackId]) {
			this._editorsStacks[stackId] = [];
		}

		this._editorsStacks[stackId].push({
			component: editorToPushInfo.component,
			componentType: editorToPushInfo.componentType,
			editorId: editorToPushInfo.editorId
		});
	}

	private popEditorEventHandler(eventId: string, editorToPopInfo: GenericEditorInfo): void {
		this.validateId(editorToPopInfo);

		const stackId = editorToPopInfo.editorStackId;
		const stack = this._editorsStacks[stackId];
		if (!stack) {
			this.$log.warn('genericEditorStackService - Stack of editors not found. Cannot pop editor.');
			return;
		}

		stack.pop();
		if (stack.length === 0) {
			delete this._editorsStacks[stackId];
		}
	}

	private validateId(editorInfo: GenericEditorInfo): void {
		if (!editorInfo.editorStackId) {
			throw new Error('genericEditorStackService - Must provide a stack id.');
		}
	}

}
