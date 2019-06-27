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
import {SeModule, SeValueProvider} from "smarteditcommons";

import {GenericEditorFactory} from './GenericEditorFactory';
import {GenericEditorComponent} from "./GenericEditorComponent";
import {GenericEditorTabComponent} from "./GenericEditorTabComponent";
import {GenericEditorFieldComponent} from "./GenericEditorFieldComponent";
import {SeGenericEditorFieldMessagesComponent} from "./GenericEditorFieldMessagesComponent";

import {SeBooleanModule} from "./components/boolean/SeBooleanModule";
import {SeRichTextFieldModule} from "./components/richText/SeRichTextFieldModule";
import {SeDropdownModule} from "./components/dropdown/SeDropdownModule";
import {DateTimePickerModule} from "./components/dateTimePicker/DateTimePickerModule";
import {GenericEditorBreadcrumbModule} from "./components/breadcrumb/GenericEditorBreadcrumbModule";

import {GenericEditorServicesModule} from "./services/GenericEditorServicesModule";
import {EditorFieldMappingService} from "./services/EditorFieldMappingService";
import {DropdownPopulatorModule} from "./populators/DropdownPopulatorModule";

/**
 * @deprecated since 6.5 use GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT.
 * @ngdoc object
 * @name genericEditorModule.object:GENERIC_EDITOR_UNRELATED_VALIDATION_ERRORS_EVENT
 * @description
 * Event to notify GenericEditor about errors that can be relevant to it.
 * Any GenericEditor may receive notification from another GenericEditor that the latter received validation errors not relevant for themselves
 * In such a situation every GenericEditor will try and see if errors are relevant to them.
 */
export const GENERIC_EDITOR_UNRELATED_VALIDATION_ERRORS_EVENT_CONSTANT: SeValueProvider = {
	provide: 'GENERIC_EDITOR_UNRELATED_VALIDATION_ERRORS_EVENT',
	useValue: 'UnrelatedValidationErrors'
};

/**
 * @ngdoc object
 * @name genericEditorModule.object:GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT
 * @description
 * Event to notify GenericEditor about messages (errors, warnings) that can be relevant to it.
 * Any GenericEditor may receive notification from another GenericEditor that the latter received validation messages (errors, warnings) not relevant for themselves.
 * In such a situation every GenericEditor will try and see if messages are relevant to them.
 *
 * @param {Object} payload A map that contains the 'messages' and optional 'targetGenericEditorId' properties.
 * @param {Array} payload.messages List of validation messages (errors, warnings)
 * @param {String=} payload.targetGenericEditorID The id of target generic editor. Allows to send a list of messages to a specific generic editor. Otherwise the list of messages will be delivered to all generic editors.
 *
 * @example systemEventService.publishAsync(GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, {messages: unrelatedValidationMessages, targetGenericEditorId: 'optional-id'});
 */
export const GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT_CONSTANT: SeValueProvider = {
	provide: 'GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT',
	useValue: 'UnrelatedValidationMessagesEvent'
};

/**
 * @ngdoc object
 * @name genericEditorModule.object:GENERIC_EDITOR_LOADED_EVENT
 * @description
 * Event to notify subscribers that GenericEditor is loaded.
 */
export const GENERIC_EDITOR_LOADED_EVENT_CONSTANT: SeValueProvider = {
	provide: 'GENERIC_EDITOR_LOADED_EVENT',
	useValue: 'genericEditorLoadedEvent'
};

/**
 * @ngdoc overview
 * @name genericEditorModule
 */
@SeModule({
	imports: [
		'yjqueryModule',
		'smarteditServicesModule',
		'functionsModule',
		'coretemplates',
		'translationServiceModule',
		'experienceInterceptorModule',
		'yLoDashModule',
		'seConstantsModule',
		'resourceLocationsModule',
		'ui.bootstrap',
		'localizedElementModule',
		DateTimePickerModule,
		SeBooleanModule,
		SeRichTextFieldModule,
		SeDropdownModule,
		GenericEditorBreadcrumbModule,
		GenericEditorServicesModule,
		DropdownPopulatorModule
	],
	providers: [
		GENERIC_EDITOR_UNRELATED_VALIDATION_ERRORS_EVENT_CONSTANT,
		GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT_CONSTANT,
		GENERIC_EDITOR_LOADED_EVENT_CONSTANT,
		{
			provide: 'GenericEditor',
			useFactory: GenericEditorFactory
		}
	],
	declarations: [
		SeGenericEditorFieldMessagesComponent,
		GenericEditorFieldComponent,
		GenericEditorTabComponent,
		GenericEditorComponent
	],
	initialize: (editorFieldMappingService: EditorFieldMappingService) => {
		'ngInject';
		editorFieldMappingService._registerDefaultFieldMappings();
	}
})
export class GenericEditorModule {}
