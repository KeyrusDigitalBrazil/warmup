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
import {SeInjectable} from "smarteditcommons";
import {GenericEditorFieldMessage} from "smarteditcommons/components/genericEditor";

/**
 * @ngdoc service
 * @name genericEditorServicesModule.seValidationMessageParser
 * @description
 * This service provides the functionality to parse validation messages (errors, warnings) received from the backend.
 * This service is used to parse validation messages (errors, warnings) for parameters such as language and format,
 * which are sent as part of the message itself.
 */
@SeInjectable()
export class SeValidationMessageParser {

	/**
	 * @ngdoc method
	 * @name genericEditorServicesModule.seValidationMessageParser.parse
	 * @methodOf genericEditorServicesModule.seValidationMessageParser
	 * @description
	 * Parses extra details, such as language and format, from a validation message (error, warning). These details are also
	 * stripped out of the final message. This function expects the message to be in the following format:
	 *
	 * <pre>
	 * const message = "Some validation message occurred. Language: [en]. Format: [widescreen]. SomeKey: [SomeVal]."
	 * </pre>
	 *
	 * The resulting message object is as follows:
	 * <pre>
	 * {
	 *     message: "Some validation message occurred."
	 *     language: "en",
	 *     format: "widescreen",
	 *     somekey: "someval"
	 * }
	 * </pre>
	 */
	parse(message: string): GenericEditorFieldMessage {
		const expression = new RegExp('[a-zA-Z]+: (\[|\{)([a-zA-Z0-9]+)(\]|\})\.?', 'g');
		const matches = message.match(expression) || [];
		return matches.reduce((messages: GenericEditorFieldMessage, match) => {
			messages.message = messages.message.replace(match, '').trim();
			const key = match.split(':')[0].trim().toLowerCase();
			const value = match.split(':')[1].match(/[a-zA-Z0-9]+/g)[0];

			messages[key] = value;
			return messages;
		}, {
				message
			});
	}

}
