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
import {
	ILanguage,
	IRestServiceFactory,
	ISharedDataService,
	IUriContext,
	LanguageService,
	Payload,
	SystemEventService,
	TypedMap
} from "smarteditcommons";
import {
	EditorFieldMappingService,
	GenericEditorAttribute,
	GenericEditorAPI,
	GenericEditorField,
	GenericEditorFieldsMap,
	GenericEditorFieldMessage,
	GenericEditorStackService,
	GenericEditorStructure,
	GenericEditorTab,
	GenericEditorTabService,
	IFetchDataHandler,
	IGenericEditor,
	IGenericEditorFactoryOptions,
	SeValidationMessageParser
} from "smarteditcommons/components/genericEditor";
import * as angular from "angular";
import * as lo from "lodash";

/**
 * @ngdoc service
 * @name genericEditorModule.service:GenericEditor
 * @description
 * The Generic Editor is a class that makes it possible for SmartEdit users (CMS managers, editors, etc.) to edit components in the SmartEdit interface.
 * The Generic Editor class is used by the {@link genericEditorModule.directive:genericEditor genericEditor} directive.
 * The genericEditor directive makes a call either to a Structure API or, if the Structure API is not available, it reads the data from a local structure to request the information that it needs to build an HTML form.
 * It then requests the component by its type and ID from the Content API. The genericEditor directive populates the form with the data that is has received.
 * The form can now be used to edit the component. The modified data is saved using the Content API if it is provided else it would return the form data itself.
 * <br/><br/>
 * <strong>The structure and the REST structure API</strong>.
 * <br/>
 * The constructor of the {@link genericEditorModule.service:GenericEditor GenericEditor} must be provided with the pattern of a REST Structure API, which must contain the string  ":smarteditComponentType", or with a local data structure.
 * If the pattern, Structure API, or the local structure is not provided, the Generic Editor will fail. If the Structure API is used, it must return a JSON payload that holds an array within the attributes property.
 * If the actual structure is used, it must return an array. Each entry in the array provides details about a component property to be displayed and edited. The following details are provided for each property:
 *
 * <ul>
 * <li><strong>qualifier:</strong> Name of the property.
 * <li><strong>i18nKey:</strong> Key of the property label to be translated into the requested language.
 * <li><strong>editable:</strong> Boolean that indicates if a property is editable or not. The default value is true.
 * <li><strong>localized:</strong> Boolean that indicates if a property is localized or not. The default value is false.
 * <li><strong>required:</strong> Boolean that indicates if a property is mandatory or not. The default value is false.
 * <li><strong>cmsStructureType:</strong> Value that is used to determine which form widget (property editor) to display for a specified property.
 * The selection is based on an extensible strategy mechanism owned by {@link genericEditorServicesModule.service:editorFieldMappingService editorFieldMappingService}.
 * <li><strong>cmsStructureEnumType:</strong> The qualified name of the Enum class when cmsStructureType is "Enum"
 * </li>
 * <ul><br/>
 *
 * <b>Note:</b><br/>
 * The generic editor has a tabset within. This allows it to display complex types in an organized and clear way. By default, all fields are stored
 * in the default tab, and if there is only one tab the header is hidden. The selection and configuration of where each field resides is
 * controlled by the {@link genericEditorServicesModule.service:editorFieldMappingService editorFieldMappingService}. Similarly, the rendering
 * of tabs can be customized with the {@link genericEditorServicesModule.service:genericEditorTabService genericEditorTabService}.
 * <br />
 * <br />
 *
 * There are two options when you use the Structure API. The first option is to use an API resource that returns the structure object.
 * The following is an example of the JSON payload that is returned by the Structure API in this case:
 * <pre>
 * {
 *     attributes: [{
 *         cmsStructureType: "ShortString",
 *         qualifier: "someQualifier1",
 *         i18nKey: 'i18nkeyForsomeQualifier1',
 *         localized: false
 *     }, {
 *         cmsStructureType: "LongString",
 *         qualifier: "someQualifier2",
 *         i18nKey: 'i18nkeyForsomeQualifier2',
 *         localized: false
 *    }, {
 *         cmsStructureType: "RichText",
 *         qualifier: "someQualifier3",
 *         i18nKey: 'i18nkeyForsomeQualifier3',
 *         localized: true,
 *         required: true
 *     }, {
 *         cmsStructureType: "Boolean",
 *         qualifier: "someQualifier4",
 *         i18nKey: 'i18nkeyForsomeQualifier4',
 *         localized: false
 *     }, {
 *         cmsStructureType: "DateTime",
 *         qualifier: "someQualifier5",
 *         i18nKey: 'i18nkeyForsomeQualifier5',
 *         localized: false
 *     }, {
 *         cmsStructureType: "Media",
 *         qualifier: "someQualifier6",
 *         i18nKey: 'i18nkeyForsomeQualifier6',
 *         localized: true,
 *         required: true
 *     }, {
 *         cmsStructureType: "Enum",
 *         cmsStructureEnumType:'de.mypackage.Orientation'
 *         qualifier: "someQualifier7",
 *         i18nKey: 'i18nkeyForsomeQualifier7',
 *         localized: true,
 *         required: true
 *     }]
 * }
 * </pre><br/>
 * The second option is to use an API resource that returns a list of structures. In this case, the generic editor will select the first element from the list and use it to display its attributes.
 * The generic editor expects the structures to be in one of the two fields below.
 * <pre>
 * {
 *     structures: [{}, {}]
 * }
 *
 * or
 *
 * {
 *     componentTypes: [{}, {}]
 * }
 * </pre>
 * If the list has more than one element, the Generic Editor will throw an exception, otherwise it will get the first element on the list.
 * The following is an example of the JSON payload that is returned by the Structure API in this case:
 * <pre>
 * {
 *     structures: [
 *         {
 *             attributes: [{
 *                 		cmsStructureType: "ShortString",
 *                 		qualifier: "someQualifier1",
 *                 		i18nKey: 'i18nkeyForsomeQualifier1',
 *                 		localized: false
 *             		}, {
 *                 		cmsStructureType: "LongString",
 *                 		qualifier: "someQualifier2",
 *                 		i18nKey: 'i18nkeyForsomeQualifier2',
 *                 		localized: false
 *         	   		}]
 *         }
 *     ]
 * }
 * </pre>
 * <pre>
 * {
 *     componentTypes: [
 *         {
 *             attributes: [{
 *                 		cmsStructureType: "ShortString",
 *                 		qualifier: "someQualifier1",
 *                 		i18nKey: 'i18nkeyForsomeQualifier1',
 *                 		localized: false
 *             		}, {
 *                 		cmsStructureType: "LongString",
 *                 		qualifier: "someQualifier2",
 *                 		i18nKey: 'i18nkeyForsomeQualifier2',
 *                 		localized: false
 *         	   		}]
 *         }
 *     ]
 * }
 * </pre>
 * The following is an example of the expected format of a structure:
 * <pre>
 *    [{
 *         cmsStructureType: "ShortString",
 *         qualifier: "someQualifier1",
 *         i18nKey: 'i18nkeyForsomeQualifier1',
 *         localized: false
 *     }, {
 *         cmsStructureType: "LongString",
 *         qualifier: "someQualifier2",
 *         i18nKey: 'i18nkeyForsomeQualifier2',
 *         editable: false,
 *         localized: false
 *    }, {
 *         cmsStructureType: "RichText",
 *         qualifier: "someQualifier3",
 *         i18nKey: 'i18nkeyForsomeQualifier3',
 *         localized: true,
 *         required: true
 *     }, {
 *         cmsStructureType: "Boolean",
 *         qualifier: "someQualifier4",
 *         i18nKey: 'i18nkeyForsomeQualifier4',
 *         localized: false
 *     }, {
 *         cmsStructureType: "DateTime",
 *         qualifier: "someQualifier5",
 *         i18nKey: 'i18nkeyForsomeQualifier5',
 *         editable: false,
 *         localized: false
 *     }, {
 *         cmsStructureType: "Media",
 *         qualifier: "someQualifier6",
 *         i18nKey: 'i18nkeyForsomeQualifier6',
 *         localized: true,
 *         required: true
 *     }, {
 *         cmsStructureType: "Enum",
 *         cmsStructureEnumType:'de.mypackage.Orientation'
 *         qualifier: "someQualifier7",
 *         i18nKey: 'i18nkeyForsomeQualifier7',
 *         localized: true,
 *         required: true
 *     }]
 * </pre>
 *
 * <strong>The REST CRUD API</strong>, is given to the constructor of {@link genericEditorModule.service:GenericEditor GenericEditor}.
 * The CRUD API must support GET and PUT of JSON payloads.
 * The PUT method must return the updated payload in its response. Specific to the GET and PUT, the payload must fulfill the following requirements:
 * <ul>
 * 	<li>DateTime types: Must be serialized as long timestamps.</li>
 * 	<li>Media types: Must be serialized as identifier strings.</li>
 * 	<li>If a cmsStructureType is localized, then we expect that the CRUD API returns a map containing the type (string or map) and the map of values, where the key is the language and the value is the content that the type returns.</li>
 * </ul>
 *
 * The following is an example of a localized payload:
 * <pre>
 * {
 *    content: {
 * 		'en': 'content in english',
 * 		'fr': 'content in french',
 * 		'hi': 'content in hindi'
 * 	  }
 * }
 * </pre>
 *
 * <br/><br/>
 *
 * If a validation warning or error occurs, the PUT method of the REST CRUD API will return a validation warning/error object that contains an array of validation messages. The information returned for each validation message is as follows:
 * <ul>
 * 	<li><strong>subject:</strong> The qualifier that has the error</li>
 * 	<li><strong>message:</strong> The error message to be displayed</li>
 * 	<li><strong>type:</strong> The type of message returned. This is of the type ValidationError or Warning.</li>
 * 	<li><strong>language:</strong> The language the error needs to be associated with. If no language property is provided, a match with regular expression /(Language: \[)[a-z]{2}\]/g is attempted from the message property. As a fallback, it implies that the field is not localized.</li>
 * </ul>
 *
 * The following code is an example of an error response object:
 * <pre>
 * {
 *    errors: [{
 *        subject: 'qualifier1',
 *        message: 'error message for qualifier',
 *        type: 'ValidationError'
 *    }, {
 *        subject: 'qualifier2',
 *        message: 'error message for qualifier2 language: [fr]',
 *        type: 'ValidationError'
 *    }, {
 *        subject: 'qualifier3',
 *        message: 'error message for qualifier2',
 *        type: 'ValidationError'
 *    }, {
 *        subject: 'qualifier4',
 *        message: 'warning message for qualifier4',
 *        type: 'Warning'
 *    }]
 * }
 *
 * </pre>
 *
 * Whenever any sort of dropdown is used in one of the cmsStructureType widgets, it is advised using {@link genericEditorModule.service:GenericEditor#methods_refreshOptions refreshOptions method}. See this method documentation to learn more.
 *
 */
