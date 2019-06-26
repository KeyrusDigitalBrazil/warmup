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
import {diNameUtils, SeModule} from 'smarteditcommons';
import {VersionExperienceInterceptor} from './VersionExperienceInterceptor';

@SeModule({
	imports: [
		'interceptorHelperModule'
	],
	providers: [VersionExperienceInterceptor],
	config: ($httpProvider: angular.IHttpProvider) => {
		'ngInject';
		$httpProvider.interceptors.push(diNameUtils.buildServiceName(VersionExperienceInterceptor));
	}
})
export class VersionExperienceInterceptorModule {}
