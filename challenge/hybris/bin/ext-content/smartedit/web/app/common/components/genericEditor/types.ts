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
import {ILanguage, IUriContext, Payload, Primitive, TypedMap} from "smarteditcommons";
import * as angular from "angular";

export interface GenericEditorAttribute {
	cmsStructureType: string;
	cmsStructureEnumType?: string;
	qualifier: string;
	i18nKey?: string;
	localized?: boolean;
	editable?: boolean;
	required?: boolean;
	collection?: boolean;
	postfixText?: string;
}

export interface GenericEditorField extends GenericEditorAttribute {
	hasErrors?: boolean;
	hasWarnings?: boolean;
	smarteditComponentType?: string;
	messages?: GenericEditorFieldMessage[];
	paged?: boolean;
	template?: string;
	initiated?: string[];
	options?: TypedMap<string[]> | GenericEditorOption;
	customSanitize?: (payload: Primitive | Primitive[] | Payload | Payload[], sanitize: any) => {};
	requiresUserCheck?: TypedMap<boolean>;
	isUserChecked?: boolean;
	defaultValue?: string;
	params?: TypedMap<string>;
	dependsOn?: string;
	uri?: string;
	propertyType?: string;
	idAttribute?: string;
	labelAttributes?: string[];
	errors?: GenericEditorFieldMessage[];

	/**
	 * This map is only used for localized fields. Each entry contains a boolean 
	 * that specifies whether the field should be enabled for a language or not. 
	 */
	isLanguageEnabledMap?: {[languageId: string]: boolean};
}

export interface GenericEditorTab {
	id: string;
	title: string;
	templateUrl: string;
	hasErrors?: boolean;
	active?: boolean;
}

export type GenericEditorFieldsMap = TypedMap<GenericEditorField[]>;

export interface GenericEditorFieldMessage {
	fromSubmit?: boolean;
	isNonPristine?: boolean;
	message: string;
	subject?: string;
	type?: string;
	uniqId?: string;
	marker?: string;
	format?: string;
	language?: string;
	[index: string]: any;
}

export interface GenericEditorTabConfiguration {
	priority: number;
}

export interface GenericEditorStructure {
	attributes: GenericEditorAttribute[];
	category: string;
	type?: string;
}

export interface IGenericEditorFactoryOptions {
	content?: Payload;
	contentApi?: string;
	customOnSubmit?: () => angular.IPromise<any>;
	editorStackId?: string;
	id?: string;
	smarteditComponentId?: string;
	smarteditComponentType?: string;
	structure?: GenericEditorStructure;
	structureApi?: string;
	updateCallback?: (pristine: Payload, results: Payload) => void;
	uriContext?: angular.IPromise<IUriContext>;
}

export type GenericEditorPredicate = (structure: GenericEditorStructure) => boolean;

export interface GenericEditorInfo {
	editorStackId?: string;
	editorId: string;
	component: Payload;
	componentType: string;
}

export interface GenericEditorMapping {
	structureTypeMatcher: string;
	componentTypeMatcher: string;
	discriminatorMatcher: string;
	value: any;
}

export interface GenericEditorMappingConfiguration {
	template: string;
	customSanitize?: (template: string) => string;
	precision?: string;
}

export interface GenericEditorAPI {
	setSubmitButtonText: (_submitButtonText: string) => void;
	setCancelButtonText: (_cancelButtonText: string) => void;
	setAlwaysShowSubmit: (_alwaysShowSubmit: boolean) => void;
	setAlwaysShowReset: (_alwaysShowReset: boolean) => void;
	setOnReset: (_onReset: () => void) => void;
	setPreparePayload: (_preparePayload: (payload: Payload) => angular.IPromise<Payload>) => void;
	setUpdateCallback: (_updateCallback: (pristine: Payload, results: Payload) => void) => void;
	updateContent: (component: Payload) => void;
	getContent: () => Payload;
	onContentChange: () => void;
	clearMessages: () => void;
	switchToTabContainingQualifier: (qualifier: string) => void;
	considerFormDirty: () => void;
	isSubmitDisabled: () => boolean;
	getLanguages: () => void;
}

export interface IGenericEditor {
	id: string;
	component: Payload;
	api: GenericEditorAPI;
	onReset: () => void;
	fieldsMap: GenericEditorFieldsMap;
	componentForm: angular.IFormController;
	alwaysShowSubmit: boolean;
	alwaysShowReset: boolean;
	tabs: GenericEditorTab[];
	pristine: Payload;
	editorStackId: string;
	hasFrontEndValidationErrors: boolean;
	submitButtonText: string;
	cancelButtonText: string;
	parameters: IUriContext;
	inProgress: boolean;
	smarteditComponentType: string;
	smarteditComponentId: string;
	updateCallback: (pristine: Payload, results: Payload) => void;
	structure: GenericEditorStructure;
	uriContext: angular.IPromise<IUriContext>;
	editorStructureService: any;
	editorCRUDService: any;
	initialContent: Payload;
	fields: GenericEditorField[];
	languages: ILanguage[];
	initialDirty: boolean;
	fieldsNonPristineState: any;
	tabSelected: boolean;
	targetedQualifier: string;
	bcPristine: Payload;
	bcComp: Payload;
	requiredLanguages: ILanguage[];

	_finalize(): void;
	init(): angular.IPromise<{}>;
	_populateFieldsNonPristineStates(): void;
	isDirty(): boolean;
	reset(): angular.IPromise<void>;
	submit(): angular.IPromise<{}>;
	getComponent(): Payload;
	isValid(comesFromSubmit?: boolean): boolean;
	isSubmitDisabled(): boolean;
	fetch(): angular.IPromise<{}>;
	removeValidationMessages(): void;
	load(): angular.IPromise<any>;
	fieldAdaptor(fields: GenericEditorAttribute[]): GenericEditorField[];
	pushEditorToStack(): void;
	sanitizePayload(payload: Payload, fields: GenericEditorField[]): Payload;
	popEditorFromStack(): void;
	refreshOptions(field: GenericEditorField, qualifier: string, search: string): void;
}

export interface IGenericEditorConstructor {
	new(conf: IGenericEditorFactoryOptions): IGenericEditor;
}

export interface GenericEditorOption {
	id?: string;
	label?: string | TypedMap<string>;
	[key: string]: any;
}

