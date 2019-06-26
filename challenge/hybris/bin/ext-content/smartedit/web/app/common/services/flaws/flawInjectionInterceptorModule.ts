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
import * as angular from 'angular';
import {diNameUtils, SeModule} from 'smarteditcommons/services/dependencyInjection/di';
import {FlawInjectionInterceptor} from './FlawInjectionInterceptor';

/** @internal */
@SeModule({
	imports: [
		'interceptorHelperModule'
	],
	providers: [FlawInjectionInterceptor],
	config: ($httpProvider: angular.IHttpProvider) => {
		'ngInject';
		$httpProvider.interceptors.push(diNameUtils.buildServiceName(FlawInjectionInterceptor));
	},
	initialize: (flawInjectionInterceptor: FlawInjectionInterceptor) => {
		'ngInject';
		// mutates sites id
		flawInjectionInterceptor.registerRequestFlaw({
			test: (config: angular.IRequestConfig) => /sites\/[\w-]+\//.test(config.url),
			mutate: (config: angular.IRequestConfig) => {
				config.url = config.url.replace(/sites\/([\w-]+)\//, "sites/" + Math.random() + "/");
				return config;
			}
		});

	}
})
export class FlawInjectionInterceptorModule {}

