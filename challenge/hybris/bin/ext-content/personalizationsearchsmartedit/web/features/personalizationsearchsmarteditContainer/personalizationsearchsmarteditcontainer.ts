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
import {doImport as doImport1} from './forcedImports';
doImport1();

angular.module('personalizationsearchsmarteditContainer', [
	'featureServiceModule',
	'personalizationsearchsmarteditContainerTemplates',
	'personalizationsearchSearchProfilesModule',
	'yjqueryModule'])
	.run((
		yjQuery: any,
		domain: any) => {
		'ngInject';

		const loadCSS = (href: string) => {
			const cssLink = yjQuery("<link rel='stylesheet' type='text/css' href='" + href + "'>");
			yjQuery("head").append(cssLink);
		};
		loadCSS(domain + "/personalizationsearchsmartedit/css/style.css");

	});
