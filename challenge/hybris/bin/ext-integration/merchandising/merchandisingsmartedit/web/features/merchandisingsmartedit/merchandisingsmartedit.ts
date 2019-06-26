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
import 'merchandisingsmartedit/merchandisingsmartedit_bundle.js';

angular.module('merchandisingsmartedit', [
	'contextualMenuServiceModule',
	'sharedDataServiceModule'
])
	.run(function(contextualMenuService: any, sharedDataService: any) {
		'ngInject';
		const setUpContextualMenu = function() {
			contextualMenuService.addItems({
				MerchandisingCarouselComponent: [{
					key: 'MerchandisingCarouselComponent',
					i18nKey: 'Edit Strategy',
					condition(configuration: any, event: any) {
						return true;
					},
					callback(configuration: any, event: any) {
						sharedDataService.get('contextDrivenServicesMerchandisingUrl').then(function(url: string) {
							const appUrl = 'https://' + url;
							window.open(appUrl);
						}.bind(this));
					},
					displayClass: 'movebutton',
					iconIdle: '/merchandisingsmartedit/icons/strategy_off.png',
					iconNonIdle: '/merchandisingsmartedit/icons/strategy_on.png',
					smallicon: '/merchandisingsmartedit/icons/info.png'
				}]
			});
		};
		setUpContextualMenu();
	});
