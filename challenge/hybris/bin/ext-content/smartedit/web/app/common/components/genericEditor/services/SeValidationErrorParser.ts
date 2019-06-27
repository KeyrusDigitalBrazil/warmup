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
import {GenericEditorFieldMessage, SeValidationMessageParser} from "smarteditcommons/components/genericEditor";

/**
 * @deprecated since 6.5
 * @ngdoc service
 * @name genericEditorServicesModule.seValidationErrorParser
 * @description
 * This service provides the functionality to parse validation errors received from the backend. The validationErrorsParser
 * service, which is used to parse validation errors for parameters
 * such as language and format, which are sent as part of the message itself.
 */
@SeInjectable()
export class SeValidationErrorParser {

	constructor(
		private seValidationMessageParser: SeValidationMessageParser
	) {}

	/**
	 * @ngdoc method
	 * @name genericEditorServicesModule.seValidationErrorParser.parse
	 * @methodOf genericEditorServicesModule.seValidationErrorParser
	 * @description
	 * Parses extra details, such as language and format, from a validation error message. These details are also
	 * stripped out of the final message. This function expects the message to be in the following format:
	 *
	 * <pre>
	 * var message = "Some validation error occurred. Language: [en]. Format: [widescreen]. SomeKey: [SomeVal]."
	 * </pre>
	 *
	 * The resulting error object is as follows:
	 * <pre>
	 * {
	 *     message: "Some validation error occurred."
	 *     language: "en",
	 *     format: "widescreen",
	 *     somekey: "someval"
	 * }
	 * </pre>
	 */
	parse(message: string): GenericEditorFieldMessage {
		return this.seValidationMessageParser.parse(message);
	}

}
