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
import {Payload} from "smarteditcommons";
import {GenericEditorField, GenericEditorOption} from "smarteditcommons/components/genericEditor";

export interface IDropdownPopulator {
	/* @deprecated since 1811 */
	populate(payload: DropdownPopulatorPayload): angular.IPromise<GenericEditorOption[]>;
	isPaged(): boolean;
	fetchAll(payload: DropdownPopulatorPayload): angular.IPromise<GenericEditorOption[]>;
	fetchPage(payload: DropdownPopulatorPagePayload): angular.IPromise<DropdownPopulatorFetchPageResponse>;
	populateAttributes(items: GenericEditorOption[], idAttribute: string, orderedLabelAttributes: string[]): GenericEditorOption[];
	search(items: GenericEditorOption[], searchTerm: string): angular.IPromise<GenericEditorOption[]>;
	getItem(payload: DropdownPopulatorItemPayload): angular.IPromise<GenericEditorOption>;
}

/* @internal */
export interface DropdownPopulatorPayload {
	id?: string;
	field: GenericEditorField;
	model: Payload;
	selection: GenericEditorOption;
	search: string;
}

/* @internal */
export interface DropdownPopulatorItemPayload {
	id: string;
	field: GenericEditorField;
	model: Payload;
}

/* @internal */
export interface DropdownPopulatorPagePayload extends DropdownPopulatorPayload {
	pageSize: number;
	currentPage: number;
}

/* @internal */
export interface DropdownPopulatorFetchPageResponse {
	field: GenericEditorField;
	[index: string]: any;
}