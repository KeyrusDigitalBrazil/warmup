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
import {SeRichTextFieldComponent} from "smarteditcommons/components/genericEditor/components/richText/SeRichTextFieldComponent";
import {promiseHelper} from 'testhelpers';
import * as angular from "angular";
import {
	GenericEditorSanitizationService,
	SeRichTextFieldLocalizationService,
	SeRichTextLoaderService
} from "smarteditcommons/components/genericEditor";

describe('seRichTextField', () => {
	const $q = promiseHelper.$q();

	let seRichTextLoaderService: jasmine.SpyObj<SeRichTextLoaderService>;

	let genericEditorSanitizationService: jasmine.SpyObj<GenericEditorSanitizationService>;
	let seRichTextFieldLocalizationService: jasmine.SpyObj<SeRichTextFieldLocalizationService>;
	let seRichTextFieldComponent: SeRichTextFieldComponent;

	let $element;
	let editable: any;
	let editorInstance: any;
	let seRichTextConfiguration: any;
	let $scope: jasmine.SpyObj<angular.IScope>;

	let originalCKEDITOR: any;

	beforeAll(() => {
		originalCKEDITOR = (window as any).CKEDITOR;
	});

	afterAll(() => {
		(window as any).CKEDITOR = originalCKEDITOR;
	});

	beforeEach(() => {
		seRichTextLoaderService = jasmine.createSpyObj<SeRichTextLoaderService>('seRichTextLoaderService', ['load']);
		seRichTextConfiguration = {};
		seRichTextFieldLocalizationService = jasmine.createSpyObj<SeRichTextFieldLocalizationService>('seRichTextFieldLocalizationService', ['localizeCKEditor']);

		(window as any).CKEDITOR = jasmine.createSpyObj('CKEDITOR', ['replace', 'on']);

		editorInstance = jasmine.createSpyObj('editorInstance', ['destroy', 'on', 'getData', 'fire', 'editable']);

		editable = jasmine.createSpyObj('editable', ['attachListener']);
		editorInstance.editable.and.returnValue(editable);
		(window as any).CKEDITOR.replace.and.returnValue(editorInstance);
		editorInstance.getData.and.returnValue("changed value");

		genericEditorSanitizationService = jasmine.createSpyObj<GenericEditorSanitizationService>('genericEditorSanitizationService', ['isSanitized']);

		$scope = jasmine.createSpyObj<angular.IScope>('$scope', ['$apply']);
		$scope.$apply.and.callFake((fn: any) => fn());

		$element = jasmine.createSpyObj('$element', ['find', 'bind']);
		$element.find.and.returnValue('textAreaElement');

		seRichTextLoaderService.load.and.returnValue($q.when());

		seRichTextFieldComponent = new SeRichTextFieldComponent(
			seRichTextLoaderService,
			seRichTextConfiguration,
			genericEditorSanitizationService,
			seRichTextFieldLocalizationService,
			$scope,
			$element
		);

		seRichTextFieldComponent.field = {
			qualifier: 'someQualifier'
		} as any;
		seRichTextFieldComponent.qualifier = 'en';
		seRichTextFieldComponent.model = {};
	});

	describe('controller', () => {

		describe('onMode', () => {

			it('should be attach editable listener if the mode is source', () => {
				(seRichTextFieldComponent as any).mode = 'source';
				seRichTextFieldComponent.onMode();
				expect(editable.attachListener).toHaveBeenCalled();
			});
		});

		describe('onInstanceReady', () => {

			it('should be called set rules method with attributes', () => {
				const setRules = jasmine.createSpy('setRules');

				const MOCK_EV = {
					editor: {
						dataProcessor: {
							writer: {
								setRules
							}
						}
					}
				};

				seRichTextFieldComponent.onInstanceReady(MOCK_EV);
				expect(setRules).toHaveBeenCalledWith('br', {
					indent: false,
					breakBeforeOpen: false,
					breakAfterOpen: false,
					breakBeforeClose: false,
					breakAfterClose: false
				});
			});

		});
	});

	describe('on data change', () => {

		it("should call genericEditorSanitizationService", () => {
			seRichTextFieldComponent.qualifier = 'en';
			seRichTextFieldComponent.model = {
				en: '<div><script>alert(/"I am a snippet/");</script></div>'
			};
			seRichTextFieldComponent.field = {} as any;
			seRichTextFieldComponent.reassignUserCheck();
			expect(genericEditorSanitizationService.isSanitized).toHaveBeenCalled();
		});

		it("reassignUserCheck WILL set requiresUserCheck as true on field with javascript snippet WHEN sanitized content does not match unsanitized content", () => {
			seRichTextFieldComponent.qualifier = 'en';
			seRichTextFieldComponent.model = {
				en: '<div><script>alert(/"I am a snippet/");</script></div>'
			};
			seRichTextFieldComponent.field = {} as any;
			seRichTextFieldComponent.reassignUserCheck();
			expect(seRichTextFieldComponent.field.requiresUserCheck[seRichTextFieldComponent.qualifier]).toBe(true);
		});

		it("reassignUserCheck WILL set requiresUserCheck as true on field WHEN sanitized content does not match unsanitized content", () => {
			seRichTextFieldComponent.qualifier = 'en';

			seRichTextFieldComponent.model = {
				en: '\"http://\"'
			};
			seRichTextFieldComponent.field = {} as any;
			seRichTextFieldComponent.reassignUserCheck();
			expect(seRichTextFieldComponent.field.requiresUserCheck[seRichTextFieldComponent.qualifier]).toBe(true);
		});

		it("reassignUserCheck WILL not set requiresUserCheck on field WHEN sanitized content matches unsanitized content.", () => {
			genericEditorSanitizationService.isSanitized.and.returnValue(true);

			seRichTextFieldComponent.qualifier = 'en';
			seRichTextFieldComponent.model = {
				en: '<p>Valid Html</p>'
			};
			seRichTextFieldComponent.field = {} as any;
			seRichTextFieldComponent.reassignUserCheck();

			expect(seRichTextFieldComponent.field.requiresUserCheck[seRichTextFieldComponent.qualifier]).toBe(false);
		});

		it("reassignUserCheck WILL not set requiresUserCheck on field WHEN there is no content", () => {
			seRichTextFieldComponent.model = {};
			seRichTextFieldComponent.field = {} as any;

			seRichTextFieldComponent.reassignUserCheck();
			expect(seRichTextFieldComponent.field.requiresUserCheck[seRichTextFieldComponent.qualifier]).toBe(false);
		});

		it("reassignUserCheck WILL not set requiresUserCheck on field WHEN the model is not defined", () => {
			seRichTextFieldComponent.field = {} as any;

			seRichTextFieldComponent.reassignUserCheck();
			expect(seRichTextFieldComponent.field.requiresUserCheck[seRichTextFieldComponent.qualifier]).toBe(false);
		});
	});

});