export function GenericEditorFactory(
	yjQuery: JQueryStatic,
	encode: any,
	GENERIC_EDITOR_UNRELATED_VALIDATION_ERRORS_EVENT: string,
	GENERIC_EDITOR_LOADED_EVENT: string,
	GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT: string,
	VALIDATION_MESSAGE_TYPES: any,
	EDITOR_PUSH_TO_STACK_EVENT: string,
	EDITOR_POP_FROM_STACK_EVENT: string,
	lodash: lo.LoDashStatic,
	restServiceFactory: IRestServiceFactory,
	languageService: LanguageService,
	sharedDataService: ISharedDataService,
	systemEventService: SystemEventService,
	sanitize: any,
	sanitizeHTML: any,
	copy: any,
	isBlank: (value: any) => boolean,
	isObjectEmptyDeep: any,
	$q: angular.IQService,
	$log: angular.ILogService,
	$translate: angular.translate.ITranslateService,
	$injector: angular.auto.IInjectorService,
	seValidationMessageParser: SeValidationMessageParser,
	editorFieldMappingService: EditorFieldMappingService,
	genericEditorTabService: GenericEditorTabService,
	genericEditorStackService: GenericEditorStackService,
	resetObject: any,
	deepObjectPropertyDiff: any,
	CONTEXT_SITE_ID: string,
	CONTEXT_CATALOG: string,
	CONTEXT_CATALOG_VERSION: string
) {
	'ngInject';

	const primitiveTypes = ["Boolean", "ShortString", "LongString", "RichText", "Date", "Dropdown"];

	editorFieldMappingService._registerDefaultFieldMappings();

	class GenericEditor implements IGenericEditor {

		public hasFrontEndValidationErrors: boolean;
		public submitButtonText: string;
		public cancelButtonText: string;
		public alwaysShowSubmit: boolean;
		public alwaysShowReset: boolean;
		public onReset: () => void;
		public parameters: IUriContext;
		public api: GenericEditorAPI;

		public id: string;
		public inProgress: boolean;
		public smarteditComponentType: string;
		public smarteditComponentId: string;
		public editorStackId: string;
		public updateCallback: (pristine: Payload, results: Payload) => void;
		public structure: GenericEditorStructure;
		public uriContext: angular.IPromise<IUriContext>;
		public editorStructureService: any;
		public editorCRUDService: any;
		public initialContent: Payload;
		public component: Payload;
		public fields: GenericEditorField[];
		public languages: ILanguage[];
		public initialDirty: boolean;
		public fieldsNonPristineState: any;
		public _unregisterUnrelatedErrorsEvent: () => void;
		public _unregisterUnrelatedMessagesEvent: () => void;
		public pristine: Payload;
		public tabs: GenericEditorTab[];
		public fieldsMap: GenericEditorFieldsMap;
		public componentForm: angular.IFormController;
		public tabSelected: boolean;
		public targetedQualifier: string;
		public bcPristine: Payload;
		public bcComp: Payload;
		public requiredLanguages: ILanguage[];

		constructor(
			conf: IGenericEditorFactoryOptions
		) {
			this.validate(conf);
			this.id = conf.id;
			this.inProgress = false;
			this.pristine = {};
			this.smarteditComponentType = conf.smarteditComponentType;
			this.smarteditComponentId = conf.smarteditComponentId;
			this.editorStackId = conf.editorStackId;
			this.updateCallback = conf.updateCallback;
			this.structure = conf.structure;
			if (conf.structureApi) {
				this.editorStructureService = restServiceFactory.get(conf.structureApi);
			}
			this.uriContext = conf.uriContext;
			if (conf.contentApi) {
				this.editorCRUDService = restServiceFactory.get(conf.contentApi);
			}
			this.initialContent = lodash.cloneDeep(conf.content);
			this.component = null;
			this.fields = [];
			this.languages = [];
			this.initialDirty = false;
			// Object containing all the fields and their non pristine states.
			this.fieldsNonPristineState = {};

			if (conf.customOnSubmit) {
				this.onSubmit = conf.customOnSubmit;
			}

			this._unregisterUnrelatedErrorsEvent = systemEventService.subscribe(GENERIC_EDITOR_UNRELATED_VALIDATION_ERRORS_EVENT, this._handleLegacyUnrelatedValidationErrors.bind(this));
			this._unregisterUnrelatedMessagesEvent = systemEventService.subscribe(GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, this._handleUnrelatedValidationMessages.bind(this));

		}

		_finalize(): void {
			this._unregisterUnrelatedErrorsEvent();
			this._unregisterUnrelatedMessagesEvent();
			this.popEditorFromStack();
		}

		_setApi(api: GenericEditorAPI): void {
			this.api = api;
		}

		_handleLegacyUnrelatedValidationErrors(key: string, validationMessages: TypedMap<any>): void {
			this._handleUnrelatedValidationMessages(key, {
				messages: validationMessages,
				sourceGenericEditorId: validationMessages.sourceGenericEditorId
			});
		}

		_handleUnrelatedValidationMessages(key: string, validationData: TypedMap<any>): void {
			if (validationData.targetGenericEditorId && validationData.targetGenericEditorId !== this.id) {
				return;
			}

			if (validationData.sourceGenericEditorId && validationData.sourceGenericEditorId === this.id) {
				return;
			}

			this.removeValidationMessages();
			this._displayValidationMessages(validationData.messages, true);
		}

		_isPrimitive(type: string): boolean {
			return primitiveTypes.indexOf(type) > -1;
		}

		_getSelector(selector: string): any {
			return yjQuery(selector);
		}

		pushEditorToStack(): void {
			if (!this.editorStackId) {
				this.editorStackId = this.id;
			}

			systemEventService.publishAsync(EDITOR_PUSH_TO_STACK_EVENT, {
				editorId: this.id,
				editorStackId: this.editorStackId,
				component: this.component,
				componentType: this.smarteditComponentType
			});
		}

		popEditorFromStack(): void {
			if (!this.editorStackId) {
				return;
			}

			systemEventService.publishAsync(EDITOR_POP_FROM_STACK_EVENT, {
				editorStackId: this.editorStackId
			});
		}

		/**
		 * @ngdoc method
		 * @name genericEditorModule.service:GenericEditor#reset
		 * @methodOf genericEditorModule.service:GenericEditor
		 *
		 * @description
		 * Sets the content within the editor to its original state.
		 */
		reset(): angular.IPromise<void> {
			// need to empty the searches for refreshOptions to enable resetting to pristine state
			this._getSelector('.ui-select-search').val('');
			this._getSelector('.ui-select-search').trigger('input');
			this.removeValidationMessages();
			this.component = resetObject(this.component, this.pristine);

			this.fields.forEach((field: GenericEditorField) => {
				delete field.initiated;
			});

			this.tabs = [];

			/*
			 * need not to just build fieldsMap but to reassign it as well in case of a reset called
			 * on a generic editor without content API: after save component is reset but if the structure
			 * remains the same the generic fields will try to assign model[qualifier] to an obsolete component
			 */
			this.fieldsMap = this.fields.reduce((seed: GenericEditorFieldsMap, field: GenericEditorField) => {
				let tab = editorFieldMappingService.getFieldTabMapping(field, this.structure);
				if (!tab) {
					tab = genericEditorTabService.getComponentTypeDefaultTab(this.structure);
				}

				if (Object.keys(seed).indexOf(tab) === -1) {
					seed[tab] = [];

					this.tabs.push({
						id: tab,
						title: 'se.genericeditor.tab.' + tab + '.title',
						templateUrl: 'genericEditorTabWrapperTemplate.html'
					});
				}
				seed[tab].push(field);
				return seed;
			}, {});

			this._switchToTabContainingQualifier();
			genericEditorTabService.sortTabs(this.tabs);

			if (this.componentForm) {
				this.componentForm.$setPristine();
			}
			return $q.when();
		}

		/*
		 * Causes the genericEditor to switch to the tab containing a qualifier of the given name.
		 */
		_switchToTabContainingQualifier(): void {
			if (!this.tabSelected && this.targetedQualifier) {
				this.tabs.forEach((tab: any) => {
					tab.active = !!this.fieldsMap[tab.id].some((field: GenericEditorField) => {
						return field.qualifier === this.targetedQualifier;
					});
				});
				this.tabSelected = true;
			}
		}

		/**
		 * Removes validation errors generated in frontend, not the ones sent by outside or server.
		 * Removes errors only from fields, not tabs.
		 */
		_removeFrontEndValidationMessages(): void {
			this.fields.forEach((field: GenericEditorField) => {
				const messages = (field.messages || []).filter((message: GenericEditorFieldMessage) => {
					return message.fromSubmit === undefined ? true : message.fromSubmit;
				});
				field.messages = messages.length ? messages : undefined;
				field.hasErrors = this._containsValidationMessageType(field.messages, VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR);
				field.hasWarnings = this._containsValidationMessageType(field.messages, VALIDATION_MESSAGE_TYPES.WARNING);
			});
		}

		/**
		 * Removes all validation (local, outside or server) errors from fieds and tabs.
		 */
		removeValidationMessages(): void {
			(this.tabs || []).forEach((tab: GenericEditorTab) => {
				tab.hasErrors = false;
			});

			this.fields.forEach((field: GenericEditorField) => {
				field.messages = undefined;
				field.hasErrors = false;
				field.hasWarnings = false;
			});
		}

		/**
		 *  fetch will:
		 *  - return data if initialContent is provided
		 *  - make a call to the CRUD API to return the payload if initialContent is not provided
		 *
		 *  (In initialDirty is set to true, it is populated after loading and setting the content which will make the
		 *   pristine and component states out of sync thus making the editor dirty)
		 */
		fetch(): angular.IPromise<{}> {
			if (!this.initialDirty) {
				return this.initialContent ? $q.when(this.initialContent) : (this.smarteditComponentId ? this.editorCRUDService.get({
					identifier: this.smarteditComponentId
				}) : $q.when({}));
			}
			return $q.when({});
		}

		sanitizeLoad(response: any) {
			this.fields.forEach((field: GenericEditorField) => {
				if (field.localized === true && isBlank(response[field.qualifier])) {
					response[field.qualifier] = {};
				}
			});
			return response;
		}

		load(): angular.IPromise<any> {
			const deferred = $q.defer();
			this.fetch().then(
				(response: any) => {
					this.pristine = this.sanitizeLoad(response);
					this.reset();

					deferred.resolve();
				},
				(failure: any) => {
					$log.error("GenericEditor.load failed");
					$log.error(failure);
					deferred.reject();
				}
			);
			return deferred.promise;
		}

		getComponent(): Payload {
			return this.component;
		}

		sanitizePayload(payload: Payload, fields: GenericEditorField[]): Payload {

			const CMS_STRUCTURE_TYPE = {
				SHORT_STRING: "ShortString",
				LONG_STRING: "LongString"
			};

			fields.filter((field: GenericEditorField) => {
				return (field.cmsStructureType === CMS_STRUCTURE_TYPE.LONG_STRING || field.cmsStructureType === CMS_STRUCTURE_TYPE.SHORT_STRING || typeof field.customSanitize === 'function');
			}).map((field: GenericEditorField) => {
				return {
					name: field.qualifier,
					localized: !!field.localized,
					customSanitize: field.customSanitize
				};
			}).forEach((fieldInfo) => {

				if (typeof payload[fieldInfo.name] !== 'undefined' && fieldInfo.name in payload) {
					if (fieldInfo.customSanitize) {
						fieldInfo.customSanitize(payload[fieldInfo.name], sanitize);
					} else {
						if (fieldInfo.localized) {
							const qualifierValueObject = payload[fieldInfo.name] as any;
							Object.keys(qualifierValueObject).forEach((locale: string) => {
								qualifierValueObject[locale] = sanitize(qualifierValueObject[locale]);
							});
						} else {
							payload[fieldInfo.name] = sanitize(payload[fieldInfo.name]);
						}
					}
				}

			});

			return payload;
		}

		_fieldsAreUserChecked(): boolean {
			return this.fields.every((field: GenericEditorField) => {
				let requiresUserCheck = false;
				for (const qualifier in field.requiresUserCheck) {
					if (field.requiresUserCheck.hasOwnProperty(qualifier)) {
						requiresUserCheck = requiresUserCheck || field.requiresUserCheck[qualifier];
					}
				}
				return !requiresUserCheck || field.isUserChecked;
			});
		}

		/**
		 * @ngdoc method
		 * @name genericEditorModule.service:GenericEditor#preparePayload
		 * @methodOf genericEditorModule.service:GenericEditor
		 *
		 * @description
		 * Transforms the payload before POST/PUT to server
		 *
		 * @param {Object} the transformed payload
		 */
		preparePayload(originalPayload: Payload): angular.IPromise<Payload> {
			return $q.when(originalPayload);
		}

		onSubmit(): angular.IPromise<Payload> {
			let payload = copy(this.component);

			payload = this.sanitizePayload(payload, this.fields);

			if (this.smarteditComponentId) {
				payload.identifier = this.smarteditComponentId;
			}

			// if POST mode
			if (this.editorCRUDService && !this.smarteditComponentId) {
				// if we have a type field in the structure, use it for the type in the POST payload
				if (this.structure && this.structure.type) {
					// if the user already provided a type field, lets be nice
					if (!payload.type) {
						payload.type = this.structure.type;
					}
				}
			}

			return this.preparePayload(payload).then((preparedPayload: Payload) => {
				const promise = this.editorCRUDService ? (this.smarteditComponentId ? this.editorCRUDService.update(preparedPayload) : this.editorCRUDService.save(preparedPayload)) : $q.when(preparedPayload);
				return promise.then(function(response: Payload) {
					return {
						payload,
						response
					};
				});
			});
		}

		/**
		 * @ngdoc method
		 * @name genericEditorModule.service:GenericEditor#submit
		 * @methodOf genericEditorModule.service:GenericEditor
		 *
		 * @description
		 * Saves the content within the form for a specified component. If there are any validation errors returned by the CRUD API after saving the content, it will display the errors.
		 */
		submit(): angular.IPromise<{}> {
			const deferred = $q.defer();

			// It's necessary to remove validation errors even if the form is not dirty. This might be because of unrelated validation errors
			// triggered in other tab.
			this.removeValidationMessages();
			this.hasFrontEndValidationErrors = false;

			if (!this._fieldsAreUserChecked()) {
				deferred.reject(true); // Mark this tab as "in error" due to front-end validation.
				this.hasFrontEndValidationErrors = true;
			} else if (this.isValid(true)) {

				this.inProgress = true;
				/*
				 * upon submitting, server side may have been updated,
				 * since we PUT and not PATCH, we need to take latest of the fields not presented and send them back with the editable ones
				 */
				this.onSubmit().then((submitResult: Payload) => {
					// If we're doing a POST or PUT and the request returns non empty response, then this response is returned.
					// Otherwise the payload for the request is returned.
					if (submitResult.response) {
						this.pristine = copy(submitResult.response);
					} else {
						this.pristine = copy(submitResult.payload);
					}

					delete this.pristine.identifier;

					if (!this.smarteditComponentId && submitResult.response) {
						this.smarteditComponentId = (submitResult.response as any).uuid;
					}
					this.removeValidationMessages();

					this.reset();
					this.inProgress = false;
					deferred.resolve(lodash.cloneDeep(this.pristine));
					if (this.updateCallback) {
						this.updateCallback(this.pristine, submitResult.response as Payload);
					}
				}, (failure: any) => {
					this.removeValidationMessages();
					const errors = failure.data.errors as GenericEditorFieldMessage[];
					this._displayValidationMessages(errors, true);
					const hasErrors = this._containsValidationMessageType(errors, VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR);
					// send unrelated validation messages to any other listening genericEditor when no other errors
					const unrelatedValidationMessages = this._collectUnrelatedValidationMessages(errors);
					if (unrelatedValidationMessages.length > 0) {

						// send tab id in errors for the legacy event.
						const unrelatedValidationErrors = lodash.cloneDeep(unrelatedValidationMessages) as any;
						unrelatedValidationErrors.sourceGenericEditorId = this.id;
						systemEventService.publishAsync(GENERIC_EDITOR_UNRELATED_VALIDATION_ERRORS_EVENT, unrelatedValidationErrors);
						systemEventService.publishAsync(GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, {
							messages: unrelatedValidationMessages,
							sourceGenericEditorId: this.id
						});
						this.inProgress = false;
						deferred.reject(hasErrors); // Marks this tab if it has errors.
					} else {
						this.inProgress = false;
						deferred.reject(true); // Marks this tab as "in error".
					}
				});
			} else {
				$log.warn("GenericEditor.submit() - unable to submit form. Form is unexpectedly invalid.");
				deferred.reject();
			}
			return deferred.promise;
		}

		_validationMessageBelongsToCurrentInstance(validationMessage: GenericEditorFieldMessage): boolean {
			return lodash.some(this.fields, (field: GenericEditorField) => {
				return field.qualifier === validationMessage.subject;
			});
		}

		_containsValidationMessageType(validationMessages: GenericEditorFieldMessage[], messageType: string): boolean {
			return lodash.some(validationMessages, (message: GenericEditorFieldMessage) => {
				return message.type === messageType && this._validationMessageBelongsToCurrentInstance(message);
			});
		}

		_isValidationMessageType(messageType: string): boolean {
			return lodash.includes(lodash.values(VALIDATION_MESSAGE_TYPES), messageType);
		}

		/**
		 * Displays validation errors for fields and changes error states for all tabs.
		 */
		_displayValidationMessages(validationMessages: GenericEditorFieldMessage[], keepAllErrors: boolean): angular.IPromise<void> {
			validationMessages.filter((message: GenericEditorFieldMessage) => {
				return this._isValidationMessageType(message.type) && (keepAllErrors || message.isNonPristine);
			}).forEach((validationMessage: GenericEditorFieldMessage) => {

				validationMessage.type = validationMessage.type || VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR;

				const field = this.fields.filter((element: GenericEditorField) => {
					return (element.qualifier === validationMessage.subject);
				})[0];

				if (field) {
					if (field.messages === undefined) {
						field.messages = [];
					}

					const message = lodash.merge(validationMessage, seValidationMessageParser.parse(validationMessage.message));
					message.marker = field.localized ? message.language : field.qualifier;
					message.type = validationMessage.type;
					message.uniqId = encode(message);

					if (field.messages.map((msg: GenericEditorFieldMessage) => {
						return msg.uniqId;
					}).indexOf(message.uniqId) === -1) {
						field.messages.push(message);
						if (message.type === VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR) {
							field.hasErrors = true;
						} else if (message.type === VALIDATION_MESSAGE_TYPES.WARNING) {
							field.hasWarnings = true;
						}
					}

					// when a field is in error, we need to light up the internal tab containing it
					const tabId = Object.keys(this.fieldsMap).find((fieldsMapTabId: string) => {
						return this.fieldsMap[fieldsMapTabId].some((_field: GenericEditorField) => {
							return field === _field;
						});
					});
					if (tabId) {
						this.tabs.find((tab: GenericEditorTab) => {
							return tab.id === tabId;
						}).hasErrors = true;
					}
				}
			});

			return $q.when();
		}

		_collectUnrelatedValidationMessages(messages: GenericEditorFieldMessage[]): GenericEditorFieldMessage[] {
			return messages.filter((message: GenericEditorFieldMessage) => {
				return this._isValidationMessageType(message.type) && !this._validationMessageBelongsToCurrentInstance(message);
			});
		}

		fieldAdaptor(fields: GenericEditorAttribute[]): GenericEditorField[] {
			return fields.map((field: GenericEditorAttribute) => {
				const fieldMapping = editorFieldMappingService.getEditorFieldMapping(field, this.structure);
				const genericField = lodash.assign(field, fieldMapping);

				if (genericField.editable === undefined) {
					genericField.editable = true;
				}

				if (!genericField.postfixText) {
					const key = (this.smarteditComponentType ? this.smarteditComponentType.toLowerCase() : '') + '.' + field.qualifier.toLowerCase() + '.postfix.text';
					const translated = $translate.instant(key);
					genericField.postfixText = translated !== key ? translated : "";
				}

				genericField.smarteditComponentType = this.smarteditComponentType;

				return genericField;
			});
		}

		/**
		 * @ngdoc method
		 * @name genericEditorModule.service:GenericEditor#refreshOptions
		 * @methodOf genericEditorModule.service:GenericEditor
		 *
		 * @description
		 * Is invoked by HTML field templates that update and manage dropdowns.
		 *  It updates the dropdown list upon initialization (creates a list of one option) and when performing a search (returns a filtered list).
		 *  To do this, the GenericEditor fetches an implementation of the  {@link genericEditorModule.FetchDataHandlerInterface FetchDataHandlerInterface} using the following naming convention:
		 * <pre>"fetch" + cmsStructureType + "DataHandler"</pre>
		 * @param {GenericEditorField} field The field in the structure that requires a dropdown to be built.
		 * @param {string} qualifier For a non-localized field, it is the actual field.qualifier. For a localized field, it is the ISO code of the language.
		 * @param {string} search The value of the mask to filter the dropdown entries on.
		 */

		refreshOptions(field: GenericEditorField, qualifier: string, search: string): void {
			const theHandlerObj = "fetch" + field.cmsStructureType + "DataHandler";
			let theIdentifier: string;
			let optionsIdentifier: string;

			if (field.localized) {
				theIdentifier = (this.component[field.qualifier] as any)[qualifier] as string;
				optionsIdentifier = qualifier;
			} else {
				theIdentifier = this.component[field.qualifier] as string;
				optionsIdentifier = field.qualifier;
			}

			const objHandler = $injector.get(theHandlerObj) as IFetchDataHandler;

			field.initiated = field.initiated || [];
			field.options = field.options || {};

			if (field.cmsStructureType === 'Enum') {
				field.initiated.push(optionsIdentifier);
			}
			if (field.initiated.indexOf(optionsIdentifier) > -1) {
				if (search.length > 2 || field.cmsStructureType === 'Enum') {
					objHandler.findByMask(field, search).then((entities: string[]) => {
						field.options[optionsIdentifier] = entities;
					});
				}
			} else if (theIdentifier) {
				objHandler.getById(field, theIdentifier).then((entity: string) => {
					field.options[optionsIdentifier] = [entity];
					field.initiated.push(optionsIdentifier);
				});
			} else {
				field.initiated.push(optionsIdentifier);
			}
		}

		_buildComparable(source: Payload): Payload {
			if (!source) {
				return source;
			}
			const comparable: Payload = {};

			this.fields.forEach((field: GenericEditorField) => {
				let fieldValue = source[field.qualifier];
				if (field.localized) {
					fieldValue = fieldValue as TypedMap<string>;

					const sub: Payload = {};
					lodash.forEach(fieldValue, (langValue: string, lang: string) => {
						if (langValue !== null) {
							sub[lang] = this._buildFieldComparable(langValue, field);
						}
					});
					comparable[field.qualifier] = sub;
				} else {
					fieldValue = source[field.qualifier] as string;
					comparable[field.qualifier] = this._buildFieldComparable(fieldValue, field);
				}
			});

			// sometimes, such as in navigationNodeEntryEditor, we update properties not part of the fields and still want the editor to turn dirty
			lodash.forEach(source, (value: Payload, key: string) => {
				const notDisplayed = !this.fields.some((field: GenericEditorField) => {
					return field.qualifier === key;
				});
				if (notDisplayed) {
					comparable[key] = value;
				}
			});

			return comparable;
		}

		/**
		 * @ngdoc method
		 * @name genericEditorModule.service:GenericEditor#isDirty
		 * @methodOf genericEditorModule.service:GenericEditor
		 *
		 * @description
		 * A predicate function that returns true if the editor is in dirty state or false if it not.
		 * The state of the editor is determined by comparing the current state of the component with the state of the component when it was pristine.
		 *
		 * @return {Boolean} An indicator if the editor is in dirty state or not.
		 */
		isDirty(): boolean {
			// TODO: try to get away from angular.equals
			this.bcPristine = this._buildComparable(this.pristine);
			this.bcComp = this._buildComparable(this.component);
			return !angular.equals(this.bcPristine, this.bcComp);
		}

		/**
		 * Evaluates the fields that are not in pristine state and populates this.fieldsNonPristineState object.
		 */
		_populateFieldsNonPristineStates(): void {
			this.bcPristine = this._buildComparable(this.pristine);
			this.bcComp = this._buildComparable(this.component);
			this.fieldsNonPristineState = this._getFieldsNonPristineState(this.fieldsNonPristineState, this.bcPristine, this.bcComp);
		}

		/**
		 * Collects validation errors on all the form fields.
		 * Returns the list of errors or empty list.
		 * Each error contains the following properties:
		 * type - VALIDATION_MESSAGE_TYPES
		 * subject - the field qualifier.
		 * message - error message.
		 * fromSubmit - contains true if the error is related to submit operation, false otherwise.
		 * isNonPristine - contains true if the field was modified (at least once) by the user, false otherwise.
		 * language - optional language iso code.
		 */
		_collectFrontEndValidationErrors(comesFromSubmit: boolean): GenericEditorFieldMessage[] {
			comesFromSubmit = comesFromSubmit || false;
			if (!this.component) {
				return [];
			}

			// first collect HTM5 errors
			const htmlvalidationErrors = this.fields.filter((field: GenericEditorField) => {
				const formField = this.componentForm[field.qualifier];
				return !lodash.isNil(formField) && formField.$invalid === true;
			}).map((field: GenericEditorField) => {
				return {
					type: VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR,
					subject: field.qualifier,
					message: 'se.editor.html.validation.error',
					fromSubmit: comesFromSubmit,
					isNonPristine: this._isNonPristineState(field.qualifier)
				};
			});

			// then collect errors for required fields
			const requiredErrors: any = [];
			this.fields.filter((field: GenericEditorField) => {
				return field.required && field.editable;
			}).forEach((field: GenericEditorField) => {
				if (field.localized) {
					this.requiredLanguages.forEach((language: ILanguage) => {
						if (!(this.component[field.qualifier] as Payload)[language.isocode]) {
							requiredErrors.push({
								type: VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR,
								subject: field.qualifier,
								message: 'se.componentform.required.field',
								language: language.isocode,
								fromSubmit: comesFromSubmit,
								isNonPristine: this._isNonPristineState(field.qualifier, language.isocode)
							});
						}
					});
				} else if (isObjectEmptyDeep(this.component[field.qualifier])) {
					requiredErrors.push({
						type: VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR,
						subject: field.qualifier,
						message: 'se.componentform.required.field',
						fromSubmit: comesFromSubmit,
						isNonPristine: this._isNonPristineState(field.qualifier)
					});
				}
			});

			return htmlvalidationErrors.concat(requiredErrors);
		}

		/**
		 * Returns true if the field was modified (at least once) by the user, false otherwise.
		 */
		_isNonPristineState(qualifier: string, language?: string): boolean {
			if (language) {
				return (this.fieldsNonPristineState[qualifier] && this.fieldsNonPristineState[qualifier][language]) ?
					this.fieldsNonPristineState[qualifier][language] : false;
			}
			return this.fieldsNonPristineState[qualifier] ? this.fieldsNonPristineState[qualifier] : false;
		}

		/**
		 * Finds a diff between pristine and component using {@link functionsModule.deepObjectPropertyDiff deepObjectPropertyDiff} function
		 * and merge the result with initialObject based on the following logic:
		 * Do nothing if the initialObject's property is true, use the value from the diff otherwise.
		 */
		_getFieldsNonPristineState(initialObject: Payload, pristine: Payload, component: Payload): Payload {
			const nonPristineStateObj = deepObjectPropertyDiff(pristine, component);
			return lodash.mergeWith(lodash.cloneDeep(initialObject), nonPristineStateObj, (prValue, cpValue: Payload) => {
				if (!lodash.isPlainObject(prValue)) {
					// Never revert true value (if the field was changed by the user the state stays the same)
					return prValue === true ? true : cpValue;
				}
				return undefined;
			});
		}

		/**
		 * Check for html validation errors on all the form fields.
		 * If so, assign an error to a field that is not pristine.
		 * The seGenericEditorFieldError will render these errors, just like
		 * errors we receive from the backend.
		 * It also validates error states for tabs.
		 */
		isValid(comesFromSubmit?: boolean): boolean {
			comesFromSubmit = comesFromSubmit || false;
			const validationErrors = this._collectFrontEndValidationErrors(comesFromSubmit);
			this._removeFrontEndValidationMessages();
			this._displayValidationMessages(validationErrors, comesFromSubmit);
			this._validateTabsErrorStates();

			return validationErrors.length === 0;
		}

		/**
		 * Changes error states of tabs based on whether the fields inside those tabs contain errors or not.
		 */
		_validateTabsErrorStates(): void {
			this._getTabsByFieldsErrorState(false).forEach((tab: GenericEditorTab) => {
				this._setTabErrorState(tab.id, false);
			});

			this._getTabsByFieldsErrorState(true).forEach((tab: GenericEditorTab) => {
				this._setTabErrorState(tab.id, true);
			});
		}

		/**
		 * Returns the list of tabs by error states.
		 */
		_getTabsByFieldsErrorState(hasErrors: boolean): GenericEditorTab[] {
			return (this.tabs || []).filter((tab: GenericEditorTab) => {
				const tabsByErrorState = this.fieldsMap[tab.id].some((field: GenericEditorField) => {
					return field.hasErrors === hasErrors;
				});
				return tabsByErrorState;
			});
		}

		/**
		 * Sets the error state for a tab based on tab id.
		 */
		_setTabErrorState(tabId: string, hasErrors: boolean): void {
			this.tabs.find((tab: GenericEditorTab) => {
				return tab.id === tabId;
			}).hasErrors = hasErrors;
		}

		isSubmitDisabled(): boolean {
			return this.inProgress || !this.isDirty() || !this.isValid();
		}

		_getUriContext(): angular.IPromise<IUriContext> {

			return this.uriContext ? $q.when(this.uriContext) : sharedDataService.get('experience').then((experience: any) => {
				const uriContext: IUriContext = {};
				uriContext[CONTEXT_SITE_ID] = experience.siteDescriptor.uid;
				uriContext[CONTEXT_CATALOG] = experience.catalogDescriptor.catalogId;
				uriContext[CONTEXT_CATALOG_VERSION] = experience.catalogDescriptor.catalogVersion;
				return uriContext;
			});
		}

		/**
		 * Conversion function in case the first attribute of the response is an array of type structures.
		 */
		_convertStructureArray(structure: GenericEditorStructure | any): GenericEditorStructure {
			const structureArray = structure.structures || structure.componentTypes;
			if (lodash.isArray(structureArray)) {
				if (structureArray.length > 1) {
					throw new Error("init: Invalid structure, multiple structures returned");
				}
				structure = structureArray[0];
			}
			return structure;
		}

		init(): angular.IPromise<{}> {

			this.submitButtonText = 'se.componentform.actions.submit';
			this.cancelButtonText = 'se.componentform.actions.cancel';

			/**
			 * @ngdoc object
			 * @name genericEditorModule.object:genericEditorApi
			 * @description
			 * The generic editor's api object exposing public functionality
			 */
			this._setApi({

				/**
				 * @ngdoc method
				 * @name setSubmitButtonText
				 * @methodOf genericEditorModule.object:genericEditorApi
				 * @description
				 * Overrides the i18n key used bfor the submit button
				 */
				setSubmitButtonText: (_submitButtonText: string): void => {
					this.submitButtonText = _submitButtonText;
				},

				/**
				 * @ngdoc method
				 * @name setCancelButtonText
				 * @methodOf genericEditorModule.object:genericEditorApi
				 * @description
				 * Overrides the i18n key used bfor the submit button
				 */
				setCancelButtonText: (_cancelButtonText: string): void => {
					this.cancelButtonText = _cancelButtonText;
				},

				/**
				 * @ngdoc method
				 * @name setAlwaysShowSubmit
				 * @methodOf genericEditorModule.object:genericEditorApi
				 * @description
				 * If set to true, will always show the submit button
				 */
				setAlwaysShowSubmit: (_alwaysShowSubmit: boolean): void => {
					this.alwaysShowSubmit = _alwaysShowSubmit;
				},

				/**
				 * @ngdoc method
				 * @name setAlwaysShowReset
				 * @methodOf genericEditorModule.object:genericEditorApi
				 * @description
				 * If set to true, will always show the reset button
				 */
				setAlwaysShowReset: (_alwaysShowReset: boolean): void => {
					this.alwaysShowReset = _alwaysShowReset;
				},

				/**
				 * @ngdoc method
				 * @name onReset
				 * @methodOf genericEditorModule.object:genericEditorApi
				 * @description
				 * To be executed after reset
				 */
				setOnReset: (_onReset: () => void): void => {
					this.onReset = _onReset;
				},

				/**
				 * @ngdoc method
				 * @name setPreparePayload
				 * @methodOf genericEditorModule.object:genericEditorApi
				 * @description
				 * Function that passes a preparePayload function to the editor in order to transform the payload prior to submitting (see {@link enericEditorModule.service:GenericEditor#preparePayload})
				 *
				 * @param {Object} preparePayload The function that takes the original payload as argument
				 */
				setPreparePayload: (_preparePayload: (payload: Payload) => angular.IPromise<Payload>) => {
					this.preparePayload = _preparePayload;
				},

				/**
				 * @ngdoc method
				 * @name setUpdateCallback
				 * @methodOf genericEditorModule.object:genericEditorApi
				 * @description
				 * Function that passes an updateCallback function to the editor in order to perform an action upon successful submit. It is invoked with two arguments: the pristine object and the response from the server.
				 * @param {Object} updateCallback the callback invoked upon successful submit
				 */
				setUpdateCallback: (_updateCallback: (pristine: Payload, results: Payload) => void) => {
					this.updateCallback = _updateCallback;
				},

				/**
				 * @ngdoc method
				 * @name updateComponent
				 * @methodOf genericEditorModule.object:genericEditorApi
				 * @description
				 * Function that updates the content of the generic editor without having to reinitialize
				 *
				 * @param {Object} component The component to replace the current model for the generic editor
				 */
				updateContent: (component: Payload) => {
					this.component = copy(component);
				},

				/**
				 * @ngdoc method
				 * @name getContent
				 * @methodOf genericEditorModule.object:genericEditorApi
				 * @description
				 * copies of the current model
				 * @return {object} a copy
				 */
				getContent: (): Payload => {
					return copy(this.component);
				},

				/**
				 * @ngdoc method
				 * @name onContentChange
				 * @methodOf genericEditorModule.object:genericEditorApi
				 * @description
				 * Function triggered everytime the current model changes
				 */
				onContentChange(): void {
					return;
				},

				/**
				 * @ngdoc method
				 * @name clearMessages
				 * @methodOf genericEditorModule.object:genericEditorApi
				 * @description
				 * Function that clears all validation messages in the editor
				 */
				clearMessages: () => {
					this.removeValidationMessages();
				},

				/**
				 * @ngdoc method
				 * @name switchToTabContainingQualifier
				 * @methodOf genericEditorModule.object:genericEditorApi
				 * @description
				 * causes the genericEditor to switch to the tab containing a qualifier of the given name
				 * @param {String} qualifier the qualifier contained in the tab we want to switch to
				 */
				switchToTabContainingQualifier: (qualifier: string) => {
					this.targetedQualifier = qualifier;
				},

				// currently used by clone components to open editor in dirty mode
				considerFormDirty: () => {
					this.initialDirty = true;
				},

				/**
				 * @ngdoc method
				 * @name isSubmitDisabled
				 * @methodOf genericEditorModule.object:genericEditorApi
				 * @description
				 * returns true to inform that the submit button delegated to the invoker should be disabled
				 * @return {boolean} true if submit is disabled
				 */
				isSubmitDisabled: () => {
					return this.isSubmitDisabled();
				},

				/**
				 * @ngdoc method
				 * @name getLanguages
				 * @methodOf genericEditorModule.object:genericEditorApi
				 * @description
				 * Function that returns a promise resolving to language descriptors. If defined, will be resolved
				 * when the generic editor is initialized to override what languages are used for localized elements
				 * within the editor.
				 * @return {Promise} a promise resolving to language descriptors. Each descriptor provides the following
				 * language properties: isocode, nativeName, name, active, and required.
				 */
				getLanguages: () => {
					return;
				}
			});

			const deferred = $q.defer();

			const structurePromise = this.editorStructureService ? this.editorStructureService.get({
				smarteditComponentType: this.smarteditComponentType
			}) : $q.when(this.structure);

			structurePromise.then((structure: GenericEditorStructure) => {
				structure = this._convertStructureArray(structure);
				this.structure = structure;

				this._getUriContext().then((uriContext: IUriContext) => {
					const languagePromise = this.api.getLanguages() || languageService.getLanguagesForSite(uriContext[CONTEXT_SITE_ID]);
					languagePromise.then((languages: ILanguage[]) => {
						this.languages = languages;
						this.requiredLanguages = this.languages.filter((language: ILanguage) => {
							return language.required;
						});

						this.fields = this.fieldAdaptor(structure ? structure.attributes : []);
						// for setting uri params into custom widgets
						this.parameters = {
							siteId: uriContext[CONTEXT_SITE_ID],
							catalogId: uriContext[CONTEXT_CATALOG],
							catalogVersion: uriContext[CONTEXT_CATALOG_VERSION]
						};
						this.load().then(() => {

							// If initialDirty is set to true and if any initial content is provided, it is populated here which will make the
							// pristine and component states out of sync thus making the editor dirty
							if (this.initialDirty) {
								this.component = this.sanitizeLoad(this.initialContent || {});
							}

							this.pushEditorToStack();
							systemEventService.publishAsync(GENERIC_EDITOR_LOADED_EVENT, this.id);
							deferred.resolve();
						}, () => {
							deferred.reject();
						});
					}, () => {
						$log.error("GenericEditor failed to fetch storefront languages");
						deferred.reject();
					});
				});
			},
				function(e: Payload) {
					$log.error("GenericEditor.init failed");
					$log.error(e);
					deferred.reject();
				});

			return deferred.promise;
		}

		private _buildFieldComparable(fieldValue: string, field: GenericEditorField): string | boolean {
			switch (field.cmsStructureType) {
				case 'RichText':
					return fieldValue !== undefined ? sanitizeHTML(fieldValue) : null;
				case 'Boolean':
					return fieldValue !== undefined ? fieldValue : false;
				default:
					return fieldValue;
			}
		}

		private validate(conf: IGenericEditorFactoryOptions): void {
			if (isBlank(conf.structureApi) && !conf.structure) {
				throw new Error("genericEditor.configuration.error.no.structure");
			} else if (!isBlank(conf.structureApi) && conf.structure) {
				throw new Error("genericEditor.configuration.error.2.structures");
			}
		}

	}

	return GenericEditor;
}