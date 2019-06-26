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
/* forbiddenNameSpaces angular.module:false */
import * as angular from 'angular';

/**
 * Backwards compatibility for partners and downstream teams
 * The deprecated modules below were moved to smarteditCommonsModule
 * 
 * IMPORTANT: THE DEPRECATED MODULES WILL NOT BE AVAILABLE IN FUTURE RELEASES
 * @deprecated since 1808
 */
/* @internal */
const deprecatedSince1808 = () => {
	angular.module('eventServiceModule', ['smarteditCommonsModule']);
	angular.module('crossFrameEventServiceModule', ['smarteditCommonsModule']);
	angular.module('languageServiceModule', ['smarteditCommonsModule']);
	angular.module('catalogServiceModule', ['smarteditCommonsModule']);
	angular.module('gatewayFactoryModule', ['smarteditRootModule']);
	angular.module('gatewayProxyModule', ['smarteditRootModule']);
	angular.module('operationContextServiceModule', ['smarteditRootModule']);
	angular.module('compileHtmlModule', ['smarteditCommonsModule']);
	angular.module('yMoreTextModule', ['smarteditCommonsModule']);
};

/*
 * Backwards compatibility for partners and downstream teams
 * The deprecated modules below were moved to smarteditCommonsModule
 *
 * IMPORTANT: THE DEPRECATED MODULES WILL NOT BE AVAILABLE IN FUTURE RELEASES
 * @deprecated since 1811
 */
/* @internal */
export function deprecatedSince1811() {
	angular.module('permissionServiceInterfaceModule', ['smarteditCommonsModule']);
	angular.module('FetchDataHandlerInterfaceModule', ['genericEditorServicesModule']);
	angular.module('fetchEnumDataHandlerModule', ['genericEditorServicesModule']);
	angular.module('dateFormatterModule', ['dateTimePickerModule']);
	angular.module('DropdownPopulatorInterface', ['dropdownPopulatorModule']);
	angular.module('optionsDropdownPopulatorModule', ['dropdownPopulatorModule']);
	angular.module('uriDropdownPopulatorModule', ['dropdownPopulatorModule']);
	angular.module('editorFieldMappingServiceModule', ['genericEditorServicesModule']);
	angular.module('genericEditorStackServiceModule', ['genericEditorServicesModule']);
	angular.module('genericEditorTabServiceModule', ['genericEditorServicesModule']);
	angular.module('seValidationErrorParserModule', ['genericEditorServicesModule']);
	angular.module('seValidationMessageParserModule', ['genericEditorServicesModule']);
	angular.module('seGenericEditorFieldMessagesModule', ['genericEditorModule']);
	angular.module('genericEditorTabModule', ['genericEditorModule']);
	angular.module('genericEditorFieldModule', ['genericEditorModule']);
	angular.module('authorizationModule', ['smarteditCommonsModule']);
}

export const deprecate = () => {
	deprecatedSince1808();
	deprecatedSince1811();
};
