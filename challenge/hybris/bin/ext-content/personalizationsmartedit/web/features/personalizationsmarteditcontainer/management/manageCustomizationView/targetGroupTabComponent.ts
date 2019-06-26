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
import * as angular from "angular";
import {PersonalizationsmarteditDateUtils} from "personalizationcommons/PersonalizationsmarteditDateUtils";
import {TriggerTabService} from './multipleTriggersComponent/TriggerTabService';
import {ITriggerTab} from './multipleTriggersComponent/ITriggerTab';

/* @ngInject */
class TargetGroupTabController implements angular.IController {

	public customization: any;
	public sliderPanelConfiguration: any;
	public edit: any;
	public hideSliderPanel: any;
	public showSliderPanel: any;

	constructor(
		public PERSONALIZATION_MODEL_STATUS_CODES: any,
		public CUSTOMIZATION_VARIATION_MANAGEMENT_TABS_CONSTANTS: any,
		private personalizationsmarteditUtils: any,
		private personalizationsmarteditTriggerService: any,
		private $filter: angular.IFilterService,
		private $timeout: any,
		public yjQuery: JQueryStatic,
		private confirmationModalService: any,
		private isBlank: any,
		public personalizationsmarteditDateUtils: PersonalizationsmarteditDateUtils,
		private triggerTabService: TriggerTabService

	) {}

	getActivityActionTextForVariation(variation: any): string {
		if (variation.enabled) {
			return this.$filter('translate')('personalization.modal.customizationvariationmanagement.targetgrouptab.variation.options.disable');
		} else {
			return this.$filter('translate')('personalization.modal.customizationvariationmanagement.targetgrouptab.variation.options.enable');
		}
	}

	getActivityStateForCustomization(customization: any): string {
		return this.personalizationsmarteditUtils.getActivityStateForCustomization(customization);
	}

	getActivityStateForVariation(customization: any, variation: any): string {
		return this.personalizationsmarteditUtils.getActivityStateForVariation(customization, variation);
	}

	getEnablementTextForVariation(variation: any): string {
		return '(' + this.personalizationsmarteditUtils.getEnablementTextForVariation(variation, 'personalization.modal.customizationvariationmanagement.targetgrouptab') + ')';
	}

	setSliderConfigForAdd(): void {
		this.sliderPanelConfiguration.modal.save.label = "personalization.modal.customizationvariationmanagement.targetgrouptab.addvariation";
		this.sliderPanelConfiguration.modal.save.isDisabledFn = () => {
			return !this.canSaveVariation();
		};
		this.sliderPanelConfiguration.modal.save.onClick = () => {
			this.addVariationClick();
		};
	}

	setSliderConfigForEditing(): void {
		this.sliderPanelConfiguration.modal.save.label = "personalization.modal.customizationvariationmanagement.targetgrouptab.savechanges";
		this.sliderPanelConfiguration.modal.save.isDisabledFn = () => {
			return !this.canSaveVariation();
		};
		this.sliderPanelConfiguration.modal.save.onClick = () => {
			this.submitChangesClick();
		};
	}

	toggleSliderFullscreen(enableFullscreen: boolean): void {
		const modalObject = angular.element(".sliderPanelParentModal");
		const className = "modal-fullscreen";
		if (modalObject.hasClass(className) || enableFullscreen === false) {
			modalObject.removeClass(className);
		} else {
			modalObject.addClass(className);
		}
		this.$timeout((() => {
			this.yjQuery(window).resize();
		}), 0);
	}

	confirmDefaultTrigger(isDefault: any): void {
		if (isDefault && this.personalizationsmarteditTriggerService.isValidExpression(this.edit.expression[0])) {
			this.confirmationModalService.confirm({
				description: 'personalization.modal.manager.targetgrouptab.defaulttrigger.content'
			}).then(() => {
				this.edit.showExpression = false;
			}, () => {
				this.edit.isDefault = false;
			});
		} else {
			this.edit.showExpression = !isDefault;
		}
	}

	canSaveVariation(): boolean {
		const triggerTabs: ITriggerTab[] = this.triggerTabService.getTriggersTabs();
		const isValidOrEmpty: boolean = triggerTabs.every((element: ITriggerTab) => {
			return element.isValidOrEmpty();
		});
		const isTriggerDefined: boolean = triggerTabs.some((element: ITriggerTab) => {
			return element.isTriggerDefined();
		});

		let canSaveVariation: boolean = !this.isBlank(this.edit.name);
		canSaveVariation = canSaveVariation && (this.edit.isDefault || (isTriggerDefined && isValidOrEmpty));

		return canSaveVariation;
	}

	addVariationClick(): void {
		this.customization.variations.push({
			code: this.edit.code,
			name: this.edit.name,
			enabled: true,
			status: this.PERSONALIZATION_MODEL_STATUS_CODES.ENABLED,
			triggers: this.personalizationsmarteditTriggerService.buildTriggers(this.edit, this.edit.selectedVariation.triggers || []),
			rank: this.customization.variations.length,
			isNew: true
		});

		this.clearEditedVariationDetails();
		this.toggleSliderFullscreen(false);
		this.$timeout((() => {
			this.hideSliderPanel();
		}), 0);
	}

