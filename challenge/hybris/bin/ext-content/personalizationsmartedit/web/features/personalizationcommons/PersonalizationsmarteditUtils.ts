import {ICatalogService, SeInjectable, SeValueProvider, TypedMap} from 'smarteditcommons';
import * as angular from 'angular';
import * as moment from 'moment';


export const PERSONALIZATION_MODEL_STATUS_CODES_PROVIDER: SeValueProvider = {
	provide: "PERSONALIZATION_MODEL_STATUS_CODES",
	useValue: {
		ENABLED: 'ENABLED',
		DISABLED: 'DISABLED'
	}
};

export const PERSONALIZATION_VIEW_STATUS_MAPPING_CODES_PROVIDER: SeValueProvider = {
	provide: "PERSONALIZATION_VIEW_STATUS_MAPPING_CODES",
	useValue: {
		ALL: 'ALL',
		ENABLED: 'ENABLED',
		DISABLED: 'DISABLED'
	}
};

export const PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING_PROVIDER: SeValueProvider = {
	provide: "PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING",
	useValue: {
		0: {
			borderClass: 'personalizationsmarteditComponentSelected0',
			listClass: 'personalizationsmarteditComponentSelectedList0'
		},
		1: {
			borderClass: 'personalizationsmarteditComponentSelected1',
			listClass: 'personalizationsmarteditComponentSelectedList1'
		},
		2: {
			borderClass: 'personalizationsmarteditComponentSelected2',
			listClass: 'personalizationsmarteditComponentSelectedList2'
		},
		3: {
			borderClass: 'personalizationsmarteditComponentSelected3',
			listClass: 'personalizationsmarteditComponentSelectedList3'
		},
		4: {
			borderClass: 'personalizationsmarteditComponentSelected4',
			listClass: 'personalizationsmarteditComponentSelectedList4'
		},
		5: {
			borderClass: 'personalizationsmarteditComponentSelected5',
			listClass: 'personalizationsmarteditComponentSelectedList5'
		},
		6: {
			borderClass: 'personalizationsmarteditComponentSelected6',
			listClass: 'personalizationsmarteditComponentSelectedList6'
		},
		7: {
			borderClass: 'personalizationsmarteditComponentSelected7',
			listClass: 'personalizationsmarteditComponentSelectedList7'
		},
		8: {
			borderClass: 'personalizationsmarteditComponentSelected8',
			listClass: 'personalizationsmarteditComponentSelectedList8'
		},
		9: {
			borderClass: 'personalizationsmarteditComponentSelected9',
			listClass: 'personalizationsmarteditComponentSelectedList9'
		},
		10: {
			borderClass: 'personalizationsmarteditComponentSelected10',
			listClass: 'personalizationsmarteditComponentSelectedList10'
		},
		11: {
			borderClass: 'personalizationsmarteditComponentSelected11',
			listClass: 'personalizationsmarteditComponentSelectedList11'
		},
		12: {
			borderClass: 'personalizationsmarteditComponentSelected12',
			listClass: 'personalizationsmarteditComponentSelectedList12'
		},
		13: {
			borderClass: 'personalizationsmarteditComponentSelected13',
			listClass: 'personalizationsmarteditComponentSelectedList13'
		},
		14: {
			borderClass: 'personalizationsmarteditComponentSelected14',
			listClass: 'personalizationsmarteditComponentSelectedList14'
		}
	}
};

@SeInjectable()
export class PersonalizationsmarteditUtils {

	constructor(
		private $q: angular.IQService,
		private $filter: angular.IFilterService,
		private l10nFilter: any,
		private PERSONALIZATION_MODEL_STATUS_CODES: TypedMap<string>,
		private PERSONALIZATION_VIEW_STATUS_MAPPING_CODES: TypedMap<string>,
		private PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING: any,
		private catalogService: ICatalogService) {

	}

	pushToArrayIfValueExists(array: any, sKey: string, sValue: string): void {
		if (sValue) {
			array.push({
				key: sKey,
				value: sValue
			});
		}
	}


	getVariationCodes(variations: any): any {
		if ((typeof variations === undefined) || (variations === null)) {
			return [];
		}
		const allVariationsCodes = variations.map((elem: any) => {
			return elem.code;
		}).filter((elem: any) => {
			return typeof elem !== 'undefined';
		});
		return allVariationsCodes;
	}


	getVariationKey(customizationId: string, variations: any): any {
		if (customizationId === undefined || variations === undefined) {
			return [];
		}

		const allVariationsKeys = variations.map((variation: any) => {
			return {
				variationCode: variation.code,
				customizationCode: customizationId,
				catalog: variation.catalog,
				catalogVersion: variation.catalogVersion
			};
		});
		return allVariationsKeys;
	}


	getSegmentTriggerForVariation(variation: any): any {
		const triggers = variation.triggers || [];
		const segmentTriggerArr = triggers.filter((trigger: any) => {
			return trigger.type === "segmentTriggerData";
		});

		if (segmentTriggerArr.length === 0) {
			return {};
		}

		return segmentTriggerArr[0];
	}


	isPersonalizationItemEnabled(item: any): any {
		return item.status === this.PERSONALIZATION_MODEL_STATUS_CODES.ENABLED;
	}

	getTranslateFilter(): any {
		return this.$filter('translate');
	}

