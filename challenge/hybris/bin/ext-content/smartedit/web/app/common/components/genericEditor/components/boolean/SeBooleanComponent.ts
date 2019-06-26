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
import {ISeComponent, SeComponent, TypedMap} from "smarteditcommons";
import {GenericEditorField, IGenericEditor} from "smarteditcommons/components/genericEditor";

/**
 * @ngdoc directive
 * @name seBooleanModule.directive:seBoolean
 * @scope
 * @restrict E
 * @element se-boolean
 *
 * @description
 * Component responsible for generating custom toggle for the {@link genericEditorModule.service:GenericEditor genericEditor}.
 *
 * The following is an example of a possible field structures that can be returned by the Structure API for seBoolean to work:
 * {
 *   cmsStructureType: "Boolean",
 *   qualifier: "someQualifier",
 *   i18nKey: 'i18nkeyForSomeQualifier',
 *   localized: false,
 *   defaultValue: true
 * }
 *
 * There is an optional property called defaultValue (which can be set to TRUE to enable the toggle by default)
 */
@SeComponent({
	templateUrl: 'booleanComponentTemplate.html',
	inputs: [
		'field',
		'qualifier',
		'model',
		'editor'
	]
})
export class SeBooleanComponent implements ISeComponent {

	public field: GenericEditorField;
	public qualifier: string;
	public model: TypedMap<any>;
	public editor: IGenericEditor;

	$onInit(): void {
		if (this.model[this.qualifier] === undefined) {
			const defaultValue = this.field.defaultValue !== undefined ? this.field.defaultValue : false;
			this.model[this.qualifier] = defaultValue;
			this.editor.pristine[this.qualifier] = defaultValue;
		}
	}

}
