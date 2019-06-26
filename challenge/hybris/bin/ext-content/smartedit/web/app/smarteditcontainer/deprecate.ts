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
import {
	IWaitDialogService,
} from 'smarteditcommons';
import {
	IframeManagerService,
} from './services';

/**
 * Backwards compatibility for partners and downstream teams
 * The deprecated modules below were moved to smarteditServicesModule
 * 
 * IMPORTANT: THE DEPRECATED MODULES WILL NOT BE AVAILABLE IN FUTURE RELEASES
 * @deprecated since 6.7
 */
/** @internal */
const deprecatedSince67 = () => {
	angular.module('urlServiceModule', ['smarteditServicesModule']);
	angular.module('sharedDataServiceModule', ['smarteditServicesModule']);
	angular.module('waitDialogServiceModule', ['smarteditServicesModule']);
	angular.module('productServiceModule', ['smarteditServicesModule']);
	angular.module('perspectiveServiceModule', ['smarteditServicesModule']);
	angular.module('siteServiceModule', ['smarteditServicesModule']);
	angular.module('notificationServiceModule', ['smarteditServicesModule']);
	angular.module('notificationMouseLeaveDetectionServiceModule', ['smarteditServicesModule']);
	angular.module('storageServiceModule', ['smarteditServicesModule']);
	angular.module('featureServiceModule', ['smarteditServicesModule']);
	angular.module('sessionServiceModule', ['smarteditServicesModule']);
	angular.module('experienceServiceModule', ['smarteditServicesModule']);
	angular.module('bootstrapServiceModule', ['cmsSmarteditServicesModule']);
	angular.module('experienceSelectorModule', ['smarteditServicesModule']);
};

/**
 * Backwards compatibility for partners and downstream teams
 * The deprecated modules below were moved to smarteditServicesModule
 *
 * IMPORTANT: THE DEPRECATED MODULES WILL NOT BE AVAILABLE IN FUTURE RELEASES
 * @deprecated since 1811
 */
/* @internal */
export function deprecatedSince1811() {
	angular.module('permissionServiceModule', ['smarteditServicesModule']);
	angular
		.module('iFrameManagerModule', ['smarteditServicesModule'])
		.service('iFrameManager', (iframeManagerService: IframeManagerService, waitDialogService: IWaitDialogService) => {
			(iframeManagerService as any).showWaitModal = (key?: string) => {
				waitDialogService.showWaitModal(key);
			};
			(iframeManagerService as any).hideWaitModal = () => {
				waitDialogService.hideWaitModal();
			};
			return iframeManagerService;
		});
	angular.module('catalogVersionPermissionRestServiceModule', ['smarteditServicesModule']);
}

export const deprecate = () => {
	deprecatedSince67();
	deprecatedSince1811();
};