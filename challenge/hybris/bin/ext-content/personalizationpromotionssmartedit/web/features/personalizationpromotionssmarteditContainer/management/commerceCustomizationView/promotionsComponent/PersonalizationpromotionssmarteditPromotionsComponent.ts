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

import {IExperienceService, SeComponent} from 'smarteditcommons';
import {PersonalizationpromotionssmarteditIAction, PersonalizationpromotionssmarteditRestService} from 'personalizationpromotionssmarteditcommons';

@SeComponent({
	templateUrl: 'personalizationpromotionssmarteditPromotionsTemplate.html'
})
export class PersonalizationpromotionssmarteditPromotionsComponent {

	public promotion: any = null;
	public availablePromotions: any = [];

	constructor(
		private $q: angular.IQService,
		private $filter: angular.IFilterService,
		private personalizationpromotionssmarteditRestService: PersonalizationpromotionssmarteditRestService,
		private personalizationsmarteditMessageHandler: any,
		private actionsDataFactory: any,
		private experienceService: IExperienceService) {
	}

	$onInit(): void {
		this.getAvailablePromotions();
	}

	public getCatalogs(): angular.IPromise<any> {
		const deferred = this.$q.defer();

		this.experienceService.getCurrentExperience().then((experience) => {
			const catalogs: any = [];

			catalogs.push({
				catalog: experience.catalogDescriptor.catalogId,
				catalogVersion: experience.catalogDescriptor.catalogVersion
			});

			experience.productCatalogVersions.forEach((item: any) => {
				catalogs.push({
					catalog: item.catalog,
					catalogVersion: item.catalogVersion
				});
			});

			deferred.resolve(catalogs);
		});

		return deferred.promise;
	}

	public getPromotions(): angular.IPromise<any> {
		const deferred = this.$q.defer();

		this.getCatalogs().then((catalogs) => {
			this.personalizationpromotionssmarteditRestService.getPromotions(catalogs).then(
				(response: any) => {
					deferred.resolve(response);
				},
				(response: any) => {
					deferred.reject(response);
				}
			);
		});

		return deferred.promise;
	}

	public getAvailablePromotions(): void {
		this.getPromotions()
			.then((response: any) => {
				this.availablePromotions = response.promotions;
			}, () => {
				this.personalizationsmarteditMessageHandler.sendError(this.$filter('translate')('personalization.error.gettingpromotions'));
			});

	}

	public buildAction(item: any): PersonalizationpromotionssmarteditIAction {
		return {
			type: 'cxPromotionActionData',
			promotionId: item.code
		};
	}

	public comparer(a1: PersonalizationpromotionssmarteditIAction, a2: PersonalizationpromotionssmarteditIAction): boolean {
		return a1.type === a2.type && a1.promotionId === a2.promotionId;
	}

	public promotionSelected(item: any, uiSelectObject: any): void {
		const action = this.buildAction(item);
		this.actionsDataFactory.addAction(action, this.comparer);
		uiSelectObject.selected = null;
	}

	public isItemInSelectDisabled(item: any): boolean {
		const action = this.buildAction(item);
		return this.actionsDataFactory.isItemInSelectedActions(action, this.comparer);
	}

	public initUiSelect(uiSelectController: any): void {
		uiSelectController.isActive = function() {
			return false;
		};
	}

}