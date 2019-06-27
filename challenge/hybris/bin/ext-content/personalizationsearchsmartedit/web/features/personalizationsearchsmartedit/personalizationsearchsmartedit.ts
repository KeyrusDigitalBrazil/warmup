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

angular.module('personalizationsearchsmartedit', [
	'featureServiceModule', // Feature API Module from SmartEdit Application
	'perspectiveServiceModule', // Perspective API Module from SmartEdit Application
	'decoratorServiceModule' // Decorator API Module from SmartEdit Application
])
	.run(function(decoratorService: any, featureService: any, perspectiveService: any) { // Parameters are injected factory methods
		'ngInject';
	});
