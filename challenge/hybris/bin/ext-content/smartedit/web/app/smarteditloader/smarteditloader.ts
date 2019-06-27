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
import {doImport} from './forcedImports';
doImport();

import * as angular from 'angular';
import {BootstrapService, SmarteditServicesModule} from 'smarteditcontainer/services';
import {ConfigurationObject} from 'smarteditcontainer/services/bootstrap/Configuration';
import {SeModule} from 'smarteditcommons';

@SeModule({
	imports: [
		SmarteditServicesModule,
		'templateCacheDecoratorModule',
		'loadConfigModule',
		'coretemplates',
		'translationServiceModule',
		'httpAuthInterceptorModule',
		'systemAlertsModule',
		'httpErrorInterceptorServiceModule',
		'unauthorizedErrorInterceptorModule',
		'resourceNotFoundErrorInterceptorModule',
		'retryInterceptorModule'
	],
	config: ($logProvider: angular.ILogProvider) => {
		'ngInject';
		$logProvider.debugEnabled(false);
	},
	initialize: (
		loadConfigManagerService: any,
		bootstrapService: BootstrapService,
		httpErrorInterceptorService: any,
		unauthorizedErrorInterceptor: any,
		resourceNotFoundErrorInterceptor: any,
		retryInterceptor: any
	) => {
		'ngInject';
		httpErrorInterceptorService.addInterceptor(retryInterceptor);
		httpErrorInterceptorService.addInterceptor(unauthorizedErrorInterceptor);
		httpErrorInterceptorService.addInterceptor(resourceNotFoundErrorInterceptor);

		loadConfigManagerService.loadAsObject().then((configurations: ConfigurationObject) => {
			bootstrapService.bootstrapContainerModules(configurations);
		});
	}
})
export class Smarteditloader {}