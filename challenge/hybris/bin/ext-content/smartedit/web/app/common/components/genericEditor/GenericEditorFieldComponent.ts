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
import {GenericEditorComponent} from "smarteditcommons/components/genericEditor/GenericEditorComponent";
import {GenericEditorField, IGenericEditor} from "smarteditcommons/components/genericEditor/types";

/* @internal  */
interface GenericEditorFieldComponentScope extends angular.IScope {
	editor?: IGenericEditor;
	model: TypedMap<any>;
	field?: GenericEditorField;
	qualifier?: string;
	id: string;
	editorStackId: string;
	isFieldDisabled: () => boolean;
}

@SeComponent({
	templateUrl: 'genericEditorFieldComponentTemplate.html',
	inputs: [
		'field',
		'qualifier',
		'model:=',
		'id:='
	],
	require: {
		ge: '^^genericEditor'
	}
})
export class GenericEditorFieldComponent {

	public field: GenericEditorField;
	public model: TypedMap<any>;
	public qualifier: string;
	public id: string;
	public ge: GenericEditorComponent;

	constructor(
		private $scope: GenericEditorFieldComponentScope
	) {}

	$onInit() {
		// TODO: Remove scope inheritance.
		this.$scope.editor = this.ge.editor;
		this.$scope.model = this.model;
		this.$scope.field = this.field;
		this.$scope.qualifier = this.qualifier;
		this.$scope.id = this.id;
		this.$scope.editorStackId = this.ge.editorStackId;
		this.$scope.isFieldDisabled = this.isFieldDisabled.bind(this);
	}

	/**
	 * @internal 
	 * 
	 * This method is used to check if the field is disabled. If the field is not localized, 
	 * then it's the same as field.enabled. However, if the field is localized, then this 
	 * method will return a different result for each language. For example, this allows to 
	 * have 'en' disabled but 'fr' disabled, depending on language permissions. 
	 * 
	 */
	isFieldDisabled(): boolean {
		let isEnabled = this.field.editable;
		if (this.field.localized) {
			isEnabled = this.field.editable && this.field.isLanguageEnabledMap[this.qualifier];
		}

		return !isEnabled;
	}

}
