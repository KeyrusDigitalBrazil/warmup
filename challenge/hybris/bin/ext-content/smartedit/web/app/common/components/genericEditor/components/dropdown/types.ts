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
import {GenericEditorField} from "smarteditcommons/components/genericEditor";

/* @internal */
export interface SEDropdownConfiguration {
	field: GenericEditorField;
	qualifier: string;
	model: any;
	id: string;
	onClickOtherDropdown?: (key?: string, qualifier?: string) => void;
	getApi?: ($api: {$api: SEDropdownAPI}) => void;
}

/* @internal */
export interface SEDropdownAPI {
	setResultsHeaderTemplateUrl(resultsHeaderTemplateUrl: string): void;
	setResultsHeaderTemplate(resultsHeaderTemplate: string): void;
}

/* @internal */
export interface ISEDropdownService {
	qualifier: string;
	initialized: boolean;
	isMultiDropdown: boolean;
	resultsHeaderTemplateUrl: string;
	resultsHeaderTemplate: string;
	init(): void;
	onClick(): void;
	triggerAction(): void;
	reset(): void;
}

/* @internal */
export interface ISEDropdownServiceConstructor {
	new(conf: SEDropdownConfiguration): ISEDropdownService;
}