	getEnablementTextForCustomization(customization: any, keyPrefix: string): string {
		keyPrefix = keyPrefix || "personalization";
		if (this.isPersonalizationItemEnabled(customization)) {
			return this.getTranslateFilter()(keyPrefix + '.customization.enabled');
		} else {
			return this.getTranslateFilter()(keyPrefix + '.customization.disabled');
		}
	}


	getEnablementTextForVariation(variation: any, keyPrefix: string): string {
		keyPrefix = keyPrefix || "personalization";

		if (this.isPersonalizationItemEnabled(variation)) {
			return this.getTranslateFilter()(keyPrefix + '.variation.enabled');
		} else {
			return this.getTranslateFilter()(keyPrefix + '.variation.disabled');
		}
	}


	getEnablementActionTextForVariation(variation: any, keyPrefix: string): string {
		keyPrefix = keyPrefix || "personalization";

		if (this.isPersonalizationItemEnabled(variation)) {
			return this.getTranslateFilter()(keyPrefix + '.variation.options.disable');
		} else {
			return this.getTranslateFilter()(keyPrefix + '.variation.options.enable');
		}
	}


	getActivityStateForCustomization(customization: any): string {
		if (customization.status === this.PERSONALIZATION_MODEL_STATUS_CODES.ENABLED) {
			if (moment().isBetween(new Date(customization.enabledStartDate), new Date(customization.enabledEndDate), 'minute', '[]')) {
				return "perso__status--enabled";
			} else {
				return "perso__status--ignore";
			}
		} else {
			return "perso__status--disabled";
		}
	}


	getActivityStateForVariation(customization: any, variation: any): string {
		if (variation.enabled) {
			return this.getActivityStateForCustomization(customization);
		} else {
			return "perso__status--disabled";
		}
	}


	isItemVisible(item: any): boolean {
		return item.status !== 'DELETED';
	}


	getVisibleItems(items: any): any {
		return items.filter((item: any) => {
			return this.isItemVisible(item);
		});
	}


	getValidRank(items: any, item: any, increaseValue: any): any {
		const from = items.indexOf(item);
		const delta = increaseValue < 0 ? -1 : 1;

		let increase = from + increaseValue;

		while (increase >= 0 && increase < items.length && !this.isItemVisible(items[increase])) {
			increase += delta;
		}

		increase = increase >= items.length ? items.length - 1 : increase;
		increase = increase < 0 ? 0 : increase;

		return items[increase].rank;
	}


	getStatusesMapping(): any {
		const statusesMapping = [];

		statusesMapping.push({
			code: this.PERSONALIZATION_VIEW_STATUS_MAPPING_CODES.ALL,
			text: 'personalization.context.status.all',
			modelStatuses: [this.PERSONALIZATION_MODEL_STATUS_CODES.ENABLED, this.PERSONALIZATION_MODEL_STATUS_CODES.DISABLED]
		});

		statusesMapping.push({
			code: this.PERSONALIZATION_VIEW_STATUS_MAPPING_CODES.ENABLED,
			text: 'personalization.context.status.enabled',
			modelStatuses: [this.PERSONALIZATION_MODEL_STATUS_CODES.ENABLED]
		});

		statusesMapping.push({
			code: this.PERSONALIZATION_VIEW_STATUS_MAPPING_CODES.DISABLED,
			text: 'personalization.context.status.disabled',
			modelStatuses: [this.PERSONALIZATION_MODEL_STATUS_CODES.DISABLED]
		});

		return statusesMapping;
	}


	getClassForElement(index: any): string {
		const wrappedIndex = index % Object.keys(this.PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING).length;
		return this.PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING[wrappedIndex].listClass;
	}


	getLetterForElement(index: any): string {
		const wrappedIndex = index % Object.keys(this.PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING).length;
		return String.fromCharCode('a'.charCodeAt(0) + wrappedIndex).toUpperCase();
	}


	getCommerceCustomizationTooltip(variation: any, prefix: string, suffix: string): string {
		prefix = prefix || "";
		suffix = suffix || "\n";
		let result = "";
		angular.forEach(variation.commerceCustomizations, (propertyValue, propertyKey) => {
			result += prefix + this.$filter('translate')('personalization.modal.manager.commercecustomization.' + propertyKey) + ": " + propertyValue + suffix;
		});
		return result;
	}


	getCommerceCustomizationTooltipHTML(variation: any): string {
		return this.getCommerceCustomizationTooltip(variation, "<div>", "</div>");
	}


	isItemFromCurrentCatalog(item: any, seData: any): boolean {
		const cd = seData.seExperienceData.catalogDescriptor;
		return item.catalog === cd.catalogId && item.catalogVersion === cd.catalogVersion;
	}


	hasCommerceActions(variation: any): boolean {
		return variation.numberOfCommerceActions > 0;
	}


	getCatalogVersionNameByUuid(catalogVersionUuid: string): angular.IPromise<any> {
		const deferred = this.$q.defer();
		this.catalogService.getCatalogVersionByUuid(catalogVersionUuid).then((catalogVersion: any) => {
			deferred.resolve(this.l10nFilter(catalogVersion.catalogName) + ' (' + catalogVersion.version + ')');
		});
		return deferred.promise;
	}


	getAndSetCatalogVersionNameL10N(customization: any): void {
		this.getCatalogVersionNameByUuid(customization.catalog + '\/' + customization.catalogVersion).then((response: any) => {
			customization.catalogVersionNameL10N = response;
		});
	}
}