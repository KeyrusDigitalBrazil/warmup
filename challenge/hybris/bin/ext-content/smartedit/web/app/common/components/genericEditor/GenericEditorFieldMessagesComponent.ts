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
import {SeComponent} from "smarteditcommons";
import * as angular from "angular";
import {GenericEditorField, GenericEditorFieldMessage} from "smarteditcommons/components/genericEditor";

/**
 * @ngdoc directive
 * @name genericEditorModule.component:seGenericEditorFieldMessages
 * @element se-generic-editor-field-messages
 *
 * @description
 * Component responsible for displaying validation messages like errors or warnings.
 *
 * @param {< Object} field The field object that contains array of messages.
 * @param {< String} qualifier For a non-localized field, it is the actual field.qualifier. For a localized field, it is the ISO code of the language.
 */
@SeComponent({
	templateUrl: 'genericEditorFieldMessagesComponentTemplate.html',
	inputs: [
		'field',
		'qualifier'
	]
})
export class SeGenericEditorFieldMessagesComponent {

	public field: GenericEditorField;
	public qualifier: string;
	public errors: string[];
	public warnings: string[];

	private previousMessages: string = null;

	constructor(
		private VALIDATION_MESSAGE_TYPES: any
	) {}

	getFilteredMessagesByType(messageType: string): string[] {
		return (this.field.messages || []).filter((validationMessage: GenericEditorFieldMessage) => {
			return validationMessage.marker === this.qualifier && !validationMessage.format && validationMessage.type === messageType;
		}).map((validationMessage) => {
			return validationMessage.message;
		});
	}

	$doCheck(): void {
		if (this.field) {
			// TODO: Remove angular.
			const currentMessages = angular.toJson(this.field.messages);
			if (this.previousMessages !== currentMessages) {
				this.previousMessages = currentMessages;
				this.errors = this.getFilteredMessagesByType(this.VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR);
				this.warnings = this.getFilteredMessagesByType(this.VALIDATION_MESSAGE_TYPES.WARNING);
			}
		}
	}
}
