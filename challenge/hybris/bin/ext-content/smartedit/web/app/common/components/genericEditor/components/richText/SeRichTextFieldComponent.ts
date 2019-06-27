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

import {SeComponent, TypedMap} from "smarteditcommons";
import {
	GenericEditorField,
	GenericEditorSanitizationService, IGenericEditor, SeRichTextFieldLocalizationService,
	SeRichTextLoaderService
} from "smarteditcommons/components/genericEditor";
import "ckeditor";

@SeComponent({
	templateUrl: 'seRichTextFieldComponentTemplate.html',
	inputs: [
		'field:=',
		'qualifier:=',
		'model:=',
		'editor:=',
		'isDisabled'
	]
})
export class SeRichTextFieldComponent {

	public field: GenericEditorField;
	public qualifier: string;
	public model: TypedMap<any>;
	public editor: IGenericEditor;

	private mode: string;
	private editorInstance: CKEDITOR.editor;

	constructor(
		private seRichTextLoaderService: SeRichTextLoaderService,
		private seRichTextConfiguration: any,
		private genericEditorSanitizationService: GenericEditorSanitizationService,
		private seRichTextFieldLocalizationService: SeRichTextFieldLocalizationService,
		private $scope: angular.IScope,
		private $element: angular.IAugmentedJQuery
	) {
		this.seRichTextLoaderService.load().then(() => {
			const textAreaElement = this.$element.find('textarea')[0] as HTMLTextAreaElement;
			this.editorInstance = CKEDITOR.replace(textAreaElement, this.seRichTextConfiguration);

			this.seRichTextFieldLocalizationService.localizeCKEditor();

			this.$element.bind('$destroy', () => {
				if (this.editorInstance && CKEDITOR.instances[this.editorInstance.name]) {
					CKEDITOR.instances[this.editorInstance.name].destroy();
				}
			});

			this.editorInstance.on('change', this.onChange.bind(this));
			this.editorInstance.on('mode', this.onMode.bind(this));
			CKEDITOR.on('instanceReady', this.onInstanceReady.bind(this));
		});
	}

	onChange(): void {
		this.$scope.$apply(() => {
			this.model[this.qualifier] = this.editorInstance.getData();
			this.reassignUserCheck();
		});
	}

	onMode(): void {
		if (this.mode === 'source') {
			const editable = this.editorInstance.editable();
			editable.attachListener(editable, 'input', () => {
				this.editorInstance.fire('change');
			});
		}
	}

	onInstanceReady(ev: any): void {
		ev.editor.dataProcessor.writer.setRules('br', {
			indent: false,
			breakBeforeOpen: false,
			breakAfterOpen: false,
			breakBeforeClose: false,
			breakAfterClose: false
		});
	}

	requiresUserCheck(): boolean {
		let requiresUserCheck = false;
		for (const qualifier in this.field.requiresUserCheck) {
			if (this.field.requiresUserCheck.hasOwnProperty(qualifier)) {
				requiresUserCheck = requiresUserCheck || this.field.requiresUserCheck[qualifier];
			}
		}
		return requiresUserCheck;
	}

	reassignUserCheck(): void {
		if (this.model && this.qualifier && this.model[this.qualifier]) {
			const sanitizedContentMatchesContent = this.genericEditorSanitizationService.isSanitized(this.model[this.qualifier]);
			this.field.requiresUserCheck = this.field.requiresUserCheck || {};
			this.field.requiresUserCheck[this.qualifier] = !sanitizedContentMatchesContent;
		} else {
			this.field.requiresUserCheck = this.field.requiresUserCheck || {};
			this.field.requiresUserCheck[this.qualifier] = false;
		}
	}

}
