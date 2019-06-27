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
import {diNameUtils, SeModule} from "smarteditcommons";

import {GenericEditorTabService} from "./GenericEditorTabService";
import {DEFAULT_GENERIC_EDITOR_FLOAT_PRECISION, EditorFieldMappingService} from "./EditorFieldMappingService";
import {SeValidationMessageParser} from "./SeValidationMessageParser";
import {DEFAULT_EDITOR_POP_FROM_STACK_EVENT, DEFAULT_EDITOR_PUSH_TO_STACK_EVENT, GenericEditorStackService} from "./GenericEditorStackService";
import {SeValidationErrorParser} from "./SeValidationErrorParser";
import {FetchEnumDataHandler} from "./FetchEnumDataHandler";

/**
 * @ngdoc overview
 * @name genericEditorServicesModule
 */
@SeModule({
	providers: [
		diNameUtils.makeValueProvider({DEFAULT_GENERIC_EDITOR_FLOAT_PRECISION}),
		diNameUtils.makeValueProvider({DEFAULT_EDITOR_POP_FROM_STACK_EVENT}),
		diNameUtils.makeValueProvider({DEFAULT_EDITOR_PUSH_TO_STACK_EVENT}),
		GenericEditorStackService,
		EditorFieldMappingService,
		SeValidationMessageParser,
		GenericEditorTabService,
		SeValidationErrorParser,
		FetchEnumDataHandler,
	]
})
export class GenericEditorServicesModule {}