	submitChangesClick(): void {

		const triggers = this.personalizationsmarteditTriggerService.buildTriggers(this.edit, this.edit.selectedVariation.triggers || []);
		this.edit.selectedVariation.triggers = triggers;

		this.edit.selectedVariation.name = this.edit.name;
		this.edit.selectedVariation = undefined;
		this.toggleSliderFullscreen(false);
		this.$timeout((() => {
			this.hideSliderPanel();
		}), 0);
	}

	cancelChangesClick(): void {
		if (this.isVariationSelected()) {
			this.edit.selectedVariation = undefined;
		} else {
			this.clearEditedVariationDetails();
		}
		this.toggleSliderFullscreen(false);
		this.hideSliderPanel();
	}

	isVariationSelected(): boolean {
		return angular.isDefined(this.edit.selectedVariation);
	}

	clearEditedVariationDetails(): void {
		this.edit.code = '';
		this.edit.name = '';
		this.edit.expression = [];
		this.edit.isDefault = false;
		this.edit.showExpression = true;
	}

	setVariationRank(variation: any, increaseValue: number, $event: any, firstOrLast: any): any {
		if (firstOrLast) {
			$event.stopPropagation();
		} else {
			const fromIndex = this.customization.variations.indexOf(variation);
			const to = this.personalizationsmarteditUtils.getValidRank(this.customization.variations, variation, increaseValue);
			const variationsArr = this.customization.variations;
			if (to >= 0 && to < variationsArr.length) {
				variationsArr.splice(to, 0, variationsArr.splice(fromIndex, 1)[0]);
				this.recalculateRanksForVariations();
			}
		}
	}

	recalculateRanksForVariations(): void {
		this.customization.variations.forEach((part: any, index: number) => {
			this.customization.variations[index].rank = index;
		});
	}

	removeVariationClick(variation: any): void {
		this.confirmationModalService.confirm({
			description: 'personalization.modal.manager.targetgrouptab.deletevariation.content'
		}).then(() => {
			if (variation.isNew) {
				this.customization.variations.splice(this.customization.variations.indexOf(variation), 1);
			} else {
				variation.status = "DELETED";
			}
			this.edit.selectedVariation = undefined;
			this.recalculateRanksForVariations();
		});
	}

	addVariationAction(): void {
		this.clearEditedVariationDetails();
		this.setSliderConfigForAdd();
		this.showSliderPanel();
		this.edit.selectedVariation = {triggers: []};
	}

	editVariationAction(variation: any): void {
		this.setSliderConfigForEditing();
		this.edit.selectedVariation = variation;
		this.edit.code = variation.code;
		this.edit.name = variation.name;
		this.edit.isDefault = this.personalizationsmarteditTriggerService.isDefault(variation.triggers);
		this.edit.showExpression = !this.edit.isDefault;
		this.showSliderPanel();
	}

	toogleVariationActive(variation: any): void {
		variation.enabled = !variation.enabled;
		variation.status = variation.enabled ? this.PERSONALIZATION_MODEL_STATUS_CODES.ENABLED : this.PERSONALIZATION_MODEL_STATUS_CODES.DISABLED;
	}

	$onInit(): void {
		this.edit = this.triggerTabService.getTriggerDataState();
		this.sliderPanelConfiguration = {
			modal: {
				showDismissButton: true,
				title: "personalization.modal.customizationvariationmanagement.targetgrouptab.slidingpanel.title",
				cancel: {
					label: "personalization.modal.customizationvariationmanagement.targetgrouptab.cancelchanges",
					onClick: () => {
						this.cancelChangesClick();
					}
				},
				dismiss: {
					onClick: () => {
						this.cancelChangesClick();
					}
				},
				save: {}
			},
			cssSelector: "#y-modal-dialog"
		};

		const segmentTriggerTab: ITriggerTab = {
			id: "segmentTrigger",
			title: 'personalization.modal.customizationvariationmanagement.targetgrouptab.segments',
			templateUrl: 'personalizationsmarteditSegmentViewWrapperTemplate.html',
			isTriggerDefined: () => {
				return this.personalizationsmarteditTriggerService.isValidExpression(this.edit.expression[0]);
			},
			isValidOrEmpty: () => {
				this.edit.expression = this.edit.expression || [{nodes: []}];
				return this.personalizationsmarteditTriggerService.isValidExpression(this.edit.expression[0])
					|| this.edit.expression.length === 0
					|| this.edit.expression[0].nodes.length === 0
					|| this.personalizationsmarteditTriggerService.isDropzone(this.edit.expression[0].nodes[0]);
			}
		};
		this.triggerTabService.addTriggerTab(segmentTriggerTab);
	}

	$onChanges(changes: any): void {
		if (changes.variation && changes.variation.currentValue) {
			this.editVariationAction(changes.variation.currentValue);
		}
	}

}

export const targetGroupTabComponent: angular.IComponentOptions = {
	controller: TargetGroupTabController,
	templateUrl: 'targetGroupTabTemplate.html',
	bindings: {
		customization: '=?',
		variation: '<?'
	}
};
