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
/* forbiddenNameSpaces angular.module:false */
angular.element(document).ready(() => {

	// add sanitization of textarea's and input's
	Array.prototype.slice.apply(document.querySelectorAll("input:not([type='password'])"))
		.concat(Array.prototype.slice.apply(document.querySelectorAll("textarea")))
		.forEach((node: any) => {
			node.setAttribute('data-sanitize-html-input', '');
		});

	const smarteditNamespace = (window as any).smartedit;

	smarteditNamespace.applications.filter((application: string) => {
		try {
			angular.module(application);
			angular.module('smartedit').requires.push(application);
			return true;
		} catch (ex) {
			/* tslint:disable:no-console */
			console.error(`Failed to load inner application '${application}'; SmartEdit functionality may be compromised.`);
			return false;
		}
	});

	angular.module('smartedit')
		.constant('domain', smarteditNamespace.domain)
		.constant('smarteditroot', smarteditNamespace.smarteditroot);
	angular.bootstrap(document, ["smartedit"]);
});
