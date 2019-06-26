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
import * as angular from "angular";
import {IRestService, IRestServiceFactory, ISharedDataService, LanguageService, Payload, SystemEventService} from "smarteditcommons";
import {
	EditorFieldMappingService, GenericEditorFactory, GenericEditorStackService, GenericEditorTabService,
	IFetchDataHandler,
	IGenericEditorConstructor,
	SeValidationErrorParser, SeValidationMessageParser
} from "smarteditcommons/components/genericEditor";
import {jQueryHelper, LogHelper} from "testhelpers";

describe('test GenericEditor class', function() {

	let componentForm: angular.IFormController;
	let $rootScope: angular.IRootScopeService;
	let $q: angular.IQService;
	let $httpBackend: angular.IHttpBackendService;
	let $translate: jasmine.SpyObj<angular.translate.ITranslateService>;
	let smarteditComponentType: string;
	let smarteditComponentId: string;
	let updateCallback: (pristine: Payload, results: Payload) => void;
	let GenericEditor: IGenericEditorConstructor;
	let sharedDataService: jasmine.SpyObj<ISharedDataService>;
	let languageService: jasmine.SpyObj<LanguageService>;
	let restServiceFactory: jasmine.SpyObj<IRestServiceFactory>;
	let editorStructureService: jasmine.SpyObj<IRestService<any>>;
	let editorCRUDService: jasmine.SpyObj<IRestService<any>>;
	let editorMediaService: jasmine.SpyObj<IRestService<any>>;
	let fetchEnumDataHandler: jasmine.SpyObj<IFetchDataHandler>;
	let systemEventService: jasmine.SpyObj<SystemEventService>;
	let seValidationErrorParser: jasmine.SpyObj<SeValidationErrorParser>;
	let sanitize: any;
	let editorFieldMappingService: jasmine.SpyObj<EditorFieldMappingService>;
	let searchSelector: jasmine.SpyObj<any>;
	let CONTEXT_CATALOG = "CURRENT_CONTEXT_CATALOG";
	let CONTEXT_CATALOG_VERSION = "CURRENT_CONTEXT_CATALOG_VERSION";
	let GENERIC_EDITOR_UNRELATED_VALIDATION_ERRORS_EVENT: string;
	let GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT: string;
	let EDITOR_PUSH_TO_STACK_EVENT: string;
	let EDITOR_POP_FROM_STACK_EVENT: string;
	let $injector: jasmine.SpyObj<angular.auto.IInjectorService>;

	const options = [{
		code: 'code1',
		label: 'label1'
	}, {
		code: 'code2',
		label: 'label2'
	}];

	const STOREFRONT_LANGUAGES = [{
		language: 'en',
		required: true
	}, {
		language: 'pl',
		required: true
	}, {
		language: 'it'
	}];

	beforeEach(() => {
		systemEventService = jasmine.createSpyObj('systemEventService', ['subscribe', 'publishAsync']);

		languageService = jasmine.createSpyObj('languageService', ['getLanguagesForSite', 'getBrowserLocale']);
		languageService.getLanguagesForSite.and.callFake(function() {
			return $q.when(STOREFRONT_LANGUAGES);
		});
		languageService.getBrowserLocale.and.returnValue('en_US');

		seValidationErrorParser = jasmine.createSpyObj('seValidationErrorParser', ['parse']);

		sharedDataService = jasmine.createSpyObj('sharedDataService', ['get']);

		sharedDataService.get.and.callFake(function() {
			return $q.when({
				siteDescriptor: {
					uid: 'someSiteUid'
				},
				catalogDescriptor: {
					catalogId: 'somecatalogId',
					catalogVersion: 'someCatalogVersion'
				}
			});
		});

		fetchEnumDataHandler = jasmine.createSpyObj('fetchEnumDataHandler', ['findByMask', 'getById']);

		fetchEnumDataHandler.findByMask.and.callFake(function() {
			return $q.when(options);
		});
		$injector = jasmine.createSpyObj<angular.auto.IInjectorService>('$injector', ['get']);

		$injector.get.and.callFake((name: string) => {
			if (name === 'fetchEnumDataHandler') {
				return fetchEnumDataHandler;
			}
			throw new Error('Could not find provider: ' + name);
		});

		sanitize = jasmine.createSpy('sanitize');
		sanitize.and.returnValue('ESCAPED');

		editorFieldMappingService = jasmine.createSpyObj('editorFieldMappingService', ['getEditorFieldMapping', '_registerDefaultFieldMappings', 'getFieldTabMapping']);
		editorFieldMappingService.getEditorFieldMapping.and.callFake(function(type: any) {
			return {
				template: (type + 'Template')
			};
		});
		editorFieldMappingService.getEditorFieldMapping.and.callFake((): null => {
			return null;
		});
		editorFieldMappingService.getFieldTabMapping.and.returnValue("default");

		smarteditComponentType = "smarteditComponentType";
		smarteditComponentId = "smarteditComponentId";
		updateCallback = function() {
			return;
		};

		editorStructureService = jasmine.createSpyObj('restService', ['getById', 'get', 'query', 'page', 'save', 'update', 'remove']);
		editorCRUDService = jasmine.createSpyObj('restService', ['getById', 'get', 'query', 'page', 'save', 'update', 'remove']);
		editorMediaService = jasmine.createSpyObj('restService', ['getById', 'get', 'query', 'page', 'save', 'update', 'remove']);

		restServiceFactory = jasmine.createSpyObj('restServiceFactory', ['get']);
		restServiceFactory.get.and.callFake(function(uri: any) {
			if (uri === '/cmswebservices/types/:smarteditComponentType') {
				return editorStructureService;
			} else if (uri === '/cmswebservices/cmsxdata/contentcatalog/staged/Media') {
				return editorMediaService;
			} else if (uri === '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items') {
				return editorCRUDService;
			}
			return null;
		});

		$translate = jasmine.createSpyObj('$translate', ['instant']);

		componentForm = jasmine.createSpyObj('componentForm', ['$setPristine']);
		componentForm.$dirty = true;
	});

	beforeEach(angular.mock.module('genericEditorServicesModule'));

	beforeEach(angular.mock.inject((
		_$rootScope_: angular.IRootScopeService,
		I18N_RESOURCE_URI: string,
		_$httpBackend_: angular.IHttpBackendService,

		_$q_: angular.IQService,
		_encode_: any,
		_sanitizeHTML_: any,
		_copy_: any,
		_isBlank_: (value: any) => boolean,
		_isObjectEmptyDeep_: any,
		_resetObject_: any,
		_deepObjectPropertyDiff_: any,

		_seValidationMessageParser_: SeValidationMessageParser,
		_genericEditorTabService_: GenericEditorTabService,
		_genericEditorStackService_: GenericEditorStackService,
	) => {
		$q = _$q_;
		$rootScope = _$rootScope_;
		$httpBackend = _$httpBackend_;

		GENERIC_EDITOR_UNRELATED_VALIDATION_ERRORS_EVENT = 'UnrelatedValidationErrors';
		const GENERIC_EDITOR_LOADED_EVENT = 'genericEditorLoadedEvent';
		GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT = 'UnrelatedValidationMessagesEvent';
		const VALIDATION_MESSAGE_TYPES = {
			VALIDATION_ERROR: 'ValidationError',
			WARNING: 'Warning'
		};
		EDITOR_PUSH_TO_STACK_EVENT = 'EDITOR_PUSH_TO_STACK_EVENT';
		EDITOR_POP_FROM_STACK_EVENT = 'EDITOR_POP_FROM_STACK_EVENT';
		const CONTEXT_SITE_ID = 'CURRENT_CONTEXT_SITE_ID';

		GenericEditor = GenericEditorFactory(
			jQueryHelper.jQuery(),
			_encode_,
			GENERIC_EDITOR_UNRELATED_VALIDATION_ERRORS_EVENT,
			GENERIC_EDITOR_LOADED_EVENT,
			GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT,
			VALIDATION_MESSAGE_TYPES,
			EDITOR_PUSH_TO_STACK_EVENT,
			EDITOR_POP_FROM_STACK_EVENT,
			(window as any).smarteditLodash,
			restServiceFactory,
			languageService,
			sharedDataService,
			systemEventService,
			sanitize,
			_sanitizeHTML_,
			_copy_,
			_isBlank_,
			_isObjectEmptyDeep_,
			$q,
			new LogHelper(),
			$translate,
			$injector,
			_seValidationMessageParser_,
			editorFieldMappingService,
			_genericEditorTabService_,
			_genericEditorStackService_,
			_resetObject_,
			_deepObjectPropertyDiff_,
			CONTEXT_SITE_ID,
			CONTEXT_CATALOG,
			CONTEXT_CATALOG_VERSION,
		);

		CONTEXT_CATALOG = "CURRENT_CONTEXT_CATALOG";
		CONTEXT_CATALOG_VERSION = "CURRENT_CONTEXT_CATALOG_VERSION";

		$httpBackend.whenGET(I18N_RESOURCE_URI + "/" + languageService.getBrowserLocale()).respond({});

		searchSelector = jasmine.createSpyObj('searchSelector', ['val', 'trigger']);

		spyOn(GenericEditor.prototype, '_getSelector').and.callFake(function(selectorValue: any) {
			if (selectorValue === '.ui-select-search') {
				return searchSelector;
			}
		});
	}));

	it('GenericEditor fails to initialize if neither structureApi nor structure are provided', function() {

		expect(function() {
			return new GenericEditor({
				smarteditComponentType,
				smarteditComponentId,
				updateCallback
			});
		}).toThrow(new Error("genericEditor.configuration.error.no.structure"));

	});

	it('GenericEditor fails to initialize if both structureApi and structure are provided', function() {

		expect(function() {
			return new GenericEditor({
				smarteditComponentType,
				smarteditComponentId,
				structureApi: '/cmswebservices/types/:smarteditComponentType',
				structure: 'structure',
				updateCallback
			} as any);
		}).toThrow(new Error("genericEditor.configuration.error.2.structures"));

	});

	it('GenericEditor initializes fine with structure API', function() {

		const editor = new GenericEditor({
			smarteditComponentType,
			smarteditComponentId,
			structureApi: '/cmswebservices/types/:smarteditComponentType',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items',
			updateCallback
		});

		expect(editor.smarteditComponentType).toBe(smarteditComponentType);
		expect(editor.smarteditComponentId).toBe(smarteditComponentId);
		expect(editor.updateCallback).toBe(updateCallback);
		expect(editor.component).toBeNull();
		expect(editor.fields).toEqual([]);
		expect(editor.editorStructureService).toBe(editorStructureService);
		expect(editor.editorCRUDService).toBe(editorCRUDService);
	});

	it('GenericEditor initializes fine with structure', function() {

		const editor = new GenericEditor({
			smarteditComponentType,
			smarteditComponentId,
			structure: 'structure',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items',
			updateCallback
		} as any);

		expect(editor.smarteditComponentType).toBe(smarteditComponentType);
		expect(editor.smarteditComponentId).toBe(smarteditComponentId);
		expect(editor.updateCallback).toBe(updateCallback);
		expect(editor.component).toBeNull();
		expect(editor.fields).toEqual([]);
		expect(editor.editorStructureService).toBeUndefined();
		expect((editor as any).structure).toBe('structure');
		expect(editor.editorCRUDService).toBe(editorCRUDService);
	});

	it('GenericEditor fetch executes get with identifier if identifier is set', function() {

		const editor = new GenericEditor({
			smarteditComponentType,
			smarteditComponentId,
			structure: 'structure',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		} as any);

		editorCRUDService.get.and.returnValue($q.when("somedata"));

		editor.fetch().then(function(value: any) {
			expect(value).toBe("somedata");
		}, function() {
			(expect() as any).fail();
		});

		$rootScope.$digest();

		expect(editorCRUDService.get).toHaveBeenCalledWith({
			identifier: smarteditComponentId
		});

	});

	it('GenericEditor fetch executes return empty object if identifier is not set', function() {

		const editor = new GenericEditor({
			smarteditComponentType,
			structure: 'structure',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		} as any);

		editor.fetch().then(function(value: any) {
			expect(value).toEqual({});
		}, function() {
			(expect() as any).fail();
		});

		$rootScope.$digest();

		expect(editorCRUDService.get).not.toHaveBeenCalled();

	});

	it('calling reset() set component to prior pristine state and call $setPristine on the component form if componentForm is passed and set holders if not set yet', function() {

		const pristine: any = {
			a: '1',
			b: '2'
		};

		const fields: any = [{
			field: 'field1',
			initiated: true
		}, {
			field: 'field2',
			initiated: false
		}];

		const INPUT = 'input';

		const editor = new GenericEditor({
			smarteditComponentType,
			smarteditComponentId,
			structure: 'structure',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items',
		} as any);

		editor.fields = fields;
		editor.pristine = pristine;
		editor.componentForm = componentForm;
		editor.reset();

		expect(searchSelector.val).toHaveBeenCalledWith('');
		expect(searchSelector.trigger).toHaveBeenCalledWith(INPUT);
		expect(editor.fields).toEqual([{
			field: 'field1',
			messages: undefined,
			hasErrors: false,
			hasWarnings: false
		}, {
			field: 'field2',
			messages: undefined,
			hasErrors: false,
			hasWarnings: false
		}] as any);

		expect(editor.component).not.toBe(pristine);
		expect(editor.component).toEqualData(pristine);
		expect(componentForm.$setPristine).toHaveBeenCalled();

		expect(editor.fieldsMap).toEqual({
			default: [editor.fields[0], editor.fields[1]]
		});

	});

	it('successful load will set component and pristine state and call reset and "localize null" null values of localized properties', function() {

		const data: any = {
			a: '1',
			b: '2',
			c: null,
			d: {
				en: 'something'
			},
			e: null
		};

		editorCRUDService.get.and.returnValue($q.when(data));
		const editor = new GenericEditor({
			smarteditComponentType,
			smarteditComponentId,
			structure: 'structure',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		} as any);

		editor.fields = [{
			qualifier: "c",
			localized: true
		}, {
			qualifier: "d",
			localized: true
		}] as any;
		spyOn(editor, 'reset').and.returnValue(null);

		editor.load();
		// for promises to actually resolve :
		$rootScope.$digest();

		expect(editorCRUDService.get).toHaveBeenCalledWith({
			identifier: 'smarteditComponentId'
		});

		expect(editor.pristine).toEqualData({
			a: '1',
			b: '2',
			c: {},
			d: {
				en: 'something'
			},
			e: null
		} as any);
		expect(editor.reset).toHaveBeenCalled();

	});

	it('submit will do nothing if componentForm is not valid', function() {

		const editor = new GenericEditor({
			smarteditComponentType,
			smarteditComponentId,
			structure: 'structure',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items',
			updateCallback
		} as any);
		spyOn(editor, 'updateCallback').and.returnValue(null);
		spyOn(editor, 'reset').and.returnValue(null);
		spyOn(editor, 'removeValidationMessages').and.returnValue(null);
		spyOn((editor as any), '_displayValidationMessages').and.returnValue(null);

		spyOn(editor, 'isDirty').and.returnValue(true);
		spyOn(editor, 'isValid').and.returnValue(false);
		editor.componentForm = componentForm;
		editor.submit();

		// The errors should have been removed. This is necessary in case there was an associated error in a different tab.
		expect(editor.removeValidationMessages).toHaveBeenCalled();

		expect(editorCRUDService.update).not.toHaveBeenCalled();
		expect(editor.reset).not.toHaveBeenCalled();
		expect(editor.updateCallback).not.toHaveBeenCalled();
		expect((editor as any)._displayValidationMessages).not.toHaveBeenCalled();
	});

	it('GIVEN generic editor with modified component WHEN submit is clicked and the backend returns empty response THEN submit function returns original payload', function() {
		// GIVEN
		editorCRUDService.update.and.returnValue($q.when(''));

		const editor = new GenericEditor({
			smarteditComponentType,
			smarteditComponentId,
			structure: 'structure',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items',
			updateCallback
		} as any);

		const refreshedData = {
			a: '1',
			b: '2',
			c: '3'
		};

		const originalPayload = refreshedData;

		const component = {
			a: '1',
			b: '4',
			c: '3'
		};

		editor.pristine = originalPayload;
		editor.component = component;
		editor.componentForm = componentForm;

		spyOn(editor, 'fetch').and.returnValue($q.when(refreshedData));
		spyOn(editor, 'updateCallback').and.returnValue(null);
		spyOn(editor, 'reset').and.returnValue(null);
		spyOn((editor as any), '_displayValidationMessages').and.returnValue(null);
		spyOn(editor, 'removeValidationMessages').and.returnValue(null);
		spyOn(editor, 'isDirty').and.returnValue(true);

		// WHEN
		const result = editor.submit();
		$rootScope.$digest();

		// THEN
		expect(result).toBeResolvedWithData({
			a: '1',
			b: '4',
			c: '3'
		});
	});

	it('submit will refresh the non editable fields values from server, call update, set pristine state, calls removeValidationMessages, reset and updateCallback if dirty and form valid', function() {

		editorCRUDService.update.and.returnValue($q.when(null)); // not listening to response anymore

		const editor = new GenericEditor({
			smarteditComponentType,
			smarteditComponentId,
			structure: 'structure',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items',
			updateCallback
		} as any);

		const pristine = {
			a: '1',
			b: '2',
			c: '3'
		};

		const component = {
			a: '1',
			b: '4',
			c: '3'
		};

		const fields = [{
			qualifier: 'a'
		}, {
			qualifier: 'b'
		}, {
			qualifier: 'c'
		}] as any;

		editor.fields = fields;
		editor.pristine = pristine;
		editor.component = component;
		editor.componentForm = componentForm;

		spyOn(editor, 'updateCallback').and.returnValue(null);
		spyOn(editor, 'reset').and.returnValue(null);
		spyOn((editor as any), '_displayValidationMessages').and.returnValue(null);
		spyOn(editor, 'removeValidationMessages').and.returnValue(null);
		spyOn(editor, 'isDirty').and.returnValue(true);

		editor.submit();
		// for promises to actually resolve :
		$rootScope.$digest();

		expect(editorCRUDService.update).toHaveBeenCalledWith({
			a: '1',
			b: '4',
			c: '3',
			identifier: 'smarteditComponentId'
		});

		expect(editor.updateCallback).toHaveBeenCalledWith(editor.pristine, null);
		expect(editor.reset).toHaveBeenCalledWith();
		expect(editor.removeValidationMessages).toHaveBeenCalled();
		expect((editor as any)._displayValidationMessages).toHaveBeenCalled();
		expect(editor.pristine).toEqual({
			a: '1',
			b: '4',
			c: '3'
		});
	});

	it('successful init will assign editing structure from API, fetch storefront languages and process it and call load ', function() {

		const fields = {
			attributes: [{
				qualifier: 'property1',
				cmsStructureType: 'ShortString'
			}, {
				qualifier: 'id',
				cmsStructureType: 'ShortString'
			}, {
				qualifier: 'type',
				cmsStructureType: 'ShortString'
			}, {
				qualifier: 'activationDate',
				cmsStructureType: 'DateTime'
			}]
		};

		const modifiedFields: any = [];

		const deferred = $q.defer();
		deferred.resolve(fields);
		editorStructureService.get.and.returnValue(deferred.promise);

		const editor = new GenericEditor({
			smarteditComponentType,
			smarteditComponentId,
			structureApi: '/cmswebservices/types/:smarteditComponentType',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items',
			updateCallback
		});
		const deferred2 = $q.defer();
		deferred2.resolve();
		spyOn(editor as any, 'load').and.returnValue(deferred2.promise);

		spyOn(editor as any, 'fieldAdaptor').and.returnValue(modifiedFields);
		spyOn(editor as any, 'pushEditorToStack');

		editor.init().then(function() {
			expect(editor.fields).toEqualData(modifiedFields);
		}, function() {
			(expect(editor) as any).fail();
		});

		// for promises to actually resolve :
		$rootScope.$digest();

		expect(editorStructureService.get).toHaveBeenCalledWith({
			smarteditComponentType: 'smarteditComponentType'
		});
		expect(sharedDataService.get).toHaveBeenCalledWith('experience');
		expect(languageService.getLanguagesForSite).toHaveBeenCalledWith('someSiteUid');

		expect(editor.languages).toEqualData(STOREFRONT_LANGUAGES);
		expect(editor.fieldAdaptor).toHaveBeenCalledWith(fields.attributes);
		expect(editor.load).toHaveBeenCalled();
		expect(editor.pushEditorToStack).toHaveBeenCalled();
	});

	it('successful init will assign editing structure from local structure and process it and call load ', function() {

		const structure = {
			attributes: [{
				qualifier: 'property1',
				cmsStructureType: 'ShortString'
			}, {
				qualifier: 'id',
				cmsStructureType: 'ShortString'
			}, {
				qualifier: 'type',
				cmsStructureType: 'ShortString'
			}, {
				qualifier: 'activationDate',
				cmsStructureType: 'DateTime'
			}],
			category: 'TEST'
		};

		const tabId = 'testTab';

		const editor = new GenericEditor({
			smarteditComponentType,
			smarteditComponentId,
			structure,
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items',
			updateCallback,
			id: tabId
		});
		const deferred2 = $q.defer();
		deferred2.resolve();
		spyOn(editor, 'load').and.returnValue(deferred2.promise);

		spyOn(editor, 'fieldAdaptor').and.callThrough();

		editor.init().then(function() {
			expect(editor.fields).toEqualData(structure.attributes);
		}, function() {
			(expect(editor) as any).fail();
		});

		// for promises to actually resolve :
		$rootScope.$digest();

		expect(editor.fieldAdaptor).toHaveBeenCalledWith(structure.attributes);
		expect(editor.load).toHaveBeenCalled();
	});

	it('fieldAdaptor will assign postfix text when a field qualifier defines a property', function() {

		const fields = [{
			qualifier: 'media',
			cmsStructureType: 'MediaContainer'
		}];

		const componentType = "simpleResponsiveBannerComponent";
		const editor = new GenericEditor({
			smarteditComponentType: componentType,
			smarteditComponentId,
			structureApi: '/cmswebservices/types/:smarteditComponentType',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		});

		const result = "field can not be editable";
		$translate.instant.and.returnValue(result);

		const newFields = editor.fieldAdaptor(fields);

		expect(newFields[0].postfixText).toBe(result);
		expect($translate.instant).toHaveBeenCalledWith('simpleresponsivebannercomponent.media.postfix.text');
	});

	it('fieldAdaptor wont assign postfix text when a field qualifier does not define a property  ', function() {

		const fields = [{
			qualifier: 'media',
			cmsStructureType: 'MediaContainer'
		}];

		const componentType = "simpleResponsiveBannerComponent";
		const editor = new GenericEditor({
			smarteditComponentType: componentType,
			smarteditComponentId,
			structureApi: '/cmswebservices/types/:smarteditComponentType',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		});

		const key = 'simpleresponsivebannercomponent.media.postfix.text';
		$translate.instant.and.returnValue(key);

		const newFields = editor.fieldAdaptor(fields);

		expect(newFields[0].postfixText).toBe('');
		expect($translate.instant).toHaveBeenCalledWith(key);
	});

	it('_isPrimitive returns true for "Boolean", "ShortString", "LongString", "RichText", "Date" types only', function() {

		const editor = new GenericEditor({
			smarteditComponentType,
			smarteditComponentId,
			structureApi: '/cmswebservices/types/:smarteditComponentType',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		});

		const isPrim = [];
		isPrim.push((editor as any)._isPrimitive('Boolean'));
		isPrim.push((editor as any)._isPrimitive('ShortString'));
		isPrim.push((editor as any)._isPrimitive('LongString'));
		isPrim.push((editor as any)._isPrimitive('RichText'));
		isPrim.push((editor as any)._isPrimitive('Date'));
		isPrim.push((editor as any)._isPrimitive('AnyNonPrimitiveType'));

		expect(isPrim).toEqual([true, true, true, true, true, false]);

	});

	it('GIVEN that cmsStructureType is "Enum", refreshOptions  will call fetchEnumDataHandler to fetch fetch full list of enums', function() {

		const field: any = {
			qualifier: 'property1',
			cmsStructureType: 'Enum'
		};

		const editor = new GenericEditor({
			smarteditComponentType,
			smarteditComponentId,
			structureApi: '/cmswebservices/types/:smarteditComponentType',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		});

		const component = {};
		editor.component = component;

		editor.refreshOptions(field, 'qualifier', 's');
		$rootScope.$digest();
		expect(fetchEnumDataHandler.getById).not.toHaveBeenCalled();
		expect(fetchEnumDataHandler.findByMask).toHaveBeenCalledWith(field, 's');
		expect(field.options).toEqual({
			property1: [{
				code: 'code1',
				label: 'label1'
			}, {
				code: 'code2',
				label: 'label2'
			}]
		});
		expect(field.initiated).toEqual(['property1']);

	});

	it('fieldAdaptor does not transform the fields if neither external nor urlLink are found', function() {

		const fields = [{
			qualifier: 'property1',
			cmsStructureType: 'ShortString',
		}];

		const editor = new GenericEditor({
			smarteditComponentType,
			smarteditComponentId,
			structureApi: '/cmswebservices/types/:smarteditComponentType',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		});
		const newFields = editor.fieldAdaptor(fields);

		expect(newFields).toEqualData(fields);

	});

	it('fieldAdaptor does not transform the fields if urlLink is not found', function() {

		const fields = [{
			qualifier: 'property1',
			cmsStructureType: 'ShortString',
		}, {
			qualifier: 'external',
			cmsStructureType: 'Boolean',
		}];

		const editor = new GenericEditor({
			smarteditComponentType,
			smarteditComponentId,
			structureApi: '/cmswebservices/types/:smarteditComponentType',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		});
		const newFields = editor.fieldAdaptor(fields);

		expect(newFields).toEqualData(fields);

	});

	it('fieldAdaptor does not transform the fields if external is not found', function() {

		const fields = [{
			qualifier: 'property1',
			cmsStructureType: 'ShortString',
		}, {
			qualifier: 'urlLink',
			cmsStructureType: 'ShortString',
		}];

		const editor = new GenericEditor({
			smarteditComponentType,
			smarteditComponentId,
			structureApi: '/cmswebservices/types/:smarteditComponentType',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		});
		const newFields = editor.fieldAdaptor(fields);

		expect(newFields).toEqualData(fields);

	});

	it('_displayValidationErrors will add errors messages and localization languages to the field', function() {
		seValidationErrorParser.parse.and.callFake(function(message: any) {
			return {
				message
			};
		});

		const validationErrors = [{
			message: "This field cannot contain special characters",
			reason: "missing",
			subject: "field1",
			subjectType: "parameter",
			type: "ValidationError"
		}, {
			message: "This field is required and must to be between 1 and 255 characters long.",
			reason: "missing",
			subject: "field2",
			subjectType: "parameter",
			type: "ValidationError"
		}];

		const fields: any = [{
			qualifier: 'field1'
		}, {
			qualifier: 'field2'
		}];

		const editor = new GenericEditor({
			smarteditComponentType,
			smarteditComponentId,
			updateCallback,
			structure: 'structure',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		} as any);
		editor.fields = fields;
		editor.pristine = {};

		editor.reset();
		(editor as any)._displayValidationMessages(validationErrors, true);

		expect(fields[0].messages.length).toEqual(1);
		expect(fields[0].messages.length).toEqual(1);

		expect(fields[0].messages[0].message).toEqual("This field cannot contain special characters");
		expect(fields[0].messages[0].marker).toEqual("field1");

		expect(fields[1].messages[0].message).toEqual("This field is required and must to be between 1 and 255 characters long.");
		expect(fields[1].messages[0].marker).toEqual("field2");
	});

	it('_displayValidationMessages will add language from validation errors for the language property if the field is localized else will add the qualifier to the language property ', function() {
		seValidationErrorParser.parse.and.callFake(function(message: any) {
			const error: any = {};
			if (message === "This field cannot contain special characters. Language: [en]") {
				error.message = 'This field cannot contain special characters.';
				error.language = 'en';
			} else {
				error.message = message;
			}
			return error;
		});

		const validationErrors = [{
			message: "This field cannot contain special characters. Language: [en]",
			reason: "missing",
			subject: "field1",
			subjectType: "parameter",
			type: "ValidationError"
		}, {
			message: "This field is required and must to be between 1 and 255 characters long.",
			reason: "missing",
			subject: "field2",
			subjectType: "parameter",
			type: "ValidationError"
		}];

		const fields: any = [{
			qualifier: 'field1',
			localized: true
		}, {
			qualifier: 'field2'
		}];

		const editor = new GenericEditor({
			smarteditComponentType,
			smarteditComponentId,
			updateCallback,
			structure: 'structure',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		} as any);
		editor.fields = fields;
		editor.pristine = {};

		editor.reset();

		(editor as any)._displayValidationMessages(validationErrors, true);

		expect(fields[0].messages.length).toEqual(1);
		expect(fields[0].messages.length).toEqual(1);

		expect(fields[0].messages[0].message).toEqual("This field cannot contain special characters.");
		expect(fields[0].messages[0].marker).toEqual("en");

		expect(fields[1].messages[0].message).toEqual("This field is required and must to be between 1 and 255 characters long.");
		expect(fields[1].messages[0].marker).toEqual("field2");

	});

	it('_displayValidationMessages will not show the message if it has already been added to the list of messages', function() {

		const validationErrors = [{
			message: "This field cannot contain special characters. Language: [en]",
			reason: "missing",
			subject: "field1",
			subjectType: "parameter",
			type: "ValidationError"
		}, {
			message: "This field is required and must to be between 1 and 255 characters long.",
			reason: "missing",
			subject: "field2",
			subjectType: "parameter",
			type: "ValidationError"
		}];

		const fields = [{
			qualifier: 'field1',
			localized: true,
			messages: [{
				message: "This field cannot contain special characters. Language: [en]",
				reason: "missing",
				subject: "field1",
				subjectType: "parameter",
				type: "ValidationError",
				uniqId: "eyJtZXNzYWdlIjoiVGhpcyBmaWVsZCBjYW5ub3QgY29udGFpbiBzcGVjaWFsIGNoYXJhY3RlcnMuIiwibGFuZ3VhZ2UiOiJlbiIsIm1hcmtlciI6ImVuIiwidHlwZSI6IlZhbGlkYXRpb25FcnJvciJ9"
			}]
		}, {
			qualifier: 'field2'
		}] as any;

		const editor = new GenericEditor({
			smarteditComponentType,
			smarteditComponentId,
			updateCallback,
			structure: 'structure',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		} as any);
		editor.fields = fields;
		editor.pristine = {};
		editor.reset();
		(editor as any)._displayValidationMessages(validationErrors, true);

		expect(fields[0].messages.length).toEqual(1);

		expect(fields[0].messages[0]).toEqualData({
			message: "This field cannot contain special characters.",
			reason: "missing",
			subject: "field1",
			subjectType: "parameter",
			language: "en",
			marker: "en",
			type: "ValidationError",
			uniqId: "eyJtZXNzYWdlIjoiVGhpcyBmaWVsZCBjYW5ub3QgY29udGFpbiBzcGVjaWFsIGNoYXJhY3RlcnMuIiwicmVhc29uIjoibWlzc2luZyIsInN1YmplY3QiOiJmaWVsZDEiLCJzdWJqZWN0VHlwZSI6InBhcmFtZXRlciIsInR5cGUiOiJWYWxpZGF0aW9uRXJyb3IiLCJsYW5ndWFnZSI6ImVuIiwibWFya2VyIjoiZW4ifQ=="
		} as any);

	});

	it('GIVEN a list of validationMessages WHEN _displayValidationErrors is called with keepAllErrors as false THEN it will filter out all fields that are pristine', function() {
		seValidationErrorParser.parse.and.callFake(function(message: any) {
			return {
				message
			};
		});

		const validationErrors = [{
			message: "This field cannot contain special characters",
			reason: "missing",
			subject: "field1",
			subjectType: "parameter",
			type: "ValidationError",
			fromSubmit: false,
			isNonPristine: true
		}, {
			message: "This field is required and must to be between 1 and 255 characters long.",
			reason: "missing",
			subject: "field2",
			subjectType: "parameter",
			type: "ValidationError",
			fromSubmit: false,
			isNonPristine: false
		}];

		const fields: any = [{
			qualifier: 'field1'
		}, {
			qualifier: 'field2'
		}];

		const editor = new GenericEditor({
			smarteditComponentType,
			smarteditComponentId,
			updateCallback,
			structure: 'structure',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		} as any);
		editor.fields = fields;
		editor.pristine = {};

		editor.reset();
		(editor as any)._displayValidationMessages(validationErrors, false);

		expect(fields[0].messages.length).toEqual(1);
		expect(fields[0].messages[0].subject).toEqual("field1");

	});

	it('failed submit will remove existing validation errors and call _displayValidationMessages', function() {

		const failure = {
			data: {
				errors: [{
					message: "This field cannot contain special characters",
					reason: "missing",
					subject: "headline",
					subjectType: "parameter",
					type: "ValidationError"
				}, {
					message: "This field is required and must to be between 1 and 255 characters long.",
					reason: "missing",
					subject: "content",
					subjectType: "parameter",
					type: "ValidationError"
				}]
			}
		};
		editorCRUDService.update.and.returnValue($q.reject(failure));

		const editor = new GenericEditor({
			smarteditComponentType,
			smarteditComponentId,
			updateCallback,
			structure: 'structure',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		} as any);

		const pristine = {
			a: '0',
			b: '1'
		};

		const component = {
			a: '1',
			b: '2'
		};

		const fields = [{
			qualifier: 'a',
		}, {
			qualifier: 'b'
		}] as any;

		editor.pristine = pristine;
		editor.component = component;
		editor.fields = fields;
		editor.componentForm = componentForm;
		componentForm.$dirty = true;

		spyOn(editor, 'updateCallback').and.returnValue(null);
		spyOn(editor, 'reset').and.callThrough();
		spyOn((editor as any), '_displayValidationMessages').and.callThrough();
		spyOn(editor, 'removeValidationMessages').and.returnValue(null);

		editor.submit();
		// for promises to actually resolve :
		$rootScope.$digest();

		expect(editor.updateCallback).not.toHaveBeenCalled();
		expect(editor.reset).not.toHaveBeenCalledWith(componentForm);

		expect(editor.removeValidationMessages).toHaveBeenCalledWith();
		expect((editor as any)._displayValidationMessages).toHaveBeenCalledWith(failure.data.errors, true);
	});

	it('GIVEN there are errors caused by an external editor WHEN submit is called THEN the editor must raise a GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT ', function() {
		// Arrange
		const failure: any = {
			data: {
				errors: [{
					message: "This field cannot contain special characters",
					reason: "missing",
					subject: "headline",
					subjectType: "parameter",
					type: "ValidationError"
				}, {
					message: "This field is required and must to be between 1 and 255 characters long.",
					reason: "missing",
					subject: "content",
					subjectType: "parameter",
					type: "ValidationError"
				}]
			}
		};

		failure.data.errors.sourceGenericEditorId = 'someId';

		const refreshedData = {
			a: '1',
			b: '2',
			c: '5'
		};

		const editor = new GenericEditor({
			id: 'someId',
			smarteditComponentType,
			smarteditComponentId,
			updateCallback,
			structure: 'structure',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		} as any);

		editorCRUDService.update.and.returnValue($q.reject(failure));

		editor.componentForm = componentForm;
		componentForm.$dirty = true;

		editor.component = {};
		editor.component.someField = 'someFieldvalue';

		spyOn((editor as any), '_displayValidationMessages').and.callThrough();
		spyOn(editor, 'removeValidationMessages').and.returnValue(null);
		spyOn(editor, 'fetch').and.returnValue($q.when(refreshedData));

		// Act
		editor.submit();
		$rootScope.$digest(); // for promises to actually resolve

		// Assert
		expect(systemEventService.publishAsync).toHaveBeenCalledWith("UnrelatedValidationErrors", failure.data.errors);
	});

	it('GIVEN there are errors in one or more fields in the current editor detected externally WHEN GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT handler is called THEN the editor must display those validation errors', function() {
		seValidationErrorParser.parse.and.callFake(function(message: any) {
			return {
				message
			};
		});

		// Arrange
		const failure = {
			data: {
				errors: [{
					message: "This field cannot contain special characters",
					reason: "missing",
					subject: "headline",
					subjectType: "parameter",
					type: "ValidationError"
				}, {
					message: "This field is required and must to be between 1 and 255 characters long.",
					reason: "missing",
					subject: "content",
					subjectType: "parameter",
					type: "ValidationError"
				}]
			}
		};

		const fields = [{
			qualifier: 'headline'
		}] as any;

		const editor = new GenericEditor({
			smarteditComponentType,
			smarteditComponentId,
			updateCallback,
			structure: 'structure',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		} as any);
		editor.id = 'some ID';
		editor.fields = fields;
		editor.pristine = {};

		spyOn(editor, 'isDirty').and.returnValue(false);
		spyOn(editor, 'removeValidationMessages');
		spyOn((editor as any), '_displayValidationMessages').and.callThrough();

		// Act
		editor.reset();
		(editor as any)._handleUnrelatedValidationMessages("some Key", {
			messages: failure.data.errors
		});

		// Assert
		expect(editor.removeValidationMessages).toHaveBeenCalled();
		expect((editor as any)._displayValidationMessages).toHaveBeenCalledWith(failure.data.errors, true);
	});

	it('isDirty will sanitize before checking if pristine and component HTML are equal', function() {

		let pristine: any = {
			a: {
				en: '<h2>search</h2><p>Suggestions</p><ul>	<li>The</li>	<li>The</li>	<li>Test</li></ul>',
			},
			b: '1',
			c: '<h2>search</h2> \n<p>Suggestions</p><ul>\n<li>The</li><li>The</li><li>Test</li></ul>'
		};

		let component: any = {
			a: {
				en: '<h2>search</h2> \n<p>Suggestions</p><ul>\n<li>The</li><li>The</li><li>Test</li></ul>',
			},
			b: '1',
			c: '<h2>search</h2><p>Suggestions</p><ul>	<li>The</li>	<li>The</li>	<li>Test</li></ul>'
		};

		let fields = [{
			cmsStructureType: 'RichText',
			qualifier: 'a',
			localized: true
		}, {
			qualifier: 'b'
		}, {
			qualifier: 'c',
			cmsStructureType: 'RichText',
			localized: false
		}] as any;

		const editor = new GenericEditor({
			smarteditComponentType,
			smarteditComponentId,
			updateCallback,
			structure: 'structure',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		} as any);

		editor.pristine = pristine;
		editor.component = component;
		editor.fields = fields;

		(editor as any).linkToStatus = {
			hasBoth() {
				return false;
			}
		};

		let result = editor.isDirty();
		expect(result).toEqual(false);

		pristine = {
			a: {
				en: '<h2>test1</h2> <p>test2</p>',
			}
		};

		component = {
			a: {
				en: '<h2>TEST2</h2> \n<p>test1</p>',
			}
		};

		fields = [{
			cmsStructureType: 'RichText',
			qualifier: 'a',
			localized: true
		}] as any;

		editor.pristine = pristine;
		editor.component = component;
		editor.fields = fields;

		result = editor.isDirty();
		expect(result).toEqual(true);

	});

	it('isDirty will return true even for properties that are not fields', function() {

		const pristine = {
			a: '123 ',
			b: '0'
		};

		const component = {
			a: '123',
			b: ''
		};

		const editor = new GenericEditor({
			smarteditComponentType,
			smarteditComponentId,
			updateCallback,
			structure: 'structure',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		} as any);

		(editor as any).linkToStatus = {
			hasBoth() {
				return false;
			}
		};

		editor.pristine = pristine;
		editor.component = component;

		const result = editor.isDirty();
		expect(result).toEqual(true);
	});

	it('sanitizePayload will remove dangerous characters from a localized ShortString CMS component type when the user saves the form with data in the input', function() {

		const editor = new GenericEditor({
			smarteditComponentType,
			smarteditComponentId,
			updateCallback,
			structure: 'structure',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		} as any);

		let payload: any = {
			headline: {
				en: '<h1>Foo bar</h1>h1>'
			}
		};

		const fields = [{
			qualifier: "headline",
			cmsStructureType: "ShortString",
			localized: true
		}];

		payload = editor.sanitizePayload(payload, fields);

		expect(sanitize.calls.count()).toBe(1);
		expect(sanitize.calls.argsFor(0)[0]).toBe('<h1>Foo bar</h1>h1>');
		expect(payload).toEqual({
			headline: {
				en: 'ESCAPED'
			}
		});

	});

	it('sanitizePayload will not remove dangerous characters from a ShortString CMS component type when the user saves the form with no data in the input', function() {

		const editor = new GenericEditor({
			smarteditComponentType,
			smarteditComponentId,
			updateCallback,
			structure: 'structure',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		} as any);

		let payload: any = {
			id: undefined
		};

		const fields = [{
			qualifier: "id",
			cmsStructureType: "ShortString",
		}];

		payload = editor.sanitizePayload(payload, fields);

		// The function will not be called because the qualifier is undefined
		expect(sanitize.calls.count()).toBe(0);
		expect(payload).toEqual({
			id: undefined
		});

	});

	it('sanitizePayload will remove dangerous characters from a LongString CMS component type when the user saves the form with data in the textarea', function() {

		const editor = new GenericEditor({
			smarteditComponentType,
			smarteditComponentId,
			updateCallback,
			structure: 'structure',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		} as any);

		let payload: any = {
			urlLink: "/pathwithxss/onclick='alert(1)'"
		};

		const fields = [{
			qualifier: "urlLink",
			cmsStructureType: "LongString"
		}];

		payload = editor.sanitizePayload(payload, fields);

		expect(sanitize.calls.count()).toBe(1);
		expect(sanitize.calls.argsFor(0)[0]).toBe("/pathwithxss/onclick='alert(1)'");
		expect(payload).toEqual({
			urlLink: "ESCAPED"
		});

	});

	it('sanitizePayload will not remove dangerous characters from a LongString CMS component type when the user saves the form with no data in the textarea', function() {

		const editor = new GenericEditor({
			smarteditComponentType,
			smarteditComponentId,
			updateCallback,
			structure: 'structure',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		} as any);

		let payload: any = {
			metaDescription: undefined
		};

		const fields = [{
			qualifier: 'metaDescription',
			cmsStructureType: 'LongString'
		}];

		payload = editor.sanitizePayload(payload, fields);

		// The function will not be called because the qualifier is undefined
		expect(sanitize.calls.count()).toBe(0);
		expect(payload).toEqual({
			metaDescription: undefined
		});

	});

	it('_fieldsAreUserChecked WILL fail validation WHEN a required checkbox field is not checked', function() {

		const fields = [{
			qualifier: 'content',
			cmsStructureType: 'Paragraph',
			requiresUserCheck: {
				content: true
			},
			isUserChecked: false
		}];
		const componentType = "simpleResponsiveBannerComponent";
		const editor = new GenericEditor({
			smarteditComponentType: componentType,
			smarteditComponentId,
			structureApi: '/cmswebservices/types/:smarteditComponentType',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		});
		editor.fields = fields;
		const valid = (editor as any)._fieldsAreUserChecked();
		expect(valid).toEqual(false);

	});

	it('_fieldsAreUserChecked WILL pass validation WHEN not required checkbox field is not checked', function() {
		const fields = [{
			qualifier: 'content',
			cmsStructureType: 'Paragraph',
			requiresUserCheck: {
				content: true
			},
			isUserChecked: true
		}];
		const componentType = "simpleResponsiveBannerComponent";
		const editor = new GenericEditor({
			smarteditComponentType: componentType,
			smarteditComponentId,
			structureApi: '/cmswebservices/types/:smarteditComponentType',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		});
		editor.fields = fields;
		const valid = (editor as any)._fieldsAreUserChecked();
		expect(valid).toEqual(true);
	});

	it('submit WILL fail validation WHEN submit a not checked required checkbox field', function() {
		const fields = [{
			qualifier: 'content',
			cmsStructureType: 'Paragraph',
			requiresUserCheck: {
				content: true
			},
			isUserChecked: false
		}];
		const componentType = "simpleResponsiveBannerComponent";
		const editor = new GenericEditor({
			smarteditComponentType: componentType,
			smarteditComponentId,
			structureApi: '/cmswebservices/types/:smarteditComponentType',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		});
		editor.fields = fields;
		editor.submit();

		expect(editor.hasFrontEndValidationErrors).toEqual(true);
	});

	it('submit WILL pass validation WHEN submit a checked required checkbox field', function() {
		const fields = [{
			qualifier: 'content',
			cmsStructureType: 'Paragraph',
			requiresUserCheck: {
				content: true
			},
			isUserChecked: true
		}];
		const componentType = "simpleResponsiveBannerComponent";
		const editor = new GenericEditor({
			smarteditComponentType: componentType,
			smarteditComponentId,
			structureApi: '/cmswebservices/types/:smarteditComponentType',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		});
		editor.fields = fields;
		editor.component = {};

		editor.componentForm = componentForm;

		editor.submit();
		expect(editor.hasFrontEndValidationErrors).toEqual(false);
	});

	it('_convertStructureArray will properly convert the structures to a format that the GE can understand', function() {
		const fields = [{
			qualifier: 'content',
			cmsStructureType: 'Paragraph',
			requiresUserCheck: {
				content: true
			},
			isUserChecked: true
		}];

		const structures = {
			structures: [{
				attributes: fields
			}]
		};

		const componentTypes = {
			componentTypes: [{
				attributes: fields
			}]
		};

		const componentType = "simpleResponsiveBannerComponent";
		const editor = new GenericEditor({
			smarteditComponentType: componentType,
			smarteditComponentId,
			structureApi: '/cmswebservices/types/:smarteditComponentType',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		});

		let structure = (editor as any)._convertStructureArray(structures);
		expect(structure.attributes).toEqual(fields);

		structure = (editor as any)._convertStructureArray(componentTypes);
		expect(structure.attributes).toEqual(fields);
	});

	it('WHEN an editor is finalized THEN it properly cleans up', function() {
		// GIVEN 
		const _unregisterUnrelatedErrorsEvent = jasmine.createSpy('_unregisterUnrelatedErrorsEvent');
		const _unregisterUnrelatedMessagesEvent = jasmine.createSpy('_unregisterUnrelatedMessagesEvent');
		systemEventService.subscribe.and.callFake(function(eventId: any) {
			if (eventId === GENERIC_EDITOR_UNRELATED_VALIDATION_ERRORS_EVENT) {
				return _unregisterUnrelatedErrorsEvent;
			} else if (eventId === GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT) {
				return _unregisterUnrelatedMessagesEvent;
			}
			return null;
		});

		const editor = new GenericEditor({
			smarteditComponentType,
			smarteditComponentId,
			updateCallback,
			structure: 'structure',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		} as any);

		spyOn(editor, 'popEditorFromStack').and.callThrough();

		// WHEN 
		editor._finalize();

		// THEN
		expect((editor as any)._unregisterUnrelatedErrorsEvent).toHaveBeenCalled();
		expect((editor as any)._unregisterUnrelatedMessagesEvent).toHaveBeenCalled();
		expect(editor.popEditorFromStack).toHaveBeenCalled();
		expect(systemEventService.publishAsync).not.toHaveBeenCalled();
	});

	it('GIVEN no editorStackId was provided WHEN pushEditorToStack is called THEN it sends the right event with the right editor stack id', function() {
		// GIVEN 
		const EDITOR_ID = 'some editor id';
		const SAMPLE_COMPONENT = 'some sample component' as any;
		const editor = new GenericEditor({
			id: EDITOR_ID,
			smarteditComponentType,
			smarteditComponentId,
			updateCallback,
			structure: 'structure',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		} as any);
		editor.component = SAMPLE_COMPONENT;

		// WHEN 
		editor.pushEditorToStack();

		// THEN 
		expect(systemEventService.publishAsync).toHaveBeenCalledWith(EDITOR_PUSH_TO_STACK_EVENT, {
			editorId: EDITOR_ID,
			editorStackId: EDITOR_ID,
			component: SAMPLE_COMPONENT,
			componentType: smarteditComponentType
		});
	});

	it('GIVEN an editorStackId was provided WHEN pushEditorToStack is called THEN it sends the right event with the right editor stack id', function() {
		// GIVEN 
		const STACK_ID = 'some stack id';
		const EDITOR_ID = 'some editor id';
		const SAMPLE_COMPONENT = 'some sample component' as any;
		const editor = new GenericEditor({
			id: EDITOR_ID,
			smarteditComponentType,
			smarteditComponentId,
			updateCallback,
			editorStackId: STACK_ID,
			structure: 'structure',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		} as any);
		editor.component = SAMPLE_COMPONENT;

		// WHEN 
		editor.pushEditorToStack();

		// THEN 
		expect(systemEventService.publishAsync).toHaveBeenCalledWith(EDITOR_PUSH_TO_STACK_EVENT, {
			editorId: EDITOR_ID,
			editorStackId: STACK_ID,
			component: SAMPLE_COMPONENT,
			componentType: smarteditComponentType
		});
	});

	it('WHEN popEditorFromStack is called THEN it sends the right event', function() {
		// GIVEN 
		const STACK_ID = 'some stack id';
		const editor = new GenericEditor({
			smarteditComponentType,
			smarteditComponentId,
			updateCallback,
			editorStackId: STACK_ID,
			structure: 'structure',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		} as any);

		// WHEN 
		editor.popEditorFromStack();

		// THEN 
		expect(systemEventService.publishAsync).toHaveBeenCalledWith(EDITOR_POP_FROM_STACK_EVENT, {
			editorStackId: STACK_ID
		});
	});

	it('GIVEN empty initial Object WHEN _getFieldsNonPristineState is called THEN it will return an object containing the non pristine states of all the fields', function() {
		// GIVEN 
		const editor = new GenericEditor({
			smarteditComponentType,
			smarteditComponentId,
			structure: 'structure',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		} as any);

		const initialObj = {};

		const pristine: any = {
			visible: true,
			restrictions: [],
			content: {
				de: 'aaaa',
				fr: "bbbb"
			},
			position: 2,
			slotId: "id",
			removedField: 1
		};

		const component = {
			visible: false,
			restrictions: ['dddd'],
			content: {
				de: 'aaaa',
				en: "cccc"
			},
			position: 54,
			slotId: "id",
			addedField: 2
		};

		// WHEN 
		const result = (editor as any)._getFieldsNonPristineState(initialObj, pristine, component);

		// THEN 
		const outputObj = {
			content: {
				de: false,
				en: true,
				fr: true
			},
			position: true,
			restrictions: true,
			slotId: false,
			visible: true,
			removedField: true,
			addedField: true
		};
		expect(result).toEqual(outputObj);

	});

	it('GIVEN non-empty initial object WHEN _getFieldsNonPristineState is called THEN it will return an object containing the non pristine states of all the fields merged to non-empty initial object', function() {
		// GIVEN 
		const editor = new GenericEditor({
			smarteditComponentType,
			smarteditComponentId,
			structure: 'structure',
			contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
		} as any);

		const initialObj = {
			content: {
				de: false,
				en: true
			},
			position: false,
			restrictions: true,
			slotId: false,
			removedField: true,
			addedField: true
		};

		const pristine: any = {
			visible: true,
			restrictions: [],
			content: {
				de: 'aaaa',
				fr: "bbbb",
				en: "cccc"
			},
			position: 2,
			slotId: "id",
			removedField: 1
		};

		const component = {
			visible: false,
			restrictions: ['dddd'],
			content: {
				de: 'aaaa',
				en: "cccc"
			},
			position: 54,
			slotId: "id",
			addedField: 2
		};

		// WHEN 
		const result = (editor as any)._getFieldsNonPristineState(initialObj, pristine, component);

		// THEN 
		const outputObj = {
			content: {
				de: false,
				en: true,
				fr: true
			},
			position: true,
			restrictions: true,
			slotId: false,
			visible: true,
			removedField: true,
			addedField: true
		};
		expect(result).toEqual(outputObj);

	});
});
