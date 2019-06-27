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
import {SeModule} from 'smarteditcommons';
import {PersonalizationpromotionssmarteditPromotionsComponent} from './promotionsComponent/PersonalizationpromotionssmarteditPromotionsComponent';
import {PersonalizationpromotionssmarteditIAction, PersonalizationpromotionssmarteditServiceModule} from 'personalizationpromotionssmarteditcommons';

@SeModule({
	imports: [
		'personalizationsmarteditCommons',
		'personalizationsmarteditCommerceCustomizationModule',
		PersonalizationpromotionssmarteditServiceModule,
		'experienceServiceModule'
	],
	config: ($logProvider: angular.ILogProvider) => {
		'ngInject';
		$logProvider.debugEnabled(false);
	},
	declarations: [PersonalizationpromotionssmarteditPromotionsComponent],
	initialize: (
		personalizationsmarteditCommerceCustomizationService: any,
		$filter: any
	) => {
		'ngInject';
		personalizationsmarteditCommerceCustomizationService.registerType({
			type: 'cxPromotionActionData',
			text: 'personalization.modal.commercecustomization.action.type.promotion',
			template: 'personalizationpromotionssmarteditPromotionsWrapperTemplate.html',
			confProperty: 'personalizationsmartedit.commercecustomization.promotions.enabled',
			getName: (action: PersonalizationpromotionssmarteditIAction) => {
				return $filter('translate')('personalization.modal.commercecustomization.promotion.display.name') + " - " + action.promotionId;
			}
		});
	}
})
export class PersonalizationpromotionssmarteditPromotionsModule {}